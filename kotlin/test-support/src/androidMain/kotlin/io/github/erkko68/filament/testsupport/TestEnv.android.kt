package io.github.erkko68.filament.testsupport

actual object TestEnv {
    actual val target: TestTarget = TestTarget.ANDROID
    actual val gpuBackendAvailable: Boolean = true // emulator/device provides GLES
    actual val emulatedGpu: Boolean =
        android.os.Build.HARDWARE in listOf("ranchu", "goldfish", "cutf_cvm") ||
            android.os.Build.FINGERPRINT.contains("generic") ||
            android.os.Build.PRODUCT.contains("sdk_gphone")
}

actual annotation class IgnoreJs
