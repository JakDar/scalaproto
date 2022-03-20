package com.github.jakdar.scalaproto.parser

object CommonHomomorphisms {

  // REVIEW: remove knownTypes alltogethe?

  def unknownIdTypesAsString(asts: List[Ast.AstEntity]): List[Ast.AstEntity] = {
    val knownTypes = CommonUtils.declaredCustomSimpleTypes(asts)

    asts.map(unknownIdTypesAsStringForKnown(_)(knownTypes))
  }

  private def unknownIdTypesAsStringForKnown(ast: Ast.AstEntity)(knownTypes: List[Ast.CustomSimpleTypeIdentifier]): Ast.AstEntity =
    ast match {
      case e: Ast.ClassAst  => treatIdAsStrinForClass(e)(knownTypes)
      case e: Ast.ObjectAst => treatIdAsStrinForObj(e)(knownTypes)
    }

  private def mapTypeId(t: Ast.TypeIdentifier)(knownTypes: List[Ast.CustomSimpleTypeIdentifier]): Ast.TypeIdentifier =
    t match {
      case t: Ast.CustomSimpleTypeIdentifier if t.id.value.endsWith("Id") /* && !knownTypes.contains(t)*/ => Ast.StringType

      case t: Ast.HigherTypeIdentifier => CommonUtils.mapHigherTypeIdentifier(t)(mapTypeId(_)(knownTypes))
      case other                       => other
    }

  private def treatIdAsStrinForClass(ast: Ast.ClassAst)(knownTypes: List[Ast.CustomSimpleTypeIdentifier]) =
    ast.copy(argLists = ast.argLists.map(fields => fields.copy(args = fields.args.map { case (id, typeId) => (id, mapTypeId(typeId)(knownTypes)) })))

  private def treatIdAsStrinForObj(ast: Ast.ObjectAst)(knownTypes: List[Ast.CustomSimpleTypeIdentifier]) =
    ast.copy(
      definitions = ast.definitions.map(unknownIdTypesAsStringForKnown(_)(knownTypes)),
      enumEntries = ast.enumEntries.map(_.left.map(treatIdAsStrinForClass(_)(knownTypes))),
    )

}
