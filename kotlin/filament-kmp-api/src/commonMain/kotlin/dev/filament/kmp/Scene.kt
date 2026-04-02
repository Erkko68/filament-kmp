package dev.filament.kmp

expect class Scene {
    val isValid: Boolean

    internal fun invalidate()
}

