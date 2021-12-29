{ lib, buildGraalvmNativeImage, fetchurl }:

buildGraalvmNativeImage rec {
  pname = "scalaproto";
  version = "0.1";

  src = fetchurl {
    url = "https://github.com/JakDar/${pname}/releases/download/${version}/scalaproto.jar";
    sha256 = "sha256-PiRQa4pV+wwk94JYvBKKNonu/U9ivGYug32Q+wmthYQ=";
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
