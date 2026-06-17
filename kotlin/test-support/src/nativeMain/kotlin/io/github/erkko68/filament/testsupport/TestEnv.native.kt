package io.github.erkko68.filament.testsupport

actual object TestEnv {
    actual val target: TestTarget = TestTarget.NATIVE
    // iOS sim tests run inside a booted device (gradle standalone=false); if no device
    // is booted, gradle fails before tests run, so a real backend is always available here.
    actual val gpuBackendAvailable: Boolean = true
}

actual annotation class IgnoreJs
