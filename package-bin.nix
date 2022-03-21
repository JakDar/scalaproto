{ lib, stdenv, fetchurl }:

stdenv.mkDerivation rec {
  pname = "scalaproto";
  version = "0.4";

  src = fetchurl {
    url = "https://github.com/JakDar/${pname}/releases/download/${version}/${pname}-linux.tar.gz";
    sha256 = "sha256-BgbKp8HYpNS1/zuqaX7E4U/2ug5j6u1PMQV8/9OkpO4=";
  };

  installPhase = ''
    mkdir -p "$out/bin"
    cp ./${pname}-linux $out/bin/${pname}
    chmod u+x  "$out/bin/${pname}"
  '';

  meta = with lib; {
    description = "Scalaproto";
    homepage = "https://github.com/JakDar/scalaproto";
    license = licenses.mit;
    maintainers = with maintainers; [ ];
  };
}
