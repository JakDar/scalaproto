package com.github.jakdar.scalaproto

import org.scalatest.flatspec.AnyFlatSpec
import com.softwaremill.diffx.scalatest.DiffMatcher._
import org.scalatest.matchers.should.Matchers
import com.github.jakdar.scalaproto.proto2.Proto2Parser

class ProtoHomomorphismsTest extends AnyFlatSpec with Matchers {

  "scala to proto" should "work in basic case " in {

    val example = """
            message Ala{
                required string ala =1 ;


                repeated int32 ola = 5;
                optional int64 ula = 7;
                optional bytes tracing = 4;
            }



            message Ola {
                optional string ala = 7 ;
                repeated int32 ola = 555;
                optional bool ula = 45;
            }


           enum AlaMakota {
            ALA_MAKOTA = 44;
            OLA_MAPSA = 22;
           }
""".trim()

    val expected = s"""
          |message Ala {
          |    required string ala = 1;
          |    repeated int32 ola = 2;
          |    optional int64 ula = 3;
          |    optional bytes tracing = 100;
          |}
          |
          |
          |message Ola {
          |    optional string ala = 1;
          |    repeated int32 ola = 2;
          |    optional bool ula = 3;
          |}
          |
          |
          |enum AlaMakota {
          |    ALA_MAKOTA = 1;
          |    OLA_MAPSA = 2;
          |}""".stripMargin

    // def diff(a: String, b: String) = {
    //   Diff[String].apply(a, b) match {
    //     case c: DiffResultDifferent =>
    //       val diff = c.show.split('\n').map(x => s"<$x>").mkString(Console.RESET, s"${Console.RESET}\n${Console.RESET}", Console.RESET)
    //       print(diff)
    //     case _ => ()
    //   }
    // }

    val result = Application.protoFixNumbers(example)
    result.trim() should matchTo(expected.trim())

  }

  it should "support nested messages" in {
    val example = """
            message Ala{
                required string ala =1 ;


                repeated int32 ola = 5;
                message Ula {
                   required string a = 3;
                   required string b = 10;
                }
                optional Ula ula = 7;
                enum Cola {
                   ALAN = 3;
                   OLAN = 1;
                }
                optional bytes tracing = 4;
            }



            message Ola {
                optional string ala = 7 ;
                repeated int32 ola = 555;
                optional bool ula = 45;
            }


           enum AlaMakota {
            ALA_MAKOTA = 44;
            OLA_MAPSA = 22;
           }
""".trim()

    val expected = s"""
          |message Ala {
          |    required string ala = 1;
          |    repeated int32 ola = 2;
          |    optional Ula ula = 3;
          |    optional bytes tracing = 100;
          |
          |
          |message Ula {
          |    required string a = 1;
          |    required string b = 2;
          |}
          |
          |
          |enum Cola {
          |    ALAN = 1;
          |    OLAN = 2;
          |}
          |
          |}
          |
          |
          |message Ola {
          |    optional string ala = 1;
          |    repeated int32 ola = 2;
          |    optional bool ula = 3;
          |}
          |
          |
          |enum AlaMakota {
          |    ALA_MAKOTA = 1;
          |    OLA_MAPSA = 2;
          |}""".stripMargin

    val result = Application.protoFixNumbers(example)
    result.trim() should matchTo(expected.trim())

  }

}
