package dev.filament.kmp

/**
 * A View defines a camera + scene pairing and rendering options.
 */
expect class View {
    /**
     * Returns whether this View wrapper currently points to a valid native instance.
     */
    val isValid: Boolean

    /**
     * Sets or gets the scene associated with this View.
     */
    var scene: Scene?

    internal fun invalidate()
}

