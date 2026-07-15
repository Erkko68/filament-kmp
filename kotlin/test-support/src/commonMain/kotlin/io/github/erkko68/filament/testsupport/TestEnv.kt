package io.github.erkko68.filament.testsupport

/** The Kotlin compile target the test is currently running on. */
enum class TestTarget { JVM, JS, NATIVE, ANDROID }

/**
 * Runtime test-environment facts shared by every module's commonTest.
 *
 * [gpuBackendAvailable] decides whether a real Filament backend can be created here.
 * It must be checked BEFORE `Engine.create(DEFAULT)` — on a host without a GPU/display
 * (e.g. headless Linux/Windows CI) Filament aborts on its driver thread, which is not
 * catchable. macOS always has Metal; other JVM hosts are gated on a display being
 * present, overridable via the `FILAMENT_TEST_GPU` env var ("true"/"false").
 */
expect object TestEnv {
    val target: TestTarget
    val gpuBackendAvailable: Boolean

    /**
     * True when the GPU is an emulated/software Android stack (goldfish/ranchu).
     * Those advertise GLES 3.1 but render lit content black and can crash the
     * whole emulator on shadow workloads — semantic frame tests must skip.
     */
    val emulatedGpu: Boolean
}

/** Skips the annotated test on JS only (web-wrapper feature gaps); runs elsewhere. */
expect annotation class IgnoreJs()
