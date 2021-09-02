package com.github.jakdar.scalaproto.scala

import cats.instances.either.catsStdInstancesForEither
import cats.instances.list.catsStdInstancesForList
import cats.syntax.alternative.catsSyntaxAlternativeSeparate
import cats.syntax.traverse.toTraverseOps
import com.github.jakdar.scalaproto.parser.ToCommon
import com.github.jakdar.scalaproto.parser.{Ast => CommonAst}
import com.github.jakdar.scalaproto.scala.Ast.HigherTypeIdentifer
import com.github.jakdar.scalaproto.scala.Ast.SimpleTypeIdentifier
import mouse.boolean.booleanSyntaxMouse

object ScalaToCommon extends ToCommon[Ast.AstEntity] {

  override def toCommon(other: Ast.AstEntity): Either[ToCommon.Error, List[CommonAst.AstEntity]] = other match {
    case c: Ast.Clazz     => Right(classToCommon(c) :: Nil)
    case _: Ast.Trait     => Right(Nil)
    case o: Ast.ObjectAst => objectToCommon(o)
  }

  private def classToCommon(c: Ast.Clazz): CommonAst.ClassAst = {
    val args = c.argLists.map(_.args.map { case (id, typePath) => (CommonAst.Identifier(id.value), typeToCommon(typePath)) }).map(CommonAst.Fields(_))
    CommonAst.ClassAst(id = CommonAst.Identifier(c.id.value), argLists = args, parents = Nil)
  }

  private def objectToCommon(o: Ast.ObjectAst): Either[ToCommon.Error, List[CommonAst.ObjectAst]] = {

    val (enums, nonEnums: List[Either[ToCommon.Error, List[CommonAst.AstEntity]]]) = o.definitions.map {
      case c: Ast.Clazz     => (c.parents.exists(_.typeId.singleType.exists(_.id == o.id))).either(Left(classToCommon(c)), Right(classToCommon(c) :: Nil))
      case c: Ast.ObjectAst =>
        (c.parents.exists(_.typeId.singleType.exists(_.id == o.id))).either(Right(objectToEnumValue(c)), objectToCommon(c))
      case _: Ast.Trait     => Right(Right(Nil))
    }.separate

    val defs = nonEnums.traverse(identity).getOrElse(???).flatten // TODO:bcm

    Right(List(CommonAst.ObjectAst(id = CommonAst.Identifier(o.id.value), enumEntries = enums, definitions = defs, parents = Nil)))
  }

  def objectToEnumValue(o: Ast.ObjectAst) = CommonAst.EnumValue(CommonAst.Identifier(o.id.value), parents = Nil)

  private def typeToCommon(t: Ast.TypePath): CommonAst.TypeIdentifier = {

    val packagePath = t.packagePath.map(x => CommonAst.Identifier(x.value))

    t.typeId match {
      case SimpleTypeIdentifier(Ast.Identifier("Int"))                                       => CommonAst.IntType
      case SimpleTypeIdentifier(Ast.Identifier("Long" | "ZonedDateTime" | "FiniteDuration")) => CommonAst.LongType
      case SimpleTypeIdentifier(Ast.Identifier("Float"))                                     => CommonAst.FloatType
      case SimpleTypeIdentifier(Ast.Identifier("Double"))                                    => CommonAst.DoubleType
      case SimpleTypeIdentifier(Ast.Identifier("String"))                                    => CommonAst.StringType
      case SimpleTypeIdentifier(Ast.Identifier("Boolean"))                                   => CommonAst.BooleanType
      case SimpleTypeIdentifier(Ast.Identifier("Short"))                                     => CommonAst.ShortType
      case SimpleTypeIdentifier(Ast.Identifier("Byte"))                                      => CommonAst.ByteType
      case SimpleTypeIdentifier(id)                                                          => CommonAst.CustomSimpleTypeIdentifier(packagePath, CommonAst.Identifier(id.value))

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
