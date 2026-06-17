package io.github.erkko68.filament.testsupport

actual object TestEnv {
    actual val target: TestTarget = TestTarget.JVM
    actual val gpuBackendAvailable: Boolean by lazy {
        when (System.getenv("FILAMENT_TEST_GPU")?.lowercase()) {
            "true", "1" -> true
            "false", "0" -> false
            else -> {
                val os = System.getProperty("os.name").orEmpty().lowercase()
                when {
                    os.contains("mac") -> true // Metal, even headless
                    os.contains("win") -> false
                    else -> !java.awt.GraphicsEnvironment.isHeadless() // Linux: require a display
                }
            }
        }
    }
}

actual annotation class IgnoreJs
