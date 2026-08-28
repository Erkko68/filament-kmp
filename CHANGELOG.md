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
- **`KTX1Loader` decoding options default** (`filament-utils`): `createTexture`/`createIndirectLight`/`createSkybox` take `options: Options = Options()`, so the common case needs no options object.
- **Self-destroying types implement `AutoCloseable`** (`filament`, `gltfio`, `filament-utils`): `Engine`, `SurfaceOrientation`, `Manipulator`, `IBLPrefilterContext`, `EquirectangularToCubemap`, `SpecularFilter`, `ResourceLoader` and `MaterialProvider` gain `close()` alongside `destroy()`, so `use { }` and try-with-resources work.
- **Web API parity with the expanded JS bindings** (`filament`, `gltfio`, web only): the web actuals now call the engine wherever upstream's expanded `jsbindings.cpp` exposes an entry point, instead of mirroring state in Kotlin or throwing. `@PlatformGap` annotations drop from 224 to 108 — fences, `SkinningBuffer`/`MorphTargetBuffer`, `MaterialInstance.material`/`duplicate`, `Material` reflection, the `ToneMapper` hierarchy, `LightManager.shadowOptions`/`getComponentCount`, `View.shadowType`/`isFrustumCullingEnabled`, the `Texture` getters/`swizzle`/`external`, `Renderer.readPixels`/`copyFrame`, the non-indexed `RenderableManager` geometry overloads, `Camera.getFieldOfViewInDegrees`, `Scene.skybox`/`indirectLight` and the `gltfio` instance/material accessors all reach the engine now.
- **`gltfio.ResourceLoader` is a real loader on web** (`gltfio`): backed by `gltfio$ResourceLoader` with the Stb and Ktx2 texture providers registered, so `asyncGetLoadProgress()` reports real progress instead of a placeholder.
- **Option-struct fields missing from common** (`filament`): `Engine.Config.disableParallelShaderCompile`/`disableHandleUseAfterFreeCheck`/`assertNativeWindowIsValid`, `View.SoftShadowOptions.maxPenumbraRatio`/`maxSearchRadius`, `View.DepthOfFieldOptions.cocAspectRatio`, `LightManager.ShadowOptions.maxPenumbraRatio`/`maxSearchRadius`, and a nested `View.AmbientOcclusionOptions.Gtao` (6 fields) so GTAO can be tuned and not just selected. Only `Gtao` is `@PlatformGap`-annotated on web — see [Platform notes](docs/platform-notes.md).
- **`check-common-api.sh` audits struct fields** (build): the audit compared classes, nested types, constants and method *names*, so the contents of every option struct went unchecked — Filament's Android bindings expose them as bare fields. Findings are now attributed to their owning nested type, so ignoring `Renderer.FrameInfo` ignores its fields too, and `View`'s two different `maxPenumbraRatio` fields are distinguishable.
- **`check-common-api.sh` scopes the field check to the owning type** (build): coverage came from one flat, module-wide token set, so a field name declared on *some* type counted as covering it on *every* type. `--self-test` checks the parser.
- **`LightManager.ShadowOptions.penumbraScale` / `penumbraRatioScale`** (`filament`): the per-light PCSS softness pair, public in Filament's Android bindings since 1.75.0 and marshalled by `nBuilderShadowOptions`, but missing here — `check-common-api.sh` looks tokens up module-wide, so `View.SoftShadowOptions.penumbraScale` was covering for them.
- **`LightManager.ShadowOptions.polygonOffsetConstant` / `polygonOffsetSlope`** (`filament`): the depth-bias pair, unbound until now. Ignored when the View's `ShadowType` is VSM, and `@PlatformGap`-annotated on Android — the fields are package-private in Android's `ShadowOptions`, so only the other platforms reach the engine.

