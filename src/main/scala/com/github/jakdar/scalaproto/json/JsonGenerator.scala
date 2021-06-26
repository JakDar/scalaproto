package com.github.jakdar.scalaproto.json

object JsonGenerator {

  def generate(j: Seq[ujson.Obj]): String = j.map(_.toString).reduce(_ + "\n\n" + _)

}
