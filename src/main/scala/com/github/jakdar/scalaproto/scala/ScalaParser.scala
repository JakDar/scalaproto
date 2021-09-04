package com.github.jakdar.scalaproto.scala

import cats.data.NonEmptyList
import scala.meta._
import com.github.jakdar.scalaproto.parser.Parser
import scala.meta.parsers.Parsed

object ScalaParser extends Parser[Ast.AstEntity] {

  override def parse(code: String): Either[Parser.ParseError, Seq[Ast.AstEntity]] = {
    code.parse[Source] match {
      case e: Parsed.Error        => Left(Parser.ParseError.GenericErr(s"Couldnt parse error due to $e"))
      case Parsed.Success(source) => Right(source.children.map(treeToAst(_)))
    }
  }

  //REVIEW: go directly to /from CommonAst removing need of scala.Ast?

  def treeToAst(t: Tree): Ast.AstEntity = t match {
    case c: Defn.Class  => classToAst(c)
    case c: Defn.Trait  => traitToAst(c)
    case c: Defn.Object => objectToAst(c)
    case other          => throw new IllegalArgumentException(s"Cannot convert $other")
  }

  def classToAst(t: Defn.Class): Ast.Clazz = t match {
    case Defn.Class(_, name, _, constructor, _) =>
      val argLists = NonEmptyList
        .fromList(constructor.paramss)
        .get
        .map(params => Ast.ArgList(params.map(p => (Ast.Identifier(p.name.value), typeToAst(p.decltpe.get)))))
      Ast.Clazz(id = Ast.Identifier(name.value), argLists = argLists, parents = Nil) // TODO:bcm  fix parents
  }

  def typeToAst(p: Type): Ast.TypePath = {

    p match {
      case Type.Apply(prent, internal) =>
        val select                           = typeToAst(prent)
        val typeId: Ast.SimpleTypeIdentifier = select.typeId.singleType.getOrElse(throw new IllegalArgumentException("Expected simple type"))
        Ast.TypePath(
          packagePath = select.packagePath,
          typeId = Ast.HigherTypeIdentifer(id = typeId.id, internal = NonEmptyList.fromListUnsafe(internal.map(typeToAst))),
        )

      case Type.Select(ref, name) =>
        Ast.TypePath(
          packagePath = ref.toString().split(".").map(Ast.Identifier).toList,
          typeId = Ast.SimpleTypeIdentifier(Ast.Identifier(name.value)),
        )

      case Type.Name(name) => Ast.TypePath(packagePath = Nil, typeId = Ast.SimpleTypeIdentifier(Ast.Identifier(name)))

      case other => throw new IllegalArgumentException(s"Found other : ${other.structure}")
    }
  }

  def traitToAst(t: Defn.Trait): Ast.Trait = {
    val isSealed = t.mods.exists { case Mod.Sealed() => true; case _ => false }
    Ast.Trait(isSealed = isSealed, id = Ast.Identifier(t.name.value), parents = Nil) // TODO:bcm
  }

  def objectToAst(t: Defn.Object): Ast.ObjectAst = {

    val innerDefs = t.templ.stats.map(treeToAst(_))

    Ast.ObjectAst(id = Ast.Identifier(t.name.value), definitions = innerDefs, parents = Nil) // TODO:bcm  parents
  }

}
