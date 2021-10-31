package com.github.jakdar.scalaproto.scala

import scala.meta._
import com.github.jakdar.scalaproto.parser.Generator
object ScalaMetaGenerator extends Generator[Tree] {

  override def generate(s: Seq[Tree]): String = s.map(_.toString()).mkString("\n")

}
