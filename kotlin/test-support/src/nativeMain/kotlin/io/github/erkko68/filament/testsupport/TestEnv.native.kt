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
    // The iOS simulator's GPU ("Apple iOS simulator GPU") is an emulated Metal device:
    // binding-level GPU calls work, but lit content renders ~black — the same class as
    // emulated Android GPUs, so Tier C frame-semantics tests must skip on it. Xcode sets
    // SIMULATOR_UDID in every simulator process; it's absent on devices and desktop hosts.
    actual val emulatedGpu: Boolean = getenv("SIMULATOR_UDID") != null
}

actual annotation class IgnoreJs
