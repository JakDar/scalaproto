package com.github.jakdar.scalaproto.scala

import cats.data._
import com.github.jakdar.scalaproto.parser._
import Ast._, Common._
import fastparse._

import ScalaWhitespace._
import language.postfixOps

object ScalaParser {

  def identifier[_: P]: P[Ast.Identifier] =
    P(CharsWhileIn("0-9a-zA-Z_") !).map(Identifier(_)) // TODO:bcm disallow start with 0-9

  def higherTypeIdentifier[_: P]: P[HigherTypeIdentifer] =
    P(identifier ~ "[" ~ (typePath ~ ",").rep() ~ typePath ~ "]").map { // TODO - not only unitype
      case (external, internal, internalLast) =>
        HigherTypeIdentifer(id = external, internal = NonEmptyList.fromListUnsafe(internal.toList :+ internalLast))
    }
  def simpleTypeIdentifier[_: P]: P[SimpleTypeIdentifier] = P(identifier).map(SimpleTypeIdentifier)

  def typePath[_: P] = P((identifier ~ ".").rep() ~ (higherTypeIdentifier | simpleTypeIdentifier)).map {
    case (path, typee) => TypePath(path.toList, typee)
  }

  def argpair[_: P]       = P(identifier ~ ":" ~ typePath)
  def comma_argpair[_: P] = P(identifier ~ ":" ~ typePath ~ "," ~ WS.? ~ Newline.?)

  def arglist[_: P] = P(OneNLMax ~ "(" ~ "implicit".? ~ comma_argpair.rep() ~ argpair ~ ")" ~ ("extends" ~ typePath).?).map {
    case (argpairList, l, _) => ArgList(argpairList.toList :+ l)
  }

  def clazz[_: P] = P("case".? ~ "class" ~ identifier ~ arglist.rep() ~ WS.? ~ Newline.?).map {

    case (className, argList) =>
      Clazz(
        className,
        NonEmptyList.fromListUnsafe( // TODO:bcm - make it safe using rep(1)
          argList.toList
        )
      )

  }

  def program[_: P] = P(clazz.rep() ~ End)

}
