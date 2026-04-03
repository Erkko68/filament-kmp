# Java vs Expect Parity Audit

This tool compares Filament Android Java public API methods to KMP `expect` methods.

## Script

- `filament_api_parity_audit.py`

## What It Checks

- Public Java methods by class (including nested public types when present)
- Kotlin `expect` `fun` declarations by class (including nested classes/objects)
- Parity key: `methodName/arity` (name + parameter count)

## Usage

```bash
python3 /Users/eric/IdeaProjects/filament-kmp-core/kotlin/filament-kmp-api/tools/filament_api_parity_audit.py
```

Optional custom paths:

```bash
python3 /Users/eric/IdeaProjects/filament-kmp-core/kotlin/filament-kmp-api/tools/filament_api_parity_audit.py \
  --java-dir /Users/eric/IdeaProjects/filament-kmp-core/filament-main/android/filament-android/src/main/java/com/google/android/filament \
  --expect-dir /Users/eric/IdeaProjects/filament-kmp-core/kotlin/filament-kmp-api/src/commonMain/kotlin/dev/filament/kmp \
  --out-json /Users/eric/IdeaProjects/filament-kmp-core/kotlin/filament-kmp-api/tools/filament_java_expect_parity_report.json \
  --out-md /Users/eric/IdeaProjects/filament-kmp-core/kotlin/filament-kmp-api/tools/filament_java_expect_parity_report.md
```

## Outputs

- JSON report: `filament_java_expect_parity_report.json`
- Markdown report: `filament_java_expect_parity_report.md`

