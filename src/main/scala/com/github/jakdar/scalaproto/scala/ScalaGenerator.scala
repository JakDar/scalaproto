package com.github.jakdar.scalaproto.scala
import com.github.jakdar.scalaproto.proto2.Ast._
import com.github.jakdar.scalaproto.proto2.Ast.ArgRepeat.Required
import com.github.jakdar.scalaproto.proto2.Ast.ArgRepeat.Optional
import com.github.jakdar.scalaproto.proto2.Ast.ArgRepeat.Repeated

object ScalaGenerator {

  def generateClass(message: Message): String = // TODO:bcm clean this below
    s"""
     |case class ${message.name.value} (${message.fields
         .map(generateField)
         .map("   " + _)
         .fold("")(_ + ",\n" + _)
         .stripSuffix(",\n")
         .stripPrefix(",")}
     |)
     |""".stripMargin.trim()

  private val simpleTypeMapper = Map(
    "int64"  -> "Long",
    "int32"  -> "Int",
    "double" -> "Double",
    "bool"   -> "Boolean",
    "string" -> "String",
    "bytes"  -> "ByteString"
  )

  def generateField(field: FieldLine): String = {
    val FieldLine(repeat, typePath: TypePath, identifier, _) = field

    val typeIdentifier = typePath.last.id.value

    val typeId = if (typeIdentifier == "int64" && identifier.value == "time") {
      "ZonedDateTime"
    } else {
      simpleTypeMapper.getOrElse(typeIdentifier, typeIdentifier)
    }

    val `type` = typePath.init.map(_.value).foldRight(typeId)(_ + "." + _)

    repeat match {
      case Required => s"${identifier.value}: ${`type`}"
      case Optional => s"${identifier.value}: Option[${`type`}]"
      case Repeated => s"${identifier.value}: Seq[${`type`}]"
    }

  }

}
