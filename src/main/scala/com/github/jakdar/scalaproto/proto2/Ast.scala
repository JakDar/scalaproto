package com.github.jakdar.scalaproto.proto2

object Ast {

  case class Identifier(value: String)

  sealed trait ArgRepeat

  object ArgRepeat {
    case object Required extends ArgRepeat
    case object Optional extends ArgRepeat
    case object Repeated extends ArgRepeat
  }

  case class TypeIdentifier(id: Identifier)

  case class TypePath(init: List[Identifier], last: TypeIdentifier) {
    def initString = init.map(_.value).fold("")(_ + "." + _)
  }

  case class FieldLine(repeat: ArgRepeat, typePath: TypePath, identifier: Identifier, number: Int)

  case class Message(name: Identifier, fields: List[FieldLine])

}
