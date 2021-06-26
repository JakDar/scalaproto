package com.github.jakdar.scalaproto.parser

trait Parser[AstEntity] {
  def parse(code: String): Either[Parser.ParseError, Seq[AstEntity]]
}

object Parser {
  sealed trait ParseError {
    def widen: ParseError = this
  }

  object ParseError {
    case class GenericErr(error: String) extends ParseError
  }
}
