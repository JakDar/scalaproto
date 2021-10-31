package com.github.jakdar.scalaproto.scala
import scala.meta._
import com.github.jakdar.scalaproto.parser.FromCommon
import com.github.jakdar.scalaproto.parser.Ast

object ScalaMetaFromCommon extends FromCommon[Tree] {
  val emptyTemplate = Template(early = List(), inits = List(), self = Self(name = Name(""), decltpe = None), stats = List(), derives = List())
  val noConstructor = Ctor.Primary(mods = Nil, name = Name(""), paramss = Nil)

  override def fromCommon(ast: Seq[Ast.AstEntity]): Seq[Tree] = ast.flatMap {
    case c: Ast.ClassAst  => classToScala(c) :: Nil
    case e: Ast.ObjectAst => enumToScala(e)
  }

  private def classToScala(clazz: Ast.ClassAst): Defn.Class = {
    val paramss = clazz.argLists.toList.map(fields =>
      fields.args.map { case (id, typeId) =>
        Term.Param(mods = Nil, name = Name(id.value), decltpe = Some(typeIdentifierToScala(typeId)), default = None)
      }
    )

    val ctor = Ctor.Primary(mods = Nil, name = Name(""), paramss = paramss)
    Defn.Class(mods = Nil, name = Type.Name(clazz.id.value), tparams = Nil, ctor = ctor, templ = emptyTemplate)
  }

  private def typeIdentifierToScala(t: Ast.TypeIdentifier): Type = {
    import Ast._

    t match {
      case IntType                                     => Type.Name("Int")
      case LongType                                    => Type.Name("Long")
      case FloatType                                   => Type.Name("Float")
      case DoubleType                                  => Type.Name("Double")
      case StringType                                  => Type.Name("String")
      case BooleanType                                 => Type.Name("Boolean")
      case ShortType                                   => Type.Name("Short")
      case ByteType                                    => Type.Name("Byte")
      case CustomSimpleTypeIdentifier(packagePath, id) => foldPath(packagePath, id)

      case CustomHigherTypeIdentifer(outerPackagePath, outer, inner) =>
        val tpe = foldPath(outerPackagePath, outer)

        Type.Apply(tpe = tpe, args = inner.toList.map(typeIdentifierToScala))
      case OptionType(inner)                                         =>
        Type.Apply(tpe = Type.Name("Option"), args = List(typeIdentifierToScala(inner)))
      case ArrayType(inner)                                          =>
        Type.Apply(tpe = Type.Name("List"), args = List(typeIdentifierToScala(inner)))

    }
  }

  // REVIEW:  rename
  private def foldPath(packagePath: List[Ast.Identifier], name: Ast.Identifier): Type.Ref = {
    def foldPathRec(list: List[Ast.Identifier]): Term.Ref = list match {
      case a :: Nil => Term.Name(a.value)
      case l        => Term.Select(qual = foldPathRec((l.init)), name = Term.Name(l.last.value))
    }

    if (packagePath.isEmpty) Type.Name(name.value)
    else Type.Select(qual = foldPathRec(packagePath), name = Type.Name(name.value))
  }

  private def enumToScala(objCommon: Ast.ObjectAst): List[Stat] = {
    val sealedTrait = Defn.Trait(mods = List(), name = Type.Name(objCommon.id.value), Nil, ctor = noConstructor, templ = emptyTemplate)

    def innerWithExtends(d: Ast.AstEntity): Ast.AstEntity = d match {
      case c: Ast.ClassAst  => c.copy(parents = List(Ast.CustomSimpleTypeIdentifier(Nil, objCommon.id)))
      case o: Ast.ObjectAst => o.copy(parents = List(Ast.CustomSimpleTypeIdentifier(Nil, objCommon.id)))
    }

    val innerEnums = objCommon.enumEntries
      .flatMap {
        case Right(enumValue) =>
          List(Defn.Object(List(Mod.Case()), name = Term.Name(enumValue.id.value), templ = emptyTemplate)) // TODO:bcm  add parents
        case Left(clazz)      => (innerWithExtends _).andThen((fromCommon _).compose(List(_)))(clazz)
      }
      .map(_.asInstanceOf[Stat])

    val inner = objCommon.definitions.flatMap((innerWithExtends _).andThen((fromCommon _).compose(List(_)))).map(_.asInstanceOf[Stat])

    val obj = Defn.Object(mods = Nil, name = Term.Name(objCommon.id.value), templ = emptyTemplate.copy(stats = innerEnums ++ inner))

    if (objCommon.enumEntries.nonEmpty) { // TODO:bcm  wtf - enums fail there?
      List(sealedTrait, obj)
    } else {
      List(obj)
    }
  }

}
