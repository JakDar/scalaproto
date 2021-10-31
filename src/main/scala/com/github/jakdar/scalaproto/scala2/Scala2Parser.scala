package com.github.jakdar.scalaproto.scala2

import scala.meta._
import com.github.jakdar.scalaproto.parser.Parser
import scala.meta.parsers.Parsed

object Scala2Parser extends Parser[Stat] {

  override def parse(code: String): Either[Parser.ParseError, Seq[Stat]] = {
    code.parse[Source] match {
      case e: Parsed.Error        => Left(Parser.ParseError.GenericErr(s"Couldn't parse error due to $e"))
      case Parsed.Success(source) => Right(source.children.collect{ case s:Stat => s })
      case other => Left(Parser.ParseError.GenericErr(s"Parsed - other $other"))
    }
  }

}
