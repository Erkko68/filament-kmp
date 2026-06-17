package io.github.erkko68.filament.testsupport

actual object TestEnv {
    actual val target: TestTarget = TestTarget.JVM
    // Decided by Gradle and injected as FILAMENT_TEST_GPU (see filament-kmp-module.gradle.kts).
    actual val gpuBackendAvailable: Boolean = System.getenv("FILAMENT_TEST_GPU").toBoolean()
}

actual annotation class IgnoreJs
