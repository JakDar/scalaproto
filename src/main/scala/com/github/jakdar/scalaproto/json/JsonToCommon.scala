package com.github.jakdar.scalaproto.json
import com.github.jakdar.scalaproto.parser.Ast
import com.github.jakdar.scalaproto.util.StringUtils
import cats.data.NonEmptyList
import com.github.jakdar.scalaproto.parser.ToCommon
import ujson.Obj

object JsonToCommon extends ToCommon[ujson.Obj] {

  override def toCommon(other: Obj): Either[ToCommon.Error, Seq[Ast.AstEntity]] = {
    val (_, res) = innerToCommon(other, "Root")
    Right(res)
  }

  def innerToCommon(j: ujson.Value, rootName: String): (Ast.TypeIdentifier, Seq[Ast.ClassAst]) = j match {
    case ujson.Null => throw new IllegalArgumentException("Unsupported Null toCommon")

    case _: ujson.Str  => Ast.StringType  -> Nil
    case _: ujson.Num  => Ast.IntType     -> Nil
    case _: ujson.Bool => Ast.BooleanType -> Nil

    case ujson.Arr(arr) => // HACK: using only first array element and defaulting to string on empty
      val default = Ast.ArrayType(Ast.StringType) -> Nil

      if (arr.isEmpty) {
        default
      } else {
        val rootNameTitle = StringUtils.titleCase(rootName) + "Arr"

        val (typeIds, innerEntities) = arr.toList.zipWithIndex.map { case (innerJ, idx) =>
          innerToCommon(innerJ, rootNameTitle + idx.toString)
        }.unzip

        if (typeIds.distinct.size == 1) {
          Ast.ArrayType(typeIds.head) -> innerEntities.flatten
        } else {
          //HACK - wont work if nested the same field names - reproduce it in tests
          // val (thisLvlEntities, childEntities) = innerEntities.flatten.partition(_.id.value.startsWith(rootNameTitle))
          val objIds = typeIds.collect { case Ast.CustomSimpleTypeIdentifier(_, id) => id }.toSet

          val (thisLvlEntities, childEntities) = innerEntities.flatten.partition(e => objIds.contains(e.id))
          val allObjects                       = typeIds.distinct.size == thisLvlEntities.size

          if (allObjects) {
            mergeClasses(thisLvlEntities) match {
              case None        => default
              case Some(clazz) => Ast.ArrayType(Ast.CustomSimpleTypeIdentifier(Nil, clazz.id)) -> (clazz :: childEntities)
            }
          } else default
        }
      }

    case ujson.Obj(value) =>
      val objContent = value.map { case (k, v) =>
        val (vType, nestedEntites: List[Ast.ClassAst]) = innerToCommon(v, rootName + StringUtils.titleCase(k))
        val id                                         = Ast.Identifier(k)
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

  private def mergeClasses(classes: List[Ast.ClassAst]) = {

    NonEmptyList.fromList(classes).map { nelClasses =>
      val mergedFields = classes.flatMap(_.argLists.toList.flatMap(_.args)).toMap.toList

      def inAll(t: (Ast.Identifier, Ast.TypeIdentifier)) = classes.forall(_.argLists.toList.flatMap(_.args).contains(t))

      val fields = mergedFields.map { case t @ (id, typeId) => if (inAll(t)) t else (id, Ast.OptionType(typeId)) }

      nelClasses.head.copy(argLists = NonEmptyList.of(Ast.Fields(fields)))
    }
  }

}
