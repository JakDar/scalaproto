package com.github.jakdar.scalaproto.parser

trait FromCommon[Other] {

  def fromCommon(ast: Ast.AstEntity): List[Other]

}
