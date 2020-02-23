package com.github.jakdar.scalaproto

import org.scalatest.FlatSpec

class Proto2ParserTest extends FlatSpec {

  "scala to proto" should "work in basic case " in {

    val example = """
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


           enum AlaMakota {
            ALA_MAKOTA = 1;
            OLA_MAPSA = 2;
           }
""".trim()
    print(Application.toScala(example))

  }

}
