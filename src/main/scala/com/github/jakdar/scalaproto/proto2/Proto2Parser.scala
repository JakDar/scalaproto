package com.github.jakdar.scalaproto.proto2

import cats.data.NonEmptyList
import cats.parse.{Parser => P, _}
import cats.syntax.apply._
import com.github.jakdar.scalaproto.parser

import Ast._

object Proto2Parser extends parser.Parser[AstEntity] {
  // TODO  cleanup - especially whitespace
  override def parse(code: String): Either[parser.Parser.ParseError, Seq[AstEntity]] = {

    program.parse(code) match {
      case Left(ex)          => Left(parser.Parser.ParseError.GenericErr(s"Parsing proto failed with $ex").widen)
      case Right((_, value)) => Right(value.toList)
    }

  }

  val whitespace: P[Unit] = P.charIn(" \t\r\n").void
  val whitespaces0        = whitespace.rep0.void
  val whitespaces         = whitespace.rep.void
  val spaces: P[Unit]              = P.charsWhile(_.isSpaceChar).void // TODO:bcm  nonempty
  val newline: P[Unit]             = P.char('\n')

  def identifier: P[Ast.Identifier] =
    P.charsWhile(c => c.isLetterOrDigit || c == '_').map(Identifier(_)) // TODO:bcm not start with 0-9

  def repeated: P[ArgRepeat.Repeated.type] = P.string("repeated").as(ArgRepeat.Repeated)
  def required: P[ArgRepeat.Required.type] = P.string("required").as(ArgRepeat.Required)
  def optional: P[ArgRepeat.Optional.type] = P.string("optional").as(ArgRepeat.Optional)

  def argrepeat: P[Ast.ArgRepeat] = P.defer(optional | repeated | required)

  def typePath: P[Ast.TypePath] = (P.char('.').?.with1 ~ ((identifier ~ P.char('.')).backtrack.rep0.with1 ~ identifier)).map {
    case (_, (path, tpe)) =>
      Ast.TypePath(path.map(_._1).toList, Ast.TypeIdentifier(tpe))
  }

  def fieldline: P[FieldLine] =
    (argrepeat, spaces, typePath, spaces, identifier, P.char('=').surroundedBy(spaces.?), Numbers.digits, P.char(';').surroundedBy(spaces.?)).tupled
      .map { // TODO:bcm  check strailing whitespace + optional whitespce before chat
        case (argrepeat, _, typePath, _, id, _, num, _) => FieldLine(argrepeat, typePath, id, num.toInt)
      }

  def oneofEntry: P[OneofEntry] =
    (typePath, spaces, identifier, P.char('=').surroundedBy(spaces.?), Numbers.digits, P.char(';').surroundedBy(spaces.?)).tupled.map {
      case (typePath, _, id, _, num, _) => OneofEntry(typePath, id, num.toInt)
    }

  def oneofField: P[OneofField] = (
    P.string("oneof").void,
    identifier.surroundedBy(whitespaces0),
    P.char('{'),
    oneofEntry.surroundedBy(whitespaces0).rep,
    P.char('}'),
  ).tupled.map { case (_, id, _, entries, _) =>
    Ast.OneofField(id, entries.toList)
  }

  def messageEntry: P[MessageEntry] = P.defer((fieldline | message | enum | oneofField))

  def message: P[Ast.Message] =
    ((P.string("message"), identifier.surroundedBy(whitespaces0), P.char('{')).tupled ~ (messageEntry.surroundedBy(whitespaces0).rep0.with1 ~ P.char(
      '}'
    ))).map { case ((_, id, _), (entries, _)) =>
      Ast.Message(id, entries.toList)
    }

  def enumline: P[EnumLine] =
    (identifier, P.char('=').surroundedBy(whitespaces0), Numbers.digits, P.char(';').surroundedBy(whitespaces0)).tupled.map { case (id, _, num, _) =>
      EnumLine(id, num.toInt)
    }

  def enum: P[EnumAst] =
    (P.string("enum"), identifier.surroundedBy(whitespaces0), P.char('{'), enumline.surroundedBy(whitespaces0).rep, P.char('}')).tupled.map {
      case (_, id, _, lines, _) => EnumAst(id, lines.toList)
    }

  def program: P[NonEmptyList[AstEntity]] = P.defer((message | enum).surroundedBy(whitespaces0).rep)
}
