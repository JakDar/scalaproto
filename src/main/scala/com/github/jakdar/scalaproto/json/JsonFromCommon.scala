package com.github.jakdar.scalaproto.json

import com.github.jakdar.scalaproto.parser.Ast
import com.github.jakdar.scalaproto.parser.Ast.ClassAst
import com.github.jakdar.scalaproto.parser.Ast.ObjectAst
import com.github.jakdar.scalaproto.parser.FromCommon

object JsonFromCommon extends FromCommon[ujson.Obj]{

  def fromCommon(entities: Seq[Ast.AstEntity]): List[ujson.Obj] = {
    val typeIdToEntity = entities.map(e => Ast.CustomSimpleTypeIdentifier(Nil, e.id) -> e).toMap

    def classAstToValue(c: ClassAst) =
      ujson.Obj.from(c.argLists.toList.flatMap(_.args).map { case (id, typeId) => (id.value, toValue(typeId)) })

    def toValue(typeId: Ast.TypeIdentifier): ujson.Value = {
      typeId match {
        case Ast.ByteType | Ast.FloatType | Ast.DoubleType | Ast.ShortType | Ast.IntType | Ast.LongType => ujson.Num(1.0)
        case Ast.StringType                                                                             => ujson.Str("string")
        case Ast.BooleanType                                                                            => ujson.Bool(false)
        case a: Ast.CustomSimpleTypeIdentifier                                                          =>
          val named = typeIdToEntity.get(a)

          named match {
            case Some(c: ClassAst)  => classAstToValue(c)
            case Some(o: ObjectAst) => throw new IllegalArgumentException(s"Object To json not supported yet for obj $o")
            case None               => throw new IllegalArgumentException(s"Unknown object $a")
          }

        case id: Ast.CustomHigherTypeIdentifer => throw new IllegalArgumentException(s"CustomHigherTypeIdentifier not supported yet $id")
        case Ast.OptionType(inner)             => toValue(inner)
        case Ast.ArrayType(inner)              => ujson.Arr.from(List(toValue(inner)))
      }

    }

    entities.collect { case c: Ast.ClassAst => classAstToValue(c) }.toList
  }

}
