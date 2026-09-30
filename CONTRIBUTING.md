# Contributing to Filament KMP

Thanks for helping out! Filament KMP is a **Kotlin Multiplatform wrapper** around
[Google Filament](https://github.com/google/filament), with first-class Compose
Multiplatform support across Android, iOS, JVM/Desktop and Web (JS & Wasm). This guide covers how the
project is built, where a given problem belongs, and how to get changes merged.

## Wrapper philosophy — is this our bug or Filament's?

The single most useful thing you can do before filing an issue or PR is to figure out
**whether the problem is in this wrapper or in Filament itself.**

- **Wrapper bugs** live here: missing/incorrect Kotlin bindings (Native / JNI /
  wasm externals), Gradle/prebuilt plumbing, Compose integration, samples, packaging.
- **Engine bugs** live in [google/filament](https://github.com/google/filament): rendering
  artifacts, backend (GL/Metal/Vulkan/WebGPU) errors, shader/material issues, crashes inside
  the native engine. These reproduce independently of Kotlin.

A good rule of thumb: if the same symptom would happen from C++/JS using Filament directly,
it's an engine issue — report it upstream and link it here with the **`upstream-filament`**
label so we can track it (and patch our prebuilts if needed).

## Project layout

| Path | What it is |
|---|---|
| `kotlin/*` | The published library modules (`filament`, `filamat`, `gltfio`, `filament-utils`, `filament-compose`). API classes and their `external fun` bindings live in `commonMain` ([Native Bindings](docs/internals/bindings.md)). |
| `c/` | The `Fila*` C API every platform builds: `c/<module>/generated` from Filament's headers (scoped by `c/api-headers.txt`), `c/<module>/manual` for the few hand-written functions, and the CMake entry point. |
| `jni/`, `desktop/`, `android/`, `web/` | The native runtimes: the JNI runtime shared by desktop and Android, the desktop loader + per-platform runtime jars, `libfilament-c.so` per ABI, and the wasm runtimes (each has a README). |
| `build-logic/` | Convention plugins and the `buildlogic.*` tooling: Filament targets, prebuilts, CMake, and the API generator (`apigen`). |
| `prebuilts/` | Filament binaries (downloaded per `filaVersion`; git-ignored). |
| `samples/` | Sample apps (a composite `includeBuild`). |
| `scripts/` | Dev cross-check + maintenance scripts (`scripts/README.md`). |

## Building

You need JDK 17+. Native targets fetch Filament's binaries automatically:

```sh
./gradlew prebuilts                    # pre-fetch every release target + headers (or prebuilts_<id>)
./gradlew compileKotlinJvm             # build a target (compileKotlinJs, compileReleaseKotlinAndroid, …)
./gradlew :kotlin:filament:jvmTest     # tests
./gradlew apiCheck                     # public-API surface check (CI-enforced; regen dumps with apiDump)
```

If you intentionally change the public API of a `:kotlin:*` module, run `./gradlew apiDump`
and commit the updated `<module>/api/` files with your change — `apiCheck` fails otherwise.

- **Web** needs the Emscripten SDK and Filament's wasm libraries, which upstream doesn't publish: the
  build installs emsdk into `.emsdk/` and builds `prebuilts/wasm/` from source on first use
  (`./gradlew prebuilts_wasm`; it takes a while, once per `filaVersion`).
- **Adding a binding**: the C function and its Kotlin `external fun` are generated
  (`./gradlew generateCApi generateKotlinExternals`); you write the public Kotlin method over it in
  `commonMain`. Commit the regenerated files with it. See [The Generated C API](docs/internals/c-api.md) and
  [Native Bindings](docs/internals/bindings.md).
- **Bumping `filaVersion`** (in `gradle.properties`): the prebuilts and headers are version-stamped and
  refetch on the next build; then regenerate and run `./gradlew apiGaps`
  ([Upgrading Filament](docs/internals/upgrading-filament.md)).

## API parity

The Kotlin API follows **Filament's public C++ headers**, not the Android Java API. Every platform
calls the same generated `c/` API, so a binding reaches all of them at once. `./gradlew apiGaps`
reports the public C++ methods (headers and libraries, by symbol) that `c/` doesn't call, and the
`Fila*` functions without a Kotlin external, into `build/reports/api-gaps.txt`.

### Kotlin idiom vs. C++ shape

- **Names, owners and defaults are C++'s.** A method lives on the class that declares it in C++,
  keeps its name and takes C++'s default arguments. Nested types stay where C++ nests them; the
  `Options.h` structs (`BloomOptions`, `FogOptions`, …) are top-level because they are in C++.
- **Builders mirror C++ verbatim**: the fluent `.width(64).height(64).build(engine)` chain, one
  method per C++ setter. This keeps Filament's own docs readable against this library. Don't turn a
  builder into a DSL or a data class.
- **Kotlin shape where Kotlin has one.** A `getX()`/`setX()` pair is a `var`, a zero-argument getter
  a `val`, a value struct a plain class with `var` fields, a nullable C++ result a nullable type, a
  self-destroying type `AutoCloseable`. Raw handles are `internal`, reachable only through the
  `@InternalFilamentApi` `nativeObject` accessors.
- **No invented API.** Don't add a wrapper, field or overload C++ doesn't have; conveniences belong in
  `filament-compose`. Before trusting an unfamiliar struct field, grep the upstream header for it.
- **No compatibility shims.** When C++ renames or removes something, so does Kotlin, in the same
  release, listed in the changelog.

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
