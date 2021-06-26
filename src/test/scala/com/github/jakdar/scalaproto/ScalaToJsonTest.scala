package com.github.jakdar.scalaproto

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import com.github.jakdar.scalaproto.scala.ScalaParser
import com.github.jakdar.scalaproto.json.JsonFromCommon
import com.github.jakdar.scalaproto.scala.ScalaToCommon

class ScalaToJsonTest extends AnyFlatSpec with Matchers {

  def scalaToJson(code: String): Seq[ujson.Obj] = {
    val scalaAst  =
      ScalaParser.parse(code)
    val commonAst = scalaAst.flatMap(x => ScalaToCommon.toCommon(x).getOrElse(throw new IllegalStateException("Empty to Common")))
    JsonFromCommon.fromCommon(commonAst)
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
