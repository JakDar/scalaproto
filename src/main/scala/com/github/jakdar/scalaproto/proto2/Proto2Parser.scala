package com.github.jakdar.scalaproto.proto2

import com.github.jakdar.scalaproto.parser.Common
import Ast._, Common._
import fastparse._

import ScalaWhitespace._
import language.postfixOps

object Proto2Parser {

  def identifier[_: P]: P[Ast.Identifier] =
    P(CharsWhileIn("0-9a-zA-Z_") !).map(Identifier(_)) // TODO:bcm not start with 0-9

  def repeated[_: P] = P("repeated").map(_ => ArgRepeat.Repeated)
  def required[_: P] = P("required").map(_ => ArgRepeat.Required)
  def optional[_: P] = P("optional").map(_ => ArgRepeat.Optional)

  def argrepeat[_: P]: P[Ast.ArgRepeat] = P(optional | repeated | required)

  def typePath[_: P] = P((identifier ~ ".").rep() ~ identifier).map {
    case (path, typee) => TypePath(path.toList, TypeIdentifier(typee))
  }

  def fieldline[_: P] = P(argrepeat ~ typePath ~ identifier ~ "=" ~ Common.Num ~ ";" ~ WS.? ~ Newline.rep()).map(FieldLine.tupled)

  def messageEntry[_: P]: P[MessageEntry] = P((fieldline | message | enum))

  def message[_: P] = P("message" ~ identifier ~ "{" ~ messageEntry.rep() ~ "}" ~ WS.? ~ Newline.?).map {
    case (id, fields) => Message(id, fields.toList)
  }

  def enumline[_: P] = P(identifier ~ "=" ~ Common.Num ~ ";").map(EnumLine.tupled)

  def enum[_: P] = P("enum" ~ identifier ~ "{" ~ enumline.rep() ~ "}").map { case (id, lines) => EnumAst(id, lines.toList) }

  def program[_: P]: P[Seq[AstEntity]] = P((message | enum).rep() ~ End)
}
