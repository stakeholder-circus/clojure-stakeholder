#!/usr/bin/env python3
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
REQUIRED = [
    ROOT / 'deps.edn',
    ROOT / 'src' / 'stakeholder' / 'catalog.clj',
    ROOT / 'src' / 'stakeholder' / 'core.clj',
    ROOT / 'test' / 'stakeholder' / 'core_test.clj',
    ROOT / 'Dockerfile',
    ROOT / '.github' / 'workflows' / 'ci-native.yml',
    ROOT / '.github' / 'workflows' / 'docker-smoke.yml',
    ROOT / 'README.md',
    ROOT / 'STATUS.md',
    ROOT / 'GAPS.md',
    ROOT / 'docs' / 'toolchain.md',
    ROOT / 'docs' / 'traceability' / 'first-push-families.md',
]
missing = [str(path.relative_to(ROOT)) for path in REQUIRED if not path.exists()]
if missing:
    raise SystemExit('missing required files: ' + ', '.join(missing))
print('clojure tranche validated')
