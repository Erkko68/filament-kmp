# Changelog

All notable changes to this project are documented here.

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).
Pre-1.0 releases may break public API on any minor bump; see the [README](README.md) for
the current stability promise.

Each entry is one line; click the version link at the bottom for the full diff.

## [Unreleased]

## [0.1.2-beta01] — 2026-05-30

- **JVM/Desktop bindings migrated from JNI to Project Panama (FFM).** The per-module JNI stack is replaced by a single `:java` module that binds the combined `libfilament-c` shared library via the Foreign Function & Memory API (jextract-generated); see [`java/README.md`](java/README.md).
  - **Breaking:** the Desktop/JVM native runtime now requires a **JDK 22+** runtime (the FFM API floor).
  - **Breaking:** the JVM native runtime artifact moved to `io.github.erkko68.filament-ffm:filament-ffm` (pulled in transitively — consumers only need JDK 22+).
- The Kotlin/JS externals (`:js`) are now **generated at build time** by [Karakum](https://github.com/karakum-team/karakum) from Filament's `filament.d.ts`, replacing the hand-maintained `filament.js.kt`. Because the d.ts under-reports the real `jsbindings.cpp` surface, the build first patches it with a curated overlay (`js/patches/filament.patch.d.ts`) and non-additive corrections (`js/patches/filament.dts-overrides.json`). Nothing generated is committed; see [`js/README.md`](js/README.md). The JS externals now target the kotlin-wrappers types.
  - `scripts/gradle/download_filament_prebuilts.py` now also extracts `filament.d.ts` for the `web` target.
  - `scripts/dev/check-js-bindings.sh` audits the overlay/overrides (not a committed externals file) against `jsbindings.cpp`, and a stale `REPO_ROOT` path (broken when the script moved to `scripts/dev/`) is fixed.
- Dokka HTML published as the `-javadoc` artifact (replaces empty placeholder jar).
- KDocs on every `commonMain` `expect` declaration.
- Filament bumped to **1.71.5**.
  - **Breaking:** `Renderer.ClearOptions.clearColor` is now `DoubleArray` (was `FloatArray`), matching the upstream Android API change. JNI `nSetClearOptions` and the C wrapper's `FilaRendererClearOptions::clearColor` follow suit.
  - **Added:** non-indexed (attribute-less / procedural) overloads of `RenderableManager.Builder.geometry(...)` and `RenderableManager.setGeometryAt(...)` (requires `FEATURE_LEVEL_1`+; not bound in Filament.js, so the web actuals throw `UnsupportedOperationException`).
  - **Added:** `VertexBuffer.Builder.bufferCount` now accepts `0` for attribute-less rendering (passthrough — no client-side validation existed).
  - `scripts/download_filament_includes.py` now synthesizes a cross-platform `gltfio/materials/uberarchive.h` from the per-platform release tarballs (the source tarball doesn't ship this auto-generated header).
- `include/` is no longer checked into the repo — it's regenerated on every build by the `downloadIncludes` Gradle task (CMake/cinterop tasks depend on it). Mirrors how `prebuilts/` already worked.

## [0.1.1-rc02] — 2026-05-26

### Added
- Cross-platform test suite (JVM / Android / iOS / JS) with shared fixtures and a bundled `Duck.glb`.
- Karma + WASM bootstrap staged into every KMP module's jsTest via the shared convention plugin.
- Animation sample app; JS bindings for `View` getters/setters and `BufferObject`.
- GitHub Pages deployment workflow.

### Fixed
- `MaterialKey` JNI `globalInit` now binds: missing `hasSpecular*`, `hasVolume`, `hasDispersion`, `specular*UV` fields added to the Java class.
- `UbershaderProvider.getMaterials()` (JVM) filters null slots that the upstream binding wraps in `Material` unconditionally.
- More JS bindings: `View.*Options` defaults aligned with Filament C++, `BloomOptions` defaults match native, dynamic-call sites replaced with typed externals.
- GPU `view.pick` now returns real hits on Web.
- JVM `AssetLoader` / `FilamentInstance` wrapper cleanup.

### Changed
- Python-based coverage tools replaced by `upgrade-diff.sh` for Filament version management.

## [0.1.1-rc01] — 2026-05-23

> Skipped `0.1.0` stable — the `0.1.0-aplha03` typo sorts above any later `0.1.0-*` pre-release.

### Added
- Compose DSL primitives: `Cube`, `Sphere`, `Cylinder`, `Plane` with tangent frames and `onCreate` callback.
- `Group { }` composable parents nested scene composables under a single transform.
- `pivot: Position` on `GltfInstance` and every primitive: `T(pos) * R(rot) * S(scale) * T(-pivot)`.
- `rememberSceneClock()` — per-frame seconds counter for ambient animations.
- Suspend-lambda resource helpers: `rememberMaterial { }`, `rememberTexture(type) { }`, `rememberGltfAsset { }`.
- Post-processing composables: `Dithering`, `RenderQuality`; `Bloom` gains `resolution` / `levels`.
- Group-aware `Light` (light entities carry a transform component).
- `FilaView_getViewport` C wrapper getter.
- Materials guide: [`docs/compose/materials.md`](docs/compose/materials.md).

### Changed
- `Light` setters driven from `SideEffect` — no more entity rebuild on every recomposition.
- Sync byte-array resource overloads are now `internal`; suspend-lambda variants are public.
- Samples app restructured (`Home → Duck / Primitives / Picking / Solar`) with Material3 + safe-area handling.
- `PrimitivesScene` uses a precompiled `lit_color.filamat` instead of runtime `filamat`.
- CI Gradle cache footprint cut ~15 GB → ~2–4 GB via `setup-gradle-cached` composite action.

### Fixed
- GPU `view.pick` callbacks: JVM/Android JNI now passes `Executor { it.run() }` instead of dropping the `Runnable`.
- iOS `view.pick` lifecycle: per-query `StableRef`, self-disposed in the C callback.
- iOS taps reach `pointerInput` (`interactionMode = null` instead of `Cooperative`).
- iOS `View.viewport` returns the real size via `FilaView_getViewport`.
- Android primitives render lit: `SurfaceOrientation` / `RenderableManager` bone APIs use direct native-order NIO buffers.
- Web bloom banding: `renderQuality` setter now reaches the JS view; `dithering` defaults to `TEMPORAL`; `hdrColorBuffer` defaults to `HIGH`.
- `Sphere` normals: triangle winding flipped so normals point outward.
- `Plane` invisible from below: now two-sided by default (opt out with `doubleSided = false`).
- JS unsupported APIs throw `UnsupportedOperationException` with workarounds instead of returning `null` silently.

### Migration from `0.1.0-beta01`
Byte-array overloads → suspend lambdas:
```kotlin
val mat = rememberMaterial { Res.readBytes("files/materials/lit_color.filamat") }
```
Runtime `filamat` still works on every target except Web — see the [materials guide](docs/compose/materials.md).

## [0.1.0-beta01] — 2026-05-21

### Fixed
- Android: `FilamentView` no longer freezes after the first drag — `SwapChain` retained via `Ref`-backed slot.
- Publishing: `:js` external-bindings module is now actually uploaded to Maven Central.

### Changed
- Promoted alpha → beta; dodges Gradle's lexicographic resolution against the misspelled `0.1.0-aplha03` tag.
- POM enriched with `inceptionYear`, `issueManagement`, SSH `developerConnection`.

## [0.1.0-alpha04] — 2026-05-21
- Switched local mutable refs to `androidx.compose.ui.node.Ref<T>`.
- `:js` module wired into the publish pipeline (workflow change actually landed in `0.1.0-beta01`).

## [0.1.0-aplha03] — 2026-05-21 *(typo — do not use)*
Published with a misspelled qualifier. Maven Central artifacts are immutable; resolve to a later version.

## [0.1.0-alpha02] — 2026-05-21

### Fixed
- JVM/Desktop: single combined `libfilament-jni` dylib replaces per-module natives.
- Linux/Windows: cross-platform path handling + static-library linker flags for JNI on Linux.
- Header/prebuilt ABI mismatch: `downloadIncludes` pins headers to the same Filament version as the prebuilts.

## [0.1.0-alpha01] — 2026-05-19
Initial public release. Targets: Android, iOS (arm64/sim-arm64/x64), JVM (macOS/Linux/Windows), legacy Kotlin/JS. Modules: `filament`, `filament-compose`, `filament-utils`, `gltfio`, `filamat`.

[Unreleased]: https://github.com/Erkko68/filament-kmp/compare/0.1.2-beta01...HEAD
[0.1.2-beta01]: https://github.com/Erkko68/filament-kmp/compare/0.1.1-rc02...0.1.2-beta01
[0.1.1-rc02]: https://github.com/Erkko68/filament-kmp/compare/0.1.1-rc01...0.1.1-rc02
[0.1.1-rc01]: https://github.com/Erkko68/filament-kmp/compare/0.1.0-beta01...0.1.1-rc01
[0.1.0-beta01]: https://github.com/Erkko68/filament-kmp/compare/0.1.0-alpha04...0.1.0-beta01
[0.1.0-alpha04]: https://github.com/Erkko68/filament-kmp/compare/0.1.0-alpha02...0.1.0-alpha04
[0.1.0-aplha03]: https://github.com/Erkko68/filament-kmp/releases/tag/0.1.0-aplha03
[0.1.0-alpha02]: https://github.com/Erkko68/filament-kmp/compare/0.1.0-alpha01...0.1.0-alpha02
[0.1.0-alpha01]: https://github.com/Erkko68/filament-kmp/releases/tag/0.1.0-alpha01
