package com.github.jakdar.scalaproto

import com.github.jakdar.scalaproto.scala2.Scala2Parser

class ScalaToJsonTest extends munit.FunSuite {

  def scalaToJson(code: String): Seq[ujson.Obj] = {
    val scalaAst = Scala2Parser.parse(code)
    Application.convertAst(scalaAst.getOrElse(???), Application.scalaSupport, Application.jsonSupport).getOrElse(???)
  }

  test("scala to json work in basic case ") {
    val example =
      """case class Mama (
        |    ala: Int,
        |    ola: String,
        |    ula: Papa,
        |    kola: List[Boolean])
        |
        |case class Papa (
        |    id: Int)""".stripMargin.trim

    val result   = scalaToJson(example)
    val expected = ujson
      .read("""
            |{
            |  "ala": 1,
            |  "ola": "string",
            |  "ula": {
            |    "id": 1
            |  },
            |  "kola" : [  false ]
            |}""".stripMargin)
      .asInstanceOf[ujson.Obj]

    assertEquals(result.head, expected)
  }

}
