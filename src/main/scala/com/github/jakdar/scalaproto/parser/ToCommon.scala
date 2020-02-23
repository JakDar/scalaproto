package com.github.jakdar.scalaproto.parser

trait ToCommon[Other] {

  def toCommon(other: Other): Either[ToCommon.Error, List[Ast.AstEntity]]

}

object ToCommon {
  sealed trait Error

  object Error {
    // TODO:bcm
  }
}
