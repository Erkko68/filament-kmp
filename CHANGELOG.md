# Changelog

All notable changes to this project are documented here.

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).
Pre-1.0 releases may break public API on any minor bump; see the [README](README.md) for
the current stability promise.

## [Unreleased] — targeting `0.1.1-rc01`

### Planned

- Kotlin/Wasm (`wasmJs`) target alongside the existing legacy Kotlin/JS target.
- Dokka HTML published as the `-javadoc` artifact (replaces the empty placeholder jar
  currently shipped to satisfy Maven Central).
- KDocs on every `commonMain` `expect` declaration.

## [0.1.0-beta01] — 2026-05-21

### Fixed

- **Android**: dragging in `FilamentView` no longer freezes the render after the first
  gesture. The `SwapChain` is now retained across recompositions via a `Ref`-backed slot;
  previously the local `var` was reset on every recomposition, causing the render-loop
  closure to capture `null` and stop drawing.
- **Publishing**: the `:js` external-bindings module is now actually published to Maven
  Central. The transitive dep recorded in every `*-js` POM (`io.github.erkko68.filament:js`)
  previously had no matching artifact, breaking all JS-target consumers.

### Changed

- Project promoted from alpha to beta to reflect the stabilizing API surface and to dodge
  Gradle's lexicographic version resolution against the misspelled `0.1.0-aplha03` tag
  (which sorts above `0.1.0-alpha04`).
- POM enriched with `inceptionYear`, `issueManagement`, and a proper SSH
  `developerConnection` SCM URL.

## [0.1.0-alpha04] — 2026-05-21

### Changed

- Switched local mutable refs to `androidx.compose.ui.node.Ref<T>` in the Compose
  integration for clearer intent than `arrayOfNulls(1)`.
- `:js` module wired into the publish pipeline (build config only — the workflow change
  landed in `0.1.0-beta01`, so this tag does not actually upload the `js` artifact).

## [0.1.0-aplha03] — 2026-05-21 *(typo — do not use)*

Published with a misspelled qualifier (`aplha` instead of `alpha`). The artifacts on Maven
Central are immutable and cannot be removed. Resolve to a later version instead.

## [0.1.0-alpha02] — 2026-05-21

### Fixed

- JVM/Desktop: single combined JNI dylib (`libfilament-jni`) replaces per-module natives;
  resolves duplicate-resource errors and simplifies the publish matrix.
- Linux/Windows: cross-platform path handling in download scripts; static-library linker
  flags for JNI resolution on Linux targets.
- Header/prebuilt ABI mismatch crash — `downloadIncludes` now pins headers to the same
  Filament version as the prebuilts.

## [0.1.0-alpha01] — 2026-05-19

Initial public release. Targets: Android, iOS (arm64/sim-arm64/x64), JVM (macOS/Linux/
Windows), legacy Kotlin/JS. Modules: `filament`, `filament-compose`, `filament-utils`,
`gltfio`, `filamat`.

[Unreleased]: https://github.com/Erkko68/filament-kmp/compare/0.1.0-beta01...HEAD
[0.1.0-beta01]: https://github.com/Erkko68/filament-kmp/compare/0.1.0-alpha04...0.1.0-beta01
[0.1.0-alpha04]: https://github.com/Erkko68/filament-kmp/compare/0.1.0-alpha02...0.1.0-alpha04
[0.1.0-aplha03]: https://github.com/Erkko68/filament-kmp/releases/tag/0.1.0-aplha03
[0.1.0-alpha02]: https://github.com/Erkko68/filament-kmp/compare/0.1.0-alpha01...0.1.0-alpha02
[0.1.0-alpha01]: https://github.com/Erkko68/filament-kmp/releases/tag/0.1.0-alpha01
