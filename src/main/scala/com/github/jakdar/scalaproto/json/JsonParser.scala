package com.github.jakdar.scalaproto.json

import ujson.Value

object JsonParser {
  def parse(s: String): Value = ujson.read(s)
}
