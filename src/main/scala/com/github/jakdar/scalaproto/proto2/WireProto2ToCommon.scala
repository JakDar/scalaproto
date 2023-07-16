package com.github.jakdar.scalaproto.proto2

import com.github.jakdar.scalaproto.parser.ToCommon
import com.squareup.wire.schema.internal.parser.TypeElement
import com.github.jakdar.scalaproto.parser.Ast.AstEntity
import com.squareup.wire.schema.internal.parser.EnumElement
import com.squareup.wire.schema.internal.parser.MessageElement
import scala.jdk.CollectionConverters._
import com.github.jakdar.scalaproto.parser.{Ast => CommonAst}
import cats.data.NonEmptyList

object WireProto2ToCommon extends ToCommon[TypeElement] {

  override def toCommon(other: TypeElement): Either[ToCommon.Error, Seq[AstEntity]] = {

    if (other.isInstanceOf[MessageElement]) {
      ???
    } else if (other.isInstanceOf[EnumElement]) {
      ???
    } else {
      ???
    }
    // other.get

    ???
  }

  private def messageToCommon(m: MessageElement): NonEmptyList[CommonAst.AstEntity] = {
    // import com.github.jakdar.scalaproto.proto2.Ast

    m.getFields().asScala.toList

    m.getOneOfs().asScala.toList

    m.getNestedTypes().asScala.toList

    // val objsAndArgs = m.entries.map {
    //   case f: Ast.FieldLine =>
    //     Ior.Right((CommonAst.Identifier(f.identifier.value), typeToCommon(f.typePath, f.repeat)))

    //   case oneOf: Ast.OneofField =>
    //     val entries = oneOf.entries.map { e =>
    //       val entryId = List(m.name.value, oneOf.identifier.value, e.identifier.value).map(StringUtils.titleCase).mkString

    //       CommonAst.ClassAst(
    //         id = CommonAst.Identifier(entryId),
    //         argLists = NonEmptyList.of(CommonAst.Fields(List((CommonAst.Identifier(e.identifier.value), typeIdentifierToCommon(e.typePath))))),
    //         parents = Nil,
    //       )
    //     }

    //     val oneOfId = List(m.name.value, oneOf.identifier.value).map(StringUtils.titleCase).mkString
    //     val typeId  = CommonAst.Identifier(oneOfId)
    //     val fieldId = CommonAst.Identifier(oneOf.identifier.value)

    //     Ior.Both(
    //       List(CommonAst.ObjectAst(id = typeId, enumEntries = entries.map(Left(_)), definitions = Nil, parents = Nil)), // title case
    //       (fieldId, CommonAst.CustomSimpleTypeIdentifier(Nil, typeId)),
    //     )

    //   case ala: Ast.AstEntity => Ior.Left(toCommon(ala).getOrElse(???))
    // }

    // val args  = objsAndArgs.flatMap(_.right)
    // val inner = objsAndArgs.flatMap(_.left.toList.flatten)

    // NonEmptyList.of(
    //   CommonAst.ClassAst(id = CommonAst.Identifier(m.name.value), argLists = NonEmptyList.of(CommonAst.Fields(args)), parents = Nil),
    //   inner: _*
    // )
    ???
  }

  private def enumToCommon(e: EnumElement): CommonAst.ObjectAst = {
    val defs =
      e.getConstants().asScala.map(contElement => CommonAst.EnumValue(id = CommonAst.Identifier(contElement.getName()), parents = Nil)).toList
    CommonAst.ObjectAst(id = CommonAst.Identifier(e.getName()), enumEntries = defs.map(Right(_)), definitions = Nil, parents = Nil)
  }

  private def typeIdentifierToCommon(t: String) = t match {
    case "int64"  => CommonAst.LongType
    case "int32"  => CommonAst.IntType
    case "double" => CommonAst.DoubleType
    case "bool"   => CommonAst.BooleanType
    case "string" => CommonAst.StringType
    case "bytes"  => CommonAst.ArrayType(CommonAst.ByteType)
    // TODO:bcm  map
    case other    =>
      val customPath = t.split('.').toList

      CommonAst.CustomSimpleTypeIdentifier(packagePath = customPath.init.map(CommonAst.Identifier(_)), CommonAst.Identifier(customPath.last))
  }

}
