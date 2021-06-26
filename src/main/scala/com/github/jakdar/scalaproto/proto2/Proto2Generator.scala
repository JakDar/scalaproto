package com.github.jakdar.scalaproto.proto2
import Ast._
import com.github.jakdar.scalaproto.parser.Generator

object Proto2Generator extends Generator[AstEntity] {

  override def generate(s: Seq[AstEntity]): String = s.map(generateAstEntity(_)).fold("")(_ + "\n\n" + _)

  val indent = (0 until 4).map(_ => " ").mkString

  def generateAstEntity(ast: AstEntity): String = {
    ast match {
      case e: EnumAst => generateEnum(e)
      case m: Message => generateMessage(m)
    }
  }

  def generateEnum(e: EnumAst) = {

    val values = e.values
      .map { case EnumLine(name, number) =>
        s"${indent}${name.value} = $number;"
      }
      .foldLeft("")(_ + "\n" + _)

    s"""
      |enum ${e.name.value} {$values
      |}
      |""".stripMargin

  }

  def generateMessage(m: Message) = {

    val formattedFields = m.entries
      .collect {
        case Ast.FieldLine(repeat, typePath, identifier, number) =>
          s"${indent}${repeatToString(repeat)} ${typePath.generate} ${identifier.value} = $number;"

        case oneOf: Ast.OneofField =>
          val entries = oneOf.entries
            .map { case e =>
              s"${indent}${indent}${e.typePath.generate} ${e.identifier.value} = ${e.number};"
            }
            .mkString("\n")

          s"${indent}oneof ${oneOf.identifier.value} {\n${entries}\n${indent}}"
      }

    val formattedInnerEntities = m.innerEntities.map(generateAstEntity)

    val break = if (formattedFields.nonEmpty && formattedInnerEntities.nonEmpty) List("") else Nil

    val formattedMessageContent = (formattedFields ++ break ++ formattedInnerEntities)
      .foldLeft("")(_ + "\n" + _)

    s"""
      |message ${m.name.value} {$formattedMessageContent
      |}
      |""".stripMargin

  }

  def repeatToString: Ast.ArgRepeat => String = _ match {
    case ArgRepeat.Required => "required"
    case ArgRepeat.Optional => "optional"
    case ArgRepeat.Repeated => "repeated"
  }

}
