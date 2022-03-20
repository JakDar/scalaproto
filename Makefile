build:
	sbt assembly


just-install-jar-locally:
	mkdir -p ~/.local/usr/share/scalaproto
	cp ./target/scala-3.0.2/scalaproto.jar ~/.local/usr/share/scalaproto/scalaproto.jar

	printf '#!/usr/bin/env sh\njava -jar ~/.local/usr/share/scalaproto/scalaproto.jar "$$@"' > ~/.local/bin/scalaproto
	chmod u+x ~/.local/bin/scalaproto

install-jar-locally: build just-install

release:
	gh workflow run release.yml

steward:
	gh workflow run steward.yml

test:
	time scalaproto "scala-to-json" "case class Entity(id: Long, name: String)"
	time scalaproto "scala-to-proto2" "case class Entity(id: Long, name: String)"
	time scalaproto "proto2-to-scala" "message Entity{ required string ala = 1;}"
	time scalaproto "proto2-to-json" "message Entity{ required string ala = 1;}"
	time scalaproto "auto-to-json" "message Entity{ required string ala = 1;}"
