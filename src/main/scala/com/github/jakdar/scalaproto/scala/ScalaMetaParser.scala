package com.github.jakdar.scalaproto.scala

import scala.meta._
import com.github.jakdar.scalaproto.parser.Parser
import scala.meta.parsers.Parsed

object ScalaMetaParser extends Parser[Tree] {

  override def parse(code: String): Either[Parser.ParseError, Seq[Tree]] = {
    code.parse[Source] match {
      case e: Parsed.Error        => Left(Parser.ParseError.GenericErr(s"Couldnt parse error due to $e"))
      case Parsed.Success(source) => Right(source.children)
    }
  }

}
