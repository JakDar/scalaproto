package com.github.jakdar.scalaproto.json
import com.github.jakdar.scalaproto.parser.Ast
import com.github.jakdar.scalaproto.util.StringUtils
import cats.data.NonEmptyList

object JsonToCommon {

  def toCommon(j: ujson.Value, rootName: String): Seq[Ast.AstEntity] = j match {
    case _: ujson.Str | _: ujson.Num | _: ujson.Bool | ujson.Null | _: ujson.Arr => Seq.empty
    case ujson.Obj(value)                                                        =>
      val objContent = value.map { case (k, v) =>
        val (vType, nestedEntites: List[Ast.AstEntity]) = v match {
          case _: ujson.Str   => (Ast.StringType, Nil)
          case _: ujson.Num   => (Ast.IntType, Nil)
          case _: ujson.Bool  => (Ast.BooleanType, Nil)
          case ujson.Null     => ???
          case ujson.Arr(arr) => // HACK: using only first array element and defaulting to string on empty
            if (arr.isEmpty) {
              Ast.ArrayType(Ast.StringType) -> Nil
            } else {

              val inner = arr.zipWithIndex.map {
                case (_: ujson.Str, _)   => Ast.StringType
                case (_: ujson.Num, _)   => Ast.IntType
                case (_: ujson.Bool, _)  => Ast.BooleanType
                case (ujson.Null, _)     => throw new IllegalArgumentException("Null not supported yet")
                case (_: ujson.Obj, idx) => Ast.CustomSimpleTypeIdentifier(Nil, Ast.Identifier(StringUtils.titleCase(k) + idx.toString)) // TODO:bcm  handle nested
                case (_: ujson.Arr, _)   => throw new IllegalArgumentException("Not supported yet")
              }

              // TODO:bcm  inner jsons

              if (inner.distinct.size == 1) {
                Ast.ArrayType(inner.head) -> Nil
              } else {
                Ast.ArrayType(Ast.StringType) -> Nil // a fallback
              }
            }

          case obj: ujson.Obj => // NOTE: Naively not deduplicating shit
            (
              Ast.CustomSimpleTypeIdentifier(packagePath = Nil, id = Ast.Identifier(StringUtils.titleCase(k))),
              toCommon(obj, rootName = StringUtils.titleCase(k))
            )
        }

        // TODO:bcm  add and escape scala reserved words
        val id = Ast.Identifier(k)

        (id, vType,nestedEntites)
      }

      val fields = objContent.map{case (id,vType,_) => (id,vType)}.toList
      val nestedEntites = objContent.flatMap{case (_,_,entities) => entities}.toList

      Ast.ClassAst(id = Ast.Identifier(rootName), argLists = NonEmptyList.of(Ast.Fields(fields)), parents = Nil) :: nestedEntites
  }

}
