package com.github.jakdar.scalaproto

class AutoConvertTest extends munit.FunSuite {

  test("auto convert for json source") {
    val example  = """
            |{
            |  "ala": 4,
            |  "ola": "ela",
            |  "ula": {
            |    "id": 5
            |  },
            |  "kola" : [ true, false ]
            |}""".stripMargin.trim
    val result   = Application.autoConvert(example, Application.scalaSupport).getOrElse(???)
    val expected =
      """case class Root(ala: Int, ola: String, ula: RootUla, kola: List[Boolean])
        |
        |case class RootUla(id: Int)""".stripMargin.trim

    assertEquals(result.trim(), expected.trim())
  }

}
