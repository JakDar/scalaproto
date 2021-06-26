package com.github.jakdar.scalaproto.parser

trait Generator[AstEntity] {
  def generate(s: Seq[AstEntity]): String
}
