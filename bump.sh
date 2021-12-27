#!/usr/bin/env bash


# 1. Bump versions
# 3. sbt test
# 4. Assemble jar
# 5. Add scalaproto.jar artifact
# 6. Commit, tag  & push


old_version=$(grep "^version" < build.sbt | sed 's/"//g' | awk -F'.' '{print $2 }')
new_version=$((old_version + 1))


echo "Bumping version $old_version => $new_version"

awk -v ver="$new_version" '/^version/{print "version := \"0." ver "\"" ;next} 1 ' < build.sbt > build.sbt.bak
mv build.sbt.bak build.sbt

echo $old_version $new_version

awk -v old="$old_version" -v new="$new_version" '/^\s+version/{gsub("0." old, "0." new );print ;next} 1 ' < package.nix  > package.nix.bak
mv package.nix.bak package.nix

# git add -A
# git commit -m "Version 0.${new_version}"
# git tag "0.${new_version}"
