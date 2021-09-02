package com.github.jakdar.scalaproto.json

import com.github.jakdar.scalaproto.parser.Ast
import com.github.jakdar.scalaproto.parser.Ast.ClassAst
import com.github.jakdar.scalaproto.parser.Ast.ObjectAst
import com.github.jakdar.scalaproto.parser.FromCommon
import com.github.jakdar.scalaproto.proto2
import cats.data.NonEmptyList
import com.github.jakdar.scalaproto.util.StringUtils
import cats.syntax.either.catsSyntaxEitherId

object JsonFromCommon extends FromCommon[ujson.Obj] {

  def fromCommon(entities: Seq[Ast.AstEntity]): List[ujson.Obj] = {
    val typeIdToEntity = entities.map(e => Ast.CustomSimpleTypeIdentifier(Nil, e.id) -> e).toMap

    def classAstToValue(c: ClassAst) =
      ujson.Obj.from(c.argLists.toList.flatMap(_.args).flatMap { case (id, typeId) =>
        toValue(typeId) match {
          case Right(values) => values
          case Left(value)   => List((id.value, value))
        }

      })

    def toValue(typeId: Ast.TypeIdentifier): Either[ujson.Value, List[(String, ujson.Value)]] = {
      typeId match {
        case Ast.ByteType | Ast.FloatType | Ast.DoubleType | Ast.ShortType | Ast.IntType | Ast.LongType => ujson.Num(1.0).asLeft
        case Ast.StringType                                                                             => ujson.Str("string").asLeft
        case Ast.BooleanType                                                                            => ujson.Bool(false).asLeft
        case a: Ast.CustomSimpleTypeIdentifier                                                          =>
          val named = typeIdToEntity.get(a)

          named match {
            case Some(c: ClassAst)                                                                   => classAstToValue(c).asLeft
            case Some(o: ObjectAst) if o.definitions.nonEmpty && o.definitions.forall(_.isEnumEntry) =>
              ujson.Str(o.definitions.head.id.value).asLeft
            case Some(o: ObjectAst)                                                                  =>
              // Returns Fields we should add to parent document
              List(o.enumEntries.map {
                case Left(clazz)      => StringUtils.titleToPascal(clazz.id.value) -> classAstToValue(clazz)
                case Right(enumValue) =>
                  StringUtils.titleToPascal(enumValue.id.value) -> classAstToValue(
                    Ast.ClassAst(enumValue.id, argLists = NonEmptyList.of(Ast.Fields.empty), parents = Nil)
                  )
              }).flatten.asRight

            case None => throw new IllegalArgumentException(s"Unknown object $a")
          }

        case id: Ast.CustomHigherTypeIdentifer => throw new IllegalArgumentException(s"CustomHigherTypeIdentifier not supported yet $id")
        case Ast.OptionType(inner)             => toValue(inner)
        case Ast.ArrayType(inner)              =>
          val innerRes = toValue(inner) match {
            case Right(values) => ujson.Obj.from(values)
            case Left(value)   => value
          }

          ujson.Arr.from(List(innerRes)).asLeft
      }

    }

    entities.collect { case c: Ast.ClassAst => classAstToValue(c) }.toList
  }
}
