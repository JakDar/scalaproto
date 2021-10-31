package com.github.jakdar.scalaproto.scala2

import scala.meta._
import com.github.jakdar.scalaproto.parser.Generator

object Scala2Generator extends Generator[Stat] {
  override def generate(s: Seq[Stat]): String = s.map(_.toString()).mkString("\n\n")
}
