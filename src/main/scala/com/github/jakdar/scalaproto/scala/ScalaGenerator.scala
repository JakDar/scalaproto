package com.github.jakdar.scalaproto.scala
import Ast._

object ScalaGenerator {
  val indent = (0 until 4).map(_ => " ").mkString

  def generateScala(ast: AstEntity): String = ast match {
    case a: Clazz     => generateClass(a)
    case t: Trait     => generateTrait(t)
    case a: ObjectAst => generateObject(a)
  }

  def generateClass(ast: Ast.Clazz): String = {

    val lines = ast.argLists.map(_.args.map { case (id, typeId) => s"${indent}${id.value}: ${typePathToString(typeId)}" })

    val fields = lines.map(_.fold("")(_ + ",\n" + _).stripSuffix(",\n").stripPrefix(",")).map(d => "(" + d + ")").reduceLeft(_ + _)
    s"""
     |case class ${ast.id.value} $fields ${patentsToString(ast.parents)}
     |""".stripMargin.trim()

  }

  private def patentsToString(parents: List[Ast.TypePath]) = {
    parents.map(typePathToString).reduceOption(_ + " with " + _) match {
      case Some(p) => "extends " + p
      case None    => ""
    }
  }

  def generateTrait(t: Trait): String = {
    val p = patentsToString(t.parents)
    if (t.isSealed) {
      s"sealed trait ${t.id.value} ${p}"
    } else {
      s"trait ${t.id.value} ${p}"
    }
  }

  def generateObject(o: ObjectAst): String = {

    val enumFields = o.definitions.map(generateScala).map(indent + _).fold("")(_ + "\n" + _)

    if (o.definitions.isEmpty) {
      s"case object ${o.id.value} ${patentsToString(o.parents)}" // TODO:bcm  - toCammel
    } else {
      s"""
     |object ${o.id.value}${" " + patentsToString(o.parents)} {$enumFields
     |}
     |""".stripMargin.trim()
    }
  }

  private def typeIdToString(t: Ast.TypeIdentifier) =
    t match {
      case SimpleTypeIdentifier(id) => id.value
      case HigherTypeIdentifer(id, internal) =>
        id.value + "[" + internal.map(typePathToString).toList.reduce(_ + "," + _) + "]"
    }

  private def typePathToString(a: Ast.TypePath): String = {

    val typeId = typeIdToString(a.typeId)

    if (a.packagePath.isEmpty) {
      typeId
    } else {
      a.packagePath.map(_.value).reduce(_ + "," + _) + "." + typeId
    }
  }

}
