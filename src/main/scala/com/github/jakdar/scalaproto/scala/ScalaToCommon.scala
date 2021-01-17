package com.github.jakdar.scalaproto.scala

import com.github.jakdar.scalaproto.parser.{Ast => CommonAst}
import com.github.jakdar.scalaproto.parser.ToCommon
import com.github.jakdar.scalaproto.scala.Ast.SimpleTypeIdentifier
import com.github.jakdar.scalaproto.scala.Ast.HigherTypeIdentifer
import com.github.jakdar.scalaproto.scala.Ast.ObjectAst

object ScalaToCommon extends ToCommon[Ast.AstEntity] {

  override def toCommon(other: Ast.AstEntity): Either[ToCommon.Error, List[CommonAst.AstEntity]] = other match {
    case c: Ast.Clazz     => Right(classToCommon(c) :: Nil)
    case _: Ast.Trait     => Right(Nil)
    case o: Ast.ObjectAst => objectToCommon(o)
  }

  private def classToCommon(c: Ast.Clazz): CommonAst.ClassAst = {
    val args = c.argLists.map(_.args.map { case (id, typePath) => (CommonAst.Identifier(id.value), typeToCommon(typePath)) }).map(CommonAst.Fields)
    CommonAst.ClassAst(id = CommonAst.Identifier(c.id.value), argLists = args, parents = Nil)
  }

  private def objectToCommon(o: Ast.ObjectAst): Either[ToCommon.Error, List[CommonAst.EnumAst]] = {
    val isEnum = o.definitions.forall(_.isCaseObject)

    if (isEnum) {
      val defs = o.definitions.map {
        case ObjectAst(id, _, _) => CommonAst.EnumAst(CommonAst.Identifier(id.value), definitions = Nil, parents = Nil)
        case other               => throw new IllegalStateException(s"Unhandled state $other")
      }

      Right(List(CommonAst.EnumAst(id = CommonAst.Identifier(o.id.value), definitions = defs, parents = Nil)))
    } else {
      Left(ToCommon.Error.NotSupportedEnumtoCommon)
    }
  }

  private def typeToCommon(t: Ast.TypePath): CommonAst.TypeIdentifier = {

    val packagePath = t.packagePath.map(x => CommonAst.Identifier(x.value))

    t.typeId match {
      case SimpleTypeIdentifier(Ast.Identifier("Int"))                    => CommonAst.IntType
      case SimpleTypeIdentifier(Ast.Identifier("Long" | "ZonedDateTime")) => CommonAst.LongType
      case SimpleTypeIdentifier(Ast.Identifier("Float"))                  => CommonAst.FloatType
      case SimpleTypeIdentifier(Ast.Identifier("Double"))                 => CommonAst.DoubleType
      case SimpleTypeIdentifier(Ast.Identifier("String"))                 => CommonAst.StringType
      case SimpleTypeIdentifier(Ast.Identifier("Boolean"))                => CommonAst.BooleanType
      case SimpleTypeIdentifier(Ast.Identifier("Short"))                  => CommonAst.ShortType
      case SimpleTypeIdentifier(Ast.Identifier("Byte"))                   => CommonAst.ByteType
      case SimpleTypeIdentifier(id)                                       => CommonAst.CustomSimpleTypeIdentifier(packagePath, CommonAst.Identifier(id.value))

      case HigherTypeIdentifer(Ast.Identifier("Option" | "Optional"), internal) if internal.size == 1 =>
        CommonAst.OptionType(typeToCommon(internal.head))

      case HigherTypeIdentifer(Ast.Identifier("List" | "Seq" | "Set" | "Array" | "NonEmptyList"), internal) if internal.size == 1 =>
        CommonAst.ArrayType(typeToCommon(internal.head))

      // TODO:bcm  special cases for bytearray types + date types

      case HigherTypeIdentifer(id, internal) =>
        CommonAst.CustomHigherTypeIdentifer(packagePath, outer = CommonAst.Identifier(id.value), inner = internal.map(typeToCommon))
    }

  }

}
