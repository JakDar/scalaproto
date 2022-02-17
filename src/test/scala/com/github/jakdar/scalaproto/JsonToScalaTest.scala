package com.github.jakdar.scalaproto

class JsonToScalaTest extends munit.FunSuite {

  def jsonToScala(code: String): String = Application.convert(code, Application.jsonSupport, Application.scalaSupport).getOrElse(???)

  test("json to scala work in basic case") {
    val example  = """
            |{
            |  "ala": 4,
            |  "ola": "ela",
            |  "ula": {
            |    "id": 5
            |  },
            |  "kola" : [ true, false ]
            |}""".stripMargin.trim
    val result   = jsonToScala(example)
    val expected =
      """case class Root(ala: Int, ola: String, ula: RootUla, kola: List[Boolean])
        |
        |case class RootUla(id: Int)""".stripMargin.trim

    assertEquals(result.trim(), expected.trim())
  }

  test("json to scala work in basic case") {
    val example = """
            |{
            |  "ala": 4,
            |  "ola": "ela",
            |  "kola" : [[1,2,3], [1,3,0]]
            |}""".stripMargin.trim

    val result   = jsonToScala(example)
    val expected =
      "case class Root(ala: Int, ola: String, kola: List[List[Int]])"

    assertEquals(result.trim(), expected.trim())
  }

  test("work for nested arrays") {
    val example = """
            |{
            |  "ala": 4,
            |  "ola": "ela",
            |  "kola" : [[1,2,3], [1,3,0]]
            |}""".stripMargin.trim

    val result   = jsonToScala(example)
    val expected =
      "case class Root(ala: Int, ola: String, kola: List[List[Int]])"

    assertEquals(result.trim(), expected.trim())
  }

  test("merge objects in array") {
    val example = """
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
             |      "v2": 5
             |    },
             |    {
             |      "time": 1,
             |      "v3": "5",
             |      "v4": false
             |    }
             |  ]
             |}""".stripMargin.trim

    val result   = jsonToScala(example)
    val expected =
      """case class Root(ala: Int, ola: String, kola: List[RootkolaArr0])
        |
        |case class RootkolaArr0(v1: Option[Int], v3: Option[String], v4: Option[Boolean], v2: Option[Int], time: Int)""".stripMargin.trim

    assertEquals(result.trim(), expected.trim())
  }

  test("merge nested objects in an array") {
    val example = """
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
             |      "v2":  {
             |        "ala" : 1,
             |        "ola": {"xd":3}
             |      }
             |    },
             |    {
             |      "time": 1,
             |      "v3": "5",
             |      "v4": false
             |    }
             |  ]
             |}""".stripMargin.trim

    val result   = jsonToScala(example)
    val expected =
      """case class Root(ala: Int, ola: String, kola: List[RootkolaArr0])
        |
        |case class RootkolaArr0(v1: Option[Int], v3: Option[String], v4: Option[Boolean], v2: Option[RootkolaArr1V2], time: Int)
        |
        |case class RootkolaArr1V2(ala: Int, ola: RootkolaArr1V2Ola)
        |
        |case class RootkolaArr1V2Ola(xd: Int)""".stripMargin.trim

    assertEquals(result.trim(), expected.trim())
  }

  test("work with name collisions 1") {
    val example = """
          {
            "ala": [
              {
                "ala": [
                  {
                    "ala": 1
                  }
                ]
              }
            ]
          }""".stripMargin.trim

    val result   = jsonToScala(example)
    val expected =
      """case class Root(ala: List[RootalaArr0])
        |
        |case class RootalaArr0(ala: List[Rootalaarr0alaArr0])
        |
        |case class Rootalaarr0alaArr0(ala: Int)""".stripMargin.trim

    assertEquals(result.trim(), expected.trim())
  }

  test("work with name collisions 2") {
    val example = """
          {
            "ala": [
              {
                "ala":
                  {
                    "ala": 1
                  }

              }
            ]
          }""".stripMargin.trim

    val result   = jsonToScala(example)
    val expected =
      """case class Root(ala: List[RootalaArr0])
        |
        |case class RootalaArr0(ala: RootalaArr0Ala)
        |
        |case class RootalaArr0Ala(ala: Int)""".stripMargin.trim

    assertEquals(result.trim(), expected.trim())
  }
}
