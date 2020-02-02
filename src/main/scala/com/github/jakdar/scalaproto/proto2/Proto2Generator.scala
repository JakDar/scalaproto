package com.github.jakdar.scalaproto.proto2
import com.github.jakdar.scalaproto.parser._
import Ast._
import mouse.all._

object Proto2Generator {

  val indent = (0 until 4).map(_ => " ").mkString

  def generateClass(clazz: Clazz): String = {
    val fields: List[(Identifier, TypePath)] = clazz.argLists.toList.flatMap(_.args)

    val formattedFields: String = fields.zipWithIndex
      .map {
        case ((id, typePath), idx) => indent + fieldFormatter(id, typePath, idx + 1)
      }
      .foldLeft("")(_ + "\n" + _)

    s"""
    |message ${clazz.name.value} {$formattedFields
    |}
    """.stripMargin
  }

  private val specialTypes = Map(
    "Option" -> "optional",
    "List"   -> "repeated",
    "Seq"    -> "repeated",
    "Set"    -> "repeated",
    "Array"  -> "repeated"
  )

  private def fieldFormatter(name: Identifier, typePath: TypePath, id: Int) = {
    typePath.last match {
      case UniHigherTypeIdentifer(Identifier(specTypeId), simpleType) =>
        val prefix   = specialTypes.getOrElse(specTypeId, "required")
        val typeName = specialTypes.contains(specTypeId).fold(t = formatSimpleType(simpleType), f = specTypeId)

        s"$prefix $typeName ${name.value} = $id;"

      case s: SimpleTypeIdentifier =>
        val typeName = formatSimpleType(s)
        s"required $typeName ${name.value} = $id;"

    }

  }

  private val simpleTypeMapper = Map(
    "Long"          -> "int64",
    "Int"           -> "int32",
    "Double"        -> "double",
    "ZonedDateTime" -> "int64",
    "Boolean"       -> "bool",
    "String"        -> "string",
    "ByteString"    -> "bytes"
  )

  private def formatSimpleType(s: SimpleTypeIdentifier): String =
    simpleTypeMapper.getOrElse(s.id.value, s.id.value)

}