### Changed
- **`MaterialKey` is a data class** (`gltfio`): 35 mutable fields with a no-argument constructor was the worst construct-then-mutate struct in the API. It is now a common `data class` — `MaterialKey(unlit = true, hasBaseColorTexture = true)` — and `constrainMaterial` becomes an extension, matching upstream where it is a free function.
- **`MaterialKey.alphaMode` is an `AlphaMode` enum** (`gltfio`, **source-breaking**): was a raw `Int`. `AlphaMode.OPAQUE`/`MASK`/`BLEND`.
- **`MaterialProvider.needsDummyData` takes a `VertexBuffer.VertexAttribute`** (`gltfio`, **source-breaking**): was a raw `Int` attribute ordinal.
- **`enum.entries` replaces `enum.values()`** (`filament`, `filamat`, `gltfio`, `filament-utils`): 216 lookups, many of them in property getters that ran on every read. `values()` allocates a fresh array per call; `entries` is cached.
- **`Engine.getSteadyClockTimeNano()` is `Engine.steadyClockTimeNano`** (`filament`): the last zero-argument getter left in the common surface.
- **`Engine.paused` is `Engine.isPaused`** (`filament`, **source-breaking**): the only mutable boolean in the library without the `is` prefix, and upstream's Android binding already calls it `isPaused`.
- **`Viewport` is a data class and `Box` has value semantics** (`filament`): both had no `equals`, so hoisting one into Compose state saw every recomposition as a change. `Box`'s redundant no-arg secondary constructor is gone.
- **Symmetric accessor names on the managers** (`filament`): `setCulling`/`isCullingEnabled` becomes `setCullingEnabled`/`isCullingEnabled`, `getFogEnabled` becomes `isFogEnabled`, and `setCastShadows`/`setReceiveShadows` become `setShadowCaster`/`setShadowReceiver` to match `isShadowCaster`/`isShadowReceiver`.
- **`Entity` where it was a bare `Int`** (`filament`, `gltfio`): `View.fogEntity`, `FilamentInstance.root` and `attachSkin`/`detachSkin`'s `target`.
- **`Filamat.shutdown()` replaces `MaterialBuilder.shutdown()`** (`filamat`): init and teardown now live on the same object, matching `Filament.init()` and `Gltfio.init()`.
- **`Fov` is declared once** (`filament-utils`): `Manipulator.Fov` becomes a `typealias Fov = Camera.Fov`, removing five duplicate per-platform enums.
- **`SwapChain` frame callbacks are clearable** (`filament`): `setFrameCompletedCallback`/`setFrameScheduledCallback` take `(() -> Unit)? = null`, matching every other callback in the library; passing null releases the arena or `StableRef` the previous callback held.
- **One idiom for bitmask constants** (`filament`): `Texture.Usage` and `Material.UserVariantFilterBit` are `object`s rather than empty classes wrapping a companion, and `Renderer`'s `MIRROR_FRAME_FLAG_*` constants move into `Renderer.MirrorFrameFlag` as `COMMIT`/`SET_PRESENTATION_TIME`/`CLEAR`.
- **One out-parameter convention** (`filament`, **source-breaking** for named arguments): every optional output buffer is now `out: T? = null`. `LightManager.getDirection`/`getPosition`/`getColor` no longer require an array, `TransformManager`/`RenderableManager` no longer require an explicit `null` and their `outLocalTransform`/`outWorldTransform`/`outEntities`/`outBox` parameters are named `out`, and `Scene.getEntities()`'s two overloads collapse into one.
- **Zero-argument getters are properties** (`filament`, `filamat`, `gltfio`, `filament-utils`): 70 `getX()`/`isX()` functions become `val`s, and `Engine`'s `activeFeatureLevel` / `isAutomaticInstancingEnabled` become `var`s. `Material` now reads like `MaterialInstance` — `material.name`, `material.isDoubleSided`, `material.maskThreshold`.
- **gltfio returns `List` instead of `Array`** (`gltfio`): `resourceUris`, `skinNames`, `materialVariantNames`, `materialInstances`, `assetInstances`, `materials` and `getMorphTargetNames()` hand back read-only collections, matching `Material.parameters` in `filament`.
- **Raw native handles are no longer public API** (`filament`, `filamat`, `gltfio`, `filament-utils`): every wrapper's backend handle is `internal`; reading one goes through a new `@InternalFilamentApi val X.nativeObject` per platform, and the setters are gone entirely. `NativeHandle` no longer appears in any api dump.
- **Web `View` stops mirroring engine state** (`filament`): 25 cached fields drop to 4 — only `scene`/`camera`/`renderTarget`/`colorGrading` stay cached for wrapper identity, exactly as the JVM and Android actuals do. Every option struct, `name`, `viewport`, `visibleLayers`, `dithering`, `antiAliasing` and `getLastDynamicResolutionScale` is engine-backed, and the option setters push the fields they had been dropping (bloom `quality`/`lensFlare`/`ghost*`/`halo*`, DoF ring counts, AO `quality`/`bentNormals`).
- **[Platform notes](docs/platform-notes.md) rewritten for the new web surface**: the remaining gaps are now mostly structural rather than unbound — canvas-scoped `createSwapChain`, non-blocking `Fence.wait`, browser-managed frame pacing, and the five option fields beamsplitter skips because they are pointers or nested structs.

