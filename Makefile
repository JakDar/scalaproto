build:
	export PATH=/usr/lib/jvm/java-11-graalvm/bin:${PATH}; sbt graalvm-native-image:packageBin

just-install:
	mkdir -p ~/.local/bin
	cp target/graalvm-native-image/com.github.jakdar.scalaproto ~/.local/bin/scalaproto

install: build just-install
