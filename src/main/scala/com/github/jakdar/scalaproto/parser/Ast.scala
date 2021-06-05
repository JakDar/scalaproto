package com.github.jakdar.scalaproto.parser
import cats.data.NonEmptyList

object Ast {

  case class Identifier(value: String)

  sealed trait TypeIdentifier {
    def isSingleType = this match {
      case _: HigherTypeIdentifier => false
      case _                       => true
    }
  }

  case object IntType     extends TypeIdentifier
  case object LongType    extends TypeIdentifier
  case object FloatType   extends TypeIdentifier
  case object DoubleType  extends TypeIdentifier
  case object StringType  extends TypeIdentifier
  case object BooleanType extends TypeIdentifier
  case object ShortType   extends TypeIdentifier
  case object ByteType    extends TypeIdentifier

  sealed trait CustomTypeIdentifier                                                    extends TypeIdentifier
  case class CustomSimpleTypeIdentifier(packagePath: List[Identifier], id: Identifier) extends CustomTypeIdentifier

  sealed trait HigherTypeIdentifier extends TypeIdentifier
  case class CustomHigherTypeIdentifer(
      outerPackagePath: List[Identifier],
      outer: Identifier,
      inner: NonEmptyList[TypeIdentifier]
  ) //NOTE: inner is NEL to support Either[L,R] etc
      extends HigherTypeIdentifier
      with CustomTypeIdentifier
  case class OptionType(inner: TypeIdentifier) extends HigherTypeIdentifier
  case class ArrayType(inner: TypeIdentifier)  extends HigherTypeIdentifier

  case class Fields(args: List[(Identifier, TypeIdentifier)]) {
    def isEmpty  = args.isEmpty
    def nonEmpty = !isEmpty
  }

  sealed trait AstEntity {
    def parents: List[CustomTypeIdentifier]

    def maybeEnum = this match {
      case o: ObjectAst => Some(o)
      case _            => None
    }

    def isEnum = this match {
      case obj: ObjectAst => obj.definitions.isEmpty
      case _              => false
    }

  }

  case class ClassAst(id: Identifier, argLists: NonEmptyList[Fields], parents: List[CustomTypeIdentifier]) extends AstEntity
  case class ObjectAst(id: Identifier, definitions: List[AstEntity], parents: List[CustomTypeIdentifier])
      extends AstEntity // TODO:bcm should empty enum represent enumValue? doubt it

}
