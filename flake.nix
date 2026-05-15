{
  description = "clojure-stakeholder deterministic tranche";

  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-25.05";

  outputs = { self, nixpkgs }:
    let
      system = "aarch64-darwin";
      pkgs = import nixpkgs { inherit system; };
    in {
      devShells.${system}.default = pkgs.mkShell {
        packages = [ pkgs.clojure pkgs.temurin-jre-bin ];
      };
      apps.${system}.check = {
        type = "app";
        program = toString (pkgs.writeShellScript "check" ''
          set -euo pipefail
          cd ${self}
          python3 scripts/validate_scaffold.py
          clojure -M:test
        '');
      };
    };
}
