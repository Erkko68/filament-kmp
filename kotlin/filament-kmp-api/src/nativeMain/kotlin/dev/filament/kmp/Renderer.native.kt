package dev.filament.kmp

actual class Renderer internal constructor() {
    actual val isValid: Boolean
        get() = false

    actual internal fun invalidate() = Unit
}

