package dev.filament.kmp

import com.google.android.filament.Engine as AndroidFilamentEngine

actual class Engine private constructor(
    private var androidEngine: AndroidFilamentEngine?,
) {
    actual val isValid: Boolean
        get() = androidEngine != null

    actual fun close() {
        androidEngine?.destroy()
        androidEngine = null
    }

    actual companion object {
        actual fun create(config: EngineConfig): Engine {
            require(config == EngineConfig()) {
                "Android Engine.create currently supports default EngineConfig only."
            }
            return Engine(AndroidFilamentEngine.create())
        }
    }
}