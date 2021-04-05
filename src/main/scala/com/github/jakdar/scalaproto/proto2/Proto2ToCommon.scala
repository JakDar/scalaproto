package com.github.jakdar.scalaproto.proto2

import com.github.jakdar.scalaproto.parser.ToCommon
import com.github.jakdar.scalaproto.parser.{Ast => CommonAst}
import com.github.jakdar.scalaproto.proto2.Ast.EnumAst
import com.github.jakdar.scalaproto.proto2.Ast.Message
import com.google.common.base.CaseFormat
import cats.data.NonEmptyList
import com.github.jakdar.scalaproto.proto2.Ast.ArgRepeat.Required
import com.github.jakdar.scalaproto.proto2.Ast.ArgRepeat.Optional
import com.github.jakdar.scalaproto.proto2.Ast.ArgRepeat.Repeated

object Proto2ToCommon extends ToCommon[Ast.AstEntity] {

  override def toCommon(other: Ast.AstEntity): Either[ToCommon.Error, List[CommonAst.AstEntity]] = {
    Right(other match {
      case e: EnumAst => enumToCommon(e) :: Nil
      case m: Message => messageToCommon(m) :: Nil
    })

  }

  private def enumToCommon(e: EnumAst): CommonAst.EnumAst = {

    def fixCasing(s: String) =
      if (s.filter(_.isLetter).forall(_.isUpper)) {
        CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, s)
      } else s
    val defs                 = e.values.map(line => CommonAst.EnumAst(CommonAst.Identifier(fixCasing(line.name.value)), definitions = Nil, parents = Nil))

    CommonAst.EnumAst(id = CommonAst.Identifier(e.name.value), definitions = defs, parents = Nil) // TODO:bcm snake case to cammel here?
  }

  private def messageToCommon(m: Message): CommonAst.ClassAst = {
    val args = m.fields.map(f => (CommonAst.Identifier(f.identifier.value), typeToCommon(f.typePath, f.repeat)))
    CommonAst.ClassAst(id = CommonAst.Identifier(m.name.value), argLists = NonEmptyList.of(CommonAst.Fields(args)), parents = Nil)
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
    case other    => CommonAst.CustomSimpleTypeIdentifier(packagePath = t.init.map(p => CommonAst.Identifier(p.value)), CommonAst.Identifier(other))
  }
}
