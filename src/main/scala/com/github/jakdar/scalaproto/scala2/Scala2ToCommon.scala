package com.github.jakdar.scalaproto.scala2

import cats.data.NonEmptyList
import cats.instances.list.catsStdInstancesForList
import cats.syntax.alternative.catsSyntaxAlternativeSeparate
import com.github.jakdar.scalaproto.parser
import com.github.jakdar.scalaproto.parser.ToCommon
import com.github.jakdar.scalaproto.parser.{Ast => CommonAst}
import mouse.boolean.booleanSyntaxMouse

import scala.meta._

object Scala2ToCommon extends ToCommon[Stat] {

  override def toCommon(other: Stat): Either[ToCommon.Error, Seq[parser.Ast.AstEntity]] = Right(treeToAst(other))

  def treeToAst(t: Tree): List[CommonAst.AstEntity] = t match {
    case c: Defn.Class  => classToCommon(c) :: Nil
    case _: Defn.Trait  => Nil
    case c: Defn.Object => objectToAst(c)
    case other          => throw new IllegalArgumentException(s"Cannot convert $other")
  }

  def classToCommon(t: Defn.Class): CommonAst.ClassAst =
    t match {
      case Defn.Class(_, name, _, constructor, _) =>
        val argLists = NonEmptyList
          .fromList(constructor.paramss)
          .get
          .map(params => CommonAst.Fields(params.map(p => (CommonAst.Identifier(p.name.value), TypeConversion.typeToAst(p.decltpe.get)))))
        CommonAst.ClassAst(id = CommonAst.Identifier(name.value), argLists = argLists, parents = Nil) // TODO:bcm  fix parents
    }

  def objectToAst(o: Defn.Object): List[parser.Ast.ObjectAst] = {
    val id = CommonAst.Identifier(o.name.value)

    val (nonEnums, enums) = o.templ.stats.map {
      case c: Defn.Object => (firstParentName(c).contains(id.value).either(objectToAst(c), Right(objectToEnumValue(c))))
      case c: Defn.Class  => firstParentName(c).contains(id.value).either(classToCommon(c) :: Nil, Left(classToCommon(c)))
      case _: Defn.Trait  => ???
    }.separate

    val defs = nonEnums.flatten
    List(CommonAst.ObjectAst(id = id, enumEntries = enums, definitions = defs, parents = Nil))
  }

  def objectToEnumValue(o: Defn.Object): CommonAst.EnumValue = CommonAst.EnumValue(CommonAst.Identifier(o.name.value), parents = Nil)

  def firstParentName(t: Tree): Option[String] = t match {
    case c: Defn.Class  => c.templ
    case c: Defn.Object => c.templ
    case c: Defn.Trait  => c.templ
  }.inits.headOption.map(_.tpe).flatMap { case n: Type.Name => Some(n.value); case _ => None }

  object TypeConversion {
    def typeToAst: Type => CommonAst.TypeIdentifier               = basicTypeToAst _ andThen fillPredefnidedTypes _
    private def basicTypeToAst(p: Type): CommonAst.TypeIdentifier = {

      p match {
        case Type.Apply(prent, internal) =>
          val outer = basicTypeToAst(prent)
          if (!outer.isSingleType || !outer.isInstanceOf[CommonAst.CustomSimpleTypeIdentifier]) {
            throw new IllegalArgumentException("Expected outer to be singletype")
          }

          val o = outer.asInstanceOf[CommonAst.CustomSimpleTypeIdentifier]

          CommonAst.CustomHigherTypeIdentifer(
            outerPackagePath = o.packagePath,
            outer = o.id,
            inner = NonEmptyList.fromListUnsafe(internal.map(basicTypeToAst(_))),
          )

        case Type.Select(ref, name) =>
          val packagePath = ref.toString().split(".").map(CommonAst.Identifier(_)).toList
          CommonAst.CustomSimpleTypeIdentifier(packagePath = packagePath, id = CommonAst.Identifier(name.value))

        case Type.Name(name) =>
          CommonAst.CustomSimpleTypeIdentifier(packagePath = Nil, id = CommonAst.Identifier(name))

        case other => throw new IllegalArgumentException(s"Found other : ${other.structure}")
      }
    }

    private def fillPredefnidedTypes(c: CommonAst.TypeIdentifier): CommonAst.TypeIdentifier = {
      import CommonAst.{CustomHigherTypeIdentifer, CustomSimpleTypeIdentifier, Identifier}

      c match {

        case CustomSimpleTypeIdentifier(Nil, Identifier("Int"))                                     => CommonAst.IntType
        case CustomSimpleTypeIdentifier(_, Identifier("Long" | "ZonedDateTime" | "FiniteDuration")) => CommonAst.LongType
        case CustomSimpleTypeIdentifier(Nil, Identifier("Float"))                                   => CommonAst.FloatType
        case CustomSimpleTypeIdentifier(Nil, Identifier("Double"))                                  => CommonAst.DoubleType
        case CustomSimpleTypeIdentifier(Nil, Identifier("String"))                                  => CommonAst.StringType
        case CustomSimpleTypeIdentifier(Nil, Identifier("Boolean"))                                 => CommonAst.BooleanType
        case CustomSimpleTypeIdentifier(Nil, Identifier("Short"))                                   => CommonAst.ShortType
        case CustomSimpleTypeIdentifier(Nil, Identifier("Byte"))                                    => CommonAst.ByteType
        case CustomSimpleTypeIdentifier(_, Identifier("ByteString"))                                => CommonAst.ArrayType(CommonAst.ByteType)

        case CustomHigherTypeIdentifer(_, Identifier("Option" | "Optional"), internal) if internal.size == 1 =>
          CommonAst.OptionType(fillPredefnidedTypes(internal.head))

        case CustomHigherTypeIdentifer(_, Identifier("List" | "Seq" | "Set" | "Array" | "NonEmptyList" | "NonEmptySeq"), internal)
            if internal.size == 1 =>
          CommonAst.ArrayType(fillPredefnidedTypes(internal.head))

        case c: CommonAst.CustomHigherTypeIdentifer => c.copy(inner = c.inner.map(fillPredefnidedTypes))
        case CommonAst.OptionType(inner)            => CommonAst.OptionType(fillPredefnidedTypes(inner))
        case CommonAst.ArrayType(inner)             => CommonAst.ArrayType(fillPredefnidedTypes(inner))
        case other                                  => other
      }

    }
  }
}
