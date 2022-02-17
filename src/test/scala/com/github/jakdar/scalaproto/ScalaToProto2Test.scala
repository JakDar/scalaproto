package com.github.jakdar.scalaproto

class ScalaToProto2Test extends munit.FunSuite {
  def scalaToProto(code: String): String = Application.convert(code, Application.scalaSupport, Application.proto2Support).getOrElse(???)

  test("work in basic case, event with trailing comma") {

    val example = """
            case class Ala(
                id:Option[String],
            ola:List[Int],
            time :Option[ZonedDateTime]
            )


            case class Ola(
                id: String,
                mamacita: Boolean,
                ola: Option[Int],
            ) extends Ala[Makota,and.it.is.fasinating.That[It.Works],
            Psa]

""".trim()

    val result = scalaToProto(example)

    val expected = """|message Ala {
                      |    optional string id = 1;
                      |    repeated int32 ola = 2;
                      |    optional int64 time = 3;
                      |}
                      |
                      |
                      |
                      |message Ola {
                      |    required string id = 1;
                      |    required bool mamacita = 2;
                      |    optional int32 ola = 3;
                      |}""".stripMargin

    assertEquals(result.trim(), expected.trim())

  }

  test("work with enums too") {

    val example = """
           |sealed trait Kulka
           |
           |object Kulka {
           |
           |case object AlaMakota extends Kulka
           |
           |case object Ola extends Kulka
           |
           |case object Ula extends Kulka
           |
           |}
           |
           |
           |case class Ala(
           |    id:Option[String],
           |ola:List[Int],
           |time :Option[ZonedDateTime]
           |)
""".stripMargin.trim()
    val result  = scalaToProto(example)

    val expected = """|enum Kulka {
                      |    ALA_MAKOTA = 1;
                      |    OLA = 2;
                      |    ULA = 3;
                      |}
                      |
                      |
                      |
                      |message Ala {
                      |    optional string id = 1;
                      |    repeated int32 ola = 2;
                      |    optional int64 time = 3;
                      |}""".stripMargin

    assertEquals(result.trim(), expected.trim())
  }

  test("work in simple cases") {

    val example = "case class Entity(id: Long, name: String)"

    val result = scalaToProto(example)

    val expected = """|message Entity {
                      |    required int64 id = 1;
                      |    required string name = 2;
                      |}""".stripMargin.trim()

    assertEquals(result.trim, expected)

  }

  test("assume id types if configured to do so") {
    val example = """
           |sealed trait Kulka
           |
           |object Kulka {
           |case object AlaMakota extends Kulka
           |case object EloId extends Kulka
           |}
           |
           |
           |case class Ala(
           | field:Int,
           |    id:MyId,
           |ola:List[AnId],
           |time :Option[AnotherId]
           |)
""".stripMargin.trim()
    val result  = scalaToProto(example) // NOTE: by default assumes id is string

    val expected = """|enum Kulka {
                      |    ALA_MAKOTA = 1;
                      |    ELO_ID = 2;
                      |}
                      |
                      |
                      |
                      |message Ala {
                      |    required int32 field = 1;
                      |    required string id = 2;
                      |    repeated string ola = 3;
                      |    optional string time = 4;
                      |}""".stripMargin

    assertEquals(result.trim(), expected.trim())
  }

  test("convert sealed traits with classes to oneofs") {
    val example = """
           |sealed trait Kulka
           |
           |object Kulka {
           |case object AlaMakota extends Kulka
           |case class EloId(id:String) extends Kulka
           |case class Lol(id:Option[String]) extends Kulka
           |case class Bigger(id:String, ala:Int) extends Kulka
           |}
           |
           |
           |case class Ala(
           | field:Int,
           |    id:MyId,
           |ola:List[AnId],
           |time :Option[Kulka]
           |)
""".stripMargin.trim()
    val result  = scalaToProto(example)

    val expected = """|message Kulka {
                      |    oneof is {
                      |        AlaMakota alaMakota = 1;
                      |        string eloId = 2;
                      |        Lol lol = 3;
                      |        Bigger bigger = 4;
                      |    }
                      |
                      |
                      |message AlaMakota {
                      |}
                      |
                      |
                      |message Lol {
                      |    optional string id = 1;
                      |}
                      |
                      |
                      |message Bigger {
                      |    required string id = 1;
                      |    required int32 ala = 2;
                      |}
                      |
                      |}
                      |
                      |
                      |
                      |message Ala {
                      |    required int32 field = 1;
                      |    required string id = 2;
                      |    repeated string ola = 3;
                      |    optional Kulka time = 4;
                      |}""".stripMargin

    assertEquals(result.trim(), expected.trim())
  }

  test("convert certain predefined types well") {

    val example = """
        |case class Ala(
        |    id:Option[String],
        |    ola:Array[Byte],
        |    ula:Option[ByteString],
        |    time :Option[java.time.ZonedDateTime]
        |)""".stripMargin.trim()

    val result = scalaToProto(example)

    val expected = """|message Ala {
                      |    optional string id = 1;
                      |    required bytes ola = 2;
                      |    optional bytes ula = 3;
                      |    optional int64 time = 4;
                      |}""".stripMargin

    assertEquals(result.trim(), expected.trim())
  }

}
