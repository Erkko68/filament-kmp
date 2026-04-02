package dev.filament.kmp

expect class Renderer {
    val isValid: Boolean

    internal fun invalidate()
}

