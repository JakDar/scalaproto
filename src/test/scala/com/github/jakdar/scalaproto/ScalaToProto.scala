package com.github.jakdar.scalaproto

import org.scalatest.FlatSpec
import fastparse._
import fastparse.Parsed
import com.github.jakdar.scalaproto.scala.ScalaParser
import com.github.jakdar.scalaproto.proto2.Proto2Generator

class ScalaToProto extends FlatSpec {

  "scala to proto" should "work in basic case " in {

    val example                   = """
            case class Ala(
                id:Option[String],
            ola:List[Int],
            time :Option[ZonedDateTime]
            )


            case class Ola(
                id: String,
                mamacita: Boolean,
                ola: Option[Int]
            )

""".trim()
    val Parsed.Success(parsed, _) = parse(example, ScalaParser.program(_))
    parsed.map(Proto2Generator.generateClass).foreach(println)

  }

}
