package com.github.jakdar.scalaproto

@scala.main
def main(mode: String, code: String) = {

  def support(s: String) = s match {
    case "json"   => Application.jsonSupport
    case "scala"  => Application.scalaSupport
    case "proto2" => Application.proto2Support
    case other    => throw new IllegalArgumentException(s"Lang $other not supported")
  }

  val from: String = mode.split("-").head
  val to: String   = mode.split("-").last

  mode match {
    case "fix-proto-numbers" =>
      print(Application.protoFixNumbers(code.trim))

    case other if other.contains("-to-") =>
      print(Application.convert(code.trim, support(from), support(to)))
  }

}
