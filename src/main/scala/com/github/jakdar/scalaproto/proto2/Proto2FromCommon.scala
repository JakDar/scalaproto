package com.github.jakdar.scalaproto.proto2

import com.github.jakdar.scalaproto.parser.Ast.ClassAst
import com.github.jakdar.scalaproto.parser.Ast.EnumAst
import com.github.jakdar.scalaproto.parser.FromCommon
import com.github.jakdar.scalaproto.parser.{Ast => CommonAst}
import com.google.common.base.CaseFormat
import Proto2FromCommon.Options

class Proto2FromCommon(options: Options) extends FromCommon[Ast.AstEntity] {

  override def fromCommon(other: CommonAst.AstEntity): List[Ast.AstEntity] =
    (other match {
      case c: ClassAst => List(messageFromCommon(c))
      case e: EnumAst  => List(enumFromCommon(e))
    }).map(Proto2Homomorphisms.correctNumbers)

  private def enumFromCommon(e: CommonAst.EnumAst): Ast.EnumAst = {
    def fixCasing(s: String) =
      if (!s.filter(_.isLetter).forall(_.isUpper)) {
        CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, s)
      } else s

    val defs = e.definitions.map {
      case c: CommonAst.EnumAst => Ast.EnumLine(name = Ast.Identifier(fixCasing(c.id.value)), 0)
      case _                    => ???
    }

    Ast.EnumAst(name = Ast.Identifier(e.id.value), values = defs)
  }

  private def messageFromCommon(m: CommonAst.ClassAst): Ast.Message = {
    val fieldLines = m.argLists.toList.flatMap(_.args).map {
      case (id, typeId) => typeIdToProto(id, typeId)
    }

    Ast.Message(name = Ast.Identifier(m.id.value), entries = fieldLines)
  }

  def typeIdToProto(id: CommonAst.Identifier, typeId: CommonAst.TypeIdentifier): Ast.FieldLine = {

    def primitive(tpe: String) =
      Ast.FieldLine(
        Ast.ArgRepeat.Required,
        Ast.TypePath(Nil, Ast.TypeIdentifier(Ast.Identifier(tpe))),
        identifier = Ast.Identifier(id.value),
        number = 0
      )

    typeId match {
      case CommonAst.IntType                          => primitive("int32")
      case CommonAst.LongType                         => primitive("int64")
      case CommonAst.FloatType | CommonAst.DoubleType => primitive("double")
      case CommonAst.StringType                       => primitive("string")
      case CommonAst.BooleanType                      => primitive("bool")
      case CommonAst.ShortType | CommonAst.ByteType   => ???
      // case CommonAst.ArrayType(CommonAst.ByteType)    => primitive("bytes") // TODO:bcm
      case CommonAst.CustomSimpleTypeIdentifier(packagePath, typeId) =>
        (options.assumeIdType, typeId.value.endsWith("Id")) match {
          case (Some(idType), true) => primitive(idType.id.value)
          case _ =>
            Ast.FieldLine(
              Ast.ArgRepeat.Required,
              Ast.TypePath(packagePath.map(tId => Ast.Identifier(tId.value)), Ast.TypeIdentifier(Ast.Identifier(typeId.value))),
              identifier = Ast.Identifier(id.value),
              number = 0
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
