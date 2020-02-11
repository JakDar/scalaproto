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

  case class Clazz(name: Identifier, argLists: NonEmptyList[ArgList])

}
