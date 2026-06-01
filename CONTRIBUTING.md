# Contributing to Filament KMP

Thanks for helping out! Filament KMP is a **Kotlin Multiplatform wrapper** around
[Google Filament](https://github.com/google/filament), with first-class Compose
Multiplatform support across Android, iOS, JVM/Desktop and Web. This guide covers how the
project is built, where a given problem belongs, and how to get changes merged.

## Wrapper philosophy — is this our bug or Filament's?

The single most useful thing you can do before filing an issue or PR is to figure out
**whether the problem is in this wrapper or in Filament itself.**

- **Wrapper bugs** live here: missing/incorrect Kotlin bindings (cinterop / JNI / FFM /
  Kotlin-JS externals), Gradle/prebuilt plumbing, Compose integration, samples, packaging.
- **Engine bugs** live in [google/filament](https://github.com/google/filament): rendering
  artifacts, backend (GL/Metal/Vulkan/WebGPU) errors, shader/material issues, crashes inside
  the native engine. These reproduce independently of Kotlin.

A good rule of thumb: if the same symptom would happen from C++/JS using Filament directly,
it's an engine issue — report it upstream and link it here with the **`upstream-filament`**
label so we can track it (and patch our prebuilts if needed). See the WebGL "uniform buffer
too small" case ([google/filament#10077](https://github.com/google/filament/pull/10077)) for
a worked example of diagnosing, patching, and upstreaming an engine bug from this repo.

## Project layout

| Path | What it is |
|---|---|
| `kotlin/*` | The published library modules (`filament`, `filamat`, `gltfio`, `filament-utils`, `filament-compose`) — `commonMain` + per-target actuals. |
| `js/` | Kotlin/JS externals, **generated at build time** by Karakum from `filament.d.ts` (+ patches in `js/patches/`). |
| `c/`, `java/`, `buildSrc/` | Native glue, the JVM Panama/FFM runtime, and the convention plugins. |
| `prebuilts/` | Filament binaries (downloaded per `filaVersion`; git-ignored). |
| `samples/` | Sample apps (a composite `includeBuild`). |
| `scripts/` | Prebuilt/header download + dev cross-check scripts (`scripts/README.md`). |

## Building

You need JDK 22+ (the daemon runs on 25) and Python 3. Native targets download prebuilt
Filament binaries automatically:

```sh
./gradlew downloadPrebuilts            # all targets + headers (or downloadPrebuilts_<target>)
./gradlew compileKotlinJvm             # build a target (compileKotlinJs, compileReleaseKotlinAndroid, …)
./gradlew :kotlin:filament:jvmTest     # tests
```

- **Web** externals are generated from `prebuilts/web/filament.d.ts`; regenerate with
  `./gradlew :js:generateJsExternals`.
- **Rebuilding the Filament prebuilts** (rarely needed — only to patch the engine) requires
  the toolchains in Filament's `BUILDING.md` (emscripten is pinned). When we ship a local
  engine patch ahead of an upstream release, it's recorded under `js/patches/upstream/`.
- **Bumping `filaVersion`** (in `gradle.properties`): delete `prebuilts/*` and `include/` so
  they re-download, then run the cross-checks in `scripts/README.md`
  (`check-js-bindings.sh`, `check-common-api.sh`) to catch binding drift.

## API parity

This wrapper mirrors Filament's public API. New `commonMain` surface should follow Filament's
Android Java API (the canonical Kotlin-facing surface); JS externals must match the embind
surface in upstream `web/filament-js/jsbindings.cpp`. The `scripts/dev/check-*.sh` scripts
report gaps — run them when adding bindings or bumping `filaVersion`.

## Commit & PR conventions

- **Conventional Commits** with a platform scope, matching history:
  `fix(js): …`, `feat(compose)!: …`, `chore(release): …`, `refactor(c): …`. Use `!` for
  breaking changes.
- Keep PRs focused; update docs/samples alongside code.
- Label your issue/PR (platform + area) — see the label list below.

## Running CI (important)

The full platform matrix is **expensive and does not run automatically on PRs.** Branch
protection requires the `ci-gate` check to be green before merge, so:

1. Open your PR.
2. A maintainer adds the **`ci:run`** label — this runs the full matrix (jvm / js / ios /
   android) and re-runs it on every push while the label is present.
3. Merge once `ci-gate` is green.

Maintainers can also trigger a subset manually from the **Actions → CI → Run workflow**
button (pick `jvm` / `js` / `ios` / `android` / `all`) for quick checks. The matrix always
runs on push to `main`.

## Labels

- **platform:** `android` · `ios` · `jvm-desktop` · `web-js` · `common`
- **area:** `bindings` · `build` · `compose` · `samples` · `ci` · `docs`
- **type/triage:** `bug` · `enhancement` · `question` · `upstream-filament` ·
  `blocked-upstream` · `needs-repro` · `needs-triage` · `good first issue` · `help wanted`
- **control:** `ci:run` (run the platform matrix on a PR)

## License

By contributing you agree your contributions are licensed under the repository's
[Apache 2.0 license](LICENSE.md).
