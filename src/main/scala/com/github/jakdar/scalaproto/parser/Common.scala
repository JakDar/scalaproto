package com.github.jakdar.scalaproto.parser

import fastparse._
import ScalaWhitespace._ // gives us Scala commens
// and whitespaces out-of-the-box

object Common {

  def CommentChunk[_: P]              = P(CharsWhile(c => c != '/' && c != '*') | MultilineComment | !"*/" ~ AnyChar)
  def MultilineComment[_: P]: P[Unit] = P("/*" ~/ CommentChunk.rep() ~ "*/")
  def SameLineCharChunks[_: P]        = P(CharsWhile(c => c != '\n' && c != '\r') | !Newline ~ AnyChar)
  def LineComment[_: P]               = P("//" ~ SameLineCharChunks.rep() ~ &(Newline | End))
  def Comment[_: P]: P[Unit]          = P(MultilineComment | LineComment)
  def WS[_: P]: P[Unit]               = P(NoTrace((WSChars | Comment).rep()))
  def WSChars[_: P]                   = P(NoTrace(CharsWhileIn("\u0020\u0009")))
  def Newline[_: P]                   = P(NoTrace(StringIn("\r\n", "\n")))
  def WL0[_: P]: P[Unit]              = P(ScalaWhitespace.whitespace(P.current))
  def WL[_: P]: P[Unit]               = P(NoCut(WL0))

  def NotNewline[_: P]: P[Unit] = P(&(WS ~ !Newline))

  def Num[_: P] = P(CharIn("0-9").rep().!).map(_.toInt)

  def OneNLMax[_: P]: P[Unit] = {
    def ConsumeComments = P((WSChars.? ~ NoTrace(Comment) ~ WSChars.? ~ Newline).rep())
    P(NoCut(NoTrace(WS ~ Newline.? ~ ConsumeComments ~ NotNewline)))
  }

  def TrailingComma[_: P]: P[Unit] = P(("," ~ WS.? ~ Newline.?).?)

}
