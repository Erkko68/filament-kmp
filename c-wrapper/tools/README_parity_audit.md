# Filament Parity Audit Script

`filament_parity_audit.py` compares the C++ Filament prebuilt headers against C wrapper headers and emits:

- Missing wrapper headers (`filament-prebuilts/include` vs `c-wrapper/include` recursively)
- For shared headers, likely missing/unmatched methods (heuristic)

## Run

```bash
python3 /Users/eric/IdeaProjects/filament-kmp-core/c-wrapper/tools/filament_parity_audit.py
```

## Outputs

- `/Users/eric/IdeaProjects/filament-kmp-core/c-wrapper/tools/filament_parity_report.json`
- `/Users/eric/IdeaProjects/filament-kmp-core/c-wrapper/tools/filament_parity_report.md`

## Notes

- Method matching is heuristic: C++ method names are matched against `Fila*` function names by token sequence.
- This report is a triage aid, not a strict ABI proof.
- For exact parity planning, use this report plus manual review of high-value headers first (for example `filament/Engine.h`, `filament/View.h`, `filament/Renderer.h`, `filament/Scene.h`, `filament/Material.h`).
- The script intentionally excludes deferred header families (for example `backend/platforms/*` and tooling-only upstream modules) from the "missing headers" count.
- Methods marked upstream as deprecated (via `UTILS_DEPRECATED` or `@deprecated`) are ignored in gap calculations.
- A small per-header false-positive ignore list is applied for known heuristic noise tokens.

