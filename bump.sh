#!/usr/bin/env bash

# 1. Bump versions
# 3. sbt test assemble
# 4. Nix hash file s
# 5. Add scalaproto.jar artifact
# 6. Update hash
# 6. Commit, tag  & push

if [ "$1" == "--hash" ]; then
	sp_hash=$(nix hash file ./target/scala-3.0.2/scalaproto.jar)
	awk -v hash="$sp_hash" '/^\s+sha256/{print "    sha256 = \"" hash "\";";next} 1 ' <package.nix >package.nix.bak
	mv package.nix.bak package.nix
else
	old_version=$(grep "^version" <build.sbt | sed 's/"//g' | awk -F'.' '{print $2 }')
	new_version=$((old_version + 1))

	awk -v ver="$new_version" '/^version/{print "version := \"0." ver "\"" ;next} 1 ' <build.sbt >build.sbt.bak
	mv build.sbt.bak build.sbt

	awk -v old="$old_version" -v new="$new_version" '/^\s+version/{gsub("0." old, "0." new );print ;next} 1 ' <package.nix >package.nix.bak
	mv package.nix.bak package.nix

	echo "0.${new_version}"
fi

# git add -A
# git commit -m "Version 0.${new_version}"
# git tag "0.${new_version}"
