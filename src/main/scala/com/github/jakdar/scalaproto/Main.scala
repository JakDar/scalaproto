package com.github.jakdar.scalaproto

import fastparse._
import com.github.jakdar.scalaproto.scala.{ScalaGenerator, ScalaParser}
import com.github.jakdar.scalaproto.proto2.Proto2Generator
import com.github.jakdar.scalaproto.proto2.Proto2Parser

object Main extends App {

  val mode = args(0)
  val code = args(1).trim()

  mode match {
    case "to-proto" =>
      val Parsed.Success(parsed, _) = parse(code, ScalaParser.program(_))
      print(parsed.map(Proto2Generator.generateAstEntity).fold("")(_ + "\n" + _))
    case "to-scala" =>
      val Parsed.Success(parsed, _) = parse(code, Proto2Parser.program(_))
      print(parsed.map(ScalaGenerator.generateScala).fold("")(_ + "\n" + _))
  }

}
