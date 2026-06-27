# `filament-compose` tests

Status: **Tier A + most of Tier B implemented** (backlog item #1). Tier A: 17 NOOP tests, green on JVM,
compiling on JS/iOS, wired into the jvm/js/ios CI jobs. Tier B (gated real backend): `PrimitiveLifecycleTest`,
`MaterialLifecycleTest`, `EnvironmentLifecycleTest` implemented (22 compose tests total, all green on
macOS/Metal and on an Android emulator). The glTF suite and the texture-backed environment paths are
deferred (see Tier B inventory). The Android instrumented job is wired into CI (all 22 pass on-device).
Scope: bring the most lifecycle-intensive, least-verified module under automated test without a GPU CI runner.

> Reuses the gating in [`:kotlin:test-support`](test-support.md) (`TestEnv.gpuBackendAvailable`,
> `@IgnoreJs`) and the philosophy in [rendering-backend-tests.md](rendering-backend-tests.md)
> (binding-level assertions, no goldens). This doc only adds what's compose-specific.

## What we're actually verifying

The compose layer's risk isn't pixels — it's **manual native lifecycle driven by Compose effects**:

1. **Dispose ordering** — entities removed from the scene *before* the scene is destroyed; the
   light component destroyed while its entity is still alive; `ColorGrading` freed before re-apply.
   These are guarded today only by hand-written ordering comments
   ([Light.kt](../../kotlin/filament-compose/src/commonMain/kotlin/io/github/erkko68/filament/compose/scene/Light.kt#L143-L169),
   [FilamentScene.kt](../../kotlin/filament-compose/src/commonMain/kotlin/io/github/erkko68/filament/compose/FilamentScene.kt#L59-L61)).
2. **No leaks** — leaving composition returns every Filament object the composables created.
3. **Snapshot-keyed re-application** — a changed `LightSnapshot`/`CameraSnapshot`/`PostProcessing`
   pushes the new values; an unchanged one does not churn JNI.
4. **State application is correct** — `PostProcessing.applyTo`, `CameraSnapshot.applyTo`,
   `Shadows.applyTo` write the expected values onto a real `View`/`Camera`.

None of (1)–(4) needs pixels, and most needs no GPU.

## The two tiers, and why the split falls where it does

| Tier | Backend | Runs in CI | Covers |
| :-- | :-- | :-- | :-- |
| **A** | `NOOP` | **everywhere** (no GPU/display) | scene-graph composable lifecycle/leak (`Light`, `Group`, `CameraNode`); `applyTo` unit tests |
| **B** | `DEFAULT` (gated) | macOS/Metal, Android, iOS-sim-local, lavapipe Linux; skips elsewhere | GPU-resource composables: primitives, `rememberMaterial`/`rememberTexture`, `rememberGltfAsset`, skybox/IBL |

The line is **"does the composable allocate a GPU resource?"** NOOP executes the full binding path
for CPU-side managers (EntityManager, TransformManager, LightManager, Scene membership) and for
`View`/`Camera`/`ColorGrading` construction — already proven by the core NOOP tests
(`ViewOptionsRoundTripTest`, `ColorGradingTest`, `CameraTest`). It **panics** on vertex/index
buffers, textures, and materials — so anything building geometry or sampling a texture is Tier B and
gates via `engine ?: return`, exactly like the core `RenderingTestFixture`.

`FilamentView` + `FilamentSurface` (the live platform surface and render loop) are **out of headless
scope**: the surface is real platform UI (Skia/AWT layer, `SurfaceView`) that a headless test can't
host. We test its *constituents* instead — the `applyTo` functions it calls and the
create/destroy contract of `renderer`/`view`/`camera` — and leave the assembled surface to the
samples + manual verification. This is the compose analog of "we don't golden-image."

## The harness: a headless composition host

Scene composables are `@Composable FilamentSceneScope.() -> Unit` extensions that read
`LocalFilamentEngine` / `LocalFilamentScene` / `LocalParentEntity`. We do **not** need `FilamentView`
to drive them — only those locals. Two test utils in
`filament-compose/src/commonTest/.../testutils/ComposeSceneHarness.kt`:

- `withFilamentScene(engine, scene) { setContent -> … }` — the primitive: runs `runComposeUiTest`,
  hands the body a `setContent` that mounts scene content with the locals provided. Use it for tests
  that recompose with changed inputs (mount, mutate `mutableStateOf`, `waitForIdle`, assert).
- `composeScene(engine, scene, frames, whileComposed) { content }` — the mount→assert→dispose
  convenience: mounts `content`, runs `whileComposed` after effects apply, advances `frames` to drive
  `OnFrame`, then leaves the composition so every `onDispose` fires.

```kotlin
@OptIn(ExperimentalTestApi::class)
fun withFilamentScene(engine: Engine, scene: Scene, body: ComposeUiTest.(SetSceneContent) -> Unit) =
  runComposeUiTest {
    // OnFrame runs an unbounded withFrameNanos loop (every light registers one for
    // followGroupRotation). With the default auto-advancing clock the composition is never idle and
    // waitForIdle() hangs forever — so drive the clock manually.
    mainClock.autoAdvance = false
    // One real setContent hosts a swappable, state-driven slot. Android's setContent is one-shot
    // (a second call throws "already set content"); routing every (re)mount through this state keeps
    // us to a single call so the same harness runs on jvm/js/ios/android.
    var slot by mutableStateOf<@Composable FilamentSceneScope.() -> Unit>({})
    setContent {
        CompositionLocalProvider(LocalFilamentEngine provides engine, LocalFilamentScene provides scene) {
            FilamentSceneScopeInstance.slot()
        }
    }
    val setContent: SetSceneContent = { content ->
        slot = content
        mainClock.advanceTimeByFrame() // state-driven recompose needs a tick (autoAdvance is off)
    }
    body(setContent)
  }
```

**Three gotchas the implementation pins down (all cost real debugging):**
- `mainClock.autoAdvance = false` is mandatory. With it `true`, `OnFrame`'s perpetual
  `withFrameNanos` loop keeps the composition non-idle and `waitForIdle()` never returns (manifested
  as a ~13-minute "hang"). Frames are stepped explicitly via `mainClock.advanceTimeByFrame()`.
- **Single `setContent`, state-driven slot.** Android's `AndroidComposeUiTest.setContent` is one-shot,
  but tests mount/mutate/unmount repeatedly. We call the real `setContent` once and swap a
  `mutableStateOf` content slot; because `autoAdvance` is off, each swap needs an explicit
  `advanceTimeByFrame()` to recompose + commit the new `DisposableEffect`s before assertions.
- Assert live state **inside `whileComposed`** (and leak state inside `afterDispose`), not after
  `composeScene` returns. Two reasons: by return the composition is disposed and every component is
  correctly gone (asserting `hasComponent` post-return reads `false` — looks like a leak/bug, is just
  the dispose working); and on **JS** `runComposeUiTest` is *asynchronous* (returns a promise rather
  than blocking as on JVM), so a post-return assertion runs before the body has executed at all.
  Always `return composeScene(...)` / `withFilamentScene(...)` from the test so kotlin.test awaits the
  promise on JS.

**JS needs skiko loaded first.** `runComposeUiTest` builds its Skia raster surface synchronously in
its constructor, but on Kotlin/JS skiko's WASM loads asynchronously under Karma — calling it too early
throws `org_jetbrains_skia_Surface__1nMakeRasterN32Premul is not defined`. `ComposeTestFixture` has a
`@BeforeTest` returning `awaitGraphicsReady()` (skiko's `onWasmReady` promise on JS, `Unit` elsewhere);
kotlin.test awaits a promise returned from `@BeforeTest`, so the WASM is ready before the first test.
Karma also needs `kotlin/filament-compose/karma.config.d/filament-setup.js` (loads `filament.js` +
the WASM bootstrap, same as the core modules).

`runComposeUiTest` is headless on JVM (skiko offscreen) and resolves on JVM/JS/iOS at Compose MP
1.11.1. Requires the `org.jetbrains.compose.ui:ui-test` test dependency (`compose.uiTest`).

### Resource-accounting assertions

Filament exposes no global object count, but enough local accounting to prove "created N, disposed N":

```kotlin
// after composeScene(...) returns (composition disposed):
assertEquals(0, scene.entityCount)        // Scene.entityCount / .lightCount / .renderableCount
assertEquals(0, scene.lightCount)
assertFalse(engine.getEntityManager().isAlive(capturedEntity))   // entity actually destroyed
assertFalse(engine.getLightManager().hasComponent(capturedEntity))
```

`ResourceLedger.kt` provides `assertSceneEmpty(scene)` and `assertEntitiesDestroyed(engine, entities)`
for the post-dispose leak check. It lives in compose `commonTest` (not `:test-support`, which stays
compose-free); the cross-module "snapshot engine object counts" idea from the eval is satisfied here
because the accounting is per-`Scene`/per-manager, which is all the engine offers.

**NOOP option round-trips have feature-dependent edges** (real Filament behavior, not wrapper bugs;
found while writing `PostProcessingApplyTest`): bloom `levels` is clamped to the mip chain the
`resolution` allows (so the test sets `resolution = 384` for `levels = 8`, mirroring core
`ViewOptionsRoundTripTest`), and dynamic resolution only stays `enabled` under NOOP with
`homogeneousScaling = true` (non-homogeneous scaling needs backend support NOOP lacks). Match the
known-good core configs when asserting option values under NOOP.

## Test inventory

### Tier A — `NOOP`, CI-everywhere ✅ implemented (17 tests)

- **`LightLifecycleTest`** (2) — each of the 5 light types: while composed, exactly one light + a live
  `LightManager` component; after disposal, scene empty and entity destroyed. A second test recomposes
  with changed `intensity` five times and asserts the light updates in place — one light, the same
  entity handle — never duplicated or leaked.
- **`GroupLifecycleTest`** (2) — a child light is parented to the group's transform
  (`TransformManager.getParent`); nested groups chain (inner parent = outer, child count = 1); disposal
  destroys all transform entities, scene empty.
- **`CameraNodeLifecycleTest`** (1) — `CameraNode` drives the hoisted `CameraState` from the enclosing
  `Group`'s transform after `frames = 2` (camera eye follows the group), and leaks nothing on disposal.
- **`PostProcessingApplyTest`** (5) — `PostProcessing(...).applyTo(view, engine)` round-trips the
  `enabled` master switch, bloom / vignette / fog / SSAO / SSR / DoF / dynamic-resolution option
  structs, and each anti-aliasing mode onto a NOOP `View`; null effects clear prior state; the returned
  `ColorGrading` is non-null iff a grade is set and is freed afterward. (See the NOOP-edge note above
  for the bloom-`resolution` and dynamic-resolution-`homogeneousScaling` requirements.)
- **`CameraSnapshotApplyTest`** (5) — `applyTo(camera, aspect)` for each `Projection` variant
  (Perspective / Orthographic / Lens) plus eye position, exposure, shift, and scaling, read back via the
  camera's own getters.
- **`ShadowsApplyTest`** (2) — the `null`-disables / non-null-enables toggle (everywhere), and each
  technique's `View.ShadowType` selection (`@IgnoreJs` — `setShadowType` is unbound on web).

### Tier B — `DEFAULT`, gated (`engine ?: return`) ✅ partially implemented

Tier-B tests extend `TierBSceneFixture` (a real `Engine.Backend.DEFAULT` engine + `Scene`, gated on
`TestEnv.gpuBackendAvailable`, `engine`/`scene` null when unavailable) and reuse the `composeScene`
harness. A real material is needed to build any renderable; the fixture's `materialInstance()` builds one
from the bundled `emissive.filamat` (the compose-local `TestMaterials` expect/actual — JVM reads the
resource, other targets return empty, so the suite skips off-JVM exactly like the core fixtures).

- **`PrimitiveLifecycleTest`** ✅ — `Cube`/`Sphere`/`Plane`/`Cylinder`/`Mesh`: composes →
  `scene.renderableCount == 1` & a live `RenderableManager` component; disposes → renderable gone, scene
  empty, entity destroyed (the dispose-order comments in `MeshData.kt`). (The vertex/index buffers are
  freed in the same `onDispose`; they aren't separately asserted because the composable owns the handles
  internally — the renderable+entity teardown is the leak guard.)
- **`MaterialLifecycleTest`** ✅ — `rememberMaterial` builds → `isValidMaterial` while composed → freed on
  disposal; a bad payload returns `null` + fires `onError` without crashing. **That last path drove a core
  fix**: a malformed `.filamat` made Filament's C++ parser panic (`utils::PostconditionPanic`), which
  *terminates the process* — the throw unwinds across the prebuilt's `-fno-exceptions` frames before any
  wrapper `try/catch` can run, so it can't be trapped after the fact. `Material.Builder` now sniffs the
  `.filamat` magic (`isValidFilamatPayload`) in the FFM/native `payload()` and `build()` raises a catchable
  `IllegalArgumentException` for a non-`.filamat` blob, matching the JS embind backend (which already
  threw). `rememberTexture` is deliberately not covered: the JVM image decoder `abort()`s on undecodable
  bytes (an uncatchable upstream crash, *not* a null-return) and the repo bundles no decodable test image
  for the happy path — that waits on an image asset.
- **`EnvironmentLifecycleTest`** ✅ — `ApplySkybox` (a **color** skybox → `scene.skybox` set/cleared) and
  `ApplyIndirectLight` (an IBL built from **spherical-harmonics** coefficients → `scene.indirectLight`
  set/cleared). Both apply synchronously on the composition thread, and neither needs a bundled asset.
  The texture-backed paths (cubemap skybox, cubemap IBL, `rememberKTXEnvironment`/`rememberHDREnvironment`)
  are left for when a small KTX/HDR asset is bundled — `samples/shared/.../environment/` has candidates.
- **`GltfLifecycleTest`** ⬜ **deferred — same reason `FilamentView` is out of headless scope.**
  `rememberGltfAsset` loads via gltfio's **async** `ResourceLoader` (`asyncBeginLoad` → `asyncUpdateLoad`
  polling inside a `withFrameNanos` loop on the JobSystem's worker threads). Driven from the Compose test's
  manual clock/dispatcher rather than a real `FilamentView` render loop, the load aborts natively
  (`utils::PreconditionPanic`) — the same coupling to the live frame/thread model that keeps `FilamentView`
  itself out of the headless harness (the sample [`DuckScene`](../../samples/shared/src/commonMain/kotlin/eric/bitria/samples/scenes/DuckScene.kt)
  drives exactly this path successfully *through* `FilamentSceneView`). The synchronous Primitive/Material/
  Environment composables don't hit this. Revisiting needs either a way to create+drive the engine on the
  composition thread or a synchronous glTF-load path. Note: an aborting test in `commonTest` SIGABRTs the
  whole module run, so this can't simply be left in place skipping — it stays out until the load path is
  headless-safe.

`@IgnoreJs` on any test whose path hits an unbound web API (per the standing policy); everything else
runs on jvm/js/ios-sim/android like the core suites.

## Build & CI wiring

```kotlin
// kotlin/filament-compose/build.gradle.kts
commonTest.dependencies {
    implementation(kotlin("test"))
    implementation(project(":kotlin:test-support"))
    @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
    implementation(compose.uiTest)
}
```

`:kotlin:filament-compose:<target>Test` is wired into the **jvm**, **js**, and **iosSimulatorArm64**
jobs in `ci.yml` (alongside `filament`/`gltfio`/…). Tier A is GPU-free so it runs on every one.

The **Android instrumented** path (`:kotlin:filament-compose:connectedAndroidDeviceTest`) is wired
into the android job and runs all 22 tests on an emulator (Tier B's real backend is available there).
Two Android-only test deps make `runComposeUiTest` work on-device, declared in the `androidDeviceTest`
source set:

```kotlin
named("androidDeviceTest") {
    dependencies {
        // ui-test-manifest registers the host ComponentActivity runComposeUiTest mounts into;
        // JetBrains Compose doesn't republish it, so use the androidx artifact at the version
        // composeMultiplatform resolves to (libs.androidx.composeUi).
        implementation(libs.androidx.compose.ui.test.manifest)
        // Force a current espresso-core: compose ui-test drags in 3.5.0, which calls
        // InputManager.getInstance — removed in Android 14+ — so waitForIdle()/onIdle throws
        // NoSuchMethodException on-device. 3.7.0 doesn't.
        implementation(libs.androidx.test.espresso.core)
    }
}
```

## Follow-ups

- **glTF + texture-backed paths.** `GltfLifecycleTest` and `rememberTexture`/cubemap-skybox/cubemap-IBL
  remain deferred (see the Tier B inventory): the async glTF load is coupled to a live frame/thread
  model the headless harness doesn't provide, and the texture paths need a bundled decodable asset.
  Revisiting needs either a synchronous load path or a way to drive the engine on the composition thread.
