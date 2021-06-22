package com.github.jakdar.scalaproto

import org.scalatest.flatspec.AnyFlatSpec
import com.softwaremill.diffx.scalatest.DiffMatcher._
import org.scalatest.matchers.should.Matchers

class JsonToScalaTest extends AnyFlatSpec with Matchers {

  "json to scala" should "work in basic case " in {

    val example  = """
            |{
            |  "ala": 4,
            |  "ola": "ela",
            |  "ula": {
            |    "id": 5
            |  },
            |  "kola" : [ true, false ]
            |}""".stripMargin.trim
    val result   = Application.jsonToScala(example)
    val expected =
      """case class Root (
        |    ala: Int,
        |    ola: String,
        |    ula: Ula,
        |    kola: List[Boolean])
        |
        |case class Ula (
        |    id: Int)""".stripMargin.trim

    result.trim() should matchTo(expected.trim())

  }

}
