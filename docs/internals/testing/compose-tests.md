# `filament-compose` tests

The compose layer's risk isn't pixels: it's **native lifecycle driven by Compose effects**. The suite checks
dispose ordering (nothing destroyed while the scene still draws with it), no leaks (leaving composition
returns every Filament object), in-place updates (a changed parameter updates the live object instead of
rebuilding it), and that state reaches Filament (read back through Filament's own getters).

## Backends

| Where | Backend | Covers |
| :-- | :-- | :-- |
| `commonTest` | `NOOP`, every target (JVM, JS, Wasm, iOS sim, Android) | every scene composable: lights, groups, cameras, primitives, materials, glTF, environments, loaders, teardown order |
| `commonTest` via `withEngine` | `NOOP`, JVM and Android | `FilamentViewTest`, `ScreensTest`: `FilamentView` / `FilamentSceneView` themselves (attach and detach, picks answered, engine and scene swaps, paused rendering, screens, lists) |
| `commonTest` via `surfaceTest` | real surface, gated on `TestEnv.gpuBackendAvailable` | `SurfaceLifecycleTest`: `FilamentView` on the platform's real surface |
| `jvmTest` / `nativeTest` / `webTest` / `androidDeviceTest` | real backend and window | what a pick hits and the view showing its scene (`FilamentViewRealBackendTest`), window/page/activity lifecycle, gestures, the JVM GPU-sharing path |

NOOP runs Filament's whole CPU side, so materials, geometry, textures, IBL and glTF all build on it. Only
pixels and async calls need a real backend (see the [strategy](strategy.md)).

Compose's `runComposeUiTest` host on web and iOS has no interop container, so a `FilamentView` there fails
with "LocalInteropContainer not provided" (the view is an HTML canvas or a `UIView`). The `withEngine`
classes carry `@IgnoreWithoutViewInterop` (skipped on web and iOS); those platforms' views are covered by
`surfaceTest`'s own hosts and `PageLifecycleTest`.

## Fixtures

- **`ComposeTestFixture`**: a NOOP engine and scene per test. `materialInstance()` builds the built-in
  emissive material; `gltfAsset(bytes)` loads a glb synchronously on the test thread. Both are destroyed in
  `tearDown`.
- **`withFilamentScene(engine, scene) { setContent -> }`**: runs `runComposeUiTest` and hands the body a
  `setContent` that mounts scene content with the engine and scene locals provided. No `FilamentView`.
- **`composeScene(engine, scene, frames, whileComposed, afterDispose) { }`**: mount, assert, dispose, assert.
- **`withUiThreadFilamentScene { setContent, engine, scene -> }`**: the same host, but it creates the NOOP
  engine on Compose's UI thread. Use it for anything that calls Filament from a `LaunchedEffect`
  (`rememberGltfAsset`, the async loaders): gltfio's `ResourceLoader` is thread-affine, and on JVM effects
  run on `AWT-EventQueue-0`, not the JUnit thread.
- **`withEngine(backend = NOOP) { engine, setContent -> }`** (`ViewHost.kt`): runs `runComposeUiTest` with an
  engine created and destroyed on the UI thread, for tests that mount the real `FilamentView`. A real
  `backend` skips without a GPU. `renderFrames(engine, n)` steps frames, flushing the engine before each.
- **`requestsFrames` / `compositionFailure`**: compose synchronously in a composition of their own, on
  every target. For "does this keep a frame loop running" and for misuse checks.
- **`ResourceLedger.kt`**: `assertSceneEmpty`, `assertEntitiesDestroyed`, `assertDestroyed`, `danglingInScene`.

Every harness also watches the scene at the start of each frame: it must not draw with anything already
destroyed (its IBL and skybox with their textures, its renderables' material instances). A violation fails
the test at its next mount or at its end.

## Harness gotchas

- **`mainClock.autoAdvance = false`** is required. `OnFrame` runs an unbounded `withFrameNanos` loop, so
  with an auto-advancing clock `waitForIdle()` never returns. Step frames with `advanceTimeByFrame()`; a
  state change only recomposes on a tick.
- **One `setContent`, swapped through state.** Android's `setContent` is one-shot; every (re)mount goes
  through a `mutableStateOf` slot.
- **Return the harness result** (`fun t() = withFilamentScene(...) { }`) and keep assertions inside the
  body: on web `runComposeUiTest` is asynchronous, so code after the call runs before the composition.
- **Dispose before the harness returns.** On wasmJs, teardown awaits the frame clock; a live `OnFrame`
  loop with auto-advance off never lets it settle (Mocha's 30 s timeout).
- **JS needs skiko loaded first.** `ComposeTestFixture`'s `@BeforeTest` returns `awaitGraphicsReady()`
  (skiko's `onWasmReady` promise on JS), which kotlin.test awaits.
- **Sizes in pixels.** Assert a viewport against `with(density) { 32.dp.roundToPx() }`: the JVM host's
  density is 1, the Android emulator's is not.
- **NOOP option edges.** Bloom `levels` is clamped by `resolution`, and dynamic resolution only stays
  enabled with `homogeneousScaling = true`. Match core `ViewOptionsRoundTripTest`'s configs.

## Android

`androidDeviceTest` needs `androidx.compose.ui:ui-test-manifest` (the host activity) and a current
`espresso-core` (the 3.5.0 that compose ui-test pulls in calls an `InputManager` method removed in
Android 14). The activity lifecycle tests `assumeFalse(TestEnv.emulatedGpu)`: they show up as failed
assumptions on an emulator.
