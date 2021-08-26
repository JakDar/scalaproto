package com.github.jakdar.scalaproto

import com.softwaremill.diffx.scalatest.DiffMatcher._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ScalaToProto2Test extends AnyFlatSpec with Matchers {

  def scalaToProto(code: String): String = Application.convert(code, Application.scalaSupport, Application.proto2Support)

  "scala to proto" should "work in basic case, event with trailing comma" in {

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

    result.trim() should matchTo(expected.trim())

  }

  it should "work with enums too" in {

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

    result.trim() should matchTo(expected.trim())

  }

  it should "work in simple cases" in {

    val example = "case class Entity(id: Long, name: String)"

    val result = scalaToProto(example)

    val expected = """|message Entity {
                      |    required int64 id = 1;
                      |    required string name = 2;
                      |}""".stripMargin.trim()

    result.trim() should matchTo(expected)

  }

  it should "assume id types if configured to do so" in {
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

    result.trim() should matchTo(expected.trim())
  }

  it should "convert sealed traits with classes to oneofs" ignore {
    fail()
  }

  it should "convert certain predefined types well" ignore {
    fail()
  }

}
