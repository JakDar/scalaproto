package com.github.jakdar.scalaproto.proto2
import com.github.jakdar.scalaproto.proto2.Ast.EnumAst
import com.github.jakdar.scalaproto.proto2.Ast.Message

object Proto2Homomorphisms {

  def correctNumbers(ast: Ast.AstEntity): Ast.AstEntity =
    ast match {
      case EnumAst(name, lines) => EnumAst(name, correctNumbersEnumLines(lines))
      case msg: Message         => Message(msg.name, correctNumbersFieldLines(msg.fields) ++ msg.innerEntities.map(correctNumbers(_)))
    }

  def correctNumbersEnumLines(lines: List[Ast.EnumLine])   =
    lines.zipWithIndex.map { case (line, idx) => line.copy(number = idx + 1) }

  def correctNumbersFieldLines(lines: List[Ast.FieldLine]) =
    lines.zipWithIndex.map { case (line, idx) =>
      if (line.identifier.value == "tracing") {
        line.copy(number = 100)
      } else {
        line.copy(number = idx + 1)
      }
    }

}
