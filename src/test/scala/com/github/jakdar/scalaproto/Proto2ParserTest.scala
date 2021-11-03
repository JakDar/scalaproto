package com.github.jakdar.scalaproto

import com.github.jakdar.scalaproto.proto2.Proto2Parser

import cats.parse.Parser.{Error => ParseError}
import com.github.jakdar.scalaproto.proto2.Ast

class Proto2ParserTest() extends munit.FunSuite {

  def id: String => Ast.Identifier = Ast.Identifier.apply _

  test("line parsing should work") {

    val res: Either[cats.parse.Parser.Error, Ast.FieldLine] = Proto2Parser.fieldline
      .parse(
        "required int32 alaId = 1;"
      )
      .map(_._2)

    val expected: Either[cats.parse.Parser.Error, Ast.FieldLine] = Right[cats.parse.Parser.Error, Ast.FieldLine](
      Ast.FieldLine(Ast.ArgRepeat.Required, Ast.TypePath(Nil, Ast.TypeIdentifier(Ast.Identifier("int32"))), Ast.Identifier("alaId"), 1)
    )

    assertEquals(res, expected)
  }

  test("message parsing should work") {

    val res: Either[ParseError, Ast.Message] = Proto2Parser.message
      .parse(
        """
      message Ala {
          required int32 alaId = 1;
          repeated Ola olas = 2;
      }
""".trim()
      )
      .map(_._2)

    val msg: Either[ParseError, Ast.Message] = Right(
      Ast.Message(
        name = Ast.Identifier("Ala"),
        entries = List(
          Ast.FieldLine(Ast.ArgRepeat.Required, Ast.TypePath(Nil, Ast.TypeIdentifier(id("int32"))), id("alaId"), 1),
          Ast.FieldLine(Ast.ArgRepeat.Repeated, Ast.TypePath(Nil, Ast.TypeIdentifier(id("Ola"))), id("olas"), 2),
        ),
      )
    )

    assertEquals(res, msg)
  }

}
