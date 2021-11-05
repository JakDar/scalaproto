package com.github.jakdar.scalaproto.proto2

object Ast {

  case class Identifier(value: String)

  enum ArgRepeat {
    case Required, Optional, Repeated
  }

  case class TypeIdentifier(id: Identifier)

  case class TypePath(init: List[Identifier], last: TypeIdentifier) {
    def initString: String = init.map(_.value).fold("")(_ + "." + _)
    def generate: String   = init.map(_.value).foldRight(last.id.value) { case (a, b) => a + "." + b }
  }

  case class EnumLine(name: Identifier, number: Int)

  sealed trait MessageEntry {
    def widen: MessageEntry = this
  }

  sealed trait AstEntity extends MessageEntry

  case class EnumAst(name: Identifier, values: List[EnumLine]) extends AstEntity

  case class OneofField(identifier: Identifier, entries: List[OneofEntry]) extends MessageEntry
  case class OneofEntry(typePath: TypePath, identifier: Identifier, number: Int)

  case class FieldLine(repeat: ArgRepeat, typePath: TypePath, identifier: Identifier, number: Int) extends MessageEntry
  case class Message(name: Identifier, entries: List[MessageEntry])                                extends AstEntity {
    def innerEntities: List[AstEntity] = entries.collect { case a: AstEntity => a }
  }

  val stringTypeIdentifier: TypeIdentifier = TypeIdentifier(Identifier("string"))

}
