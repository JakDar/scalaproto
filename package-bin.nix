{ lib, stdenv, fetchurl }:

stdenv.mkDerivation rec {
  pname = "scalaproto";
  version = "0.3";

  src = fetchurl {
    url = "https://github.com/JakDar/${pname}/releases/download/${version}/${pname}-linux.tar.gz";
    sha256 = "sha256-o3HjKRGVh2dGaXSyjOFTMb4qT/nZfOrPOpqfqRDXCgM=";
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
