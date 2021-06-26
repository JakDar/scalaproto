package com.github.jakdar.scalaproto.parser

trait FromCommon[Other] {

  def fromCommon(ast: Seq[Ast.AstEntity]): Seq[Other]

}
