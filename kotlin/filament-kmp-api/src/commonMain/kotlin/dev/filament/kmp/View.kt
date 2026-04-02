package dev.filament.kmp

expect class View {
    val isValid: Boolean

    var scene: Scene?

    internal fun invalidate()
}

