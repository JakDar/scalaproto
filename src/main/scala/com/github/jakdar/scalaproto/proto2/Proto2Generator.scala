package com.github.jakdar.scalaproto.proto2
import Ast._

object Proto2Generator {
  val indent = (0 until 4).map(_ => " ").mkString

  def generateAstEntity(ast: AstEntity) = {
    ast match {
      case e: EnumAst => generateEnum(e)
      case m: Message => generateMessage(m)
    }
  }

  def generateEnum(e: EnumAst) = {

    val values = e.values
      .map {
        case EnumLine(name, number) => s"${indent}${name.value} = $number;"
      }
      .foldLeft("")(_ + "\n" + _)

    s"""
      |enum ${e.name.value} {$values
      |}
      |""".stripMargin

  }

  def generateMessage(m: Message) = {

    val formattedFields: String = m.fields
      .map {
        case Ast.FieldLine(repeat, typePath, identifier, number) =>
          s"${indent}${repeatToString(repeat)} ${typePath.generate} ${identifier.value} = $number;"
      }
      .foldLeft("")(_ + "\n" + _)

    s"""
      |message ${m.name.value} {$formattedFields
      |}
      |""".stripMargin

  }

  def repeatToString: Ast.ArgRepeat => String = _ match {
    case ArgRepeat.Required => "required"
    case ArgRepeat.Optional => "optional"
    case ArgRepeat.Repeated => "repeated"
  }

}
