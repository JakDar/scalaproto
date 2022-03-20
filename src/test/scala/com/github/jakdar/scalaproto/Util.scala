package com.github.jakdar.scalaproto
import com.github.jakdar.scalaproto.Application.ConversionSupport
import com.github.jakdar.scalaproto.parser.ToCommon
import com.github.jakdar.scalaproto.parser.Parser.ParseError
import mouse.all.anySyntaxMouse
import com.github.jakdar.scalaproto.parser.CommonHomomorphisms

object Util {
  def toDestAst[D](code: String, source: ConversionSupport[_], dest: ConversionSupport[D]): Either[ParseError | ToCommon.Error, Seq[D]] =
    source
      .parseToCommon(code)
      .map(_.toList |> CommonHomomorphisms.unknownIdTypesAsString)
      .map(dest.fromCommon.fromCommon)
}
