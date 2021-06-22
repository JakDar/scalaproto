package com.github.jakdar.scalaproto.json
import com.github.jakdar.scalaproto.parser.Ast
import com.github.jakdar.scalaproto.util.StringUtils
import cats.data.NonEmptyList

object JsonToCommon {

  def toCommon(j: ujson.Value, rootName: String): (Ast.TypeIdentifier, Seq[Ast.AstEntity]) = j match {
    case ujson.Null => ???

    case _: ujson.Str  => Ast.StringType  -> Nil
    case _: ujson.Num  => Ast.IntType     -> Nil
    case _: ujson.Bool => Ast.BooleanType -> Nil

    case ujson.Arr(arr) => // HACK: using only first array element and defaulting to string on empty
      if (arr.isEmpty) {
        Ast.ArrayType(Ast.StringType) -> Nil // fallback :D
      } else {

        val (typeIds, innerEntities) = arr.toList.zipWithIndex.map { case (innerJ, idx) =>
          toCommon(innerJ, StringUtils.titleCase(rootName) + "Child" + idx.toString)
        }.unzip

        if (typeIds.distinct.size == 1) {
          Ast.ArrayType(typeIds.head) -> innerEntities.flatten
        } else {
          // FIXME: if all are objects - try to merge them
          Ast.ArrayType(Ast.StringType) -> Nil // a fallback
        }
      }

    case ujson.Obj(value) =>
      val objContent = value.map { case (k, v) =>
        val (vType, nestedEntites: List[Ast.AstEntity]) = toCommon(v, StringUtils.titleCase(k))
        val id                                          = Ast.Identifier(k)
        (id, vType, nestedEntites)
      }

      val fields        = objContent.map { case (id, vType, _) => (id, vType) }.toList
      val nestedEntites = objContent.flatMap { case (_, _, entities) => entities }.toList
      val id            = Ast.Identifier(rootName)

      (
        Ast.CustomSimpleTypeIdentifier(Nil, id),
        Ast.ClassAst(id = id, argLists = NonEmptyList.of(Ast.Fields(fields)), parents = Nil) :: nestedEntites
      )
  }

}
