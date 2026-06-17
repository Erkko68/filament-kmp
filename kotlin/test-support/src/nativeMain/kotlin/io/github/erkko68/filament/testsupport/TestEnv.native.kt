package io.github.erkko68.filament.testsupport

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.getenv

@OptIn(ExperimentalForeignApi::class)
actual object TestEnv {
    actual val target: TestTarget = TestTarget.NATIVE

    // Local iOS-sim tests run in a booted device backed by the host GPU, so a real
    // backend works. On a headless CI runner (no GPU) the sim's Metal driver aborts
    // on real draw/compute, so default off under CI. Override with FILAMENT_TEST_GPU.
    actual val gpuBackendAvailable: Boolean by lazy {
        when (getenv("FILAMENT_TEST_GPU")?.toKString()?.lowercase()) {
            "true", "1" -> true
            "false", "0" -> false
            else -> getenv("CI") == null
        }
    }
}

actual annotation class IgnoreJs
