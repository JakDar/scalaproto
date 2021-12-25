{ stdenv, lib, graalvm8, sbt }:

stdenv.mkDerivation rec{
  pname = "scalaproto";
  version = "0.1";


  src = lib.fetchFromGitHub {
    owner = "JakDar";
    repo = "scalaproto";
    rev = "master"; # or just "master"
    sha256 = lib.fakeSha256;
  };

  buildInputs = [ sbt graalvm8 ];

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
    platforms = graalvm8.meta.platforms;
    maintainers = with maintainers; [ ];
  };
}
