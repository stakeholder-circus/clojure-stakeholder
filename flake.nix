{
  description = "clojure-stakeholder deterministic tranche";

  inputs.nixpkgs.url = "github:NixOS/nixpkgs/nixos-25.05";

  outputs = { self, nixpkgs }:
    let
      systems = [ "aarch64-darwin" "x86_64-darwin" "aarch64-linux" "x86_64-linux" ];
      forAllSystems = nixpkgs.lib.genAttrs systems;
      pkgsFor = system: import nixpkgs { inherit system; };
    in {
      devShells = forAllSystems (system:
        let pkgs = pkgsFor system;
        in {
          default = pkgs.mkShell {
            packages = [ pkgs.clojure pkgs.clj-kondo pkgs.python3 pkgs.temurin-jre-bin ];
          };
        });
      apps = forAllSystems (system:
        let pkgs = pkgsFor system;
        in {
          check = {
            type = "app";
            program = "${pkgs.writeShellApplication {
              name = "check";
              runtimeInputs = [ pkgs.clojure pkgs.clj-kondo pkgs.python3 pkgs.temurin-jre-bin ];
              text = ''
                cd ${self}
                python3 scripts/validate_scaffold.py
                clj-kondo --lint src test
                clojure -M:test
              '';
            }}/bin/check";
          };
        });
    };
}
