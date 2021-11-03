package com.github.jakdar.scalaproto.proto2
import com.github.jakdar.scalaproto.proto2.Ast.EnumAst
import com.github.jakdar.scalaproto.proto2.Ast.Message
import com.github.jakdar.scalaproto.proto2.Ast.FieldLine
import com.github.jakdar.scalaproto.proto2.Ast.OneofField

object Proto2Homomorphisms {

  def correctNumbers(ast: Ast.AstEntity): Ast.AstEntity =
    ast match {
      case EnumAst(name, lines) => EnumAst(name, correctNumbersEnumLines(lines))
      case msg: Message         => Message(msg.name, correctNumbersEntries(msg.entries))
    }

  def correctNumbersEnumLines(lines: List[Ast.EnumLine]): List[Ast.EnumLine] =
    lines.zipWithIndex.map { case (line, idx) => line.copy(number = idx + 1) }

  def correctNumbersEntries(lines: List[Ast.MessageEntry]): List[Ast.MessageEntry] = {
    if (lines.isEmpty) Nil
    else {
      val zero        = (List.empty[Ast.MessageEntry], 0)
      val (result, _) = lines.foldLeft(zero) { case ((acc, lastNr), entry) =>
        entry match {
          case e: Ast.EnumAst                  => (acc :+ correctNumbers(e), lastNr)
          case m: Ast.Message                  => (acc :+ correctNumbers(m), lastNr)
          case f: FieldLine                    =>
            val insertNr = if (f.identifier.value == "tracing") 100 else (lastNr + 1)
            (acc :+ f.copy(number = insertNr), lastNr + 1)
          case OneofField(identifier, entries) =>
            val newEntries = entries.zipWithIndex.map { case (e, idx) => e.copy(number = idx + lastNr + 1) }
            (acc :+ OneofField(identifier, newEntries), newEntries.last.number)
        }
      }

      result
    }
  }

}
