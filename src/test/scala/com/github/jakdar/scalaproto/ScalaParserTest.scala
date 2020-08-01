package com.github.jakdar.scalaproto

import fastparse._
import fastparse.Parsed
import com.github.jakdar.scalaproto.scala.ScalaParser
import com.github.jakdar.scalaproto.proto2.FromScalaProto2Generator
import org.scalatest.flatspec.AnyFlatSpec

class ScalaParserTest extends AnyFlatSpec {

  "scala to proto" should "work in basic case " in {

    val example                   = """
            case class Ala(
                id:Option[String],
            ola:List[Int],
            time :Option[ZonedDateTime]
            )


            case class Ola(
                id: String,
                mamacita: Boolean,
                ola: Option[Int]
            ) extends Ala[Makota,and.it.is.fasinating.That[It.Works],
            Psa]

""".trim()
    val Parsed.Success(parsed, _) = parse(example, ScalaParser.program(_))
    parsed.map(FromScalaProto2Generator.generateAstEntity).foreach(println) // TODO:bcm

  }

  val example                   = """
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
  val Parsed.Success(parsed, _) = parse(example, ScalaParser.program(_))
  parsed.map(FromScalaProto2Generator.generateAstEntity).foreach(println) // TODO:bcm
  it should "work for enums and classes" in {}

}
