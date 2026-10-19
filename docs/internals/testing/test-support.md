# Test support (`:kotlin:test-support`)

Shared test helpers for deciding **where a test can run**, and for reading frames back on a real backend.
Real-backend tests can't run everywhere (no GPU in some CI), and a few wrapper calls aren't implemented on
the web target: both gaps are handled here, in one place, so every module gates them the same way.

The convention plugin (`filament-kmp-module`) adds the module to every module's `commonTest`. It depends
on `:kotlin:filament`, and on `kotlin-test-junit` for JVM and Android (the fixtures' `@BeforeTest` and
`@AfterTest` need a JUnit flavour there). Everything is in `io.github.erkko68.filament.testsupport`.

## `TestEnv`: runtime environment facts

```kotlin
expect object TestEnv {
    val target: TestTarget               // JVM | JS | NATIVE | ANDROID
    val gpuBackendAvailable: Boolean
    val emulatedGpu: Boolean
}
```

`gpuBackendAvailable` answers "can `Engine.create(DEFAULT)` succeed here?" and **must be checked before**
creating a real backend. On a host with no GPU/display Filament aborts on its driver thread, which a
`try/catch` cannot recover from, so the check is a gate, not a fallback. Tests don't read it directly:
real-backend tests call `assumeRealBackend()` (below), and compose's surface hosts skip on it.

The value is **decided once by Gradle** (`filament-kmp-module.gradle.kts`), not re-derived per platform at
runtime: only Gradle reliably sees the host env (the forked JVM and the iOS simulator don't inherit it).
Gradle injects the result as the `FILAMENT_TEST_GPU` env var (the simulator gets the
`SIMCTL_CHILD_`-prefixed form), and each `TestEnv` actual just reads it. Force either way with
`-PfilamentTestGpu=true|false` or `FILAMENT_TEST_GPU=true|false`.

| Target | `gpuBackendAvailable` default | Reasoning |
|---|---|---|
| Android | always `true` | emulator/device provides GLES |
| JS | always `true` | WebGL engine creation works; per-feature gaps use `@IgnoreJs` instead |
| JVM | `mac` → true, `win` → false, `linux` → has-display | **CI-independent**: macOS Actions runners are Apple-silicon with a real GPU, so Metal runs on CI too. Windows DEFAULT=Vulkan aborts uncatchably without a driver; headless Linux opts in via lavapipe |
| Native (iOS sim) | `true` locally, `false` under CI | a locally-booted sim reaches the host GPU; on a CI runner the sim's Metal driver aborts on real draw. Flip with `-PfilamentTestGpu=true` to test a runner |

`emulatedGpu` is true on the Android emulator and the iOS simulator, whose GPUs render lit content black.

## Real-backend tests: `assumeRealBackend()` and `RealBackendFixture`

Pixel tests live in each module's `pixelTest` source set, compiled for jvm and web only (see the
[strategy](strategy.md)). `assumeRealBackend()` reports the test as **skipped** where the host has no
rasterizer. `RealBackendFixture` calls it, then creates a `DEFAULT` engine and fails the test if that
resolves to `NOOP`:

```kotlin
class ReadbackTest : RealBackendFixture() {
    @Test
    fun clearColourReachesBothReadbackOverloads() { /* uses `engine` */ }
}
```

A module with its own fixture overrides its engine factory instead (`FilamatTestFixture.createEngine()`).

## Reading frames back: `FrameProbe`

`FrameProbe(engine)` owns a readable headless swapchain, renderer, view, camera and scene, plus everything
created through it (`material`, `instance`, `addHorizontalQuad`, `track`). `renderAndRead()` renders a few
frames and returns the last one as RGBA8; `destroy()` tears everything down in dependency order.
`regionStats` and `meanAbsoluteDifference` turn readbacks into the region relations the tests assert.
`Engine.pumpUntil { }` renders empty frames until an async callback lands, for up to 10 s.

## `@IgnoreJs`: skip a test on the web target only

For wrapper calls the web binding can't serve yet, or callbacks a synchronous web test never sees (a
`readPixels` result). The test is valid everywhere else, so skipping the whole class or excluding the
target would lose coverage.

```kotlin
@IgnoreJs // a web readback only lands once the browser gets a frame, which a synchronous test never yields
class ReadbackTest : RealBackendFixture() { ... }
```

`@IgnoreJs` is `kotlin.test.Ignore` on JS and Wasm and a no-op annotation everywhere else, so the test
reports as **skipped** on web and runs normally elsewhere. Always add a trailing comment saying *why*.

### Prefer `@IgnoreJs` over `try/catch (UnsupportedOperationException)`

A `try/catch` that swallows "not supported on this platform" reports the test as **passed** while
verifying nothing, and hides a real regression if the exception is ever thrown for the wrong reason.
`@IgnoreJs` reports *skipped*, is greppable, and documents the gap.

## See also

- [Testing strategy](strategy.md): the NOOP-first tiers, the rules every test follows, and why there is no
  visual-regression testing.
