# scripts/

Build- and maintenance-time helpers. Most are Python 3; one bash. None are part of consumer
runtime — they exist to keep this repo's bindings in sync with upstream Filament releases
and to support local dev workflows.

| Script | Purpose |
| :--- | :--- |
| [`download_filament_prebuilts.py`](download_filament_prebuilts.py) | Downloads the official Filament prebuilt static libraries (or Filament.js + WASM for `web`) for one or more targets into `prebuilts/<target>/`. Invoked by every `downloadPrebuilts_<target>` Gradle task in [build.gradle.kts](../build.gradle.kts). |
| [`download_filament_includes.py`](download_filament_includes.py) | Downloads Filament's public C/C++ headers from the GitHub source tarball into `include/`. Headers must match the prebuilt ABI exactly — version drift causes runtime crashes. Invoked by the `downloadIncludes` Gradle task. |
| [`download_filament.py`](download_filament.py) | Downloads the Android Java API sources for a given Filament version into `scripts/filament-<version>/`. Used by `check_coverage.py`; not part of the build. |
| [`check_coverage.py`](check_coverage.py) | Diffs the Android Java public API against this repo's `commonMain` `expect` declarations and prints what's missing. Output drives [`API_COVERAGE_ROADMAP.md`](API_COVERAGE_ROADMAP.md). |
| [`diff_versions.py`](diff_versions.py) | Diffs the Java public API between two Filament versions. Useful when bumping `filaVersion` to see what upstream changed. |
| [`clean_all.sh`](clean_all.sh) | Nukes every Gradle/CMake/Kotlin build directory in the repo. Last-resort cache reset. |

## Python requirements

All scripts use only the stdlib — no `pip install` step. They expect `python3` on PATH; you
can override via the `PYTHON` env var (the Gradle tasks honor it).

## Updating the Filament version

1. Bump `filaVersion` in [gradle.properties](../gradle.properties).
2. Delete the matching `prebuilts/*/` directories so they re-download.
3. Run `./gradlew downloadPrebuilts` to refresh natives and headers.
4. Optionally run `python3 scripts/diff_versions.py <old> <new>` to see what API changed
   upstream, then update `commonMain` expects accordingly.
