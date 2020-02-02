package com.github.jakdar.scalaproto.parser

import cats.data.NonEmptyList

object Ast {

  case class Identifier(value: String)

  sealed trait TypeIdentifier

  case class SimpleTypeIdentifier(id: Identifier) extends TypeIdentifier

  case class UniHigherTypeIdentifer(id: Identifier, internal: SimpleTypeIdentifier) extends TypeIdentifier

  case class TypePath(init: List[Identifier], last: TypeIdentifier) {
    def initString = init.fold("")(_ + "." + _)
  }

  case class ArgList(args: List[(Identifier, TypePath)]) {
    def isEmpty  = args.isEmpty
    def nonEmpty = !isEmpty
  }

  case class Clazz(name: Identifier, argLists: NonEmptyList[ArgList])

}