### Fixed
- **`Renderer.FrameRateOptions` defaults disagreed across platforms** (`filament`): web answered `headRoomRatio = 1.0`, `scaleRate = 1.0` and `history = 1` where every other platform — and upstream — used `0.0`, `1/15` and `15`.
- **Android's option structs exposed their upstream object** (`filament`): the `native`/`nativeInfo`/`nativeOptions` field on each of `View`'s 14 option structs and `Renderer`'s 3 was public. They are body properties rather than constructor parameters, so the handle-sealing pass missed them, and the JVM-only api dumps could not catch them.
- **Leaked `Surface` on transparent Android views** (`filament-compose`): the `TextureView` path wrapped the `SurfaceTexture` in a `Surface` and never released it, leaving it to the finalizer.
- **Integer material parameters were marshalled as floats on web** (`filament`): `MaterialInstance.setParameter(name, Int…)` — scalar, `int2`/`int3`/`int4` and the array forms — all routed through `setFloatParameter`, so an `int` uniform silently received a float. They use `setIntParameter`/`setInt{2,3,4}Parameter` now, and boolean vectors `setBool{2,3,4}Parameter`.
- **Web `Material` reflection returned invented values** (`filament`): `getShading()` always answered `LIT`, `getBlendingMode()` `OPAQUE` and `getParameterCount()` `0`, so `hasParameter()` was always `false`. All query the material now.
- **Web tone mappers silently degraded** (`filament`): `PBRNeutral`, `GT7` and `Agx` all fell back to ACES, and `Generic` ignored its contrast/mid-gray/hdrMax parameters. Each constructs its own operator now.
- **Web texture formats fell back to `RGBA8`** (`filament`): `Texture.Builder.format` mapped only 10 of the 109 `InternalFormat` values, so e.g. a `DEPTH24_STENCIL8` texture was built as colour. The mapping is exact in both directions now.
- **`AssetLoader.destroy` leaked the loader on web** (`gltfio`): it called `delete()`, whose embind destructor for `AssetLoader` is a no-op; it calls the static `destroy` that actually frees it now.
- **`Engine.destroy` leaked its canvas and GL context on web** (`filament`): the hidden canvas `Engine.create` allocates stayed in the document with its WebGL context live, so repeated create/destroy walked into the browser's context limit. A caller-supplied canvas is left untouched.
- **Web `View.depthOfFieldOptions` dropped `cocAspectRatio`** (`filament`): the setter pushed it to the engine but the getter never read it back, so it always answered the 1.0 default.
- **`Filamat.init()` did nothing on iOS** (`filamat`): the native actual was an empty body while `shutdown()` still called through, so every `build()` returned `Package::invalidPackage()` and each unpaired `shutdown()` tore down glslang's process state again — enough repetitions segfaulted. `MaterialBuilderTest` now asserts the package is valid rather than merely non-null.
- **Sampler and subpass material parameters reported the wrong type on JVM and iOS** (`filament`): `Material.parameters` read `ParameterInfo.type` as a `UniformType` without checking `isSampler`/`isSubpass`, which select the other two arms of a union — so a `sampler2d` came back as `BOOL`. JVM, iOS and web share one decoder now; Android's flattening already came from the AAR.
- **An exception from a Filament callback killed the process** (`filament`): anything thrown out of a `readPixels`, buffer-upload, `compile`, picking or frame callback escaped into native code, which terminates the VM on JVM and the process on iOS. They now report as uncaught exceptions.
- **`SwapChain` frame callbacks outlived the swapchain** (`filament`): `Engine.destroySwapChain` left the upcall stub's arena (JVM) or the `StableRef` (iOS) allocated. Re-setting a callback also freed the stub the backend may still hold for an in-flight frame; each swapchain now keeps one stub and swaps the function behind it.
- **`SwapChain.setFrameScheduledCallback` froze presentation on Metal** (`filament`): the C wrapper dropped the `PresentCallable`, which on Metal makes the callback's owner responsible for presenting — so the frame was never presented and its memory leaked. It is invoked before the callback now, matching the order every other backend already uses.
- **`SwapChain.setFrameScheduledCallback` reaches the engine on web** (`filament`): it was tracked locally only. Requires the `setFrameScheduledCallback`/`isFrameScheduledCallbackSet` embind bindings added upstream.
- **`SwapChain.setFrameCompletedCallback(null)` never detached the callback** (`filament`): the C wrapper always installed a lambda, so `isFrameScheduledCallbackSet` stayed `true` after clearing. Android's binding cannot detach at all, so it now answers from what you set through the wrapper — see [Platform notes](docs/platform-notes.md).
- **Off-heap buffers handed to the backend were byte-aligned** (`filament`, JVM): `readPixels` and the vertex/index/texture uploads allocated with `allocate(size)`/`allocateFrom(JAVA_BYTE, …)`, which promise no more than 1-byte alignment for data the backend reads element-wise.
- **`Engine.config` reported defaults on JVM and iOS** (`filament`): the C wrapper has no `getConfig`, and both actuals answered a fresh `Config()` rather than the one the `Builder` was given. `Builder.build()` hands the Config to the Engine now.

### Removed
- **`View.FogOptions.densityMap`** (`filament`, **source-breaking**): the field was fabricated — upstream's `FogOptions` has no such member, so it was write-only and did nothing. Use `skyColor`.

## [0.4.0] — 2026-08-19

> [!WARNING]
> **Recompile your materials** — `MATERIAL_VERSION` 74 → 75. Any `.filamat` you ship must be rebuilt with 1.75.0's `matc`; the engine rejects blobs from another version. The built-in `StandardMaterial`s ship recompiled.

