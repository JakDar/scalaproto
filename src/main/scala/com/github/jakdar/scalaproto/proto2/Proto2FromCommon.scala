package com.github.jakdar.scalaproto.proto2

import com.github.jakdar.scalaproto.parser.{Ast => CommonAst}
import com.google.common.base.CaseFormat
import com.github.jakdar.scalaproto.parser.FromCommon
import com.github.jakdar.scalaproto.parser.Ast.ClassAst
import com.github.jakdar.scalaproto.parser.Ast.EnumAst
import com.github.jakdar.scalaproto.parser.Ast.CustomHigherTypeIdentifer
import com.github.jakdar.scalaproto.parser.Ast.OptionType
import com.github.jakdar.scalaproto.parser.Ast.ArrayType

object Proto2FromCommon extends FromCommon[Ast.AstEntity] {

  override def fromCommon(other: CommonAst.AstEntity): List[Ast.AstEntity] = {

    other match {
      case c: ClassAst => ??? // TODO:bcm
      case e: EnumAst  => List(enumFromCommon(e))
    }

  }

  private def enumFromCommon(e: CommonAst.EnumAst): Ast.EnumAst = {

    def fixCasing(s: String) =
      if (s.filter(_.isLetter).forall(_.isUpper)) {
        CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, s) // TODO:bcm use it or loose it
      } else {
        s
      }


    val defs = e.definitions.zipWithIndex.map {
      case (c: CommonAst.EnumAst, idx) => Ast.EnumLine(name = Ast.Identifier(c.id.value), idx)
      case _                           => ???
    }

    Ast.EnumAst(name = Ast.Identifier(e.id.value), values = defs)
  }

  private def messageToCommon(m: CommonAst.ClassAst): Ast.Message = {
    val fields = m.argLists.toList.map(_.args).flatten.zipWithIndex.map { case ((id, typeId), idx) => typeIdentifierToProtoLine(typeId, id)(idx) }

    Ast.Message(name = Ast.Identifier(m.id.value), fields)
  }

  private def typeIdentifierToProtoLine(t: CommonAst.TypeIdentifier, name: CommonAst.Identifier)(number: Int) = {

    def higer(ty: CommonAst.HigherTypeIdentifier) = ty match {
      case c: CustomHigherTypeIdentifer => ???
      case OptionType(inner)            => (Ast.ArgRepeat.Optional, simpleTypeIdToProto(inner))
      case ArrayType(inner)             => (Ast.ArgRepeat.Repeated, simpleTypeIdToProto(inner))
    }

    val (repeat, typeId) =
      t match {
        case ty: CommonAst.HigherTypeIdentifier => higer(ty)
        case other                              => (Ast.ArgRepeat.Required, simpleTypeIdToProto(other))
      }

    Ast.FieldLine(repeat, typePath = typeId, identifier = Ast.Identifier(name.value), number = number)

  }

  def simpleTypeIdToProto(t: CommonAst.TypeIdentifier) = {

    def primitive(s: String) = Ast.TypePath(Nil, Ast.TypeIdentifier(Ast.Identifier(s)))

    t match {
      case CommonAst.IntType     => primitive("Int")
      case CommonAst.LongType    => primitive("Long")
      case CommonAst.FloatType   => primitive("Float")
      case CommonAst.DoubleType  => primitive("Double")
      case CommonAst.StringType  => primitive("String")
      case CommonAst.BooleanType => primitive("Boolean")
      case CommonAst.ShortType   => primitive("Short")
      case CommonAst.ByteType    => primitive("Byte")
      case CommonAst.CustomSimpleTypeIdentifier(packagePath, id) =>
        Ast.TypePath(init = packagePath.map(id => Ast.Identifier(id.value)), last = Ast.TypeIdentifier(Ast.Identifier(id.value)))
      case _: CommonAst.HigherTypeIdentifier => ???
    }
  }

  // private def typeIdentifierToCommon(t: Ast.TypePath) = t.last.id.value match {
  //   case "int64"  => CommonAst.LongType
  //   case "int32"  => CommonAst.IntType
  //   case "double" => CommonAst.DoubleType
  //   case "bool"   => CommonAst.BooleanType
  //   case "string" => CommonAst.StringType
  //   case other    => CommonAst.CustomSimpleTypeIdentifier(packagePath = t.init.map(p => CommonAst.Identifier(p.value)), CommonAst.Identifier(other))
  // }
}
