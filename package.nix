{ stdenv, lib, fetchFromGitHub, graalvm11, sbt }:

stdenv.mkDerivation rec{
  pname = "scalaproto";
  version = "0.1";


  src = fetchFromGitHub {
    owner = "JakDar";
    repo = "scalaproto";
    rev = "master"; # or just "master"
    sha256 = "sha256-kV2L9cdTZiWnpiFwjCbeVXbMPtGnLGVumqlalAk12qA=";
  };

  buildInputs = [ sbt graalvm11 ];

  buildPhase = ''
    cd ${src}
    sbt assemble
    cp ${src}/target/scala-3.0.2/scalaproto.jar .


    native-image  \
      -jar ${src}/scalaproto.jar \
      -H:Name=clj-kondo \
      -H:+ReportExceptionStackTraces \
      --initialize-at-build-time  \
      -H:Log=registerResource: \
      --verbose \
      --no-fallback \
      --no-server \
      "-J-Xmx3g"
  '';

  installPhase = ''
    mkdir -p $out/bin
    cp ${src}/scalaproto $out/bin/scalaproto
  '';

  meta = with lib; {
    description = "Scalaproto - converter between json, proto & scalala.";
    homepage = https://github.com/JakDar/scalaproto;
    license = licenses.mit;
    platforms = graalvm11.meta.platforms;
    maintainers = with maintainers; [ ];
  };
}
