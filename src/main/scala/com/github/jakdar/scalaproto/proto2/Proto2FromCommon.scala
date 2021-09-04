package com.github.jakdar.scalaproto.proto2

import com.github.jakdar.scalaproto.parser.Ast.ClassAst
import com.github.jakdar.scalaproto.parser.Ast.ObjectAst
import com.github.jakdar.scalaproto.parser.FromCommon
import com.github.jakdar.scalaproto.parser.{Ast => CommonAst}
import cats.syntax.alternative.catsSyntaxAlternativeSeparate
import com.google.common.base.CaseFormat
import Proto2FromCommon.Options
import com.github.jakdar.scalaproto.util.StringUtils

class Proto2FromCommon(options: Options) extends FromCommon[Ast.AstEntity] {

  override def fromCommon(other: Seq[CommonAst.AstEntity]): Seq[Ast.AstEntity] =
    other
      .flatMap {
        case c: ClassAst  => List(messageFromCommon(c))
        case e: ObjectAst => List(enumFromCommon(e))
      }
      .map(Proto2Homomorphisms.correctNumbers)

  private def enumFromCommon(e: CommonAst.ObjectAst): Ast.AstEntity = {
    def fixCasing(s: String) =
      if (!s.filter(_.isLetter).forall(_.isUpper)) {
        CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, s)
      } else s

    if (e.enumEntries.forall(_.isRight)) {

      val defs = e.enumEntries.flatMap(_.toOption).map(c => Ast.EnumLine(name = Ast.Identifier(fixCasing(c.id.value)), 0))

      Ast.EnumAst(name = Ast.Identifier(e.id.value), values = defs)
    } else {

      // TODO:bcm here
      // val (entries,classes)

      val (oneOfEntires, nestedMsgs) = e.enumEntries.zipWithIndex.map {

        case (Left(c: CommonAst.ClassAst), idx) =>
          val allArgs = c.argLists.toList.flatMap(_.args)

          if (allArgs.size == 1 && (typeIdToProto _).tupled(allArgs.head).repeat == Ast.ArgRepeat.Required) {

            val (fieldName, fieldType) = allArgs.head
            val mockLine               = typeIdToProto(fieldName, fieldType)

            val entry = Ast.OneofEntry(
              identifier = Ast.Identifier(StringUtils.titleToPascal(c.id.value)),
              typePath = mockLine.typePath,
              number = idx + 1,
            )

            (entry, None)

          } else {

            val msg   = messageFromCommon(c)
            val entry = Ast.OneofEntry(
              identifier = Ast.Identifier(StringUtils.titleToPascal(c.id.value)),
              typePath = Ast.TypePath(init = Nil, last = Ast.TypeIdentifier(msg.name)),
              number = idx + 1,
            )
            (entry, Some(msg))
          }

        case (Right(value), idx) => // case object-like
          val msg   = Ast.Message(Ast.Identifier(value.id.value), Nil)
          val entry = Ast.OneofEntry(
            identifier = Ast.Identifier(StringUtils.titleToPascal(value.id.value)),
            typePath = Ast.TypePath(init = Nil, last = Ast.TypeIdentifier(msg.name)),
            number = idx + 1,
          )
          (entry, Some(msg))

      }.separate

      // todoue: Oneof
      val oneof =
        Ast.OneofField(identifier = Ast.Identifier("is"), entries = oneOfEntires)

      Ast.Message(name = Ast.Identifier(e.id.value), entries = oneof :: nestedMsgs.flatten)
    }
  }

  private def messageFromCommon(m: CommonAst.ClassAst): Ast.Message = {
    val fieldLines = m.argLists.toList.flatMap(_.args).map { case (id, typeId) =>
      typeIdToProto(id, typeId)
    }

    Ast.Message(name = Ast.Identifier(m.id.value), entries = fieldLines)
  }

  def typeIdToProto(id: CommonAst.Identifier, typeId: CommonAst.TypeIdentifier): Ast.FieldLine = {

    def primitive(tpe: String) =
      Ast.FieldLine(
        Ast.ArgRepeat.Required,
        Ast.TypePath(Nil, Ast.TypeIdentifier(Ast.Identifier(tpe))),
        identifier = Ast.Identifier(id.value),
        number = 0,
      )

    typeId match {
      case CommonAst.IntType                                         => primitive("int32")
      case CommonAst.LongType                                        => primitive("int64")
      case CommonAst.FloatType | CommonAst.DoubleType                => primitive("double")
      case CommonAst.StringType                                      => primitive("string")
      case CommonAst.BooleanType                                     => primitive("bool")
      case CommonAst.ShortType | CommonAst.ByteType                  => ???
      // case CommonAst.ArrayType(CommonAst.ByteType)    => primitive("bytes") // TODO:bcm
      case CommonAst.CustomSimpleTypeIdentifier(packagePath, typeId) =>
        (options.assumeIdType, typeId.value.endsWith("Id")) match {
          case (Some(idType), true) => primitive(idType.id.value)
          case _                    =>
            Ast.FieldLine(
              Ast.ArgRepeat.Required,
              Ast.TypePath(packagePath.map(tId => Ast.Identifier(tId.value)), Ast.TypeIdentifier(Ast.Identifier(typeId.value))),
              identifier = Ast.Identifier(id.value),
              number = 0,
            )
        }

      case CommonAst.OptionType(inner)                => typeIdToProto(id, inner).copy(repeat = Ast.ArgRepeat.Optional)
      case CommonAst.ArrayType(inner)                 => typeIdToProto(id, inner).copy(repeat = Ast.ArgRepeat.Repeated)
      case other: CommonAst.CustomHigherTypeIdentifer => throw new IllegalArgumentException(s"Proto doesnt support custom higher types $other")
    }
  }

}

object Proto2FromCommon {
  case class Options(assumeIdType: Option[Ast.TypeIdentifier])
  object Options {
    val empty = Options(None)
  }
}
