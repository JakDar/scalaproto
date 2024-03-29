package com.github.jakdar.scalaproto

class Proto2ToScalaTest extends munit.FunSuite {

  def protoToScala(code: String): String = Application.convert(code, Application.proto2Support, Application.scalaSupport).getOrElse(???)

  test("work in basic case") {

    val example  = """
            message Ala{
                required string ala =1 ;
                repeated int32 ola = 2;
                optional int64 ula = 3;
            }



            message Ola {
                optional string ala =1 ;
                repeated int32 ola = 2;
                oneof ela {
                   string koala = 3;
                   int32 panda = 4;
                }
                optional bool ula = 5;
            }


           enum AlaMakota {
            ALA_MAKOTA = 1;
            OLA_MAPSA = 2;
           }
""".trim()
    val result   = protoToScala(example)
    val expected = """|
                      |case class Ala(ala: String, ola: List[Int], ula: Option[Long])
                      |
                      |case class Ola(ala: Option[String], ola: List[Int], ela: OlaEla, ula: Option[Boolean])
                      |
                      |sealed trait OlaEla
                      |
                      |object OlaEla {
                      |  case class OlaElaKoala(koala: String) extends OlaEla
                      |  case class OlaElaPanda(panda: Int) extends OlaEla
                      |}
                      |
                      |sealed trait AlaMakota
                      |
                      |object AlaMakota {
                      |  case object AlaMakota extends AlaMakota
                      |  case object OlaMapsa extends AlaMakota
                      |}""".stripMargin

    assertEquals(result.trim(), expected.trim())
  }

}
