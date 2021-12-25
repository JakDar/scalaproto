#!/usr/bin/env bash

update_hash(){
	file_hash="$1"
	src="$2"

	awk -v hash="$file_hash" '/^\s+sha256/{print "    sha256 = \"" hash "\";";next} 1 ' <"$src" >"${src}.bak"
	mv "${src}.bak" "$src"
}

update_nix_version(){
	old_version="$1"
	new_version="$2"
	src="$3"

	awk -v old="$old_version" -v new="$new_version" '/^\s+version/{gsub("0." old, "0." new );print ;next} 1 ' <"$src" >"${src}.bak"
	mv "${src}.bak" "$src"
}

if [ "$1" == "--hash" ]; then
	jar_hash=$(nix hash file ./scalaproto.jar)
	update_hash "$jar_hash" package.nix

	linux_hash=$(nix hash file ./scalaproto-linux)
	update_hash "$linux_hash" package-bin.nix
else
	old_version=$(grep "^version" <build.sbt | sed 's/"//g' | awk -F'.' '{print $2 }')
	new_version=$((old_version + 1))

	awk -v ver="$new_version" '/^version/{print "version := \"0." ver "\"" ;next} 1 ' <build.sbt >build.sbt.bak
	mv build.sbt.bak build.sbt

	update_nix_version "$old_version" "$new_version" package.nix
	update_nix_version "$old_version" "$new_version" package-bin.nix

	echo "0.${new_version}"
fi
