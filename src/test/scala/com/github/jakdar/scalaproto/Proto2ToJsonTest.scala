package com.github.jakdar.scalaproto

import org.scalatest.flatspec.AnyFlatSpec
import com.softwaremill.diffx.scalatest.DiffMatcher._
import org.scalatest.matchers.should.Matchers
import com.github.jakdar.scalaproto.proto2.Proto2Parser

class Proto2ToJsonTest extends AnyFlatSpec with Matchers {

  def protoToJson(code: String): Seq[ujson.Obj] = {
    val protoAst = Proto2Parser.parse(code)
    Application.convertAst(protoAst.getOrElse(???), Application.proto2Support, Application.jsonSupport).getOrElse(???)
  }

  "proto to json" should "work in basic case " in {

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

    result.head shouldBe expected

  }

  it should "convert oneofs" in {

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

    result.head shouldBe expected

  }

}
