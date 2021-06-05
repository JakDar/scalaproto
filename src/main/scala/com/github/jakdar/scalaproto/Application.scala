package com.github.jakdar.scalaproto
import fastparse._
import com.github.jakdar.scalaproto.scala.{ScalaGenerator}
import com.github.jakdar.scalaproto.proto2.Proto2Parser
import com.github.jakdar.scalaproto.proto2.Proto2ToCommon
import com.github.jakdar.scalaproto.scala.ScalaFromCommon
import com.github.jakdar.scalaproto.proto2.Proto2Homomorphisms
import com.github.jakdar.scalaproto.proto2.Proto2Generator
import com.github.jakdar.scalaproto.scala.ScalaToCommon
import com.github.jakdar.scalaproto.proto2.Proto2FromCommon
import com.github.jakdar.scalaproto.scala.ScalaParser

object Application {
  val proto2FromCommon = new Proto2FromCommon(
    Proto2FromCommon.Options(assumeIdType = Some(proto2.Ast.stringTypeIdentifier))
  )

  def scalaToProto(code: String): String = {
    val scalaAst  =
      ScalaParser.parse(code)
    val commonAst = scalaAst.flatMap(x => ScalaToCommon.toCommon(x).getOrElse(throw new IllegalStateException("Empty to Common")))
    val protoAst  = commonAst.flatMap(proto2FromCommon.fromCommon)
    protoAst.map(Proto2Generator.generateAstEntity).fold("")(_ + "\n\n" + _)
  }

  def protoFixNumbers(code: String): String = {
    val Parsed.Success(parsed, _) = parse(code, Proto2Parser.program(_))
    val withFixedNumbers          = parsed.map(Proto2Homomorphisms.correctNumbers)
    withFixedNumbers.map(Proto2Generator.generateAstEntity(_)).fold("")(_ + "\n" + _)
  }

  def protoToScala(code: String): String = {
    val Parsed.Success(protoAst, _) = parse(code, Proto2Parser.program(_))
    val commonAst                   = protoAst.flatMap(x => Proto2ToCommon.toCommon(x).getOrElse(throw new IllegalStateException("Empty to Common")))
    val scalaAst                    = commonAst.flatMap(ScalaFromCommon.fromCommon)
    scalaAst.map(ScalaGenerator.generateScala).fold("")(_ + "\n\n" + _)
  }

}
