# scripts/

Maintenance-time helpers. Not part of consumer runtime — they exist to keep this
repo's bindings in sync with upstream Filament releases and to support local dev workflows.

Getting Filament itself (release downloads, source builds for `wasm`/`windows-arm64`, the Emscripten SDK,
headers) is Gradle's job: the `prebuilts`, `prebuilts_<id>`, `setupEmsdk` and `downloadIncludes` tasks from
[`build-logic/…/buildlogic/prebuilts`](../build-logic/src/main/kotlin/buildlogic/prebuilts).

## `dev/` — manual developer utilities

| Script | Purpose |
| :--- | :--- |
| [`dev/upgrade-diff.sh`](dev/upgrade-diff.sh) | Diffs upstream Filament between two tags across every surface that drives this repo's bindings: public C++ headers, backend headers, Android Java sources, upstream's web build config, material/engine enums, feature-flag defaults, and `RELEASE_NOTES.md`. Opens with a **HIGHLIGHTS** section (MATERIAL_VERSION bump, `CONFIG_MAX_*` changes, feature-flag flips, added/removed Java classes). Tags optional: no args = `filaVersion` → latest upstream release; one arg = `filaVersion` → that tag. Run on every `filaVersion` bump. `--summary` for a file-level overview; omit for full unified diffs. Keeps a shallow clone in `scripts/dev/.filament-src-cache/`. |
| [`dev/rebuild-materials.sh`](dev/rebuild-materials.sh) | Recompiles every committed `.filamat` with the `matc` of the current `filaVersion` (pulled from the release tarball cached by `./gradlew prebuilts`), and syncs the shared `emissive.filamat` copy. Run whenever `MATERIAL_VERSION` changes — the engine rejects blobs built by another version. |
| [`dev/run-tests.sh`](dev/run-tests.sh) | Runs the test suite across every KMP target this repo supports (JVM, JS, iOS simulator, Android). Mirrors what [`.github/workflows/ci.yml`](../.github/workflows/ci.yml) does on CI. Auto-boots the first available AVD if no device is attached when running android tests. Pass `jvm`/`js`/`ios`/`android` (any combination) to scope; or `--no-<target>` to skip one. iOS is skipped automatically off macOS. |
| [`dev/clean_all.sh`](dev/clean_all.sh) | Nukes every Gradle/CMake/Kotlin build directory in the repo. Last-resort cache reset. |

## First-time setup

Nothing to run by hand: every build task fetches the Filament libraries it needs. To pre-fetch:

```sh
./gradlew prebuilts         # every release target + headers
./gradlew prebuilts_wasm    # web: emsdk + Filament's wasm libs, built from source (slow, once per filaVersion)
```

`windows-arm64` is built from source the same way on a Windows on ARM host (MSVC).

## Updating the Filament version

The full end-to-end workflow lives in **[docs/internals/upgrading-filament.md](../docs/internals/upgrading-filament.md)** —
scoping the diff, refreshing prebuilts, the per-layer recipe for adding/removing binding
surface, and verification. The short version:

```sh
scripts/dev/upgrade-diff.sh --summary                 # 1. scope: filaVersion → latest upstream (re-run without --summary on hot areas)
#                                                       2. bump filaVersion in gradle.properties
./gradlew prebuilts prebuilts_wasm                    # 3. refresh prebuilts (version-stamped: redone on bump)
./gradlew generateCApi generateKotlinExternals       # 4. regenerate, review the diff
./gradlew apiGaps                                     #    C++ API missing from the bindings (build/reports/api-gaps.txt)
scripts/dev/rebuild-materials.sh                      #    recompile .filamat when MATERIAL_VERSION changed
#                                                       5. adapt the Kotlin API + update tests (see the doc)
scripts/dev/run-tests.sh                               # 6. verify jvm + js + ios (+ android if attached)
```
