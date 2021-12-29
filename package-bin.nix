{ lib, stdenv, fetchurl}:

stdenv.mkDerivation rec {
  pname = "scalaproto";
  version = "0.1";
  src = fetchurl {
    url = "https://github.com/JakDar/${pname}/releases/download/${version}/${pname}-linux";
    sha256 = "sha256-z3cenpfEIG8vJYtkoqcIu2rAyPnEVDWrUMvvbB8pYxM=";
  };


  dontUnpack = true;
  # issues if zap tries to copy config on it's own.
  installPhase = ''
    mkdir -p "$out/bin"
    cp $src $out/bin/${pname}
    chmod u+x  "$out/bin/${pname}"
  '';

  meta = with lib; {
    description = "Scalaproto";
    homepage = "https://github.com/JakDar/scalaproto";
    license = licenses.mit;
    maintainers = with maintainers; [ ];
  };
}
