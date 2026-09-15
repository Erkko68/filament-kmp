# Contributing to Filament KMP

Thanks for helping out! Filament KMP is a **Kotlin Multiplatform wrapper** around
[Google Filament](https://github.com/google/filament), with first-class Compose
Multiplatform support across Android, iOS, JVM/Desktop and Web (JS & Wasm). This guide covers how the
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
| `web/` | Hand-maintained Kotlin externals over Filament.js (embind), vendored in `web/src/webMain` and shared by the `js` and `wasmJs` targets; carried engine patches live in `web/patches/upstream/` (`web/README.md`). |
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

- **Web** externals are hand-maintained Kotlin sources in `web/src/webMain`, shared by the
  `js` and `wasmJs` targets — edit them directly and run `scripts/dev/check-js-bindings.sh`
  to cross-check against upstream's embind surface (see `web/README.md`).
- **Rebuilding the Filament prebuilts** (rarely needed — only to patch the engine) requires
  the toolchains in Filament's `BUILDING.md` (emscripten is pinned). When we ship a local
  engine patch ahead of an upstream release, it's recorded under `web/patches/upstream/`.
- **Bumping `filaVersion`** (in `gradle.properties`): delete `prebuilts/*` and `include/` so
  they re-download, then run the cross-checks in `scripts/README.md`
  (`check-js-bindings.sh`, `check-common-api.sh`) to catch binding drift.

## API parity

This wrapper mirrors Filament's public API. New `commonMain` surface should follow Filament's
Android Java API (the canonical Kotlin-facing surface); JS externals must match the embind
surface in upstream `web/filament-js/jsbindings.cpp`. The `scripts/dev/check-*.sh` scripts
report gaps — run them when adding bindings or bumping `filaVersion`.

### Kotlin idiom vs. upstream shape

Mirroring the API is not the same as mirroring the Java. Two deliberate rules:

- **Builders mirror upstream verbatim.** The seventeen `Builder` classes keep the fluent
  `.width(64).height(64).build(engine)` chain, one method per upstream setter, in upstream's
  order. This is what makes Filament's own C++ and Android docs readable against this library,
  and `MaterialBuilder`'s fifty-odd setters would be an unusable constructor. Do not "modernize"
  a builder into a DSL or a data class.
- **Data classes only where nothing is nested in an `expect class`.** `Viewport` and
  `MaterialKey` are `data class`es because they are plain common types. The option structs
  (`View.BloomOptions`, `Renderer.ClearOptions`, `Engine.Config`, `LightManager.ShadowOptions`)
  cannot be, and stay `class X()` with `var` fields configured through `apply { }`: an expect
  constructor takes no `val`/`var` parameters, a nested `typealias` onto a top-level data class
  is not actualized, and a nested classifier inherited from a supertype does not resolve through
  the subclass name. The only shape that works is `interface` + internal per-platform impls; it
  was built and rejected (an interface cannot be `sealed` across source sets, and Android's
  wrapping actuals come out line-neutral). Re-verified on Kotlin 2.4.10 — don't re-litigate.
- **Everything else is idiomatic Kotlin.** Outside a builder, a zero-argument `getX()` should be
  a `val`, a `getX()`/`setX()` pair a `var`, a returned collection a `List`/`Set` rather than an
  array, and an optional out-parameter `out: T? = null`. Raw backend handles are `internal`,
  reachable only through the `@InternalFilamentApi` `nativeObject` accessors.

## Versioning & releases

Versions are plain `X.Y.Z` (no pre-release suffixes since `0.2.0`), and all
`io.github.erkko68.filament:*` artifacts share one version:

- **minor (`0.X.0`)** — a new upstream Filament feature release (1.73 → 1.74) plus any
  wrapper API changes since the last one; pre-1.0, breaking changes may land here.
- **patch (`0.0.X`)** — bug fixes only: upstream Filament point releases (e.g. 1.73.1) or
  wrapper-only fixes.
- **`1.0.0`** — tagged when the public API stabilizes; breaking changes then require a
  major bump.

Every user-visible change needs a one-line entry under `[Unreleased]` in
[CHANGELOG.md](CHANGELOG.md). Cutting a release is a maintainer task — see
[.github/workflows/README.md](.github/workflows/README.md).

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
