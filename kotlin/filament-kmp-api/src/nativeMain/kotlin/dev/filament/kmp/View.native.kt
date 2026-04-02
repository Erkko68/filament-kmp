package dev.filament.kmp

actual class View internal constructor() {
    actual val isValid: Boolean
        get() = false

    actual var scene: Scene?
        get() = null
        set(value) = Unit

    actual internal fun invalidate() = Unit
}

