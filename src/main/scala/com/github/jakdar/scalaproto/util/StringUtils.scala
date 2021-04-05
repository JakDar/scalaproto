package com.github.jakdar.scalaproto.util

object StringUtils {

  def titleCase(s: String) = s.toList match {
    case head :: tail => (head.toUpper :: tail.map(_.toLower)).mkString
    case Nil          => ""
  }

  def titleToPascal(s: String) = s.toList match {
    case h :: tail => (h.toLower :: tail).mkString
    case Nil       => ""
  }
}
