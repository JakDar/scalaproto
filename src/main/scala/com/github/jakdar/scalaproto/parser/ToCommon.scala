package com.github.jakdar.scalaproto.parser

trait ToCommon[Other] {

  def toCommon(other: Other): Either[ToCommon.Error, Seq[Ast.AstEntity]]

}

object ToCommon {
  sealed trait Error
  // no errors for now
}
