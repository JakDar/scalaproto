package com.github.jakdar.scalaproto
import fastparse._
import com.github.jakdar.scalaproto.scala.{ScalaGenerator, ScalaParser}
import com.github.jakdar.scalaproto.proto2.FromScalaProto2Generator
import com.github.jakdar.scalaproto.proto2.Proto2Parser
import com.github.jakdar.scalaproto.proto2.Proto2ToCommon
import com.github.jakdar.scalaproto.scala.ScalaFromCommon
import com.github.jakdar.scalaproto.proto2.Proto2Homomorphisms
import com.github.jakdar.scalaproto.proto2.Proto2Generator
object Application {

  def toProto(code: String): String = {

    val Parsed.Success(parsed, _) = parse(code, ScalaParser.program(_))
    parsed.map(FromScalaProto2Generator.generateAstEntity).fold("")(_ + "\n" + _)

  }

  def protoFixNumbers(code: String): String = {
    val Parsed.Success(parsed, _) = parse(code, Proto2Parser.program(_))
    val withFixedNumbers = parsed.map(Proto2Homomorphisms.correctNumbers)
    withFixedNumbers.map(Proto2Generator.generateAstEntity(_)).fold("")(_ + "\n" + _)
  }

  def toScala(code: String): String = {
    val Parsed.Success(protoAst, _) = parse(code, Proto2Parser.program(_))
    val commonAst                   = protoAst.flatMap(x => Proto2ToCommon.toCommon(x).getOrElse(throw new Exception("")))
    val scalaAst                    = commonAst.flatMap(ScalaFromCommon.fromCommon)
    scalaAst.map(ScalaGenerator.generateScala).fold("")(_ + "\n\n" + _)
  }

}
