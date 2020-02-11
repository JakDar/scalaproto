package com.github.jakdar.scalaproto.proto2

import com.github.jakdar.scalaproto.parser._
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

  def argline[_: P] = P(argrepeat ~ typePath ~ identifier ~ "=" ~ Common.Num ~ ";" ~ WS.? ~ Newline.rep()).map(FieldLine.tupled)

  def message[_: P] = P("message" ~ identifier ~ "{" ~ argline.rep() ~ "}" ~ WS.? ~ Newline.?).map { case (id, fields) => Message(id, fields.toList) }

  def enum[_: P] = ??? // TODO:bcm

  def program[_: P] = P(message.rep() ~ End)
}
