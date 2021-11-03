package com.github.jakdar.scalaproto

object Main extends App {

  val mode: String = args(0)
  val code: String = args(1).trim()

  def support(s: String): Application.ConversionSupport[_ <: Object] = s match {
    case "json"   => Application.jsonSupport
    case "scala"  => Application.scalaSupport
    case "proto2" => Application.proto2Support
    case other    => throw new IllegalArgumentException(s"Lang $other not supported")
  }

  val from: String = mode.split("-").head
  val to: String   = mode.split("-").last

  mode match {
    case "fix-proto-numbers" =>
      print(Application.protoFixNumbers(code))

    case other if other.contains("-to-") =>
      print(Application.convert(code, support(from), support(to)))
  }

}
