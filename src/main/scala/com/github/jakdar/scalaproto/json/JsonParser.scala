package com.github.jakdar.scalaproto.json

import ujson.Value
import com.github.jakdar.scalaproto.parser.Parser
import ujson.Obj
import cats.syntax.either._

object JsonParser extends Parser[ujson.Obj]{

  override def parse(code: String): Either[Parser.ParseError,Seq[Obj]] = Either.catchNonFatal{
    Seq(ujson.read(code) match {case o:ujson.Obj => o
                              case other => throw new IllegalArgumentException(s"Not supported $other")}
    )}.leftMap(err => Parser.ParseError.GenericErr(err.getMessage()).widen)

}
