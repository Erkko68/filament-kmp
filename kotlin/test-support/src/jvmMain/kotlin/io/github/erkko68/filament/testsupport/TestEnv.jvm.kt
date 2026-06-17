package io.github.erkko68.filament.testsupport

actual object TestEnv {
    actual val target: TestTarget = TestTarget.JVM
    actual val gpuBackendAvailable: Boolean by lazy {
        when (System.getenv("FILAMENT_TEST_GPU")?.lowercase()) {
            "true", "1" -> true
            "false", "0" -> false
            else -> {
                val os = System.getProperty("os.name").orEmpty().lowercase()
                // Apple silicon always has Metal (even headless); elsewhere require a display.
                os.contains("mac") || !java.awt.GraphicsEnvironment.isHeadless()
            }
        }
    }
}

actual annotation class IgnoreJs
