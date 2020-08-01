package com.github.jakdar.scalaproto

import org.scalatest.flatspec.AnyFlatSpec
import com.softwaremill.diffx.scalatest.DiffMatcher._
import org.scalatest.matchers.should.Matchers

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

}
