package com.github.jakdar.scalaproto.json
import com.github.jakdar.scalaproto.parser.Ast
import com.github.jakdar.scalaproto.util.StringUtils
import cats.data.NonEmptyList

object JsonToCommon {

  def toCommon(j: ujson.Value, rootName: String): Seq[Ast.AstEntity] = j match {
    case _: ujson.Str | _: ujson.Num | _: ujson.Bool | ujson.Null | _: ujson.Arr => Seq.empty
    case ujson.Obj(value)                                                        =>
      val fields = value.map { case (k, v) =>
        val vType = v match {
          case _: ujson.Str   => Ast.StringType
          case _: ujson.Num   => Ast.IntType
          case _: ujson.Bool  => Ast.BooleanType
          case ujson.Null     => ???
          case ujson.Arr(arr) => // HACK: using only first array element and defaulting to string on empty
            if (arr.isEmpty) {
              Ast.ArrayType(Ast.StringType)
            }
            else {
              Ast.ArrayType(Ast.IntType) // FIXME: Resolve type
            }

          case _: ujson.Obj => // NOTE: Naively not deduplicating shit
            Ast.CustomSimpleTypeIdentifier(packagePath = Nil, id = Ast.Identifier(StringUtils.titleCase(k))) // FIXME - convert kebab case
        }

        // TODO:bcm  add and escape scala reserved words
        val id = Ast.Identifier(k)

        (id, vType)
      }

      val nestedEntities = value.collect { case (k, obj: ujson.Obj) => toCommon(obj, rootName = StringUtils.titleCase(k)) }.flatten.toList
      Ast.ClassAst(id = Ast.Identifier(rootName), argLists = NonEmptyList.of(Ast.Fields(fields.toList)), parents = Nil) :: nestedEntities
  }

}
