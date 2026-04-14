  # Clojure Toolchain

  - State: scaffold-only next-20 prep
  - Toolchain source: `brew`

  ## Planned commands after promotion
    - `brew install clojure`
- `clojure -e '(println (+ 1 2))'`

  ## Scaffold-time checks
  - `python3 scripts/validate_scaffold.py`
  - `/nix/var/nix/profiles/default/bin/nix --extra-experimental-features 'nix-command flakes' flake lock`

  ## Current limitation
  - Requires a Brew install before implementation.
