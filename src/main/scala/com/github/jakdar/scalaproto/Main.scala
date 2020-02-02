package com.github.jakdar.scalaproto

import cats.effect.{ExitCode, IO, IOApp}
import com.typesafe.config.ConfigFactory
import fastparse._
import com.github.jakdar.scalaproto.scala.ScalaParser
import com.github.jakdar.scalaproto.proto2.Proto2Generator
object Main extends App {

  val example = "case class Ala(id:String, ola:Int, time :ZonedDateTime)"

  val Parsed.Success(parsed, _) = parse(example, ScalaParser.program(_))
  print(Proto2Generator.generateClass(parsed))

}
