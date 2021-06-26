package com.github.jakdar.scalaproto.json

import com.github.jakdar.scalaproto.parser.Generator

object JsonGenerator extends Generator[ujson.Obj]{
  def generate(j: Seq[ujson.Obj]): String = j.map(_.toString).reduce(_ + "\n\n" + _)

}
