# Toolchain

- Host CLI: `/opt/homebrew/bin/clojure`
- Host Java: system Temurin/OpenJDK runtime on this machine
- Native test entry: `clojure -M:test`
- Docker runtime: `eclipse-temurin:21-jdk` plus official Clojure install script `1.12.4.1618`
- Current Docker status: validated locally with build and runtime smokes
- Nix: repo-level `flake.nix` remains the CI-native wrapper
