# Changelog

All notable changes to this project are documented here.

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).
Pre-1.0 releases may break public API on any minor bump; see the [README](README.md) for
the current stability promise.

## [Unreleased]

### Planned

- Kotlin/Wasm (`wasmJs`) target alongside the existing legacy Kotlin/JS target.
- Dokka HTML published as the `-javadoc` artifact (replaces the empty placeholder jar
  currently shipped to satisfy Maven Central).
- KDocs on every `commonMain` `expect` declaration.
- Upstream a fix for the WebGL picking buffer `glClearBuffer*v` format mismatch in
  Filament so `view.pick` returns real hits on Web (see Platform Notes).

## [0.1.1-rc01] — 2026-05-23

Skipped the `0.1.0` stable tag — the `0.1.0-aplha03` typo sorts above any later `0.1.0-*`
pre-release in semver, so this line moves to `0.1.1` to escape that ordering for good.

### Added

- **3D primitives in the Compose DSL** — `Cube`, `Sphere`, `Cylinder`, `Plane` composables
  with proper tangent frames. Each accepts `position` / `rotation` / `scale` / `pivot` and
  an `onCreate: (entity: Int) -> Unit` callback so picking is a one-liner.
- **`Group { }` composable** — parents nested scene composables under a single transform
  entity via an internal `LocalParentEntity` composition local. `Light`, `GltfInstance`
  and every primitive participate in the hierarchy automatically; groups nest cleanly.
- **`pivot: Position` on `GltfInstance` and every primitive** — bakes into the transform
  as `T(position) * R(rotation) * S(scale) * T(-pivot)`. Rotation and scale revolve around
  an explicit point in mesh space; default `(0, 0, 0)` keeps existing call sites unchanged.
- **`rememberSceneClock(): State<Float>`** — per-frame seconds counter for ambient
  animations without the inline `LaunchedEffect { while(true) { withFrameNanos { ... } } }`
  boilerplate.
- **Suspend-lambda resource helpers** — `rememberMaterial { load() }`,
  `rememberTexture(type) { load() }`, `rememberGltfAsset { load() }`. All accept an
  optional `engine: Engine = LocalFilamentEngine.current` so they can be called *outside*
  `FilamentView { }` when the engine is hoisted via `rememberFilamentEngine()`.
- **New post-processing composables** — `Dithering`, `RenderQuality`. `Bloom` gains
  `resolution: Int` and `levels: Int` parameters so the bloom downsample chain can be
  tuned for hi-DPI displays.
- **Group-aware `Light`** — light entities now carry a transform component, so a `Light`
  placed inside a `Group` follows the group's transform.
- **C wrapper getter** — `FilaView_getViewport` so Kotlin/Native reads back the current
  viewport instead of returning zeros.
- **Materials guide** — `docs/compose/materials.md` covering the precompiled `.filamat`
  workflow, `matc` (with explicit pointer to the per-OS release tarballs), parameter
  type reference, and when to use runtime `filamat` instead. Linked from
  [docs/README.md](docs/README.md) and [docs/compose/README.md](docs/compose/README.md).

### Changed

- **`Light` rewritten for in-place updates.** `LightManager` setters are now driven from
  a `SideEffect` so changing colour, intensity, direction etc. no longer destroys and
  rebuilds the entity on every recomposition. Entity destruction ordered so light /
  transform cleanup still sees a live entity at dispose time.
- **Sync resource overloads are now `internal`.** `rememberMaterial(bytes: ByteArray)`,
  `rememberTexture(bytes, type)` and `rememberGltfAsset(bytes: ByteArray)` survive only
  as building blocks for the public suspend-lambda variants. Every realistic byte source
  on Compose Multiplatform is suspending, so the sync forms weren't pulling their weight.
- **Samples app restructured** — `Home → Duck / Primitives / Picking / Solar` with
  Material3 defaults, safe-area-aware overlays for the iOS notch and Android status bar,
  and `BackButton` shared via `windowInsetsPadding(safeDrawing)`.
- **`PrimitivesScene` uses a precompiled `lit_color.filamat`** instead of compiling at
  runtime via `filamat`. Demonstrates the recommended workflow and works on every target.
- **CI Gradle cache** — new `setup-gradle-cached` composite action wraps
  `gradle/actions/setup-gradle@v4` with `cache-write-only-on-default-branch`,
  `cache-cleanup: on-success`, and excludes for JDK distributions + Gradle
  artifact-transform output directories. Cuts per-repo cache footprint from ~15 GB
  to ~2–4 GB and stops PRs from evicting `main`'s caches.

### Fixed

- **GPU picking actually delivers results.** The JVM and Android JNI bridges silently
  dropped every `view.pick` callback when called with `handler = null` (the obvious
  "run on current thread" choice). The result `Runnable` was created and immediately
  garbage-collected without being executed. Both wrappers now pass a direct
  `Executor { it.run() }` so the callback reaches Kotlin.
