package com.github.jakdar.scalaproto.scala

import cats.data._
import com.github.jakdar.scalaproto.parser._
import Ast._, Common._
import fastparse._

import ScalaWhitespace._
import language.postfixOps

object ScalaParser {

  def identifier[_: P]: P[Ast.Identifier] =
    P(CharsWhileIn("0-9a-zA-Z_") !).map(Identifier(_)) // TODO:bcm not start with 0-9

  def argpair[_: P] = P(identifier ~ ":" ~ identifier ~ TrailingComma).map { case (id, id2) => (id, TypePath(Nil, SimpleTypeIdentifier(id2))) }
  def comma_argpair[_: P] = P(identifier ~ ":" ~ identifier ~ "," ~ WS.? ~ Newline.?).map {
    case (id, id2) => (id, TypePath(Nil, SimpleTypeIdentifier(id2)))
  }

  def arglist[_: P] = P(OneNLMax ~ "(" ~ "implicit".? ~ comma_argpair.rep() ~ argpair ~ ")").map {
    case (argpairList, l) => ArgList(argpairList.toList :+ l)
  }

  def clazz[_: P] = P("case".? ~ "class" ~ identifier ~ arglist.rep()).map {

    case (className, argList) =>
      Clazz(
        className,
        NonEmptyList.fromListUnsafe( // TODO:bcm - make it safe
          argList.toList
        )
      )

  }

  def program[_: P] = P(clazz ~ End)

}