### Added
- **Filament 1.75.0**: engine upgraded. Newly bound: `EntityManager.advanceEpoch()`/`getMaxEntityCount()`, `AssetLoader.gc()`.
- **`LensShift` / `LensScaling`** (`filament-compose`): typed replacements for the raw `Float2` on `CameraState.shift`/`scaling`; both convert to and from `Float2`.
- **`Color.toLinearColor()`** (`filament-compose`): Compose UI `Color` → scene `LinearColor`, applying the sRGB→linear transfer.
- **Transparent views** (`filament-compose`): `FilamentView`/`FilamentSceneView` take `transparent = true` to render over the Compose UI behind them — see [Transparency](docs/compose/integration-strategies.md#transparency) and the new `Transparency (GLB)` sample.
- **WebGL context attributes** (`web`): `EngineCreateOptions` declares `alpha`, `antialias`, `depth`, `majorVersion`, `minorVersion`, forwarded to `getContext("webgl2", …)`; `Engine.create` always requests `alpha: true`.

### Changed
- **Geometry parameters precede the transform on every primitive** (`filament-compose`, **source-breaking** for positional callers): `Cube`/`Sphere`/`Cylinder`/`Plane`/`Mesh` all read material → shape → transform → flags → `onCreate`; `Mesh.boundingBox` joins the geometry, and `GltfInstance` moves `visible` ahead of the shadow flags.
- **`ShadowConfig.bulbRadius` defaults to automatic** (`filament-compose`, behaviour-changing): `-1f` lets Filament derive it per light type (1.0 directional, 0.06 spot, sun angular radius × halo) instead of the hardcoded `0.02f`. PCSS-only as of 1.75.
- **Required parameters lead every Compose signature** (`filament-compose`, **source-breaking** for positional callers): `FilamentSceneView(engine, environment, modifier, …)` and `rememberKTX`/`rememberHDREnvironment(engine, …)`; `modifier` keeps its slot as the first optional.
- **`engine` is required on `rememberKTXEnvironment`/`rememberHDREnvironment`** (`filament-compose`): both run outside `rememberFilamentScene { }`, so the `LocalFilamentEngine` default could only ever throw.
- **Actionable error when no engine is in scope** (`filament-compose`): the failure now throws from the call site naming both fixes, not from inside `compositionLocalOf`.
- **`LocalFilamentScene` is now internal** (`filament-compose`, **source-breaking**): use `FilamentEffect { }`, which hands you engine + scene and disposes what you add.
- **Composition locals split by whether they change** (`filament-compose`): engine/scene moved to `staticCompositionLocalOf`; `LocalParentEntity`/`LocalGroupVisible` stay dynamic.
- **`rememberRenderTargetTexture(postProcessing = …)` is no longer nullable** (`filament-compose`): defaults to `PostProcessing(enabled = false)`, matching `FilamentView`'s type.
- **`FrameInfo` annotated `@Immutable`** (`filament-compose`) for Compose stability tracking.

### Fixed
- **Conditional `DisposableEffect` in `rememberMaterialInstance`** (`filament-compose`): the disposal effect entered and left the composition as the material loaded; the null material short-circuits before it now.
- **Filament objects mutated during composition** (`filament-compose`): `FilamentView` and `rememberRenderTargetTexture` applied view wiring inside `remember { }`; both use a keyed `DisposableEffect` now.
- **Unkeyed `remember` on engine-owned objects** (`filament-compose`): `FilamentEffect`'s scope and the `Group`/`Light` entities are keyed on `engine`, so swapping engines no longer leaves them pointing at a destroyed one.
- **`CameraState` matrix scratch buffers** (`filament-compose`): `viewMatrix`/`projectionMatrix` reuse pre-allocated arrays instead of allocating per read.

### Migration from `0.3.1`

The API changes below break **positional** callers only.

| Old | New |
|---|---|
| `Cube(mat, position, rotation, scale, pivot, size, …)` | `Cube(mat, size, position, rotation, scale, pivot, …)` — shape leads the transform on `Cube`/`Sphere`/`Cylinder`/`Plane` |
| `Mesh(…, indices, position, rotation, scale, pivot, boundingBox, …)` | `Mesh(…, indices, boundingBox, position, rotation, scale, pivot, …)` |
| `GltfInstance(…, castShadows, receiveShadows, visible, …)` | `GltfInstance(…, visible, castShadows, receiveShadows, …)` |
| `ShadowConfig()` gave a 2 cm bulb | `ShadowConfig()` now means *automatic*; pass `bulbRadius = 0.02f` to keep the old look |
| `FilamentSceneView(modifier, …, environment)` / `rememberKTXEnvironment(…, engine = …)` | `FilamentSceneView(engine, environment, modifier, …)` / `rememberKTXEnvironment(engine, …)` |
| `rememberRenderTargetTexture(postProcessing = null)` | `rememberRenderTargetTexture(postProcessing = PostProcessing(enabled = false))` |
| `LocalFilamentScene.current` | now internal — use `FilamentEffect { }` |
| `Float2` on `CameraState.shift`/`scaling` | `LensShift` / `LensScaling` |

## [0.3.1] — 2026-08-14

### Added
- **Filament 1.74.1**: engine upgraded; embedded standard, test and sample `.filamat` recompiled.
- **`Renderer` APIs** (`filament`): bound `materialTime`, `setMaterialTimeEpoch` and `pauseRenderThread` on all 5 platforms.
- **Named axis directions** (`filament-compose`): `Direction.Up`/`Down`/`Left`/`Right`/`Forward`/`Back`/`Zero`, with `Forward` spelling out the −Z convention; `Rotation.lookTowards` defaults `up` to `Direction.Up`.
- **`Rotation.toRotationMatrix()`** (`filament-compose`): the column-major 3×3 that Filament's builders take.
- **Scene value-type test suite** (tests): `TypesTest` covers `Position`/`Direction`/`Scale`/`Rotation`/`Color` — operators, interop, edge cases, and the copy-not-alias property the Compose stability work rests on.

### Changed
- **`Color` renamed to `LinearColor`** (`filament-compose`, source-breaking): it collided with `androidx.compose.ui.graphics.Color`, and the two are *not* interchangeable. `Float3.toColor()` → `toLinearColor()`; construction from a Compose colour moves to `LinearColor.fromComposeColor(…)`. No deprecated alias.
- **`CameraState.viewMatrix`/`projectionMatrix` return `Mat4`** (`filament-compose`, source-breaking): were raw `FloatArray`/`DoubleArray`. The projection narrows from Filament's `double`s — go through the camera directly for full precision.
- **`rotation` takes a `Rotation`, not a `Quaternion`** (`filament-compose`, source-breaking): every transform composable takes the new immutable value type — `Quaternion` is a `data class` with `var` components, so Compose inferred it unstable. Build with `Rotation.axisAngle`/`euler`, compose with `*`, convert losslessly via `Rotation(quat)`/`toQuaternion()`.
- **`Rotation`'s hot operators no longer round-trip through `Quaternion`** (`filament-compose`): `*`, `inverse`, `normalized`, `axisAngle` and `nlerp` do their own float math — one allocation instead of four per call.
- **Compose stability audited end to end** (`filament-compose`): a `compose-stability.conf` marks Filament's opaque handles stable and `@Stable`/`@Immutable` annotate every state holder and value type. Unstable composable arguments dropped 108 → 36. Build with `-PcomposeMetrics` for the reports.
- **`IndirectLightState.rotation` takes a `Rotation`** (`filament-compose`, source-breaking): was a raw `FloatArray` whose identity equality forced a `toList()` copy as the effect key.

### Fixed
- **Compose UI colour interop ignored the sRGB transfer function** (`filament-compose`, behaviour-breaking): the 0.3.0 `Color(composeColor)` constructor copied gamma-encoded channels into linear space — sRGB 0.5 became 0.5 instead of 0.214. `LinearColor.fromComposeColor()` converts properly; drop any pre-darkening workaround.
- **`Rotation.fromTo` was wrong for non-unit inputs** (`filament-compose`): both inputs are normalized now — `fromTo((3,0,0), (0,5,0))` gave 172° for a 90° turn. `lookTowards` had the same flaw in its degenerate-basis check.
- **`Rotation.axisAngle` around a zero-length axis produced a NaN transform** (`filament-compose`): returns `Rotation.Identity` now.
- **`Rotation.inverse()` was only correct for unit-length rotations** (`filament-compose`): scales by 1/‖q‖² now; `Rotation * Direction` likewise no longer scales the vector.
- **`Rotation.nlerp` could take the long way round** (`filament-compose`): it now negates the end rotation across hemispheres, as `slerp` already did.
- **Right-click panning did nothing on desktop and web** ([#98](https://github.com/Erkko68/filament-kmp/issues/98)) (`filament-compose`): `awaitFirstDown` is primary-button-only on skiko, so the gesture never opened. Flight mouse-look was dead on right-drag for the same reason.
- **Vertical panning, orbiting and mouse-look were inverted** (`filament-compose`, behavior-breaking, all platforms): `Manipulator` reads bottom-left viewport coordinates and the gesture modifiers passed Compose's top-left `y` through. Also fixes the pinch-zoom centre and the scroll-wheel anchor. Pass a negative `panSpeedY`/`orbitSpeedY` for the classic inverted axis.
- **Flight camera speed is finally settable** (`filament-compose`, behavior-breaking): `rememberFlightCameraController` gains `initialMoveSpeed` and `speedSteps` (default 20, was 80) plus `adjustSpeed(steps)` — `maxMoveSpeed` alone did nothing until you scrolled. Scrolling up now speeds up.
- **`GltfInstance` boxed every morph weight on every recomposition** (`filament-compose`): the effect keyed on `morphWeights?.toList()`, allocating before the gate could skip anything. Compared with `contentEquals` against a remembered copy now. Adds `GltfInstanceLifecycleTest` and `GltfAssetLoadingTest`.
- **Documented `key()` for dynamic scene contents** (docs): without it, inserting or reordering destroys and rebuilds every entity after the change point — silently. See [Compose Integration](docs/compose/README.md#dynamic-scene-contents-use-key).
- **Morph weights below four targets did nothing on web** (`filament`): `setMorphWeights` bailed out under four weights, so every 1–3 target model was unanimated. Zero-padded now; a non-zero `offset` returns early rather than writing to slot 0. Four instance-side methods are marked `@PlatformGap` — see [Platform notes](docs/platform-notes.md).
- **Unparseable glb bytes threw on `wasmJs` instead of returning null** (`gltfio`): `createAsset`/`createInstancedAsset` were declared non-null following upstream's `filament.d.ts`, but return null on parse failure. Kotlin/JS was unaffected.
- **`getAnimator()` before resource load handed back a broken `Animator`** (`gltfio`, behavior-breaking): gltfio returns `nullptr` until resources load. `jvm`/`native`/`web` throw `IllegalStateException` naming the fix; Android cannot check.
- **Flight start orientation was off by a factor of 57** (`filament-compose`, behavior-breaking): `startPitch`/`startYaw` are documented in degrees but were passed as radians. Drop any manual conversion.

## [0.3.0] — 2026-07-22

> [!WARNING]
> **Recompile your materials** — `MATERIAL_VERSION` 73 → 74. Any `.filamat` you ship must be rebuilt with 1.74.0's `matc`; the engine rejects blobs from another version. The built-in `StandardMaterial`s ship recompiled.

### Added
- **Filament 1.74.0**: engine upgraded; no public C++ API changes. The web prebuilt comes from our fork's `feat/webgl-bindings-coverage-1.74` branch pending the upstream PR.
- **Android-API parity sweep** (`filament`): bound every remaining public Filament Android API missing from common — GTAO selection, typed TAA options (`boxType`/`boxClipping`/`jitterPattern` are enums now, source-breaking if you set them), `View.gridSize`/`effectiveGridSize`, `Engine.hasUnrecoverableFailure`, `MaterialInstance.getConstant*`, `ColorGrading.Builder.customLut`, `RenderableManager.getEnabledAttributesAt`. `getConstant*` and `customLut` throw on web (`@PlatformGap`).
- **Transparent built-in material** (`filament-compose`): `StandardMaterial.Transparent` + `rememberTransparentColorMaterialInstance(color, alpha, …)` — pre-multiplied, two-pass, precompiled, works on every target.
- **`visible` on primitives, `GltfInstance` and `Group`** (`filament-compose`): a show/hide toggle that pulls renderables from the scene while keeping entities and state alive; on `Group` it hides the whole subtree.
- **`CameraState.focusDistance`** (`filament-compose`): the depth-of-field focal plane is settable — it was stuck at the native default.
- **Compose UI colour interop** (`filament-compose`): construct a scene `Color` from `androidx.compose.ui.graphics.Color` and back via `toComposeColor()`.
- **Shared `CameraController` interface** (`filament-compose`): orbit/map/flight controllers share `setViewport`/`resetToHome`/`saveBookmark`/`jumpToBookmark`, so generic UI can drive whichever is active.
- **`environment` overloads** (`filament-compose`): `rememberFilamentScene(engine, env) { … }` and `FilamentSceneView(engine, env, …)` take a loaded `Environment` in one argument.
- **`castShadows`/`receiveShadows` on `GltfInstance`** (`filament-compose`): nullable per-instance overrides (`null` keeps what the asset authored), closing the gap with the primitives.
- **Smarter upgrade tooling** (build): `check-common-api.sh` audits classes, nested types and enum constants, reads suppressions from an ignores file and exits non-zero on gaps; `upgrade-diff.sh` gained a HIGHLIGHTS section.

### Known issues
- Upstream regression (stock 1.74.0, all backends): a scene lit **only** by punctual lights panics in `renderView` under VSM/DPCF/PCSS — the requested variant collides with the reserved SSR variant. Use PCF for spot-only scenes.

### Changed
- **JVM surface readback is zero-copy** (`filament-compose`): `readPixels` writes straight into Skia-owned memory, dropping two full-frame CPU copies and an off-heap allocation per frame. The blit uses linear sampling and flips vertically on OpenGL, whose row order is bottom-up unlike Metal's.
- **Scene-state creators name their seeds `initial*`** (`filament-compose`, source-breaking for named args): these parameters seed initial composition only, per the `rememberPagerState` convention — mutate the returned object to change them later. Manipulator tuning params stay reactive and now restore the current pose on rebuild.
- **Material loading chains without unwrapping** (`filament-compose`, source-breaking): `rememberMaterialInstance` accepts `Material?` and returns `MaterialInstance?`, and every primitive accepts a nullable material — no more `?.let` scaffolding.
- **Picking takes Compose coordinates everywhere** (`filament-compose`, behavior-breaking if you flipped Y yourself): `FilamentViewState.pick` converts internally; remove manual `viewport.height - y`. Gesture modifiers also sync `Manipulator.setViewport` automatically.
- **`ColorGrade.toneMapping` is a value type** (`filament-compose`, source-breaking): `toneMapper: ToneMapper` → `toneMapping: ToneMapping` (sealed, structural equality); the native operator is built at apply time.
- **Lights follow group rotation by default** (`filament-compose`, behavior-breaking): `followGroupRotation = true`; pass `false` to pin world-space direction.
- **Assorted API cleanups** (`filament-compose`, source-breaking): `Light` uses `vararg keys` instead of `rebuildKey`; `rememberRenderTarget` → `rememberRenderTargetTexture`; `rememberEmissiveMaterialInstance` gained a default `intensity`; `CameraState` throws when attached to two views instead of racing.
- **Loader `engine` parameter moved last** (`filament-compose`, source-breaking for positional callers): every loader shares `(config…, key, onError, engine, load)`. Pass `engine` by name — positional-first now silently binds elsewhere.
- **All gesture modifiers are plain `Modifier` extensions** (`filament-compose`): `flightGestures` is no longer `@Composable`; its `FocusRequester` moved into the controller.
- **Camera controllers renamed `*CameraState` → `*CameraController`** (`filament-compose`, source-breaking): they *drive* a `CameraState` rather than being siblings of one.
- **`LightIntensity` value type replaces `intensity`/`intensityUnit`/`efficiency`** (`filament-compose`, source-breaking): one value — `LuminousPower(v)`, `Candela(v)` or `Watts(watts, efficiency)` — which also makes the Watts-only `efficiency` unrepresentable with other units.
- **Library warnings route to the platform log** (`filament-compose`): an internal per-platform sink (Logcat / `console.warn` / stderr / `NSLog`) replaces raw `println`.
- **`onCreate` callbacks are scope receivers everywhere** (`filament-compose`, source-breaking): primitives and `Group` take `EntityScope.() -> Unit` (entity + engine in scope), matching `GltfInstance`.
- **Boolean flags renamed to the `*Enabled` convention** (`filament-compose`, source-breaking): `Bloom.threshold` → `thresholdEnabled`, `enablePanning` → `panningEnabled`, `SkyboxSource.Color(rgb)` → `color`.
- **`rememberRenderTargetTexture` takes a full `PostProcessing?`** (`filament-compose`, source-breaking): replaces `postProcessingEnabled: Boolean`, so bloom and colour grading work off-screen. The deprecated `rememberRenderTarget` alias is gone.
- **`AnimationMixer.isPaused` is snapshot state** (`filament-compose`): reading it in composition subscribes.

### Fixed
- **`PostProcessing(renderQuality = null)` restores the native default** (`filament-compose`): it stuck at whatever a prior config set instead of returning to `HIGH`.
- **Flight camera no longer jumps on tuning changes** (`filament-compose`): it carries the current pose across a rebuild like orbit/map, instead of snapping to `startPitch`/`startYaw`.
- **`Mesh` no longer re-uploads geometry on every recomposition** (`filament-compose`): geometry is compared by array content, so identical inline `floatArrayOf(…)` arguments don't rebuild the buffers.
- **JVM surface readback thread-safety** (`filament-compose`): buffer bookkeeping is atomic and Skia images close only on the UI thread — a backend-thread callback could close the image mid-draw.
- **`PostProcessing()` no longer disables FXAA** (`filament-compose`): `antiAliasing = null` set AA to `NONE`; `null` now means "leave the native default".
- **Colour grading no longer re-bakes its LUT every recomposition** (`filament-compose`): `ToneMapper`'s identity equality made every `colorGrade` value unequal, forcing an expensive rebake per frame in animated scenes.
- **`GltfInstance` no longer silently aliases the primary instance** (`filament-compose`): when `createInstance` fails only the first instance falls back; a second renders nothing and warns.
- **Web TAA options no longer drop fields** (`filament`): the setter forwards all nine option fields instead of resetting them to defaults.
- **`downloadPrebuilts` works again on 1.73.0** (build): dropped the dead `macosX64` target — upstream stopped shipping mac x86_64 libs.

### Migration from `0.2.0`
`filament-compose` had an API-consistency sweep; most changes are mechanical renames. `filament`/`gltfio` gained bindings only.

| Old | New |
|---|---|
| `rememberOrbitCameraState`/`rememberMapCameraState`/`rememberFlightCameraState` and `OrbitCameraState`/`MapCameraState`/`FlightCameraState` | `remember*CameraController` and `*CameraController` |
| `intensity = 100_000f` (light) | `intensity = LightIntensity.LuminousPower(100_000f)` (or `.Candela`/`.Watts`) |
| `Bloom.threshold`, `enablePanning` | `Bloom.thresholdEnabled`, `panningEnabled` |
| `SkyboxSource.Color(rgb = …)` | `SkyboxSource.Color(color = …)` |
| `rememberRenderTarget` | `rememberRenderTargetTexture` — takes `postProcessing: PostProcessing? = null` |
| `ColorGrade.toneMapper: ToneMapper` | `ColorGrade.toneMapping: ToneMapping` |
| `onCreate = { entity -> … }` (primitives, `Group`) | `onCreate = { /* entity, engine in scope */ }` |
| `rememberMaterial(engine, key, onError) { … }` (positional `engine`) | pass `engine` by name — its position moved next to the trailing lambda |
| Manual `viewport.height - y` before `FilamentViewState.pick` | remove it — `pick` converts from Compose coords itself |

Also check: state-creator parameters now seed initial composition only; `Modifier.flightGestures` is non-`@Composable`; lights in a rotating `Group` re-aim by default.

## [0.2.0] — 2026-07-16

> [!WARNING]
> **Recompile your materials** — `MATERIAL_VERSION` 72 → 73. Any `.filamat` you ship must be rebuilt with 1.73.0's `matc`; the engine rejects blobs from another version. The built-in `StandardMaterial`s ship recompiled.

### Added
- **Filament 1.73.0**: engine upgraded; the DYN variant became a specialization constant. New APIs: `Renderer.setDesiredPresentationTime`/`setRenderingDeadline`, `SwapChain.isFrameRateChangeSupported`/`setFrameRate`, `View.getVisibleRenderableCount`. The web prebuilt is stock upstream again — our colored-penumbra patch landed in 1.73.0.
- **`@PlatformGap` annotation + binding-coverage table** (all modules): every common API whose platform binding is missing or degraded is annotated and listed in [Platform Notes](docs/platform-notes.md#binding-coverage). All current gaps are on web; Android/iOS/JVM expose the full common API.
- **Tier C semantic frame tests** (tests): a `FrameProbe` harness renders a lit scene headless and asserts *relations between image regions* (shadow darker than open floor, removing the sun changes the frame) — rasterizer-invariant property checks, not goldens, aimed at the "wrong pixels, no exception" bug class.
- **Vendored kotlin-math test suite** (tests): upstream's `HalfTest`/`MatrixTest`/`QuaternionTest` now run on every target, guarding the ~5,300-line vendored math library against drift (utils line coverage 2.9% → 28.5%).
- **Exhaustive enum round-trip tests** (tests): every entry of every gettable enum-typed property is set→get round-tripped on every target, turning the silently-misaligned-enum bug class into a test failure.
- **Test materials load on every target** (tests): `.filamat` blobs are base64-embedded into commonTest at build time, so material/renderable tests run on web, iOS and Android instead of silently skipping.

### Changed
- **Dropped the `-beta` suffix** (release): versions are plain `X.Y.Z` — see [README → Versioning & stability](README.md#versioning--stability).
- **Python is no longer a build dependency** (build): the download scripts are pure-JVM Gradle tasks in `build-logic`, and prebuilt downloads are version-aware, so bumping `filaVersion` re-extracts automatically.
- **`buildSrc` became the `build-logic` included build** (build): convention-plugin edits no longer invalidate the whole task graph.
- **Public-API surface is CI-enforced** (build): binary-compatibility-validator guards the JVM ABI of the five published modules; API changes must ship a regenerated `apiDump`.

### Fixed
- **Half-precision arithmetic was wrong in the subnormal range** (`filament-utils`, all platforms): the vendored `Half.kt` had drifted from upstream — multiplication/division and float↔half conversion were off by 2× for subnormals, and `Quaternion.fromEuler` (YZX) was slightly off. Both files re-vendored verbatim.
- **Readback smoke test could pass without verifying anything** (tests): `RendererRenderingTest` only checked pixels *if* the async callback landed; delivery is asserted now (web excluded — `readPixels` is unbound).
- **Soft shadows crashed with built-in materials** (`filament-compose`, all platforms): the precompiled lit materials filtered out `vsm` variants, which Filament selects for *all* soft shadow types — enabling any of them panicked the engine. The VSM variants ship now.

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

[Unreleased]: https://github.com/Erkko68/filament-kmp/compare/0.4.0...HEAD
[0.4.0]: https://github.com/Erkko68/filament-kmp/compare/0.3.1...0.4.0
[0.3.1]: https://github.com/Erkko68/filament-kmp/compare/0.3.0...0.3.1
[0.3.0]: https://github.com/Erkko68/filament-kmp/compare/0.2.0...0.3.0
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
