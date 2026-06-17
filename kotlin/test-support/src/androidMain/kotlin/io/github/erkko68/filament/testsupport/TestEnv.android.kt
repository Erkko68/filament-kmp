package io.github.erkko68.filament.testsupport

actual object TestEnv {
    actual val target: TestTarget = TestTarget.ANDROID
    actual val gpuBackendAvailable: Boolean = true // emulator/device provides GLES
}

actual annotation class IgnoreJs
