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
    def generate   = init.map(_.value).foldRight(last.id.value) { case (a, b) => a + "." + b }
  }

  case class EnumLine(name: Identifier, number: Int)

  sealed trait MessageEntry {
    def widen: MessageEntry = this
  }

  sealed trait AstEntity extends MessageEntry

  case class EnumAst(name: Identifier, values: List[EnumLine]) extends AstEntity

  case class OneofField(identifier: Identifier, entries: List[OneofEntry]) extends MessageEntry // TODO:bcm  support it furtsher
  case class OneofEntry(typePath: TypePath, identifier: Identifier, number: Int)

  case class FieldLine(repeat: ArgRepeat, typePath: TypePath, identifier: Identifier, number: Int) extends MessageEntry
  case class Message(name: Identifier, entries: List[MessageEntry])                                extends AstEntity {
    def fields        = entries.collect { case f: FieldLine => f }
    def innerEntities = entries.collect { case a: AstEntity => a }
  }

  val stringTypeIdentifier = TypeIdentifier(Identifier("string"))

}