- **iOS `view.pick` lifecycle.** Each query now owns its own `StableRef`, self-disposed
  in the C callback. The previous shared `mPickCallbackRef` was double-freed on the
  next pick and dangled any callback still in flight.
- **iOS taps reach `pointerInput`.** The default `UIKitInteropProperties.Cooperative`
  interaction mode delayed touches 150 ms before Compose saw them. `FilamentSurface.ios`
  now sets `interactionMode = null` so taps route directly to Compose gestures.
- **iOS `View.viewport` returns the real size.** Was hard-coded to `Viewport(0,0,0,0)`
  because the C wrapper had no getter; now reads through the new `FilaView_getViewport`.
- **Android primitives render lit.** Same `FloatBuffer.wrap(...)` heap-buffer bug we
  fixed for JVM in `0.1.0-beta01`, manifesting on Android as the JNI silently producing
  zero tangent quaternions (rather than the aarch64 SIGBUS). `SurfaceOrientation` and
  `RenderableManager` bone APIs now copy into direct, native-order NIO buffers.
- **Web banding in emissive bloom halos.** Three JS-specific bugs combined to force
  the smaller `RGBA11_11_10` HDR color buffer: the `renderQuality` setter never called
  `setRenderQuality` on the JS view (silent no-op), `_dithering` defaulted to `NONE`
  (native default is `TEMPORAL`), and `RenderQuality.hdrColorBuffer` defaulted to
  `MEDIUM` (native default is `HIGH`). All three now match Filament's native defaults.
- **`Sphere` primitive normals.** Triangle winding `(a, c, b)` / `(b, c, d)` produced
  inward-pointing normals — lighting looked inverted. Flipped to `(a, b, c)` /
  `(b, d, c)`. Verified at the equator: `(b−a) × (c−a)` now points outward.
- **`Plane` primitive invisible from below.** The mesh is now two-sided by default
  (8 vertices, 4 triangles, top normals `+Y`, bottom normals `−Y` with reversed
  winding) so lighting works from both sides without disabling backface culling.
  Opt out with `doubleSided = false`.
- **JS-target unsupported APIs now throw.** `filamat.MaterialBuilder`,
  `gltfio.MaterialProvider.createMaterialInstance/getMaterial`,
  `KTX1Loader.getSphericalHarmonics` and `HDRLoader.createTexture` previously
  returned `null` silently; they now throw `UnsupportedOperationException` with
  workaround pointers. See [Platform Notes — Web](docs/platform-notes.md#web--wasm).

### Known issues

- **GPU `view.pick` does not return real hits on Web.** Filament's WebGL backend
  has a format mismatch in the integer-formatted picking render target — clears go
  through `glClearBufferfv` instead of `glClearBufferuiv` — and the readback hits
  the same class of bug. The Compose binding works correctly; the underlying
  Filament JS prebuilt does not. CPU ray–AABB picking against the scene's known
  entities is the workaround. Documented in
  [Platform Notes — Web](docs/platform-notes.md#web--wasm).

### Migration from `0.1.0-beta01`

If you were calling the byte-array overloads directly:

```kotlin
// before
val mat = rememberMaterial(bytesAlreadyInMemory)

// after — load asynchronously
val mat = rememberMaterial { Res.readBytes("files/materials/lit_color.filamat") }
```

If you were doing runtime `filamat` compilation:

```kotlin
// before
val pkg = MaterialBuilder().shading(LIT).uniformParameter(FLOAT3, "baseColor").build()
val mat = Material.Builder().payload(pkg.getBuffer()).build(engine)

// after — precompile with matc, ship the .filamat as a resource
val mat = rememberMaterial { Res.readBytes("files/materials/lit_color.filamat") }
```

Runtime `filamat` still works on every target except Web. See the
[Materials guide](docs/compose/materials.md) for the full precompiled workflow.

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

[Unreleased]: https://github.com/Erkko68/filament-kmp/compare/0.1.1-rc01...HEAD
[0.1.1-rc01]: https://github.com/Erkko68/filament-kmp/compare/0.1.0-beta01...0.1.1-rc01
[0.1.0-beta01]: https://github.com/Erkko68/filament-kmp/compare/0.1.0-alpha04...0.1.0-beta01
[0.1.0-alpha04]: https://github.com/Erkko68/filament-kmp/compare/0.1.0-alpha02...0.1.0-alpha04
[0.1.0-aplha03]: https://github.com/Erkko68/filament-kmp/releases/tag/0.1.0-aplha03
[0.1.0-alpha02]: https://github.com/Erkko68/filament-kmp/compare/0.1.0-alpha01...0.1.0-alpha02
[0.1.0-alpha01]: https://github.com/Erkko68/filament-kmp/releases/tag/0.1.0-alpha01
