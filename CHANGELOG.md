# Changelog

All notable changes to this project are documented here.

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).
Since `0.2.0`: minor (`X.Y.0`) tracks upstream Filament feature releases and may change
public API; patch (`X.Y.Z`) is bug fixes only; a major bump is reserved for maturity
milestones and very large changes. See
[README → Versioning & stability](README.md#versioning--stability).

Each entry is one line; click the version link at the bottom for the full diff.

## [Unreleased]

### Added
- **Android-API parity sweep** (`filament`): bound every remaining public Filament Android API missing from common — GTAO selection (`AmbientOcclusionOptions.aoType` + `AmbientOcclusionType` enum), typed TAA options (`TemporalAntiAliasingOptions.boxType`/`boxClipping`/`jitterPattern` are now `BoxType`/`BoxClipping`/`JitterPattern` enums instead of raw `Int`s — source-breaking if you set them), world-origin grid snapping (`View.gridSize`/`effectiveGridSize`), `Engine.hasUnrecoverableFailure`, `MaterialInstance.getConstantBoolean/Float/Int`, `ColorGrading.Builder.customLut`, and `RenderableManager.getEnabledAttributesAt`. On web, `getConstant*` and `customLut` throw `UnsupportedOperationException` (`@PlatformGap`: not bound in filament.js); everything else works on all targets. Intentional non-mirrors are documented in `scripts/dev/check-common-api-ignores.txt`.
- **Smarter upgrade tooling** (build): `check-common-api.sh` now audits classes, nested types, and enum constants (not just method names), strips KDoc before matching, flags upstream-deprecated members informationally, reads suppressions from `check-common-api-ignores.txt`, and exits non-zero on unsuppressed gaps; `upgrade-diff.sh` gained a HIGHLIGHTS section (MATERIAL_VERSION bump, `CONFIG_MAX_*`, feature-flag flips, added/removed Java classes and embind bindings) and optional tags (defaults: `filaVersion` → latest upstream release).

### Fixed
- **Web TAA options no longer drop fields** (`filament`): the web `temporalAntiAliasingOptions` setter now forwards `filterInput`, `useYCoCg`, `hdr`, `boxType`, `boxClipping`, `jitterPattern`, `varianceGamma`, `preventFlickering`, and `historyReprojection` instead of silently resetting them to defaults.
- **`downloadPrebuilts` works again on 1.73.0** (build): dropped the dead `macosX64` prebuilt target — upstream releases stopped shipping mac x86_64 libs, which made the umbrella task fail.

## [0.2.0] — 2026-07-16

### Added
- **Filament 1.73.0**: Upgraded the bundled Filament engine to 1.73.0; recompiled all `.filamat` materials (MATERIAL_VERSION 72→73, DYN variant became a specialization constant). New APIs: `Renderer.setDesiredPresentationTime` / `Renderer.setRenderingDeadline` (frame-history reporting and render-deadline hints), `SwapChain.isFrameRateChangeSupported` / `SwapChain.setFrameRate` (intended frame rate, Android/JVM/native), and `View.getVisibleRenderableCount`. The web prebuilt is now a stock upstream build — the carried colored-penumbra divisor patch landed upstream in 1.73.0. Removed the web-only `setImageCube` internal path (upstream dropped the binding; plain `setImage` uploads full cubemap levels).
- **`@PlatformGap` annotation + binding-coverage table** (`filament`, all modules): every common API whose platform binding is missing or degraded (all current gaps are on web, where `filament.js` doesn't bind the function) is now annotated with `@PlatformGap(platforms, behavior)` — visible in the IDE and the generated API reference — and listed in a per-API coverage table in [Platform Notes](docs/platform-notes.md#binding-coverage). Android/iOS/JVM expose the full common API.
- **Tier C semantic frame tests** (tests): a new `FrameProbe` harness renders a tiny lit scene into the readable headless swapchain and asserts *relations between image regions* (shadow darker than open floor, lit centre shows its base colour, removing the sun changes the frame, PCF→VSM still renders) — rasterizer-invariant property checks, not goldens, targeting the historical "wrong pixels, no exception" wrapper-bug classes. Backed by an embedded `test_lit.filamat` (compiled with the release `matc`, vsm variants kept).
- **Vendored kotlin-math test suite** (tests): upstream romainguy/kotlin-math's `HalfTest`/`MatrixTest`/`QuaternionTest` (Apache-2.0) now run in `filament-utils` commonTest on every target, guarding the ~5,300-line vendored math library against local drift — previously the worst coverage gap in the repo (utils 2.9% → 28.5% line).
- **Exhaustive enum round-trip tests** (tests): every entry of every gettable enum-typed property (`View` modes, option-struct enums, `TextureSampler`, `MaterialInstance`) is now set→get round-tripped through the bindings on every target — converting the silently-misaligned-enum bug class (the historical `Backend`/`StereoscopicType` swap) into a test failure wherever the mapping is asymmetric.
- **Test materials now load on every target** (tests): `.filamat` test blobs are base64-embedded into commonTest at build time (`generateEmbeddedMaterials`, sharing the `registerEmbeddedTestResources` build-logic helper with gltfio's `generateEmbeddedGlb`), replacing the JVM-only classpath resource — material/renderable rendering tests now also run on web, iOS, and Android instead of silently skipping.

### Changed
- **Dropped the `-beta` suffix** (release): versions are plain `X.Y.Z` from now on — minor bumps track upstream Filament feature releases (this one: 1.72.0 → 1.73.0), patch bumps are fixes only, and a major bump is reserved for API maturity and very large changes. See [README → Versioning & stability](README.md#versioning--stability).
- **Python is no longer a build dependency** (build): the prebuilt/header/jextract download scripts were ported to pure-JVM Gradle tasks in `build-logic` (commons-compress for tar.gz), keeping the same task names, cache dirs, and version stamping — and making the prebuilt downloads fully version-aware, so bumping `filaVersion` re-extracts automatically. `setup-python` dropped from all CI workflows.
- **`buildSrc` became the `build-logic` included build** (build): convention-plugin edits no longer invalidate the whole main build's task graph.
- **Public-API surface is now CI-enforced** (build): binary-compatibility-validator's `apiCheck` guards the JVM ABI of the five published `:kotlin:*` modules against committed `api/` dumps; intentional API changes must ship a regenerated `apiDump`.

### Fixed
- **Half-precision arithmetic was wrong in the subnormal range** (`filament-utils`, all platforms): the vendored `Half.kt` had drifted from upstream kotlin-math — multiplication/division and float↔half conversion produced values off by 2× for subnormals, and `Quaternion.fromEuler` (YZX order) was slightly off. Both files are re-vendored verbatim from upstream (found immediately by the newly vendored test suite below). Side effect: `Mat2/3/4.toFloatArrayColumn()` and `Quaternion.fromRotation(Float3, Float3)` are now available, matching upstream.
- **Readback smoke test could pass without verifying anything** (tests): `RendererRenderingTest` only checked pixel content *if* the async `readPixels` callback happened to land; the callback delivery is now asserted on every target where the binding exists (web excluded — `readPixels` is unbound in `jsbindings.cpp`).
- **Soft shadows crashed with built-in materials** (`filament-compose`, all platforms): the precompiled `StandardLit`/`StandardTextured` materials filtered out the `vsm` shader variants to shrink the blobs, but Filament selects those variants for *all* soft shadow types (`Vsm`/`Dpcf`/`Pcss`, not just VSM), so enabling any of them panicked the engine with "Requested variant … does not exist for material". The built-in lit materials now ship with the VSM variants included.

## [0.1.3-beta03] — 2026-07-11

### Fixed
- **Android builds failed to inline math utils** (all modules): the Android target was compiled at JVM 22 alongside the desktop JVM target, so published AARs shipped inline functions (`Float3.times`, `dot`, `cross`, …) as JVM 22 bytecode — Android consumers building at the conventional JVM 11 hit "Cannot inline bytecode built with JVM target 22". Android bytecode is now JVM 11; the JVM 22 floor (Project Panama / FFM) applies only to the Desktop/JVM artifacts. ([#1](https://github.com/Erkko68/filament-kmp/issues/1))

### Added
- **Built-in standard materials** (`filament-compose`): `rememberColorMaterialInstance` (LIT PBR), `rememberUnlitColorMaterialInstance`, `rememberTexturedMaterialInstance`, and `rememberEmissiveMaterialInstance` return a ready `MaterialInstance` for the common cases with **no `.mat` authoring, no `matc`, and no asset shipping** — they ship as precompiled `.filamat` blobs embedded in the library, so they work on every target including Web (where runtime material compilation isn't available). The shared base `Material` per type is built once and reused across a `rememberFilamentScene`; `rememberStandardMaterial(StandardMaterial.…)` exposes it directly. Replaces the per-app material boilerplate the samples used to carry.
- **Reactive material parameters** (`filament-compose`): new `rememberMaterialInstance(material, vararg keys) { configure }` overload creates a `MaterialInstance` and re-applies `configure` whenever a key changes — declarative parameter binding that updates in place (safe to keep referenced by a renderable), no `SideEffect`/`onUpdate` needed. A `MaterialInstance.setParameter(name, Color)` extension keeps colour call sites typed against the `Color` value class.

## [0.1.3-beta02] — 2026-06-25

### Fixed
- **Windows desktop crash on engine creation** (`filament`, JVM): the JVM native library now statically links the MSVC C++ runtime (`/MT`), avoiding an `msvcp140.dll` conflict with the JVM's own runtime that crashed the desktop app at startup on Windows.
- **Dangling `Engine.Config` on JVM/native** (`filament`): the C engine builder passed the address of a stack-local `Engine::Config` that Filament's `Builder` retains until `build()`, leaving a dangling pointer. The config now lives for the builder's full lifetime, so custom `EngineConfig` values are applied reliably.
- **Backend / stereoscopic enum mismatch on JVM/native** (`filament`): the C FFI `Backend` and `StereoscopicType` enum values were misaligned with Filament 1.72 (`OPENGL`/`VULKAN` swapped, missing `WEBGPU`/`NONE`), so requesting a backend could silently select the wrong one. Values now match upstream.

## [0.1.3-beta01] — 2026-06-25

### Added
- **`rememberAnimationState`** (`filament-compose`): hoisted, auto-advancing glTF playback state for `GltfInstance` (`animationState`). It drives the animator every frame and cross-fades smoothly when `animationIndex` changes, exposing `time`/`progress`/`isTransitioning` plus `isPaused`/`seek` — covering the "play idle, blend into walk" case without the `onUpdate` escape hatch.
- **Weighted animation mixer** (`filament-compose`): `AnimationState` now also drives a held, multi-clip **blend tree** — declare tracks with `rememberAnimationTrack` and blend by `weight` (built on chained `applyCrossFade`, so N clips compose with no native changes). `AnimationMixer` is the underlying plain-Kotlin engine for imperative game loops (`rememberAnimationMixer` + `OnFrame`); `AnimationTrack` exposes per-track `weight`/`index`/`speed`/`loop` and read-only `time`/`progress`/`isFinished`; `rememberAnimationNames` resolves clip names to indices. Whole-skeleton blending only (no per-bone masks/additive) — the native `Animator` ceiling.
- **`GltfInstance` morph weights** (`filament-compose`): new `morphWeights` parameter applies vertex morph-target weights to every renderable in the instance that has morph targets.
- **`CameraNode`** (`filament-compose`): a scene composable that drives a `CameraState` from the surrounding `Group`'s world transform every frame, for declarative chase / mounted / first-person cameras (`eyeOffset`/`targetOffset`/`up` in the group's local space).
- **`rememberRenderTarget`** (`filament-compose`): renders a scene off-screen through its own camera into a sampleable `Texture` (mini-maps, monitors, portals, thumbnails) via `Renderer.renderStandaloneView`, ready to bind as a material parameter.
- **`Light` model exposure** (`filament-compose`): `Light` gains `lightChannels` (which of channels 0–7 it affects) and a richer intensity model via `intensityUnit` (`LightUnit.LUMINOUS_POWER`/`CANDELA`/`WATTS`) plus `efficiency`, mapping onto the core `LightManager` candela/watt setters. New parameters default to the previous behaviour.
- **`OnFrame` / `FrameInfo`** (`filament-compose`): the single per-frame primitive (one `withFrameNanos` loop delivering `deltaSeconds`/`elapsedSeconds`, no recomposition). `rememberSceneClock`, `FilamentEffect.onFrame`, `rememberAnimationState`, `CameraNode`, and the flight camera are now all built on it instead of each hand-rolling their own loop.

### Added
- **`ShadowConfig` full option surface** (`filament-compose`): exposes the remaining per-light shadow options — `shadowNearHint`/`shadowFarHint`, `contactShadowSteps`, `lispsm`, `elvsm`, and `transform`. (`polygonOffset*` stays unexposed — package-private in the Android binding; use `constantBias`/`normalBias`.) `lispsm` now defaults `false` on all platforms (matching the Android binding) to reduce DPCF/PCSS/VSM penumbra artifacts — Filament's C++ default is `true`, still settable per-light.

### Fixed
- **Shadows on JVM/native** (`filament`): `LightManager.ShadowOptions()` left several fields uninitialized in the hand-allocated C struct — `polygonOffsetConstant/Slope`, the `transform` quaternion, `cascadeSplitPositions`, and a few wrong defaults (`normalBias`, `shadowNearHint`, `maxShadowDistance`, `shadowBulbRadius`). On native (`nativeHeap.alloc`, not zeroed) these were garbage; on JVM (arena-zeroed) the `transform` was a degenerate zero quaternion and depth bias was 0. The result: directional shadows silently failed to render and soft shadows showed black acne stripes. Android/web were unaffected (they use Filament's own defaulted `ShadowOptions`). All fields now initialize to Filament's documented defaults, including the identity `transform` `{0,0,0,1}`.
- **Primitives now cast shadows** (`filament-compose`): `Cube`/`Sphere`/`Plane`/`Cylinder`/`Mesh` built renderables without calling `castShadows`, which Filament defaults to `false` — so primitives silently never cast shadows. They now expose `castShadows` (default `true`) and `receiveShadows` (default `true`). Set `castShadows = false` on a pure ground/receiver `Plane` to avoid self-shadowing.

### Changed
- **Shadows are now a first-class `FilamentView` parameter** (`filament-compose`, breaking): shadows moved out of `PostProcessing` (they're a render setting, not a post-process effect) and the `shadowingEnabled: Boolean` flag was dropped. Both are replaced by one `shadows: Shadows? = Shadows.Pcf` parameter on `FilamentView`/`FilamentSceneView` — `null` disables shadowing entirely, matching how a light's `shadow = null` works. `Shadows` is now a **sealed** technique (`Shadows.Pcf`/`Pcfd`/`Vsm`/`Dpcf`/`Pcss`), each carrying only the tuning that technique uses, so there are no silently-ignored fields. **Migration:** `PostProcessing(shadows = Shadows(type = View.ShadowType.PCSS))` → `FilamentView(shadows = Shadows.Pcss())`; `shadowingEnabled = false` → `shadows = null`. Per-light `ShadowConfig` is unchanged.
- **`Engine` single-value accessors are now Kotlin properties** (`filament`, breaking): `getBackend()` → `backend`, `getSupportedFeatureLevel()` → `supportedFeatureLevel`, `getConfig()` → `config`, and `isPaused()`/`setPaused()` → `var paused`. This applies the README's stated "Kotlin properties for single-value accessors" rule to `Engine`, matching `View`/`Camera`/`MaterialInstance`. The `getXManager()` family (`getTransformManager()`, `getLightManager()`, `getRenderableManager()`, `getEntityManager()`) and argument-taking/work-performing calls (`getFeatureFlag`, `setActiveFeatureLevel`, …) deliberately stay methods — the boundary is now documented in the README's API-strategy section.
- **Sample**: the `Animation` scene now uses `rememberAnimationState` instead of feeding a manual `rememberSceneClock` into `animationTime`.
- **`Entity` typealias in `filament-compose`**: scene composables now use the `Entity` type instead of raw `Int` for entity parameters (`onCreate: (entity: Entity) -> Unit` on `Group`/`Cube`/`Sphere`/`Plane`/`Cylinder`/`Mesh`, plus internal `LocalParentEntity`). Self-documenting; `Entity` is a typealias for `Int`, so this is source- and binary-compatible.
- **`FilamentEffect`'s `onFrame`** (`filament-compose`, breaking): the callback now receives a `FrameInfo` (delta/elapsed time) instead of a raw `frameTimeNanos: Long`.
- **`Position`/`Direction`/`Scale`/`Color`** (`filament-compose`, breaking): these are now distinct **immutable** data classes instead of `typealias`es for `Float3`. The compiler now rejects passing one where another is expected (they were all interchangeable float-triples), and being immutable they're stable Compose inputs — passing them to scene composables no longer triggers the needless recompositions the mutable `Float3` caused. Construct as before (`Position(x,y,z)`, `Color(r,g,b)`, `Position(0f)`); interop with `Float3` via the new `Position(float3)` constructors, `toFloat3()`, and `Float3.toPosition()/toDirection()/toScale()/toColor()`. Common math (`+`/`-`/`*`) is available in-domain. **Migration:** `Color` is read via `.r/.g/.b` (not `.x/.y/.z`); pass a `Float3` through the new constructors/extensions instead of relying on the alias.
- **JVM build self-bootstraps jextract** (tooling): the new `downloadJextract` Gradle task (output-tracked, cached under `.gradle/jextract/`) runs as a dependency of the FFM binding generation, so a fresh `./gradlew` on the JVM path no longer fails on a missing jextract — no manual setup step, and the redundant CI install steps were dropped.

### Removed
- **`FlightCameraLoop`** (`filament-compose`, breaking): removed. `rememberFlightCameraState` now drives the flight simulation itself every frame via `OnFrame`, so the separate loop composable is no longer needed — delete the `FlightCameraLoop(state)` call.

## [0.1.2-beta06] — 2026-06-22

### Added
- **Filament 1.72.0**: Upgraded the bundled Filament engine to 1.72.0; recompiled `.filamat` materials, refreshed the `View` API surface, JS externals overrides, and the web prebuilt (the carried colored-penumbra divisor patch is reapplied, as the upstream fix landed after the 1.72.0 cut).
- **`rememberKTXEnvironment`** (`filament-compose`): one composable that loads an IBL (and optional skybox) from KTX1 data and returns populated `IndirectLightState`/`SkyboxState` to feed into `rememberFilamentScene` — replacing hand-wiring of `KTX1Loader`, texture lifetimes, and spherical harmonics.
- **`rememberHDREnvironment`** (`filament-compose`): sibling of `rememberKTXEnvironment` that loads from a raw equirectangular `.hdr` instead of pre-baked KTX, prefiltering the skybox + reflections on the GPU at load via `IBLPrefilter` (no `cmgen` step; diffuse is approximated, not baked SH).
- **`Mesh`** (`filament-compose`): public scene composable for custom triangle geometry (`positions`/`normals`/`uvs`/`indices` + material), the escape hatch beyond the built-in primitives. Auto-computes the bounding box when omitted.
- **Samples**: `Environment (KTX)` and `Environment (HDR)` scenes — the same environment loaded both ways (a model lit entirely by IBL, cubemap skybox, live intensity slider).
- **Sample**: `Texture (PNG)` scene — loads a PNG via `TextureLoader` and binds it as a lit material's `albedo` on a UV sphere.

### Changed
- **Samples**: Home menu is now a scrollable list.

### Fixed
- **`KTX1Loader.createTexture` double-free** (native/JVM): the C wrapper deleted the `Ktx1Bundle` after handing it to the ownership-taking `Ktx1Reader::createTexture` overload, which already deletes it asynchronously once the GPU consumes the upload — aborting on the render thread during `purge()`. Surfaced via `rememberKTXEnvironment` (the first real consumer of this path).
- **`HDRLoader.createTexture`** (native/JVM): rewrote the C wrapper over `stb_image`'s `stbi_loadf_from_memory`, handling both 3- and 4-channel `.hdr` data and returning a null `Texture` on decode failure. The previous `ImageDecoder` path produced a malformed equirect that aborted (`PreconditionPanic`) inside the `IBLPrefilter` chain. Surfaced via `rememberHDREnvironment`.
- **`Group` KDoc**: Corrected the stale note that lights are not parentable — a `Light` in a `Group` follows the group's translation; only its `direction` is independent of the transform.
- **`TextureLoader.createTexture`** (native): decode PNG/JPEG via `stb_image` instead of the previous path, fixing texture loads on native targets.
- **`TextureLoader.createTexture`** (web): create PNG/JPEG textures with `GEN_MIPMAPPABLE` usage so mipmaps generate correctly (carried upstream patch).
- **`Mesh` material build** (`filament-compose`): catch native errors thrown while building a material instead of aborting the render thread.
- **HDR on web**: `rememberHDREnvironment` now shows a notice instead of crashing when HDR/`IBLPrefilter` is unsupported on the web backend.

## [0.1.2-beta05] — 2026-06-17

### Added
- **Filament 1.71.6**: Upgraded the bundled Filament engine to 1.71.6; new public API: `ColorGrading.Builder.fastMath`, `MaterialBuilder.coloredPenumbra`, `Engine.Builder.colorGrading`.
- **KDoc**: Comprehensive documentation on all `commonMain` `expect` declarations across `filament`, `filament-utils`, `gltfio`, and `filamat` modules, adapted from C++ headers.
- **Test infrastructure**: `:kotlin:test-support` module with `@IgnoreJs`, `gpuBackendAvailable`, and per-target GPU gating decided at Gradle level; `RenderingTestFixture` (DEFAULT backend) covers what NOOP cannot; CI gating for iOS simulator, JVM/Windows, and web.
- **wasmJs roadmap**: Feasibility study and migration notes for a future `wasmJs` target.

### Fixed
- **Web**: Colored-penumbra divisor clamped to prevent black faces on ANGLE-D3D11; rebuilt WASM shipped.
- **Windows CMake**: Corrected flag syntax in the JVM native build (`fix(windows): correct CMake flag syntax`).
- **JVM/Windows**: `/FI<cstring>` compiler workaround for MSVC `memcpy` error in upstream `algorithm.h` `bit_cast`.

### Changed
- **`Box`**: Moved from platform actuals to a plain `commonMain` class.
- **`Light`**: Setters now gate on value equality to avoid redundant recompositions.

## [0.1.2-beta04] — 2026-06-07

### Added
- **Web entry point**: `FilamentApp { … }` helper (`filament-compose`) that handles the full web lifecycle — WASM module init, full-viewport root element creation, and `ComposeViewport` mount. The hosting `index.html` no longer needs a `<div id="root">`; `fun main() = FilamentApp { App() }` is the whole entry point.

### Fixed
- **Web Rendering**: Added a driver workaround for ANGLE-on-D3D11 instancing shader compilation errors that produced black materials.
- **Web**: Prevented a use-after-destroy crash during web `FilamentView` disposal.

## [0.1.2-beta03] — 2026-05-31

### Fixed
- **JVM Post-Processing Rendering**: Fixed a memory alignment bug in the JVM FFM bindings where `jextract` array setters for options like `Vignette`, `Bloom`, `Fog`, and `AmbientOcclusion` were corrupting memory, causing a red tint on the camera due to `NaN`s in the tone mapper.
- **JVM Hardware Picking**: Corrected `View.pick` memory access for `fragCoords` inside `PickingQueryResult`.
- **JVM Memory Integrity**: Ensured proper memory slicing for inline arrays inside C structs using standard Java `MemorySegment` access instead of generated setters.
- **Web Rendering**: Implemented Web-based Filament rendering via shared `WebViewCompositor` and per-view canvas blitting, fixing the `FilamentSurface.js.kt` rendering.
- **Texture Format**: Removed `STENCIL_INDEX` from `Texture.Format` enum to correctly align with Filament's internal `PixelDataFormat` indexing.

### Changed
- **Testing**: Planned to close binding-coverage gaps with a real-backend fixture.

## [0.1.2-beta02] — 2026-05-30

- **`filament-compose` reshaped around a value-based scene/view split (breaking).** The scene is now a value and `FilamentView` a leaf that renders it — mirroring Filament's own `Scene`/`View` model.
  - **Breaking:** world content is declared in `rememberFilamentScene { … }` (returns a `FilamentScene`); `FilamentView(scene = …)` no longer takes a content lambda. `skyboxState` / `indirectLightState` moved from `FilamentView` to `rememberFilamentScene`.
  - **Added:** `FilamentSceneView` — all-in-one single-view convenience (`rememberFilamentScene` feeding one `FilamentView`).
  - **Added:** multiple `FilamentView`s can share one `FilamentScene` (multi-view) — see the new split-view sample.
  - **Breaking:** post-processing is now value-based — pass `PostProcessing(bloom = Bloom(…), …)` to `FilamentView`, replacing the per-effect composables (`Bloom()`, `Shadows()`, …). `ToneMapping` folded into `ColorGrade`; the old `postProcessingEnabled` flag is now `PostProcessing.enabled`.
  - **Breaking:** scene composables (`Light`, `GltfInstance`, `Group`, primitives) are now `FilamentSceneScope` extensions — they only compile inside a scene declaration.
  - **Breaking:** `FilamentEffect` moved to `FilamentSceneScope`; its scope is reduced to `engine` / `scene` + `onFrame` / `onDispose`.
  - **Breaking:** removed `LocalFilamentView`, `LocalFilamentCamera`, `LocalFilamentRenderer`. Per-view raw access (`View` / `Renderer`, picking) is now via the hoisted `rememberFilamentViewState()`; `Modifier.pickOnTap` takes a `FilamentViewState`.
- **Async loaders no longer crash composition on failure.** `rememberGltfAsset`, `rememberMaterial`, and `rememberTexture` return `null` (instead of throwing) when the `load` lambda throws or the bytes don't parse, and gained an optional `onError: (Throwable) -> Unit`. Coroutine cancellation is preserved.

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

[Unreleased]: https://github.com/Erkko68/filament-kmp/compare/0.2.0...HEAD
[0.2.0]: https://github.com/Erkko68/filament-kmp/compare/0.1.3-beta03...0.2.0
[0.1.3-beta03]: https://github.com/Erkko68/filament-kmp/compare/0.1.3-beta02...0.1.3-beta03
[0.1.3-beta02]: https://github.com/Erkko68/filament-kmp/compare/0.1.3-beta01...0.1.3-beta02
[0.1.3-beta01]: https://github.com/Erkko68/filament-kmp/compare/0.1.2-beta06...0.1.3-beta01
[0.1.2-beta06]: https://github.com/Erkko68/filament-kmp/compare/0.1.2-beta05...0.1.2-beta06
[0.1.2-beta05]: https://github.com/Erkko68/filament-kmp/compare/0.1.2-beta04...0.1.2-beta05
[0.1.2-beta04]: https://github.com/Erkko68/filament-kmp/compare/0.1.2-beta03...0.1.2-beta04
[0.1.2-beta03]: https://github.com/Erkko68/filament-kmp/compare/0.1.2-beta02...0.1.2-beta03
[0.1.2-beta02]: https://github.com/Erkko68/filament-kmp/compare/0.1.2-beta01...0.1.2-beta02
[0.1.2-beta01]: https://github.com/Erkko68/filament-kmp/compare/0.1.1-rc02...0.1.2-beta01
[0.1.1-rc02]: https://github.com/Erkko68/filament-kmp/compare/0.1.1-rc01...0.1.1-rc02
[0.1.1-rc01]: https://github.com/Erkko68/filament-kmp/compare/0.1.0-beta01...0.1.1-rc01
[0.1.0-beta01]: https://github.com/Erkko68/filament-kmp/compare/0.1.0-alpha04...0.1.0-beta01
[0.1.0-alpha04]: https://github.com/Erkko68/filament-kmp/compare/0.1.0-alpha02...0.1.0-alpha04
[0.1.0-aplha03]: https://github.com/Erkko68/filament-kmp/releases/tag/0.1.0-aplha03
[0.1.0-alpha02]: https://github.com/Erkko68/filament-kmp/compare/0.1.0-alpha01...0.1.0-alpha02
[0.1.0-alpha01]: https://github.com/Erkko68/filament-kmp/releases/tag/0.1.0-alpha01
