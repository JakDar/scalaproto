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

  it should "work for nested arrays" in {
    val example  = """
            |{
            |  "ala": 4,
            |  "ola": "ela",
            |  "kola" : [[1,2,3], [1,3,0]]
            |}""".stripMargin.trim

    val result   = Application.jsonToScala(example)
    val expected =
      """case class Root (
        |    ala: Int,
        |    ola: String,
        |    kola: List[List[Int]])""".stripMargin.trim

    result.trim() should matchTo(expected.trim())
  }

  it should "merge objects in array" in {
    val example  = """
             |{
             |  "ala": 4,
             |  "ola": "ela",
             |  "kola": [
             |    {
             |      "time": 5,
             |      "v1": 5
             |    },
             |    {
             |      "time": 1,
             |      "v2": "5"
             |    },
             |    {
             |      "time": 1,
             |      "v3": "5",
             |      "v4": false
             |    }
             |  ]
             |}""".stripMargin.trim

    val result   = Application.jsonToScala(example)
    val expected =
      """case class Root (
        |    ala: Int,
        |    ola: String,
        |    kola: List[RootChild1])
        |case class RootChild1 (
        |    time: Int,
        |    v1: Option[Int],
        |    v2: Option[Int],
        |    v3: Option[String],
        |    v4: Option[Boolean])""".stripMargin.trim

    result.trim() should matchTo(expected.trim())
  }

}
