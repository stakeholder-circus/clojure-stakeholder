# clojure-stakeholder

Publication-held local rewrite repo for the Clojure tranche in the stakeholder parity program.

## Current tranche
- full deterministic `classic-six + modern-core`
- grouped fallback coverage for later generator families
- native + Docker validation
- `--list-values`, deterministic same-seed JSON, and explicit `--experimental-provider` fail-fast

## Toolchain
- host runtime: Homebrew Clojure CLI `1.12.4.1618`
- JSON encoder: Cheshire
- tests: `clojure.test`
- Docker gate: Temurin 21 + official Clojure install script

## Commands
- `python3 scripts/validate_scaffold.py`
- `clojure -M:test`
- `clojure -M -m stakeholder.core --list-values`
- `clojure -M -m stakeholder.core --output-format json --focus-family code_analyzer --seed 123`
- `docker build -t clojure-stakeholder .`
