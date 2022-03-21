{ lib, buildGraalvmNativeImage, fetchurl }:

buildGraalvmNativeImage rec {
  pname = "scalaproto";
  version = "0.4";

  src = fetchurl {
    url = "https://github.com/JakDar/${pname}/releases/download/${version}/scalaproto.jar";
    sha256 = "sha256-slQ0Fg8na8RDhy6MQwhb0UdnXFw2RVhCogqvEZIEpe8=";
  };

  extraNativeImageBuildArgs = [
    "-H:+ReportExceptionStackTraces"
    "--initialize-at-build-time "
    "-H:Log=registerResource:"
    "--verbose"
    "--no-fallback"
    "--no-server"
  ];

  meta = with lib; {
    description = "Scalaproto";
    homepage = "https://github.com/JakDar/scalaproto";
    license = licenses.mit;
    maintainers = with maintainers; [ ];
  };
}
