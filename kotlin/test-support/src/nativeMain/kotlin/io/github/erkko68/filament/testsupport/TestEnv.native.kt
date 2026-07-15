package io.github.erkko68.filament.testsupport

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.getenv

@OptIn(ExperimentalForeignApi::class)
actual object TestEnv {
    actual val target: TestTarget = TestTarget.NATIVE
    // Decided by Gradle and forwarded into the simulator as FILAMENT_TEST_GPU
    // (see filament-kmp-module.gradle.kts).
    actual val gpuBackendAvailable: Boolean =
        getenv("FILAMENT_TEST_GPU")?.toKString().toBoolean()
    actual val emulatedGpu: Boolean = false
}

actual annotation class IgnoreJs
