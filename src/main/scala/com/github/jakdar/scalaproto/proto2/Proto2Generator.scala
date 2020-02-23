package com.github.jakdar.scalaproto.proto2
import com.github.jakdar.scalaproto.scala.Ast._
import mouse.all._
import cats.syntax.option.catsSyntaxOptionId
import cats.data.NonEmptyList
import com.google.common.base.CaseFormat

object Proto2Generator {
  val indent = (0 until 4).map(_ => " ").mkString

  def generateAstEntity(ast: AstEntity) = {
    ast match {
      case c: Clazz => generateClass(c)
      case _: Trait => ""
      case obj: ObjectAst if obj.definitions.nonEmpty && obj.definitions.forall(_.isCaseObject) =>
        generateEnumObj(obj.id, NonEmptyList.fromListUnsafe(obj.definitions.map(_.maybeObject.get)))

      case other => throw new IllegalArgumentException(s"not supported $other")
    }
  }

  def generateClass(clazz: Clazz): String = {
    val fields: List[(Identifier, TypePath)] = clazz.argLists.toList.flatMap(_.args)

    val formattedFields: String = fields.zipWithIndex
      .map {
        case ((id, typePath), idx) => indent + fieldFormatter(id, typePath, idx + 1)
      }
      .foldLeft("")(_ + "\n" + _)

    s"""
    |message ${clazz.id.value} {$formattedFields
    |}
    """.stripMargin
  }

  def cammelToUpperUnderscore(s: String) = CaseFormat.UPPER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, s)

  def generateEnumObj(id: Identifier, variants: NonEmptyList[ObjectAst]) = {
    val formattedFields =
      variants.zipWithIndex.map { case (obj, i) => s"${indent}${cammelToUpperUnderscore(obj.id.value)} = $i;" }.reduceLeft(_ + "\n" + _)
    s"""
    |enum ${id.value} {
    |$formattedFields
    |}
    """.stripMargin
  }

  private val specialTypes = Map(
    "Option"       -> "optional",
    "List"         -> "repeated",
    "Seq"          -> "repeated",
    "Set"          -> "repeated",
    "Array"        -> "repeated",
    "NonEmptyList" -> "repeated"
  )

  private def fieldFormatter(name: Identifier, typePath: TypePath, id: Int) = {
    typePath.typeId match {
      case HigherTypeIdentifer(Identifier(specTypeId), typeArgs) =>
        if (typeArgs.size == 1 && typeArgs.head.typeId.isSingleType) {

          val prefix = specialTypes.getOrElse(specTypeId, "required")

          val simpleType = typeArgs.head.typeId.some.collect { case s: SimpleTypeIdentifier => s }.get

          val typeName = specialTypes.contains(specTypeId).fold(t = formatSimpleType(simpleType), f = specTypeId)

          s"$prefix $typeName ${name.value} = $id;"
        } else {
          throw new IllegalArgumentException("Cannot map to proto complex scala syntax :(")
        }

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
