# scripts/

Maintenance-time helpers. Not part of consumer runtime — they exist to keep this
repo's bindings in sync with upstream Filament releases and to support local dev workflows.

All download logic (Filament prebuilts, headers, jextract) lives in pure-JVM Gradle tasks —
see [build-logic/src/main/kotlin/FilamentDownloads.kt](../build-logic/src/main/kotlin/FilamentDownloads.kt)
and the `downloadPrebuilts*` / `downloadIncludes` / `downloadJextract` tasks. No Python needed.

## `dev/` — manual developer utilities

| Script | Purpose |
| :--- | :--- |
| [`dev/upgrade-diff.sh`](dev/upgrade-diff.sh) | Diffs upstream Filament between two tags across every surface that drives this repo's bindings: public C++ headers, backend headers, Android Java sources, **web JS bindings (`jsbindings.cpp`, not just `filament.d.ts`)**, material/engine enums, feature-flag defaults, and `RELEASE_NOTES.md`. Opens with a **HIGHLIGHTS** section (MATERIAL_VERSION bump, `CONFIG_MAX_*` changes, feature-flag flips, added/removed Java classes and JS bindings). Tags optional: no args = `filaVersion` → latest upstream release; one arg = `filaVersion` → that tag. Run on every `filaVersion` bump. `--summary` for a file-level overview; omit for full unified diffs. Keeps a shallow clone in `scripts/dev/.filament-src-cache/`. |
| [`dev/check-js-bindings.sh`](dev/check-js-bindings.sh) | Cross-checks the JS bindings registered in `web/filament-js/jsbindings.cpp` against the *declared* externals surface — the committed declarations under `web/src/webMain` — and the actuals in `kotlin/*/src/webMain/`. Prints (1) bindings present upstream but absent from the externals (actionable — declare them in `web/src/webMain`), (2) bindings declared but unused (informational), and (3) JS-shaped calls in `*.web.kt` that match no binding (likely typos). Catches the class of bug where a binding exists upstream but a Kotlin stub returns a placeholder value. Run on every `filaVersion` bump alongside `upgrade-diff.sh`. |
| [`dev/check-common-api.sh`](dev/check-common-api.sh) | Cross-checks the public Filament **Android** Java API against this repo's `commonMain` `expect` declarations. Filament's Android API is the canonical Kotlin public surface; KMP common should mirror it (modulo property accessors and Android-only types). Checks five kinds of surface per module (`filament` / `filamat` / `gltfio` / `filament-utils`): whole **classes**, **nested types**, **enum/ALL_CAPS constants**, **methods**, and **fields** — the last of these matters because Filament's option structs (`ShadowOptions`, `FogOptions`, `Engine.Config`, …) expose their state as bare fields rather than accessors — with Kotlin comments stripped, so KDoc mentions don't count as coverage. Property-bridged (`getFoo`/`setFoo`/`isFoo` ↔ `foo`/`isFoo`/`isFooEnabled`) and JNI plumbing are auto-skipped, as are Filament's `mFoo`/`sFoo` internal field conventions; upstream-`@Deprecated` members are flagged informationally. Intentional gaps go in [`dev/check-common-api-ignores.txt`](dev/check-common-api-ignores.txt) (`Class` or `Class.member` + a comment why). Exits non-zero on unsuppressed gaps (CI-able). Run on every `filaVersion` bump after `check-js-bindings.sh`. |
| [`dev/run-tests.sh`](dev/run-tests.sh) | Runs the test suite across every KMP target this repo supports (JVM, JS, iOS simulator, Android). Mirrors what [`.github/workflows/ci.yml`](../.github/workflows/ci.yml) does on CI. Auto-boots the first available AVD if no device is attached when running android tests. Pass `jvm`/`js`/`ios`/`android` (any combination) to scope; or `--no-<target>` to skip one. iOS is skipped automatically off macOS. |
| [`dev/clean_all.sh`](dev/clean_all.sh) | Nukes every Gradle/CMake/Kotlin build directory in the repo. Last-resort cache reset. |

## First-time setup

```sh
./gradlew downloadPrebuilts                    # fetch Filament natives + headers
```

jextract (for the JVM/FFM bindings) and the web prebuilts download automatically as Gradle
task dependencies — no manual step needed. To pre-fetch jextract explicitly (e.g. before
going offline), run `./gradlew downloadJextract`.

## Updating the Filament version

The full end-to-end workflow lives in **[docs/upgrading-filament.md](../docs/upgrading-filament.md)** —
scoping the diff, refreshing prebuilts, the per-layer recipe for adding/removing binding
surface, and verification. The short version:

```sh
scripts/dev/upgrade-diff.sh --summary                 # 1. scope: filaVersion → latest upstream (re-run without --summary on hot areas)
#                                                       2. bump filaVersion in gradle.properties
./gradlew downloadPrebuilts                           # 3. refresh prebuilts (version-stamped: re-extracts on bump)
scripts/dev/check-common-api.sh                       # 4. Android API members missing from commonMain
scripts/dev/check-js-bindings.sh                       #    jsbindings.cpp methods missing from the JS overlay
#                                                       5. apply changes + update tests (see the doc)
scripts/dev/run-tests.sh                               # 6. verify jvm + js + ios (+ android if attached)
```
