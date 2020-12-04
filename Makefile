build-native:
	export PATH=/usr/lib/jvm/java-11-graalvm/bin:${PATH};export JAVA_PATH=/usr/lib/jvm/java-11-graalvm; sbt graalvm-native-image:packageBin

just-install-native:
	mkdir -p ~/.local/bin
	cp target/graalvm-native-image/com.github.jakdar.scalaproto ~/.local/bin/scalaproto

install-native: build-native just-install-native

build:
	export PATH=/usr/lib/jvm/java-11-graalvm/bin:${PATH};export JAVA_PATH=/usr/lib/jvm/java-11-graalvm; sbt graalvm-native-image:packageBin

just-install:
	mkdir -p ~/.local/bin
	cp target/graalvm-native-image/com.github.jakdar.scalaproto ~/.local/bin/scalaproto

install: build just-install

test:
	scalaproto "to-proto" "case class Entity(id: Long, name: String)"
