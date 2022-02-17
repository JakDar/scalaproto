package com.github.jakdar.scalaproto

import com.github.jakdar.scalaproto.proto2.Proto2Parser

class Proto2ToJsonTest extends munit.FunSuite {

  def protoToJson(code: String): Seq[ujson.Obj] = {
    val protoAst = Proto2Parser.parse(code)
    Application.convertAst(protoAst.getOrElse(???), Application.proto2Support, Application.jsonSupport).getOrElse(???)
  }

  test("proto to json should work in basic case ") {
    val example  = """

      message Ala {
          required int32 alaId = 1;
          repeated Ola olas = 2;
      }

      message Ola {
          required string olaId = 1;
          required string text = 2;
          optional bytes tracing = 100;
      }
""".trim()
    val result   = protoToJson(example)
    val expected = ujson
      .read("""
{
  "alaId": 1,
  "olas": [
    {
      "olaId": "string",
      "text": "string",
      "tracing": [
        1
      ]
    }
  ]
}

""".stripMargin)
      .asInstanceOf[ujson.Obj]

    assertEquals(result.head, expected)

  }

  test("proto to json ignores comments") {
    val example  = """

      message Ala {
          // Ala is nice
          required int32 alaId = 1;
          repeated Ola olas = 2;
      }

      message Ola {
          required string olaId = 1;
          required string text = 2;
          optional bytes tracing = 100;
      }
""".trim()
    val result   = protoToJson(example)
    val expected = ujson
      .read("""
{
  "alaId": 1,
  "olas": [
    {
      "olaId": "string",
      "text": "string",
      "tracing": [
        1
      ]
    }
  ]
}

""".stripMargin)
      .asInstanceOf[ujson.Obj]

    assertEquals(result.head, expected)

  }

  test("convert oneofs") {

    val example  = """

      message Ala {
          required int32 alaId = 1;
          oneof ola {
             string ela = 2;
             Kura kura = 3;
          }
      }

      message Kura {
          required string a = 1;
      }
""".trim()
    val result   = protoToJson(example)
    val expected = ujson
      .read("""
          |{
          |  "alaId": 1,
          |  "ela": "string",
          |  "kura": {
          |    "a": "string"
          |  }
          |}""".stripMargin)
      .asInstanceOf[ujson.Obj]

    assertEquals(result.head, expected)
  }

  test("handle enums 1") {
    val example = """
      |message Test {
      |    optional int64 time = 5;
      |    required MsgFormat format = 6;
      |    enum MsgFormat {
      |        FORMAT_A = 1;
      |        FORMAT_B = 2;
      |    }
      |}""".stripMargin.trim

    val result   = protoToJson(example)
    val expected = ujson
      .read("""
          |{
          |  "time": 1,
          |  "format": "FORMAT_A"
          |}""".stripMargin)
      .asInstanceOf[ujson.Obj]

    assertEquals(result.head, expected)

  }

  test("handle enums 2") {

    val example = """
      |message Test {
      |    optional int64 time = 5;
      |    required MsgFormat format = 6;
      |}
      |
      |    enum MsgFormat {
      |        FORMAT_A = 1;
      |        FORMAT_B = 2;
      |}""".stripMargin.trim

    val result   = protoToJson(example)
    val expected = ujson
      .read("""
          |{
          |  "time": 1,
          |  "format": "FORMAT_A"
          |}""".stripMargin)
      .asInstanceOf[ujson.Obj]

    assertEquals(result.head, expected)
  }

}
