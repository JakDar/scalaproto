{ lib, buildGraalvmNativeImage, fetchurl }:

buildGraalvmNativeImage rec {
  pname = "scalaproto";
  version = "0.3";

  src = fetchurl {
    url = "https://github.com/JakDar/${pname}/releases/download/${version}/scalaproto.jar";
    sha256 = "sha256-Kf+Ozl4hI2ZL8cDmk55rfMMZHfKvl6Hvw098Wh2MoLk=";
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
