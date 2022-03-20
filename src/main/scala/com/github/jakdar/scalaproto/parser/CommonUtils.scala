package com.github.jakdar.scalaproto.parser

import com.github.jakdar.scalaproto.parser.Ast.HigherTypeIdentifier
import com.github.jakdar.scalaproto.parser.Ast.TypeIdentifier
import com.github.jakdar.scalaproto.parser.Ast.CustomSimpleTypeIdentifier

object CommonUtils {

  def mapHigherTypeIdentifier(h: HigherTypeIdentifier)(fn: TypeIdentifier => TypeIdentifier): HigherTypeIdentifier = h match {
    case ast: Ast.OptionType                => ast.copy(fn(ast.inner))
    case ast: Ast.ArrayType                 => ast.copy(fn(ast.inner))
    case ast: Ast.CustomHigherTypeIdentifer => ast.copy(inner = ast.inner.map(fn))
  }

  def declaredCustomSimpleTypes(asts: List[Ast.AstEntity]) =
    asts.flatMap(declaredCustomSimpleType).distinct

  def declaredCustomSimpleType(ast: Ast.AstEntity): List[CustomSimpleTypeIdentifier] = {
    ast match {
      case e: Ast.ClassAst  => CustomSimpleTypeIdentifier(Nil, e.id) :: e.argLists.toList.flatMap(_.args.map(_._2).flatMap(customSimpleTypesFrom))
      case e: Ast.ObjectAst =>
        // REVIEW: should use enum values too?
        val typesInEnum = e.enumEntries.flatMap(_.left.toOption).flatMap(declaredCustomSimpleType)
        val typesInDefs = e.definitions.flatMap(declaredCustomSimpleType)

        List(
          CustomSimpleTypeIdentifier(Nil, e.id) :: Nil,
          typesInEnum,
          typesInDefs,
        ).flatten
    }
  }

  private def customSimpleTypesFrom(ast: Ast.TypeIdentifier): List[CustomSimpleTypeIdentifier] = {
    ast match {
      case t: Ast.CustomSimpleTypeIdentifier => List(t)
      case Ast.OptionType(t)                 => customSimpleTypesFrom(t)
      case Ast.ArrayType(t)                  => customSimpleTypesFrom(t)
      case t: Ast.CustomHigherTypeIdentifer  => t.inner.toList.flatMap(customSimpleTypesFrom)
      case _                                 => Nil
    }

  }
}
