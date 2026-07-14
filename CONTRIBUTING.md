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
label so we can track it (and patch our prebuilts if needed).

## Project layout

| Path | What it is |
|---|---|
| `kotlin/*` | The published library modules (`filament`, `filamat`, `gltfio`, `filament-utils`, `filament-compose`) — `commonMain` + per-target actuals. |
| `js/` | Kotlin/JS externals, **generated at build time** by Karakum from `filament.d.ts` (+ patches in `js/patches/`). |
| `c/`, `java/`, `build-logic/` | Native glue, the JVM Panama/FFM runtime, and the convention plugins. |
| `prebuilts/` | Filament binaries (downloaded per `filaVersion`; git-ignored). |
| `samples/` | Sample apps (a composite `includeBuild`). |
| `scripts/` | Dev cross-check + maintenance scripts (`scripts/README.md`). |

## Building

You need JDK 22+ (the daemon runs on 25). Native targets download prebuilt
Filament binaries automatically:

```sh
./gradlew downloadPrebuilts            # all targets + headers (or downloadPrebuilts_<target>)
./gradlew compileKotlinJvm             # build a target (compileKotlinJs, compileReleaseKotlinAndroid, …)
./gradlew :kotlin:filament:jvmTest     # tests
./gradlew apiCheck                     # public-API surface check (CI-enforced; regen dumps with apiDump)
```

If you intentionally change the public API of a `:kotlin:*` module, run `./gradlew apiDump`
and commit the updated `<module>/api/` files with your change — `apiCheck` fails otherwise.

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

The full platform matrix (jvm / js / ios / android) runs **automatically on every PR** and
on push to `main`. It's **expensive** (native prebuilts, emulators, XCFrameworks), so:

1. Open your PR — the matrix starts on its own and re-runs on every push.
2. **Fork PRs** are held until a maintainer clicks **"Approve and run"** (GitHub's native
   fork-PR approval); collaborators' PRs run with no approval step.
3. Merge once `ci-gate` (the single required check, green only when every platform job
   succeeds) passes. Docs-only changes (`**.md`) skip the matrix.

Maintainers can also trigger a single platform manually from the **Actions → CI → Run
workflow** button (pick `jvm` / `js` / `ios` / `android` / `all`).

## Labels

- **platform:** `android` · `ios` · `jvm-desktop` · `web-js` · `common`
- **area:** `bindings` · `build` · `compose` · `samples` · `ci` · `docs`
- **type/triage:** `bug` · `enhancement` · `question` · `upstream-filament` ·
  `blocked-upstream` · `needs-repro` · `needs-triage` · `good first issue` · `help wanted`

## License

By contributing you agree your contributions are licensed under the repository's
[Apache 2.0 license](LICENSE.md).
