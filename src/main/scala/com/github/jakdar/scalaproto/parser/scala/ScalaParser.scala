package com.github.jakdar.scalaproto.parser.scala

import fastparse._
import fastparse._, ScalaWhitespace._

object ScalaParser extends Core with Types with Exprs {

  def TmplBody[_: P]: P[Unit] = {
    def Prelude  = P((Annot ~ OneNLMax).rep ~ Mod./.rep)
    def TmplStat = P(Prelude ~ BlockDef)

    P("{" ~/ Semis.? ~ TmplStat.repX(sep = NoCut(Semis)) ~ Semis.? ~ `}`)
  }

  def ValVarDef[_: P] = P(BindPattern.rep(1, ","./) ~ (`:` ~/ Type).? ~ (`=` ~/ FreeCtx.Expr).?)

  def BlockDef[_: P]: P[Unit] = P(Dcl | TraitDef | ClsDef | ObjDef)

  def ClsDef[_: P] = {
    def ClsAnnot  = P(`@` ~ SimpleType ~ ArgList.?)
    def Prelude   = P(NotNewline ~ (ClsAnnot.rep(1) ~ AccessMod.? | AccessMod))
    def ClsArgMod = P(Mod.rep ~ (`val` | `var`))
    def ClsArg    = P(Annot.rep ~ ClsArgMod.? ~ Id ~ `:` ~ Type ~ (`=` ~ ExprCtx.Expr).?)

    def ClsArgs = P(OneNLMax ~ "(" ~/ `implicit`.? ~ ClsArg.repTC() ~ ")")
    P(`case`.? ~ `class` ~/ Id ~ TypeArgList.? ~~ Prelude.? ~~ ClsArgs.repX ~ DefTmpl.?)
  }

  def Constrs[_: P]      = P((WL ~ Constr).rep(1, `with`./))
  def EarlyDefTmpl[_: P] = P(TmplBody ~ (`with` ~/ Constr).rep ~ TmplBody.?)
  def NamedTmpl[_: P]    = P(Constrs ~ TmplBody.?)

  def DefTmpl[_: P]  = P((`extends` | `<:`) ~ AnonTmpl | TmplBody)
  def AnonTmpl[_: P] = P(EarlyDefTmpl | NamedTmpl | TmplBody)

  def TraitDef[_: P] = P(`trait` ~/ Id ~ TypeArgList.? ~ DefTmpl.?)

  def ObjDef[_: P]: P[Unit] = P(`case`.? ~ `object` ~/ Id ~ DefTmpl.?)

  def Constr[_: P] = P(AnnotType ~~ (NotNewline ~ ParenArgList).repX)

  def Tmpl[_: P] = P((Annot ~~ OneNLMax).rep ~ Mod.rep ~ (TraitDef | ClsDef | ObjDef))

}
