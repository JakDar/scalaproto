package com.github.jakdar.scalaproto.parser

trait Parser[AstEntity] {
  def parse(code: String): Either[Parser.ParseError, Seq[AstEntity]]
}

object Parser {
  enum ParseError {
    case GenericErr(error: String)
  }
}
