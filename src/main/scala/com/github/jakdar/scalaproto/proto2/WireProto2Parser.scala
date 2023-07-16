package com.github.jakdar.scalaproto.proto2

import com.squareup.wire.schema.Location
import com.squareup.wire.schema.internal.parser.ProtoParser
import cats.syntax.bifunctor.toBifunctorOps
import cats.syntax.either.catsSyntaxEitherObject
import scala.jdk.CollectionConverters._
import com.squareup.wire.schema.internal.parser.TypeElement
import com.github.jakdar.scalaproto.parser.Parser

object WireProto2Parser extends Parser[TypeElement]  {

  val LOCATION = new Location("", "", -1, -1)

  def parse(code: String): Either[Parser.ParseError, Seq[TypeElement]] = Either.catchNonFatal {

    val result = new ProtoParser(LOCATION, code.toArray).readProtoFile();

    result.getTypes().asScala.toList

  }.leftMap(err => Parser.ParseError.GenericErr(err.getMessage()))

}
