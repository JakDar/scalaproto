package com.github.jakdar.scalaproto

class ProtoHomomorphismsTest extends munit.FunSuite {

  test("scala to proto should work in basic case ") {

    val example = """
            message Ala{
                required string ala =1 ;


                repeated int32 ola = 5;
                optional int64 ula = 7;
                oneof a {
 string b = 1;
 string c = 102;
}
required string d = 9;
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
          |    oneof a {
          |        string b = 4;
          |        string c = 5;
          |    }
          |    required string d = 6;
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

    val result = Application.protoFixNumbers(example)
    assertEquals(result.trim, expected.trim())

  }

  test("support nested messages") {
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
    assertEquals(result.trim, expected.trim())

  }

}
