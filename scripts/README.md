# scripts/

Build- and maintenance-time helpers. Not part of consumer runtime — they exist to keep this
repo's bindings in sync with upstream Filament releases and to support local dev workflows.

Two categories:

- **`gradle/`** — build-time Python helpers (stdlib only, no `pip`). Cross-platform so they run on
  any developer's machine, Windows included.
- **`dev/`** — manual developer utilities (upgrade + maintenance workflows). Bash, by convention.

## `gradle/` — build-time Python helpers

| Script | Purpose |
| :--- | :--- |
| [`gradle/download_filament_prebuilts.py`](gradle/download_filament_prebuilts.py) | Downloads the official Filament prebuilt static libraries (or Filament.js + WASM + `filament.d.ts` for `web`) for one or more targets into `prebuilts/<target>/`. Invoked by every `downloadPrebuilts_<target>` Gradle task in [build.gradle.kts](../build.gradle.kts). Python because it does xcframework-slice extraction, SSL with Windows cert store, and multi-target dispatch — would be markedly uglier in bash. The web `filament.d.ts` drives the `:js` module's Karakum-generated externals. |
| [`gradle/download_filament_includes.py`](gradle/download_filament_includes.py) | Downloads Filament's public C/C++ headers from the GitHub source tarball into `include/`. Required by both the native (cinterop) and the combined FFM builds — see [c/CMakeLists.txt](../c/CMakeLists.txt). Headers must match the prebuilt ABI exactly — version drift causes runtime crashes. Invoked by the `downloadIncludes` Gradle task. |
| [`gradle/download_jextract.py`](gradle/download_jextract.py) | **One-time setup.** Installs the jextract tool used to generate the JVM/Panama (FFM) bindings into `.gradle/jextract/jextract-<major>/`. The Gradle build does **not** auto-download it — it expects the binary to already be present and fails with this hint otherwise. Run once after cloning (and again only when the pinned version in [buildSrc/FilamentJvmNative.kt](../buildSrc/src/main/kotlin/FilamentJvmNative.kt) changes). CI invokes it before building any JVM target. |

These use only the Python stdlib — no `pip install`. They expect `python3` on PATH; override via the
`PYTHON` env var (the Gradle tasks honor it).

## `dev/` — manual developer utilities

| Script | Purpose |
| :--- | :--- |
| [`dev/upgrade-diff.sh`](dev/upgrade-diff.sh) | Diffs upstream Filament between two tags across every surface that drives this repo's bindings: public C++ headers, backend headers, Android Java sources, **web JS bindings (`jsbindings.cpp`, not just `filament.d.ts`)**, material/engine enums, feature-flag defaults, and `RELEASE_NOTES.md`. Run on every `filaVersion` bump. `--summary` for a file-level overview; omit for full unified diffs. Keeps a shallow clone in `scripts/.filament-src-cache/`. |
| [`dev/check-js-bindings.sh`](dev/check-js-bindings.sh) | Cross-checks the JS bindings registered in `web/filament-js/jsbindings.cpp` against the *declared* externals surface — the upstream `filament.d.ts` plus the curated overlay/overrides under `js/patches/` (Karakum generates the Kotlin externals from these) — and the actuals in `kotlin/*/src/jsMain/`. Prints (1) bindings present upstream but absent from the d.ts + patches (actionable — add them to `filament.patch.d.ts`), (2) bindings declared but unused (informational), and (3) JS-shaped calls in `*.js.kt` that match no binding (likely typos). Catches the class of bug where `filament.d.ts` is incomplete and stubs end up returning placeholder values. Run on every `filaVersion` bump alongside `upgrade-diff.sh`. |
| [`dev/check-common-api.sh`](dev/check-common-api.sh) | Cross-checks the public Filament **Android** Java API against this repo's `commonMain` `expect` declarations. Filament's Android API is the canonical Kotlin public surface; KMP common should mirror it (modulo property accessors and Android-only types). Reports public methods present upstream but missing from common, per module (`filament` / `filamat` / `gltfio` / `filament-utils`). Property-bridged (`getFoo` ↔ `foo`) and JNI plumbing (`n*`, `nativeObject`, etc.) are auto-skipped; Android-only classes (`DisplayHelper`, `UiHelper`, `Stream`, `AutomationEngine`, etc.) are in a skip-list at the top of the script. Run on every `filaVersion` bump after `check-js-bindings.sh`. |
| [`dev/run-tests.sh`](dev/run-tests.sh) | Runs the test suite across every KMP target this repo supports (JVM, JS, iOS simulator, Android). Mirrors what [`.github/workflows/ci.yml`](../.github/workflows/ci.yml) does on CI. Auto-boots the first available AVD if no device is attached when running android tests. Pass `jvm`/`js`/`ios`/`android` (any combination) to scope; or `--no-<target>` to skip one. iOS is skipped automatically off macOS. |
| [`dev/clean_all.sh`](dev/clean_all.sh) | Nukes every Gradle/CMake/Kotlin build directory in the repo. Last-resort cache reset. |

## First-time setup

```sh
python3 scripts/gradle/download_jextract.py   # install jextract (needed to build the JVM/FFM bindings)
./gradlew downloadPrebuilts                    # fetch Filament natives + headers
```

## Updating the Filament version

The full end-to-end workflow lives in **[docs/upgrading-filament.md](../docs/upgrading-filament.md)** —
scoping the diff, refreshing prebuilts, the per-layer recipe for adding/removing binding
surface, and verification. The short version:

```sh
scripts/dev/upgrade-diff.sh v<old> v<new> --summary   # 1. scope (re-run without --summary on hot areas)
#                                                       2. bump filaVersion in gradle.properties
find prebuilts -mindepth 2 -maxdepth 2 -type d -name lib -exec rm -rf {} +
rm -rf prebuilts/web && ./gradlew downloadPrebuilts   # 3. refresh prebuilts (libs are NOT version-aware!)
scripts/dev/check-common-api.sh                       # 4. Android API methods missing from commonMain
scripts/dev/check-js-bindings.sh                       #    jsbindings.cpp methods missing from the JS overlay
#                                                       5. apply changes + update tests (see the doc)
scripts/dev/run-tests.sh                               # 6. verify jvm + js + ios (+ android if attached)
```

> [!WARNING]
> `downloadPrebuilts_<target>` is `upToDateWhen { the output dir exists }` — **not**
> version-aware. Bumping `filaVersion` refreshes headers (`downloadIncludes` is stamped) but
> leaves libraries stale, causing header/lib mismatch link errors. Always delete
> `prebuilts/*/lib` + `prebuilts/web` first (step 3 above).
