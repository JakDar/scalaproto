package com.github.jakdar.scalaproto

object Main extends App {

  val mode = args(0)
  val code = args(1).trim()

  mode match {
    case "to-proto" =>
      print(Application.scalaToProto(code))
    case "to-scala" =>
      print(Application.protoToScala(code))

    case "fix-proto-numbers" =>
      print(Application.protoFixNumbers(code))
  }

}
