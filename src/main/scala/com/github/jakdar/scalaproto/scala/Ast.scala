package com.github.jakdar.scalaproto.scala

import cats.data.NonEmptyList

object Ast {

  case class Identifier(value: String)

  sealed trait TypeIdentifier {
    def isSingleType = this match {
      case _: SimpleTypeIdentifier => true
      case _                       => false
    }
  }

  case class SimpleTypeIdentifier(id: Identifier) extends TypeIdentifier

  case class HigherTypeIdentifer(id: Identifier, internal: NonEmptyList[TypePath]) extends TypeIdentifier

  case class TypePath(packagePath: List[Identifier], typeId: TypeIdentifier) {
    def initString = packagePath.map(_.value).fold("")(_ + "." + _)
  }

  case class ArgList(args: List[(Identifier, TypePath)]) {
    def isEmpty  = args.isEmpty
    def nonEmpty = !isEmpty
  }

  sealed trait AstEntity {
    def parents: List[TypePath]

    def maybeObject = this match {
      case o: ObjectAst => Some(o)
      case _            => None
    }

    def isCaseObject = this match {
      case obj: ObjectAst => obj.definitions.isEmpty
      case _              => false
    }

  }

  case class Clazz(id: Identifier, argLists: NonEmptyList[ArgList], parents: List[TypePath])  extends AstEntity
  case class Trait(isSealed: Boolean, id: Identifier, parents: List[TypePath])                extends AstEntity
  case class ObjectAst(id: Identifier, definitions: List[AstEntity], parents: List[TypePath]) extends AstEntity
}
