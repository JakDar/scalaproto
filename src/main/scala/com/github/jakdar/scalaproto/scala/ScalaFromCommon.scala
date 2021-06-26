package com.github.jakdar.scalaproto.scala

import com.github.jakdar.scalaproto.parser.{FromCommon, Ast => CommonAst}
import com.github.jakdar.scalaproto.parser.Ast.IntType
import com.github.jakdar.scalaproto.parser.Ast.LongType
import com.github.jakdar.scalaproto.parser.Ast.FloatType
import com.github.jakdar.scalaproto.parser.Ast.DoubleType
import com.github.jakdar.scalaproto.parser.Ast.StringType
import com.github.jakdar.scalaproto.parser.Ast.BooleanType
import com.github.jakdar.scalaproto.parser.Ast.ShortType
import com.github.jakdar.scalaproto.parser.Ast.ByteType
import com.github.jakdar.scalaproto.parser.Ast.CustomSimpleTypeIdentifier
import com.github.jakdar.scalaproto.parser.Ast.CustomHigherTypeIdentifer
import com.github.jakdar.scalaproto.parser.Ast.OptionType
import com.github.jakdar.scalaproto.parser.Ast.ArrayType
import cats.data.NonEmptyList
import com.github.jakdar.scalaproto.scala.Ast.ArgList
import com.github.jakdar.scalaproto.parser.Ast.ClassAst
import com.github.jakdar.scalaproto.parser.Ast.ObjectAst

object ScalaFromCommon extends FromCommon[Ast.AstEntity] {

  override def fromCommon(ast: Seq[CommonAst.AstEntity]): Seq[Ast.AstEntity] = ast.flatMap {
    case c: ClassAst  => classToScala(c) :: Nil
    case e: ObjectAst => enumToScala(e)
  }

  private def classToScala(clazz: CommonAst.ClassAst): Ast.Clazz = {

    val args    = clazz.argLists.map(f => ArgList(f.args.map { case (id, typeId) => (Ast.Identifier(id.value), typeIdentifierToScala(typeId)) }))
    val parents = clazz.parents.map(typeIdentifierToScala)

    Ast.Clazz(id = Ast.Identifier(clazz.id.value), argLists = args, parents = parents)
  }

  private def typeIdentifierToScala(t: CommonAst.TypeIdentifier): Ast.TypePath = {
    def primitive(s: String) = Ast.TypePath(Nil, Ast.SimpleTypeIdentifier(Ast.Identifier(s)))

    t match {
      case IntType                                                   => primitive("Int")
      case LongType                                                  => primitive("Long")
      case FloatType                                                 => primitive("Float")
      case DoubleType                                                => primitive("Double")
      case StringType                                                => primitive("String")
      case BooleanType                                               => primitive("Boolean")
      case ShortType                                                 => primitive("Short")
      case ByteType                                                  => primitive("Byte")
      case CustomSimpleTypeIdentifier(packagePath, id)               =>
        Ast.TypePath(packagePath = packagePath.map(id => Ast.Identifier(id.value)), typeId = Ast.SimpleTypeIdentifier(Ast.Identifier(id.value)))
      case CustomHigherTypeIdentifer(outerPackagePath, outer, inner) =>
        Ast.TypePath(
          packagePath = outerPackagePath.map(id => Ast.Identifier(id.value)),
          typeId = Ast.HigherTypeIdentifer(id = Ast.Identifier(outer.value), internal = inner.map(typeIdentifierToScala))
        )
      case OptionType(inner)                                         =>
        Ast.TypePath(
          packagePath = Nil,
          typeId = Ast.HigherTypeIdentifer(id = Ast.Identifier("Option"), internal = NonEmptyList.of(typeIdentifierToScala(inner)))
        )

      case ArrayType(inner) =>
        Ast.TypePath(
          packagePath = Nil,
          typeId = Ast.HigherTypeIdentifer(id = Ast.Identifier("List"), internal = NonEmptyList.of(typeIdentifierToScala(inner)))
        )

    }
  }

  private def enumToScala(objCommon: CommonAst.ObjectAst): List[Ast.AstEntity] = {

    val sealedTrait = Ast.Trait(isSealed = true, id = Ast.Identifier(objCommon.id.value), parents = Nil)

    def innerWithExtends(d: CommonAst.AstEntity) = d match {
      case c: CommonAst.ClassAst  => c.copy(parents = List(CommonAst.CustomSimpleTypeIdentifier(Nil, objCommon.id)))
      case o: CommonAst.ObjectAst => o.copy(parents = List(CommonAst.CustomSimpleTypeIdentifier(Nil, objCommon.id)))
    }

    val inner = objCommon.definitions.flatMap((innerWithExtends _).andThen((fromCommon _).compose(List(_))))
    val obj   = Ast.ObjectAst(id = Ast.Identifier(objCommon.id.value), definitions = inner, parents = Nil)

    if (objCommon.definitions.isEmpty) {
      List(obj)
    } else {
      List(sealedTrait, obj)
    }

  }

}
