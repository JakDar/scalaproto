package com.github.jakdar.scalaproto.proto2

import com.squareup.wire.schema.internal.parser.ProtoParser
import com.squareup.wire.schema.Location
import com.squareup.wire.schema.internal.parser.MessageElement
import com.squareup.wire.schema.internal.parser.FieldElement
import com.github.os72.protobuf.dynamic.MessageDefinition

object WireProto2Test extends App {

  {
    val LOCATION = new Location("", "", -1, -1);

    val schema = """

// alusiu
message Ala {
// eloszka1
required string x = 1; // eloszka2
// matrioszka
required bool y = 2;
required ala.ma.kota z = 3;


}

enum Olga{
ILA = 1;
ELA = 2;
}
"""

    val result = new ProtoParser(LOCATION, schema.toArray).readProtoFile();

    val msg = result.getTypes().get(0)
    println(msg)
  }

}
