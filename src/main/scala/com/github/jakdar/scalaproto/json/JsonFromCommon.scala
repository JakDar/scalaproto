package com.github.jakdar.scalaproto.json

import com.github.jakdar.scalaproto.parser.Ast
import com.github.jakdar.scalaproto.parser.Ast.ClassAst
import com.github.jakdar.scalaproto.parser.Ast.ObjectAst
import com.github.jakdar.scalaproto.parser.FromCommon
import com.github.jakdar.scalaproto.proto2
import cats.data.NonEmptyList
import com.github.jakdar.scalaproto.util.StringUtils

object JsonFromCommon extends FromCommon[ujson.Obj] {

  def fromCommon(entities: Seq[Ast.AstEntity]): List[ujson.Obj] = {
    val typeIdToEntity = entities.map(e => Ast.CustomSimpleTypeIdentifier(Nil, e.id) -> e).toMap

    def classAstToValue(c: ClassAst) =
      ujson.Obj.from(c.argLists.toList.flatMap(_.args).flatMap { case (id, typeId) => toValue(id, typeId) })

    def toValue(id: Ast.Identifier, typeId: Ast.TypeIdentifier): List[(String, ujson.Value)] = {
      typeId match {
        case Ast.ByteType | Ast.FloatType | Ast.DoubleType | Ast.ShortType | Ast.IntType | Ast.LongType => id.value -> ujson.Num(1.0) :: Nil
        case Ast.StringType                                                                             => id.value -> ujson.Str("string") :: Nil
        case Ast.BooleanType                                                                            => id.value -> ujson.Bool(false) :: Nil
        case a: Ast.CustomSimpleTypeIdentifier                                                          =>
          val named = typeIdToEntity.get(a)

          named match {
            case Some(c: ClassAst)                                                                   => id.value -> classAstToValue(c) :: Nil
            case Some(o: ObjectAst) if o.definitions.nonEmpty && o.definitions.forall(_.isEnumEntry) =>
              id.value -> ujson.Str(o.definitions.head.id.value) :: Nil
            case Some(o: ObjectAst)                                                                  =>
              List(o.enumEntries.map {
                case Left(clazz)      => StringUtils.titleToPascal(clazz.id.value) -> classAstToValue(clazz)
                case Right(enumValue) =>
                  StringUtils.titleToPascal(enumValue.id.value) -> classAstToValue(
                    Ast.ClassAst(enumValue.id, argLists = NonEmptyList.of(Ast.Fields.empty), parents = Nil)
                  )
              }).flatten

            case None => throw new IllegalArgumentException(s"Unknown object $a")
          }

        case id: Ast.CustomHigherTypeIdentifer => throw new IllegalArgumentException(s"CustomHigherTypeIdentifier not supported yet $id")
        case Ast.OptionType(inner)             =>  toValue(id,inner)
        case Ast.ArrayType(inner)              => id.value -> ujson.Arr.from(List(toValue(id,inner))) :: Nil
      }

    }

    entities.collect { case c: Ast.ClassAst => classAstToValue(c) }.toList
  }

}
