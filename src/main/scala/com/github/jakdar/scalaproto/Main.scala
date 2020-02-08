package com.github.jakdar.scalaproto

import fastparse._
import com.github.jakdar.scalaproto.scala.ScalaParser
import com.github.jakdar.scalaproto.proto2.Proto2Generator
object Main extends App {

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
  )

""".trim()
  val Parsed.Success(parsed, _) = parse(example, ScalaParser.program(_))
  parsed.map(Proto2Generator.generateClass).foreach(println)
  // val typeEx = "Alamakota"
  // val Parsed.Success(parsed, _) = parse(typeEx, ScalaParser.typePath(_))
  // println(parsed)

}
