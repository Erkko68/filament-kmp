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

## [0.4.0] — 2026-08-19

### Added
- **Filament 1.75.0**: Upgraded bundled Filament engine to 1.75.0. `MATERIAL_VERSION` went **74 → 75**, so every embedded standard, test, and sample `.filamat` was recompiled — **if you ship your own compiled materials you must recompile them with 1.75.0's `matc`**, since the engine rejects a `.filamat` built by a different `matc` version. Newly bound: `EntityManager.advanceEpoch()`, `EntityManager.getMaxEntityCount()`, `AssetLoader.gc()`. 1.75's new per-light PCSS controls (`penumbraScale`, `penumbraRatioScale`, `maxPenumbraRatio`, `maxSearchRadius`) and the matching `SoftShadowOptions` globals are C++-only upstream — Filament's own Android binding doesn't expose them, so there is nothing to mirror yet.
- **`LensShift` / `LensScaling`** (`filament-compose`): typed replacements for the raw `Float2` that `CameraState.shift` and `CameraState.scaling` used to take — the last two float-tuples in the Compose surface that weren't wrapped, and the two easiest to swap by accident. `LensShift.None` and `LensScaling.Identity` name the defaults; both convert to and from `Float2` for filament-utils math.
- **`Color.toLinearColor()`** (`filament-compose`): extension converting a Compose UI `Color` to a scene-space `LinearColor`, applying the sRGB→linear transfer. Covers ARGB ints through Compose's own `Color(0xFF2196F3)` constructor.
- **Transparent views** (`filament-compose`): `FilamentView`/`FilamentSceneView` take `transparent = true`, which renders the 3D content over the Compose UI behind it instead of over an opaque background. Each platform needed a different surface: Android swaps `SurfaceView` for a non-opaque `TextureView` with a `CONFIG_TRANSPARENT` swapchain, iOS makes the `CAMetalLayer` non-opaque and hosts the interop view with `placedAsOverlay` (a plain interop view has Compose punch a hole that erases everything drawn behind it), web moves the per-view canvas in front of the Compose canvas with no hole-punch, and JVM reads back `PREMUL` instead of `OPAQUE` alpha. On top of that the view gets `BlendMode.TRANSLUCENT` and an explicit `clear = true`, alpha-0 `ClearOptions` — the default (`clear = false`, `discard = true`) leaves untouched swapchain pixels undefined, which renders as opaque garbage. Because the surface type is fixed at creation, toggling the flag rebuilds the surface. See [Compose integration strategies → Transparency](docs/compose/integration-strategies.md#transparency) and the new `Transparency (GLB)` sample.
- **WebGL context attributes** (`web`): `EngineCreateOptions` now declares `alpha`, `antialias`, `depth`, `majorVersion` and `minorVersion` — everything except `backend` is forwarded verbatim to `canvas.getContext("webgl2", …)`, which upstream's `filament.d.ts` never declared. `Engine.create` now always requests `alpha: true`, since the WebGL default of `alpha: false` forces every frame opaque no matter the view's blend mode.

### Changed
- **Geometry parameters now precede the transform on every primitive** (`filament-compose`, **source-breaking** for positional callers): `Cube`, `Sphere`, `Cylinder` and `Plane` placed their shape parameters (`size`, `radius`/`rings`/`segments`, `radius`/`height`/`segments`, `width`/`depth`/`doubleSided`) *after* `position`/`rotation`/`scale`/`pivot`, while `Mesh` — whose geometry arrays have no defaults and so cannot move — placed them first. The same five composables therefore read in two different orders. All five are now **material → shape → transform → flags → `onCreate`**, which is also the order the geometry is applied in: shape rebuilds the mesh, the transform only places it. `Mesh`'s `boundingBox` moves up to join the geometry it describes, and `GltfInstance` moves `visible` ahead of `castShadows`/`receiveShadows` to match the primitives. Every call site in this repo's samples, docs and tests already used named arguments, so this is a no-op for anyone who does the same.
- **`ShadowConfig.bulbRadius` defaults to automatic** (`filament-compose`, behaviour-changing): Filament 1.75 changed `ShadowOptions::shadowBulbRadius`'s default to `-1`, meaning "derive it from the light type" — `1.0` for directional, `0.06` (an A19 bulb) for spot, and `getSunAngularRadius() * getSunHaloSize()` for SUN. `ShadowConfig` hardcoded `0.02f` and wrote it unconditionally, so that automatic path was unreachable from Compose and every PCSS shadow got the same 2 cm bulb regardless of what was lighting it. The default is now `-1f`. Pass an explicit value only to soften or sharpen one light past what is physically correct. The KDoc also drops the claim that `bulbRadius` applies under DPCF — as of 1.75 it is PCSS-only.
- **Required parameters now lead every Compose signature** (`filament-compose`, **source-breaking** for positional callers): the `Environment` overload of `FilamentSceneView` takes `(engine, environment, modifier = Modifier, …)`, and `rememberKTXEnvironment`/`rememberHDREnvironment` take `engine` first. Previously all three placed a required parameter *after* a defaulted one, which forced every call site into named arguments and made the positional form unwriteable. `modifier` keeps its Compose-conventional slot as the first optional parameter. Trailing lambdas (`content`, `ibl`, `hdr`, `load`, `configure`) stay last, as Kotlin requires.
- **`engine` is now required on `rememberKTXEnvironment`/`rememberHDREnvironment`** (`filament-compose`): both are called *outside* `rememberFilamentScene { }` (their result feeds the scene's own parameters), so there is never an engine in scope and the `LocalFilamentEngine` default could only ever throw. The environment's textures must belong to the engine the consuming scene runs on — the signature now says so. Being required, it now leads the parameter list rather than sitting among the defaulted ones (see the ordering entry above); the three loaders whose `engine` *is* defaulted — `rememberMaterial`/`rememberTexture`/`rememberGltfAsset` — keep it just before the trailing lambda. Every existing call site already passed `engine = …` by name, so this is a no-op in practice.
- **Actionable error when no engine is in scope** (`filament-compose`): `LocalFilamentEngine` and `LocalFilamentScene` now default to `null` rather than throwing from the composition local's own default lambda, which surfaced during default-argument resolution with a stack trace pointing inside `compositionLocalOf`. The loaders' `engine` parameter stays non-null — its default is now `LocalFilamentEngine.current ?: error(…)`, which throws from the call with a message naming both fixes (declare inside `rememberFilamentScene { }`, or pass `engine = …`). Return types and parameter types are unchanged, so no call site moves: a `null` from `rememberMaterial` and friends still means "loading or failed", never "no engine". The only source-visible change is `LocalFilamentEngine.current` reading as nullable if you touch the local directly.
- **`LocalFilamentScene` is now internal** (`filament-compose`, **source-breaking**): the raw `Scene` was already internal on the `FilamentScene` handle, so publishing it through a composition local contradicted that. `FilamentEffect` is the supported route to the scene — it hands you both the engine and the scene *and* disposes what you add. `LocalFilamentEngine` stays public: user-written loaders legitimately want it as an `engine` default, and it is engine-scoped, not scene-scoped.
- **Composition locals split by whether they change** (`filament-compose`): `LocalFilamentEngine`/`LocalFilamentScene` moved to `staticCompositionLocalOf` — they never change inside a scene subtree, so tracking every read (they are read by every loader, light and primitive) bought nothing, and a new engine has to rebuild the subtree regardless. `LocalParentEntity`/`LocalGroupVisible` stay dynamic: they *do* change at runtime as groups nest and `visible` toggles, where recomposing only the readers is the cheaper trade.
- **`rememberRenderTargetTexture(postProcessing = …)` is no longer nullable** (`filament-compose`): it took `PostProcessing?` where `null` meant "skip the pass", while `FilamentView` took a non-null value — the same name and type with opposite defaults on two neighbouring APIs. `PostProcessing` already carries an `enabled` master switch, so the default is now `PostProcessing(enabled = false)`, which says the same thing in the type the rest of the API uses. Passing `null` no longer compiles; pass `PostProcessing(enabled = false)`.
- **Compose stability for frame info** (`filament-compose`): annotated `FrameInfo` with `@Immutable` for Compose compiler stability tracking.

### Fixed
- **Conditional `DisposableEffect` in `rememberMaterialInstance`** (`filament-compose`): the instance-disposal effect sat inside an `if (instance != null)`, so it entered and left the composition as the material loaded — the null material now short-circuits before the effect, keeping the call unconditional.
- **Filament objects mutated during composition** (`filament-compose`): `FilamentView` and `rememberRenderTargetTexture` applied their view wiring and render flags inside a `remember { }` calculation block, i.e. as a side effect during composition. They now use the keyed `DisposableEffect(…) { …; onDispose {} }` idiom the rest of the module already uses (`Group`'s transform, `rememberMaterialInstance`'s parameters), so the writes land after composition commits. Same keys, so the same re-application behaviour.
- **Unkeyed `remember` on engine-owned objects** (`filament-compose`): `FilamentEffect`'s scope and the entities created by `Group` and the low-level `Light` were remembered without an `engine` key, so swapping the engine left them pointing at the destroyed one. All three are now keyed like every other engine-derived value in the module.
- **`CameraState` matrix scratch buffers** (`filament-compose`): `viewMatrix` and `projectionMatrix` reuse pre-allocated arrays instead of asking the binding to allocate one per read. Saves the backing-array allocation only; each read still builds a fresh `Mat4`. Safe because a `CameraState` is attached to a single view.

### Migration from `0.3.1`

**Recompile your materials.** `MATERIAL_VERSION` went 74 → 75 with Filament 1.75.0: any `.filamat` you ship must be rebuilt with 1.75.0's `matc` or the engine will reject it at load. The built-in `StandardMaterial`s need nothing — they ship recompiled.

The API changes below are source-breaking only for **positional** callers; named arguments are unaffected.

| Old | New |
|---|---|
| `Cube(mat, position, rotation, scale, pivot, size, …)` | `Cube(mat, size, position, rotation, scale, pivot, …)` — shape leads the transform on `Cube`/`Sphere`/`Cylinder`/`Plane` |
| `Mesh(…, indices, position, rotation, scale, pivot, boundingBox, …)` | `Mesh(…, indices, boundingBox, position, rotation, scale, pivot, …)` |
| `GltfInstance(…, castShadows, receiveShadows, visible, …)` | `GltfInstance(…, visible, castShadows, receiveShadows, …)` |
| `ShadowConfig()` gave a 2 cm bulb | `ShadowConfig()` now means *automatic* (`bulbRadius = -1f`); pass `bulbRadius = 0.02f` to keep the old look |
| `FilamentSceneView(modifier, …, environment)` / `rememberKTXEnvironment(…, engine = …)` | `FilamentSceneView(engine, environment, modifier, …)` / `rememberKTXEnvironment(engine, …)` — required parameters lead |
| `rememberRenderTargetTexture(postProcessing = null)` | `rememberRenderTargetTexture(postProcessing = PostProcessing(enabled = false))` |
| `LocalFilamentScene.current` | now internal — use `FilamentEffect { }`, which hands you the engine *and* the scene |
| `Float2` on `CameraState.shift`/`scaling` | `LensShift` / `LensScaling` (both convert to and from `Float2`) |

## [0.3.1] — 2026-08-14

### Added
- **Filament 1.74.1**: Upgraded bundled Filament engine to 1.74.1. Recompiled embedded standard, test, and sample `.filamat` materials.
- **`Renderer` APIs** (`filament`): bound `materialTime`, `setMaterialTimeEpoch`, and `pauseRenderThread` across all 5 target platforms (`android`, `jvm`, `native`, `js`, `wasm`).
- **Named axis directions** (`filament-compose`): `Direction.Up`/`Down`/`Left`/`Right`/`Forward`/`Back`/`Zero`, with `Forward` spelling out the −Z convention glTF and Filament aim along — the axis literal most often got wrong. `Rotation.lookTowards` now defaults its `up` to `Direction.Up`.
- **`Rotation.toRotationMatrix()`** (`filament-compose`): the column-major 3×3 (9 floats) that Filament's builders take.
- **Scene value-type test suite** (tests): `TypesTest` covers `Position`/`Direction`/`Scale`/`Rotation`/`Color` — operators, `Float3`/`Quaternion`/Compose-UI interop, `normalized()` on a zero vector, `toComposeColor()` clamping of HDR channels, and the two properties the Compose stability work rests on: constructing from a mutable filament-utils value copies rather than aliases it, and equal values compare equal.

### Changed
- **`Color` renamed to `LinearColor`** (`filament-compose`, source-breaking): the scene colour type shared its name with `androidx.compose.ui.graphics.Color`, which is in scope in practically every file using this library — every sample in this repo had to import it as `FilColor` to compile, which is the clearest possible signal the name was wrong. The new name also states the colour space, which matters because the two types are *not* interchangeable (see below). `Float3.toColor()` becomes `Float3.toLinearColor()`, and construction from a Compose colour moves from a constructor to `LinearColor.fromComposeColor(…)`. This is a clean rename with no deprecated alias — the old name is gone, so the compiler points at every call site. `SkyboxSource.Color` is unaffected (it is always qualified).
- **`CameraState.viewMatrix`/`projectionMatrix` return `Mat4`** (`filament-compose`, source-breaking): they were raw `FloatArray`/`DoubleArray` — mismatched precision, no indication of row/column order, in a module whose other geometry is typed (`Position`, `Direction`, `Quaternion`). Both are now filament-utils `Mat4`. The projection is narrowed from Filament's `double`s; for full precision go through the camera directly (`viewState.view?.camera?.getProjectionMatrix()`).
- **`rotation` takes a `Rotation`, not a `Quaternion`** (`filament-compose`, source-breaking): every composable with a transform (`Cube`/`Sphere`/`Cylinder`/`Plane`/`Mesh`/`Group`/`GltfInstance`) now takes the new immutable `Rotation` value type, finishing the migration `Position`/`Direction`/`Scale` started — filament-utils' `Quaternion` is a `data class` with `var` components, so the Compose compiler inferred it **unstable** and it was the single most common unstable parameter in the module, defeating skipping on every scene composable that took one. Build with `Rotation.axisAngle(axis, degrees)` / `Rotation.euler(pitch, yaw, roll)`, compose with `*`, default `Rotation.Identity`. The type carries the usual scene work so the common cases never leave it — `lookTowards` (aim local −Z at a target: turrets, billboards, chase cams), `fromTo`, `slerp`/`nlerp`, `toEuler`, `angleTo`, `normalized` — and anything past that hops to filament-utils losslessly with `Rotation(quat)` / `toQuaternion()` / `Quaternion.toRotation()`.
- **`Rotation`'s hot operators no longer round-trip through `Quaternion`** (`filament-compose`): `*` (both overloads), `inverse`, `normalized`, `axisAngle` and `nlerp` do their own float math, so an operation that ran per frame per entity allocates one object instead of four. `euler`, `fromTo`, `lookTowards`, `slerp`, `toEuler` and `angleTo` still delegate, where the trig dominates anyway.
- **Compose stability audited end to end** (`filament-compose`): a `compose-stability.conf` marks Filament's opaque native handles (`Engine`, `Scene`, `View`, `MaterialInstance`, `FilamentAsset`, `Manipulator`, `Float2`, …) stable — their unstable-by-default inference was poisoning every class holding one — and `@Stable`/`@Immutable` now annotate all state holders and value types (`CameraState`, `GltfAsset`, `AnimationState`, `AnimationMixer`, `IndirectLightState`, `Environment`, `SkyboxState`, `SkyboxSource`, `PostProcessing` and all 12 effect classes, `ShadowConfig`, `Shadows`, `Projection`, `Exposure`, `SpotCone`, `SunParams`, `LightIntensity`). `ShadowConfig.transform` now takes a `Rotation?` for consistency across the API. Unstable composable arguments dropped 108 → 36 and effectively-stable classes rose 44/69 → 59/69; everything still unstable is an array or an `Any?` key, where no fix exists. Build with `-PcomposeMetrics` to regenerate the reports under `build/compose-reports`.
- **`IndirectLightState.rotation` takes a `Rotation`** (`filament-compose`, source-breaking): it was the last "rotation" in the Compose API still spelled as a raw `FloatArray`, whose identity equality forced the apply effect to key off a `toList()` copy and which never reported a change when mutated in place. It is now a `Rotation` keyed directly, converted at the Filament boundary with `toRotationMatrix()`.
### Fixed
- **Compose UI colour interop ignored the sRGB transfer function** (`filament-compose`, behaviour-breaking): the `Color(composeColor)` constructor added in 0.3.0 copied `.red`/`.green`/`.blue` straight across, but `androidx.compose.ui.graphics.Color` is gamma-encoded while Filament works in linear space — so `Color(MaterialTheme.colorScheme.primary)` produced a markedly too-bright colour (sRGB 0.5 became linear 0.5 instead of 0.214, a 2.3× error at mid-grey). `LinearColor.fromComposeColor()` now converts through Compose's own colour management, which also handles non-sRGB sources such as Display P3, and `toComposeColor()` re-encodes on the way back. If you compensated by pre-darkening your colours, remove the workaround.
- **`Rotation.fromTo` was wrong for anything but unit-length inputs** (`filament-compose`): it forwarded its `Direction`s to a shortest-arc formula that assumes unit length, so a plain displacement produced a plausible-looking but wrong angle — `fromTo((3,0,0), (0,5,0))` gave 172° for what is a 90° turn. Both inputs are normalized now, as the type's docs always implied ("only the two directions matter"). `Rotation.lookTowards` had the same flaw in its degenerate-basis check: it tested `dot(forward, up)` against a threshold without normalizing `up` first, so a longer `up` vector (say `Direction(0f, 2f, 0f)`) tripped the parallel branch for perfectly good inputs and silently substituted a fallback axis, rolling the result.
- **`Rotation.axisAngle` around a zero-length axis produced a NaN transform** (`filament-compose`): which silently hides the entity rather than failing. A zero axis names no rotation, so `Rotation.Identity` comes back — matching the guards already in `lookTowards` and `Direction.normalized()`.
- **`Rotation.inverse()` was only correct for unit-length rotations** (`filament-compose`): it returned the bare conjugate, which leaves the length error in place for a rotation that has drifted from accumulated products. It now scales by 1/‖q‖², and `Rotation * Direction` likewise rotates without also scaling the vector.
- **`Rotation.nlerp` could take the long way round** (`filament-compose`): unlike `slerp` it did not negate the end rotation when the two faced opposite hemispheres, so blending towards, say, a 350° turn swept 175° instead of 5°.
- **Right-click panning did nothing on desktop and web** ([#98](https://github.com/Erkko68/filament-kmp/issues/98)) (`filament-compose`): the drag gestures started with foundation's `awaitFirstDown`, whose `firstDownRefersToPrimaryMouseButtonOnly()` is `true` on skiko (JVM desktop, web, iOS) — a secondary-button press never opened a gesture, so `grabBegin(…, strafe = true)` was never reached and panning was silently dead (Android was unaffected). The gesture layer now awaits its own down that accepts any mouse button. Flight mouse-look responds to right-drag for the same reason.
- **Vertical panning, orbiting and mouse-look were inverted** (`filament-compose`, behavior-breaking, all platforms): Filament's `Manipulator` reads viewport coordinates with a **bottom-left** origin (its own sample apps do `y = height - y`; our picking path already did), but the gesture modifiers passed Compose's top-left `y` straight through — so horizontal drags grabbed the scene correctly while vertical ones moved it the wrong way. Every `grabBegin`/`grabUpdate`/`scroll` call site now flips `y`, which also fixes the pinch-zoom centre and the scroll-wheel anchor (zoom-to-cursor in MAP mode). Mouse-look is now the standard "cursor up looks up"; pass a negative `panSpeedY` (or `orbitSpeedY`) for the classic inverted axis — now documented as the supported opt-in.
- **Flight camera speed is finally settable** (`filament-compose`, behavior-breaking): `rememberFlightCameraController` gained `initialMoveSpeed` (default `1f`) and `speedSteps` (default `20`, was upstream's `80`). Filament's `FreeFlightManipulator` computes its speed as `maxMoveSpeed^(wheel/halfSteps)`, which is **1.0 world-unit/s at wheel 0** — so `maxMoveSpeed` alone did nothing until you scrolled, and one scroll notch over 80 steps moved the speed by ~6%, making the wheel feel dead too. The controller now seeds the wheel from `initialMoveSpeed`, carries it across a tuning rebuild, and exposes `FlightCameraController.adjustSpeed(steps)`; `maxMoveSpeed` is documented as what full scroll-up reaches. Scrolling **up** now speeds up (it was inverted relative to the orbit/map zoom convention).
- **`GltfInstance` boxed every morph weight on every recomposition** (`filament-compose`): the morph-weight effect was keyed on `morphWeights?.toList()`, which boxes each float into a `List<Float>` *before* the gate can decide anything — so the check meant to skip a redundant native push allocated a list every time it ran. `FloatArray` is an unstable Compose parameter type, so `GltfInstance` recomposes with its parent regardless of whether its arguments changed, and morph weights are animated per frame by nature; the two together made that the common path rather than a rare one. Weights are now compared against a remembered copy with `contentEquals`, which is exact (no hash collisions) and allocates only when they actually change. `GltfInstance` also gains its first tests (`GltfInstanceLifecycleTest`, Tier-B): mount/dispose leak accounting, `visible` toggling, morph-target presence, and the weight-update shapes — including in-place mutation of a handed-over array, which the stored `copyOf()` still detects. `rememberGltfAsset` gains `GltfAssetLoadingTest` alongside it, covering the async load arc, the two failure contracts (unparseable bytes and a throwing `load` both report via `onError` and never throw in composition), asset sharing across instances, and `key`-driven reload. Both run on web except where a specific web gap or the manual test clock prevents it, each `@IgnoreJs` carrying its reason.
- **Documented `key()` for dynamic scene contents** (docs): scene composables own real Filament resources, so emitting a changing list without `key()` makes inserting or reordering destroy and rebuild every entity after the change point — restarting animation playback, re-running `onCreate`, and leaving entity-keyed maps pointing at destroyed entities, all without any visible error. Covered in [Compose Integration](docs/compose/README.md#dynamic-scene-contents-use-key) and in `GltfInstance`'s KDoc.
- **Morph weights below four targets did nothing on web** (`filament`): `RenderableManager.setMorphWeights` bailed out entirely unless it was handed at least four weights, so every 1–3 morph-target model was silently unanimated in the browser. filament.js binds only the legacy `setMorphWeights(instance, a, b, c, d)` form — there is no pointer/count/offset overload — so the missing slots are now zero-padded instead, and a non-zero `offset`, which that form cannot express, returns early rather than being ignored and writing to slot 0. The four instance-side morph/skinning methods are marked `@PlatformGap` and listed in [Platform notes](docs/platform-notes.md): `getMorphTargetCount` always returns `0`, `setMorphTargetBufferOffsetAt` is a reachable silent no-op with no workaround, and `setSkinningBuffer` is unreachable because `SkinningBuffer.Builder.build` throws on web.
- **Unparseable glb bytes threw on `wasmJs` instead of returning null** (`gltfio`): the `gltfio$AssetLoader.createAsset`/`createInstancedAsset` externals were declared as returning a non-null `FilamentAsset`, following upstream's `filament.d.ts`, but both return null when the bytes don't parse. Kotlin/Wasm null-checks values crossing the JS interop boundary, so that null surfaced as a `NullPointerException` from inside `AssetLoader.createAsset` — right past the `null` return its signature promises, and past `rememberGltfAsset`'s "never throws in composition, reports via `onError`" contract. Both externals are nullable now. Kotlin/JS was unaffected (it passes the null through untyped), which is why this only ever showed on `wasmJs`.
- **`getAnimator()` before resource load handed back a broken `Animator`** (`gltfio`, behavior-breaking): gltfio creates the animator during resource loading (`FFilamentInstance::createAnimator()` is guarded by `mResourcesLoaded`), so calling `FilamentInstance.getAnimator()` on an asset whose resources have not been loaded returns `nullptr` upstream — which every wrapper passed straight into an `Animator` that then crashed on first use, or, on `wasmJs`, surfaced as a `NullPointerException` at the interop boundary. `jvm`, `native` and `web` now throw `IllegalStateException` naming the fix (call `ResourceLoader.loadResources(asset)` first); Android cannot check, because its Java binding wraps the null pointer itself — noted in the KDoc.
- **Flight start orientation was off by a factor of 57** (`filament-compose`, behavior-breaking): `rememberFlightCameraController`'s `startPitch`/`startYaw` are documented in degrees but were passed straight to `Manipulator.flightStartOrientation`, which takes radians — non-zero values aimed the camera nowhere near where they said. They are now converted; if you compensated by passing radians, drop the conversion.

## [0.3.0] — 2026-07-22

### Added
- **Filament 1.74.0**: Upgraded the bundled Filament engine to 1.74.0 (no public C++ API changes; MATERIAL_VERSION 73→74, all `.filamat` materials recompiled). The web prebuilt is built from our fork's `feat/webgl-bindings-coverage-1.74` branch (the `v1.74.0` tag + expanded JS bindings) pending the upstream PR.
- **Android-API parity sweep** (`filament`): bound every remaining public Filament Android API missing from common — GTAO selection (`AmbientOcclusionOptions.aoType` + `AmbientOcclusionType` enum), typed TAA options (`TemporalAntiAliasingOptions.boxType`/`boxClipping`/`jitterPattern` are now `BoxType`/`BoxClipping`/`JitterPattern` enums instead of raw `Int`s — source-breaking if you set them), world-origin grid snapping (`View.gridSize`/`effectiveGridSize`), `Engine.hasUnrecoverableFailure`, `MaterialInstance.getConstantBoolean/Float/Int`, `ColorGrading.Builder.customLut`, and `RenderableManager.getEnabledAttributesAt`. On web, `getConstant*` and `customLut` throw `UnsupportedOperationException` (`@PlatformGap`: not bound in filament.js); everything else works on all targets. Intentional non-mirrors are documented in `scripts/dev/check-common-api-ignores.txt`.
- **Transparent built-in material** (`filament-compose`): `StandardMaterial.Transparent` + `rememberTransparentColorMaterialInstance(color, alpha, …)` — LIT PBR with alpha transparency (pre-multiplied, two-pass so convex primitives self-composite correctly), shipped as a precompiled `.filamat` like the other standard materials, so it works on every target with no `.mat` authoring.
- **`visible` on primitives, `GltfInstance`, and `Group`** (`filament-compose`): a show/hide toggle that pulls the renderable(s) from the scene cheaply while keeping entities and state alive — previously hiding meant removing the composable and losing state. On `Group` it hides the whole subtree (propagated through a composition local, so nested groups compose).
- **`CameraState.focusDistance`** (`filament-compose`): the depth-of-field focal plane is now settable — `PostProcessing(depthOfField = …)` was previously stuck at the native default distance with no way to place focus.
- **Compose UI colour interop** (`filament-compose`): construct a scene `Color` from an `androidx.compose.ui.graphics.Color` (and back via `toComposeColor()`), instead of hand-converting channels.
- **Shared `CameraController` interface** (`filament-compose`): `OrbitCameraController`/`MapCameraController`/`FlightCameraController` now implement a common `CameraController` (`setViewport`/`resetToHome`/`saveBookmark`/`jumpToBookmark`), so generic UI can reset or bookmark whichever camera is active. `FlightCameraController` gained `resetToHome`/bookmarks (Filament's `Manipulator` supports FLIGHT bookmarks); its per-frame `update` stays flight-only.
- **`rememberFilamentScene`/`FilamentSceneView` `environment` overloads** (`filament-compose`): pass a loaded `Environment` (from `rememberKTXEnvironment`/`rememberHDREnvironment`) in one argument instead of threading `environment.skyboxState` + `environment.indirectLightState` by hand — `rememberFilamentScene(engine, env) { … }`.
- **`castShadows`/`receiveShadows` on `GltfInstance`** (`filament-compose`): nullable per-instance overrides applied to every renderable in the instance (`null`, the default, keeps what the asset authored) — closing the parity gap with the primitives' shadow toggles.
- **Smarter upgrade tooling** (build): `check-common-api.sh` now audits classes, nested types, and enum constants (not just method names), strips KDoc before matching, flags upstream-deprecated members informationally, reads suppressions from `check-common-api-ignores.txt`, and exits non-zero on unsuppressed gaps; `upgrade-diff.sh` gained a HIGHLIGHTS section (MATERIAL_VERSION bump, `CONFIG_MAX_*`, feature-flag flips, added/removed Java classes and embind bindings) and optional tags (defaults: `filaVersion` → latest upstream release).

### Known issues
- Upstream regression (present in stock 1.74.0, all backends): a scene lit **only** by punctual lights (e.g. a spot, no directional) panics in `renderView` when the view's shadow type is VSM/DPCF/PCSS — the requested shader variant collides with the reserved SSR variant after the dynamic-lighting variant removal. Use PCF for spot-only scenes until fixed upstream.

### Changed
- **JVM surface readback is zero-copy** (`filament-compose`): Filament's `readPixels` now writes straight into Skia-owned memory that the on-screen `Image` wraps without copying — previously every frame paid two full-frame CPU copies (native → `ByteArray` → Skia) plus a full-frame off-heap allocation; the blit also uses linear sampling (smoother while a resize debounces) and flips vertically on OpenGL backends, whose `readPixels` row order is bottom-up unlike Metal's top-down (pinned by the new `readPixelsRowOrderMatchesBackendConvention` Tier C test).
- **Scene-state creators now name their seeds `initial*`** (`filament-compose`, source-breaking for named args): `rememberCameraState` / `rememberSkyboxState` / `rememberIndirectLightState` / `rememberAnimationState` / `rememberKTXEnvironment` / `rememberHDREnvironment` parameters are initial-composition seeds only (the `rememberPagerState` convention) — the old names looked reactive but silently ignored later changes. Drive the state by mutating the returned object. Orbit/map manipulator tuning params (`zoomSpeed`, …) stay genuinely reactive: a change rebuilds the manipulator but restores the current pose and original home, so the camera no longer jumps.
- **Material loading chains without unwrapping** (`filament-compose`, source-breaking): `rememberMaterialInstance` accepts `Material?` and returns `MaterialInstance?`, and every primitive accepts a nullable material (rendering nothing until it loads) — `rememberMaterial { … }` now feeds straight into `Cube(material = …)` with no `?.let` scaffolding, matching how `GltfInstance` already handled loading.
- **Picking takes Compose coordinates everywhere** (`filament-compose`, behavior-breaking if you flipped Y yourself): `FilamentViewState.pick` now converts from top-left Compose coords internally, matching `Modifier.pickOnTap` — remove any manual `viewport.height - y` flips. Gesture modifiers (`orbitGestures`/`mapGestures`/`flightGestures`) also sync `Manipulator.setViewport` automatically, so the `onSizeChanged` boilerplate is gone.
- **`ColorGrade.toneMapping` is a value type** (`filament-compose`, source-breaking): the raw `toneMapper: ToneMapper` field became `toneMapping: ToneMapping` (sealed, structural equality); the native operator is built only at apply time.
- **Lights follow group rotation by default** (`filament-compose`, behavior-breaking): `followGroupRotation` defaults to `true`, so a light inside a rotating `Group` re-aims like the meshes around it; pass `false` to pin its world-space direction.
- **`Light` escape hatch uses `vararg keys`** (`filament-compose`, source-breaking for `rebuildKey =` callers), matching `FilamentEffect` and `rememberMaterialInstance`; `rememberRenderTarget` renamed to `rememberRenderTargetTexture` (it returns the colour `Texture`); `rememberEmissiveMaterialInstance` gained a default `intensity`; `CameraState` now throws when attached to two views at once instead of silently racing.
- **Loader `engine` parameter moved last** (`filament-compose`, source-breaking for positional callers): `rememberMaterial`/`rememberTexture`/`rememberGltfAsset`/`rememberKTXEnvironment`/`rememberHDREnvironment` now take their defaulted `engine` just before the trailing `load` lambda instead of first, so every loader shares one signature shape — `(config…, key, onError, engine = LocalFilamentEngine.current, load)` (the builders like `rememberColorMaterialInstance` already did). Pass it by name — `rememberGltfAsset(engine = engine) { … }` — since `engine` positional-first now silently binds as another parameter.
- **All gesture modifiers are plain `Modifier` extensions** (`filament-compose`): `Modifier.flightGestures` is no longer `@Composable` — its `FocusRequester` moved into `FlightCameraController`, so `orbitGestures`/`mapGestures`/`flightGestures` share one non-composable shape, matching Compose's own `draggable`/`scrollable`.
- **Camera controllers renamed `*CameraState` → `*CameraController`** (`filament-compose`, source-breaking): `OrbitCameraController`/`MapCameraController`/`FlightCameraController` and their factories `rememberOrbitCameraController`/`rememberMapCameraController`/`rememberFlightCameraController`. The old names read as siblings of `CameraState` when they actually *drive* one (you pass the `CameraState` to the view and the controller separately) — the new names say what they are and match the `CameraController` interface they implement.
- **`LightIntensity` value type replaces `intensity`/`intensityUnit`/`efficiency`** (`filament-compose`, source-breaking): every light's brightness is one `LightIntensity` value — `LuminousPower(v)` (the default), `Candela(v)`, or `Watts(watts, efficiency)`. Three correlated params collapse to one and the Watts-only `efficiency` becomes unrepresentable with the other units (previously `intensity = 5f, efficiency = 0.09f` silently dropped `efficiency` unless `intensityUnit == WATTS`), matching the `Projection`/`SpotCone`/`SunParams` idiom. Migrate `intensity = 100_000f` → `intensity = LightIntensity.LuminousPower(100_000f)`.
- **Library warnings route to the platform log** (`filament-compose`): the `GltfInstance` create-failure path no longer uses raw `println` — it goes through an internal per-platform sink (Logcat / `console.warn` / stderr / `NSLog`) tagged `filament-compose`.
- **`onCreate` callbacks are scope receivers everywhere** (`filament-compose`, source-breaking): primitives and `Group` now take `onCreate: EntityScope.() -> Unit` (the created `entity` plus the `engine` in scope, so one-time manager setup needs no extra plumbing) instead of `(entity: Int) -> Unit`, matching `GltfInstance`'s scope-receiver shape — migrate `{ e -> … }` to `{ …entity… }`.
- **Boolean flags renamed to the `*Enabled` convention** (`filament-compose`, source-breaking): `Bloom.threshold` → `thresholdEnabled` (it's an on/off clip flag, not a float cutoff) and `rememberOrbitCameraState(enablePanning)` → `panningEnabled`; `SkyboxSource.Color(rgb = …)` → `color = …`, matching every other colour field.
- **`rememberRenderTargetTexture` takes a full `PostProcessing?`** (`filament-compose`, source-breaking): the `postProcessingEnabled: Boolean` flag became `postProcessing: PostProcessing? = null` — the same value type `FilamentView` takes (bloom, colour grading, … now work off-screen); `null` keeps the pass off, preserving the depth attachment. The deprecated `rememberRenderTarget` alias was removed.
- **`AnimationMixer.isPaused` is snapshot state** (`filament-compose`): reading it in composition now subscribes, like `AnimationState.isPaused` and every track field.

### Fixed
- **`PostProcessing(renderQuality = null)` now restores the native default** (`filament-compose`): the HDR colour-buffer quality previously stuck at whatever a prior config set instead of returning to `HIGH`, breaking the "null = native default" contract every other field honors.
- **Flight camera no longer jumps on tuning changes** (`filament-compose`): `rememberFlightCameraController` now carries the current pose across a tuning-parameter rebuild like orbit/map — previously changing e.g. `maxMoveSpeed` mid-flight snapped orientation back to `startPitch`/`startYaw`.
- **`Mesh` no longer re-uploads geometry on every recomposition** (`filament-compose`): geometry is now compared by array *content*, so inline-constructed but identical `floatArrayOf(…)` arguments no longer rebuild the vertex/index buffers and renderable each recomposition.
- **JVM surface readback thread-safety** (`filament-compose`): the `readPixels` completion callback can fire on Filament's backend thread; buffer bookkeeping is now atomic and Skia images are closed only on the UI thread — previously a callback could close the image the UI thread was mid-draw on.
- **`PostProcessing()` no longer disables FXAA** (`filament-compose`): `antiAliasing = null` previously set the view's AA to `NONE`, silently degrading every default view below Filament's native FXAA default; `null` now consistently means "leave the native default".
- **Colour grading no longer re-bakes its LUT every recomposition** (`filament-compose`): `ToneMapper`'s identity equality made every `PostProcessing(colorGrade = …)` value unequal, destroying and rebuilding the `ColorGrading` (an expensive LUT bake) on each recomposition of an animated scene.
- **`GltfInstance` no longer silently aliases the primary instance** (`filament-compose`): when `createInstance` fails, only the first `GltfInstance` falls back to the asset's built-in instance; a second one now renders nothing and warns instead of two composables fighting over one transform/animator.
- **Web TAA options no longer drop fields** (`filament`): the web `temporalAntiAliasingOptions` setter now forwards `filterInput`, `useYCoCg`, `hdr`, `boxType`, `boxClipping`, `jitterPattern`, `varianceGamma`, `preventFlickering`, and `historyReprojection` instead of silently resetting them to defaults.
- **`downloadPrebuilts` works again on 1.73.0** (build): dropped the dead `macosX64` prebuilt target — upstream releases stopped shipping mac x86_64 libs, which made the umbrella task fail.

### Migration from `0.2.0`
`filament-compose` had an API-consistency sweep this release; most changes are mechanical renames. `filament`/`gltfio` gained bindings only — no migration needed there.

| Old | New |
|---|---|
| `rememberOrbitCameraState`/`rememberMapCameraState`/`rememberFlightCameraState` and `OrbitCameraState`/`MapCameraState`/`FlightCameraState` | `remember*CameraController` and `*CameraController` |
| `intensity = 100_000f` (light) | `intensity = LightIntensity.LuminousPower(100_000f)` (or `.Candela`/`.Watts`) |
| `Bloom.threshold`, `enablePanning` | `Bloom.thresholdEnabled`, `panningEnabled` |
| `SkyboxSource.Color(rgb = …)` | `SkyboxSource.Color(color = …)` |
| `rememberRenderTarget` | `rememberRenderTargetTexture` — now takes `postProcessing: PostProcessing? = null` instead of `postProcessingEnabled: Boolean` |
| `ColorGrade.toneMapper: ToneMapper` | `ColorGrade.toneMapping: ToneMapping` |
| `onCreate = { entity -> … }` (primitives, `Group`) | `onCreate = { /* entity, engine in scope */ }` (`EntityScope.() -> Unit`) |
| `rememberMaterial(engine, key, onError) { … }` (positional `engine`) | pass `engine` by name — its position moved next to the trailing lambda |
| Manual `viewport.height - y` before `FilamentViewState.pick` | remove it — `pick` now converts from Compose's top-left coords itself |

Also check if you rely on: `rememberCameraState`/`rememberSkyboxState`/`rememberIndirectLightState`/`rememberAnimationState`/`rememberKTXEnvironment`/`rememberHDREnvironment` parameters now seed initial composition only (mutate the returned state to change them later); `Modifier.flightGestures` is a plain (non-`@Composable`) `Modifier` extension now; lights inside a rotating `Group` re-aim by default (`followGroupRotation = true`) unless you opt out.

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
