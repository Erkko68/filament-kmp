package dev.filament.kmp

actual class Scene internal constructor() {
    actual val isValid: Boolean
        get() = false

    actual internal fun invalidate() = Unit
}

