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

Per-platform values:

| Target | `gpuBackendAvailable` | Reasoning |
|---|---|---|
| Android | always `true` | emulator/device provides GLES |
| Native (iOS sim) | always `true` | sim tests run inside a **booted** device; gradle fails earlier if none is booted, so a backend is guaranteed |
| JS | always `true` | WebGL engine creation works; per-feature gaps use `@IgnoreJs` instead |
| JVM | host-dependent | Apple silicon (Metal, even headless) → `true`; elsewhere requires a non-headless display. Override with the `FILAMENT_TEST_GPU` env var (`true`/`1` or `false`/`0`) |

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
