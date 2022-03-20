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
import mouse.all.anySyntaxMouse
import com.github.jakdar.scalaproto.proto2.Proto2FromCommon
import com.github.jakdar.scalaproto.proto2.Proto2Generator
import com.github.jakdar.scalaproto.proto2.Proto2Homomorphisms
import com.github.jakdar.scalaproto.proto2.Proto2Parser
import com.github.jakdar.scalaproto.proto2.Proto2ToCommon
import com.github.jakdar.scalaproto.scala2.{Scala2FromCommon, Scala2Generator, Scala2Parser, Scala2ToCommon}
import com.github.jakdar.scalaproto.proto2.Ast
import com.github.jakdar.scalaproto.parser.{Ast => CommonAst}
import _root_.scala.meta.Stat
import ujson.Obj
import com.github.jakdar.scalaproto.parser.Parser.ParseError
import scala.annotation.tailrec
import com.github.jakdar.scalaproto.parser.CommonHomomorphisms

object Application {
  case class ConversionSupport[AstEntity](
      generator: Generator[AstEntity],
      parser: Parser[AstEntity],
      toCommon: ToCommon[AstEntity],
      fromCommon: FromCommon[AstEntity],
  ) {
    def parseToCommon(
        code: String
    ): Either[ParseError | ToCommon.Error, Seq[CommonAst.AstEntity]] =
      parser.parse(code) match {
        case Left(error) => Left(error)
        case Right(ok)   => ok.flatTraverse(toCommon.toCommon)
      }

    def generateFromCommon: Seq[CommonAst.AstEntity] => String = generator.generate compose fromCommon.fromCommon
  }

  val scalaSupport: ConversionSupport[Stat] = ConversionSupport(Scala2Generator, Scala2Parser, Scala2ToCommon, Scala2FromCommon)

  // TODO assume id type as a common homomorphism

  val proto2Support: ConversionSupport[Ast.AstEntity] = ConversionSupport(Proto2Generator, Proto2Parser, Proto2ToCommon, Proto2FromCommon)

  val jsonSupport: ConversionSupport[Obj] = ConversionSupport(JsonGenerator, JsonParser, JsonToCommon, JsonFromCommon)

  def convert[S, D](code: String, source: ConversionSupport[S], dest: ConversionSupport[D]): Either[ParseError | ToCommon.Error, String] =
    source
      .parseToCommon(code)
      .map(_.toList |> CommonHomomorphisms.unknownIdTypesAsString)
      .map(dest.generateFromCommon)

  def autoConvert[D](code: String, dest: ConversionSupport[D]): Either[ParseError | ToCommon.Error, String] = {
    def autoToCommon(acc: List[ConversionSupport[_]]): Either[ParseError | ToCommon.Error, Seq[CommonAst.AstEntity]] =
      acc match {
        case Nil          => throw new IllegalArgumentException("Nil shouldn't happen here")
        case head :: Nil  => head.parseToCommon(code)
        case head :: tail => head.parseToCommon(code).orElse(autoToCommon(tail))
      }

    autoToCommon(List(jsonSupport, proto2Support, scalaSupport))
      .map(_.toList |> CommonHomomorphisms.unknownIdTypesAsString)
      .map(dest.generateFromCommon)

  }

  def protoFixNumbers(code: String): String = {
    val parsed = Proto2Parser.program.parse(code).getOrElse(throw new Exception("Failed parsing"))._2

    val withFixedNumbers = parsed.map(Proto2Homomorphisms.correctNumbers)
    withFixedNumbers.map(Proto2Generator.generateAstEntity(_)).toList.fold("")(_ + "\n" + _)
  }

}
