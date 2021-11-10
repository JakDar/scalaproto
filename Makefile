build-native:
	export PATH=/usr/lib/jvm/java-11-graalvm/bin:${PATH};export JAVA_PATH=/usr/lib/jvm/java-11-graalvm; sbt graalvm-native-image:packageBin

just-install-native:
	mkdir -p ~/.local/bin
	cp target/graalvm-native-image/com.github.jakdar.scalaproto ~/.local/bin/scalaproto

install-native: build-native just-install-native

build:
	sbt assembly


just-install:
	mkdir -p ~/.local/usr/share/scalaproto
	cp ./target/scala-3.0.2/scalaproto.jar ~/.local/usr/share/scalaproto/scalaproto.jar

	printf '#!/usr/bin/env sh\njava -jar ~/.local/usr/share/scalaproto/scalaproto.jar "$$@"' > ~/.local/bin/scalaproto
	chmod u+x ~/.local/bin/scalaproto

install: build just-install

test:
	scalaproto "scala-to-json" "case class Entity(id: Long, name: String)"
	scalaproto "scala-to-proto2" "case class Entity(id: Long, name: String)"
	scalaproto "proto2-to-scala" "message Entity{ required string ala = 1;}"
	scalaproto "proto2-to-json" "message Entity{ required string ala = 1;}"
