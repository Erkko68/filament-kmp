# `filament-compose` tests

Status: **Tier A implemented** (backlog item #1) — 17 tests, green on JVM, compiling on JS/iOS, wired
into the jvm/js/ios CI jobs. Tier B (real-backend GPU resources) and the Android instrumented job
remain follow-ups (see end). Scope: bring the most lifecycle-intensive, least-verified module under
automated test without a GPU CI runner.

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
    val setContent: SetSceneContent = { content -> setContent {
        CompositionLocalProvider(LocalFilamentEngine provides engine, LocalFilamentScene provides scene) {
            FilamentSceneScopeInstance.content()
        }
    } }
    body(setContent)
  }
```

**Two gotchas the implementation pins down (both cost real debugging):**
- `mainClock.autoAdvance = false` is mandatory. With it `true`, `OnFrame`'s perpetual
  `withFrameNanos` loop keeps the composition non-idle and `waitForIdle()` never returns (manifested
  as a ~13-minute "hang"). Frames are stepped explicitly via `mainClock.advanceTimeByFrame()`.
- Assert live state **inside `whileComposed`**, not after `composeScene` returns — by then the
  composition is disposed and every component is correctly gone (asserting `hasComponent` post-return
  reads `false`, which looks like a leak/bug but is just the dispose working).

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

### Tier B — `DEFAULT`, gated (`engine ?: return`)

- **`PrimitiveLifecycleTest`** — `Cube`/`Sphere`/`Plane`/`Cylinder`/`Mesh`: composes →
  `scene.renderableCount == 1` & vertex/index buffers live; disposes → renderable gone, buffers
  destroyed (the dispose-order comments in `MeshData.kt`).
- **`MaterialTextureLifecycleTest`** — `rememberMaterial`/`rememberTexture` create then free; the
  `null` + `onError` path on a bad asset (does not throw in composition).
- **`GltfLifecycleTest`** — `rememberGltfAsset` + `GltfInstance` load/share/dispose (needs a small
  bundled `.glb`); `@IgnoreJs` where the web binding gaps already documented apply.
- **`EnvironmentLifecycleTest`** — `SkyboxState`/`IndirectLightState` apply + dispose.

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

## Follow-ups

- **Android instrumented job.** `commonTest` flows into the on-device test source set
  (`connectedAndroidDeviceTest`), and `runComposeUiTest` on Android needs a test activity registered
  via `compose.uiTestManifest` (a `debugImplementation`). That wiring + an emulator run must be
  validated before adding `:kotlin:filament-compose:connectedAndroidDeviceTest` to CI — deferred so a
  red Android job isn't shipped unverified. jvm/js/ios already cover the three distinct actuals the
  Tier-A logic exercises.
- **Tier B** — the gated real-backend suite for primitives, materials/textures, glTF, environments
  (the inventory below), riding the same free GPU runners as the core rendering tests.
