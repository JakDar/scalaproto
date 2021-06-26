package com.github.jakdar.scalaproto

import com.github.jakdar.scalaproto.scala.ScalaParser
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ScalaToJsonTest extends AnyFlatSpec with Matchers {

  def scalaToJson(code: String): Seq[ujson.Obj] = {
    val scalaAst = ScalaParser.parse(code)
    Application.convertAst(scalaAst.getOrElse(???), Application.scalaSupport, Application.jsonSupport).getOrElse(???)
  }

  "scala to json" should "work in basic case " in {

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

    result.head shouldBe expected
  }

}
