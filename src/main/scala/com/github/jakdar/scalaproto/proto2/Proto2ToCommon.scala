package com.github.jakdar.scalaproto.proto2

import cats.data.Ior
import cats.data.NonEmptyList
import com.github.jakdar.scalaproto.parser.ToCommon
import com.github.jakdar.scalaproto.parser.{Ast => CommonAst}
import com.github.jakdar.scalaproto.proto2.Ast.ArgRepeat.Optional
import com.github.jakdar.scalaproto.proto2.Ast.ArgRepeat.Repeated
import com.github.jakdar.scalaproto.proto2.Ast.ArgRepeat.Required
import com.github.jakdar.scalaproto.proto2.Ast.EnumAst
import com.github.jakdar.scalaproto.proto2.Ast.Message
import com.google.common.base.CaseFormat
import com.github.jakdar.scalaproto.util.StringUtils

object Proto2ToCommon extends ToCommon[Ast.AstEntity] {

  override def toCommon(other: Ast.AstEntity): Either[ToCommon.Error, List[CommonAst.AstEntity]] = {
    Right(other match {
      case e: EnumAst => enumToCommon(e) :: Nil
      case m: Message => messageToCommon(m).toList
    })

  }

  private def enumToCommon(e: EnumAst): CommonAst.ObjectAst = {

    def fixCasing(s: String) =
      if (s.filter(_.isLetter).forall(_.isUpper)) {
        CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, s)
      } else s
    val defs                 = e.values.map(line => CommonAst.ObjectAst(CommonAst.Identifier(fixCasing(line.name.value)), definitions = Nil, parents = Nil))

    CommonAst.ObjectAst(id = CommonAst.Identifier(e.name.value), definitions = defs, parents = Nil) // TODO:bcm snake case to cammel here?
  }

  private def messageToCommon(m: Message): NonEmptyList[CommonAst.AstEntity] = {
    import com.github.jakdar.scalaproto.proto2.Ast
    val objsAndArgs = m.entries.map {
      case f: Ast.FieldLine      =>
        Ior.Right((CommonAst.Identifier(f.identifier.value), typeToCommon(f.typePath, f.repeat)))

      // case _: Ast.AstEntity      => ???
      case oneOf: Ast.OneofField =>
        val entries = oneOf.entries.map { e =>
          val entryId = List(m.name.value, oneOf.identifier.value, e.identifier.value).map(StringUtils.titleCase).mkString

          CommonAst.ClassAst(
            id = CommonAst.Identifier(entryId), // TODO:bcm  title case
            argLists = NonEmptyList.of(CommonAst.Fields(List((CommonAst.Identifier(e.identifier.value), typeIdentifierToCommon(e.typePath))))),
            parents = Nil                       // check
          )
        }

        val oneOfId = List(m.name.value, oneOf.identifier.value).map(StringUtils.titleCase).mkString
        val typeId  = CommonAst.Identifier(oneOfId)
        val fieldId = CommonAst.Identifier(oneOf.identifier.value)

        Ior.Both(
          List(CommonAst.ObjectAst(id = typeId, definitions = entries, parents = Nil)), // title case
          (fieldId, CommonAst.CustomSimpleTypeIdentifier(Nil, typeId))
        )

      case ala: Ast.AstEntity => Ior.Left(toCommon(ala).getOrElse(???))
    }

    val args  = objsAndArgs.flatMap(_.right)
    val inner = objsAndArgs.flatMap(_.left.toList.flatten)

    NonEmptyList.of(
      CommonAst.ClassAst(id = CommonAst.Identifier(m.name.value), argLists = NonEmptyList.of(CommonAst.Fields(args)), parents = Nil),
      inner: _*
    )
  }

  private def typeToCommon(tp: Ast.TypePath, argRepeat: Ast.ArgRepeat) = {

    val typeId = typeIdentifierToCommon(tp)

    argRepeat match {
      case Required => typeId
      case Optional => CommonAst.OptionType(typeId)
      case Repeated => CommonAst.ArrayType(typeId)
    }

  }

  private def typeIdentifierToCommon(t: Ast.TypePath) = t.last.id.value match {
    case "int64"  => CommonAst.LongType
    case "int32"  => CommonAst.IntType
    case "double" => CommonAst.DoubleType
    case "bool"   => CommonAst.BooleanType
    case "string" => CommonAst.StringType
    case "bytes"  => CommonAst.ArrayType(CommonAst.ByteType)
    case other    => CommonAst.CustomSimpleTypeIdentifier(packagePath = t.init.map(p => CommonAst.Identifier(p.value)), CommonAst.Identifier(other))
  }
}
