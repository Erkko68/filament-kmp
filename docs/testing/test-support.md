# Test support: environment gating (`:kotlin:test-support`)

Shared `commonTest` helpers for deciding **where a test can run**. Real-backend
tests can't run everywhere (no GPU in some CI), and a few wrapper calls aren't
implemented on the web target — both gaps are handled here, in one place, so
every module gates them the same way.

The module is a multiplatform test dependency. Add it to any module that needs
gating:

```kotlin
// <module>/build.gradle.kts
commonTest.dependencies {
    implementation(kotlin("test"))
    implementation(project(":kotlin:test-support"))
}
```

It exposes two things, both in `io.github.erkko68.filament.testsupport`.

## `TestEnv` — runtime environment facts

```kotlin
expect object TestEnv {
    val target: TestTarget               // JVM | JS | NATIVE | ANDROID
    val gpuBackendAvailable: Boolean
}
```

`gpuBackendAvailable` answers "can `Engine.create(DEFAULT)` succeed here?" and
**must be checked before** creating a real backend. On a host with no GPU/display
Filament aborts on its driver thread, which a `try/catch` cannot recover from —
so the check is a gate, not a fallback. [`RenderingTestFixture`](../../kotlin/filament/src/commonTest/kotlin/io/github/erkko68/filament/testutils/RenderingTestFixture.kt)
uses it to leave `engine == null`, and tests early-return:

```kotlin
@Test
fun something() {
    val engine = engine ?: return   // skips where no backend
    ...
}
```

The value is **decided once by Gradle** (`filament-kmp-module.gradle.kts`), not
re-derived per platform at runtime — only Gradle reliably sees the host env (the
forked JVM and the iOS simulator don't inherit it). Gradle injects the result as
the `FILAMENT_TEST_GPU` env var (the simulator gets the `SIMCTL_CHILD_`-prefixed
form), and each `TestEnv` actual just reads it. The default differs by target
because the same runner reaches the GPU differently; force either way with
`-PfilamentTestGpu=true|false` or `FILAMENT_TEST_GPU=true|false`.

Per-platform values:

| Target | `gpuBackendAvailable` default | Reasoning |
|---|---|---|
| Android | always `true` | emulator/device provides GLES |
| JS | always `true` | WebGL engine creation works; per-feature gaps use `@IgnoreJs` instead |
| JVM | `mac` → true, `win` → false, `linux` → has-display | **CI-independent**: macOS Actions runners are Apple-silicon with a real GPU, so Metal runs on CI too. Windows DEFAULT=Vulkan aborts uncatchably without a driver; headless Linux opts in via lavapipe |
| Native (iOS sim) | `true` locally, `false` under CI | a locally-booted sim reaches the host GPU; on a CI runner the sim's Metal driver aborts on real draw. Flip with `-PfilamentTestGpu=true` to test a runner |

## `@IgnoreJs` — skip a test on the web target only

For wrapper calls that simply aren't wired in the web (Karakum) binding yet —
the test is valid everywhere else, so skipping the whole class or excluding the
target would lose coverage.

```kotlin
@Test
@IgnoreJs // filamat compilation is not supported in the web wrapper.
fun testMaterialBuilderChainingAndBuild() { ... }
```

`@IgnoreJs` is `kotlin.test.Ignore` on JS and a no-op annotation everywhere else,
so the test reports as **skipped** on JS and runs normally elsewhere. Always add
a trailing comment saying *why* it's skipped.

### Prefer `@IgnoreJs` over `try/catch (UnsupportedOperationException)`

A `try/catch` that swallows "not supported on this platform" reports the test as
**passed** while verifying nothing, and hides a real regression if the exception
is ever thrown for the wrong reason. `@IgnoreJs` reports *skipped*, is greppable,
and documents the gap. Use it.

## See also

- [Real-backend rendering tests](rendering-backend-tests.md) — the fixtures
  (`RenderingTestFixture` & friends) that consume `TestEnv`, and why we don't do
  visual-regression testing.
