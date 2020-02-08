package com.github.jakdar.scalaproto

import org.scalatest.FlatSpec
import fastparse._
import fastparse.Parsed
import com.github.jakdar.scalaproto.proto2.Proto2Parser
import com.github.jakdar.scalaproto.scala.ScalaGenerator

class Proto2ParserTest extends FlatSpec {

  "scala to proto" should "work in basic case " in {

    val example                   = """
            message Ala{
                required string ala =1 ;
                repeated int32 ola = 2;
                optional int64 ula = 3;
            }



            message Ola {
                optional string ala =1 ;
                repeated int32 ola = 2;
                optional bool ula = 3;
            }
""".trim()
    val Parsed.Success(parsed, _) = parse(example, Proto2Parser.program(_))
    parsed.map(ScalaGenerator.generateClass).foreach(println)

  }

}
