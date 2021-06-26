package com.github.jakdar.scalaproto.scala
import Ast._
import com.github.jakdar.scalaproto.parser.Generator

object ScalaGenerator extends Generator[AstEntity] {

  override def generate(s: Seq[AstEntity]): String = s.map(generateEntity(_)).reduce(_ + "\n\n" + _)

  val indent = (0 until 4).map(_ => " ").mkString

  def generateEntity(ast: AstEntity): String = ast match {
    case a: Clazz     => generateClass(a)
    case t: Trait     => generateTrait(t)
    case a: ObjectAst => generateObject(a)
  }

  def generateClass(ast: Ast.Clazz): String = {

    val lines = ast.argLists.map(_.args.map { case (id, typeId) => s"${indent}${id.value}: ${typePathToString(typeId)}" })

    val fields = lines.map(_.fold("")(_ + ",\n" + _).stripSuffix(",\n").stripPrefix(",")).map(d => "(" + d + ")").reduceLeft(_ + _)
    s"""
     |case class ${ast.id.value} ${fields}${parentsToString(ast.parents)}
     |""".stripMargin.trim()

  }

  private def parentsToString(parents: List[Ast.TypePath]) =
    parents.map(typePathToString).reduceOption(_ + " with " + _).map(rest => s" extends $rest").getOrElse("")

  def generateTrait(t: Trait): String = {
    val p = parentsToString(t.parents)
    if (t.isSealed) {
      s"sealed trait ${t.id.value}${p}"
    } else {
      s"trait ${t.id.value}${p}"
    }
  }

  def generateObject(o: ObjectAst): String = {

    val enumFields = o.definitions.map(generateEntity).map(indent + _).fold("")(_ + "\n" + _)

    if (o.definitions.isEmpty) {
      s"case object ${o.id.value}${parentsToString(o.parents)}" // TODO:bcm  - toCammel
    } else {
      s"""
     |object ${o.id.value}${parentsToString(o.parents)} {$enumFields
     |}
     |""".stripMargin.trim()
    }
  }

  private def typeIdToString(t: Ast.TypeIdentifier) =
    t match {
      case SimpleTypeIdentifier(id)          => id.value
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
