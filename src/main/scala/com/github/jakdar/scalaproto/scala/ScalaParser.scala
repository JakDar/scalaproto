package com.github.jakdar.scalaproto.scala

import cats.data._
import com.github.jakdar.scalaproto.parser._
import ClassAst._, Common._
import fastparse._

import ScalaWhitespace._
import language.postfixOps

object ScalaParser {

  def identifier[_: P]: P[ClassAst.Identifier] =
    P(CharsWhileIn("0-9a-zA-Z_") !).map(Identifier(_)) // TODO:bcm disallow start with 0-9

  def higherTypeIdentifier[_: P]: P[HigherTypeIdentifer] =
    P(identifier ~ "[" ~ (typePath ~ ",").rep() ~ typePath ~ "]").map {
      case (external, internal, internalLast) =>
        HigherTypeIdentifer(id = external, internal = NonEmptyList.fromListUnsafe(internal.toList :+ internalLast))
    }
  def simpleTypeIdentifier[_: P]: P[SimpleTypeIdentifier] = P(identifier).map(SimpleTypeIdentifier)

  def typePath[_: P] = P((identifier ~ ".").rep() ~ (higherTypeIdentifier | simpleTypeIdentifier)).map {
    case (path, typee) => TypePath(path.toList, typee)
  }

  def argpair[_: P]       = P(identifier ~ ":" ~ typePath)
  def comma_argpair[_: P] = P(identifier ~ ":" ~ typePath ~ "," ~ WS.? ~ Newline.?)

  def arglist[_: P] = P(OneNLMax ~ "(" ~ "implicit".? ~ comma_argpair.rep() ~ argpair ~ ")").map {
    case (argpairList, l) => ArgList(argpairList.toList :+ l)
  }

  def extend[_: P]: P[NonEmptyList[TypePath]] = P(("extends" ~ typePath ~ ("with" ~ typePath).rep())).map {
    case (ex, tail) => NonEmptyList(ex, tail.toList)
  }

  def clazz[_: P] = P("case".? ~ "class" ~ identifier ~ arglist.rep() ~ extend.?).map {

    case (className, argList, extend) =>
      Clazz(
        className,
        NonEmptyList.fromListUnsafe( // TODO:bcm - make it safe using rep(1)
          argList.toList
        ),
        parents = extend.fold[List[TypePath]](Nil)(_.toList)
      )

  }

  def sealedTrait[_: P] = P("sealed".!.? ~ "trait" ~ identifier ~ extend.?).map {
    case (seal, id, extend) =>
      Trait(isSealed = seal.isDefined, id = id, parents = extend.fold[List[TypePath]](Nil)(_.toList))
  }

  def obj[_: P]: P[ObjectAst] = P("case".? ~ "object" ~ identifier ~ ("{" ~ dataExpr ~ "}").? ~ extend.?).map {
    case (id, data, extend) => // disallow multiple case thing without ;
      ObjectAst(id, data.fold(List.empty[AstEntity])(_.toList), extend.fold[List[TypePath]](Nil)(_.toList)) // TODO:bcm
  }

  def dataExpr[_: P]: P[Seq[AstEntity]] = P((sealedTrait | obj | clazz).rep())

  def program[_: P] = P(dataExpr ~ End)

}
