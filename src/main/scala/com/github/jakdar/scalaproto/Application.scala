package com.github.jakdar.scalaproto
import cats.instances.either.catsStdInstancesForEither
import cats.syntax.traverse.toTraverseOps
import com.github.jakdar.scalaproto.json.JsonFromCommon
import com.github.jakdar.scalaproto.json.JsonGenerator
import com.github.jakdar.scalaproto.json.JsonParser
import com.github.jakdar.scalaproto.json.JsonToCommon
import com.github.jakdar.scalaproto.parser.FromCommon
import com.github.jakdar.scalaproto.parser.Generator
import com.github.jakdar.scalaproto.parser.Parser
import com.github.jakdar.scalaproto.parser.ToCommon
import com.github.jakdar.scalaproto.proto2.Proto2FromCommon
import com.github.jakdar.scalaproto.proto2.Proto2Generator
import com.github.jakdar.scalaproto.proto2.Proto2Homomorphisms
import com.github.jakdar.scalaproto.proto2.Proto2Parser
import com.github.jakdar.scalaproto.proto2.Proto2ToCommon
import com.github.jakdar.scalaproto.scala.ScalaFromCommon
import com.github.jakdar.scalaproto.scala.ScalaGenerator
import com.github.jakdar.scalaproto.scala.ScalaParser
import com.github.jakdar.scalaproto.scala.ScalaToCommon
import fastparse._

object Application {
  case class ConversionSupport[AstEntity](
      generator: Generator[AstEntity],
      parser: Parser[AstEntity],
      toCommon: ToCommon[AstEntity],
      fromCommon: FromCommon[AstEntity]
  )

  val scalaSupport  = ConversionSupport(ScalaGenerator, ScalaParser, ScalaToCommon, ScalaFromCommon)
  val proto2Support = ConversionSupport(
    Proto2Generator,
    Proto2Parser,
    Proto2ToCommon,
    new Proto2FromCommon(
      Proto2FromCommon.Options(assumeIdType = Some(proto2.Ast.stringTypeIdentifier))
    )
  )

  val jsonSupport = ConversionSupport(JsonGenerator, JsonParser, JsonToCommon, JsonFromCommon)

  def convert[S, D](code: String, source: ConversionSupport[S], dest: ConversionSupport[D]) = {
    val fromAst = source.parser.parse(code).getOrElse(???)
    val destAst = convertAst(fromAst, source, dest).getOrElse(???)
    val result  = dest.generator.generate(destAst)
    result
  }

  def convertAst[S, D](ast: Seq[S], source: ConversionSupport[S], dest: ConversionSupport[D]) = {
    ast.traverse(source.toCommon.toCommon(_)).map { commonAst =>
      dest.fromCommon.fromCommon(commonAst.flatten)
    }
  }

  def scalaToProto(code: String): String = convert(code, scalaSupport, proto2Support)
  def protoToScala(code: String): String = convert(code, proto2Support, scalaSupport)
  def jsonToScala(code: String): String  = convert(code, jsonSupport, scalaSupport)
  def scalaToJson(code: String): String  = convert(code, scalaSupport, jsonSupport)

  def protoFixNumbers(code: String): String = {
    val Parsed.Success(parsed, _) = parse(code, Proto2Parser.program(_))
    val withFixedNumbers          = parsed.map(Proto2Homomorphisms.correctNumbers)
    withFixedNumbers.map(Proto2Generator.generateAstEntity(_)).fold("")(_ + "\n" + _)
  }

}
