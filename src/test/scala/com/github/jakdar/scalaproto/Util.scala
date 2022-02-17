package com.github.jakdar.scalaproto
import com.github.jakdar.scalaproto.Application.ConversionSupport
import com.github.jakdar.scalaproto.parser.ToCommon
import com.github.jakdar.scalaproto.parser.Parser.ParseError

object Util {
  def toDestAst[D](code: String, source: ConversionSupport[_], dest: ConversionSupport[D]): Either[ParseError | ToCommon.Error, Seq[D]] =
    source.parseToCommon(code).map(dest.fromCommon.fromCommon)
}
