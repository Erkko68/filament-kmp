package dev.filament.kmp

/**
 * A Scene contains all visible entities and lighting information.
 */
expect class Scene {
    /**
     * Returns whether this Scene wrapper currently points to a valid native instance.
     */
    val isValid: Boolean

    internal fun invalidate()
}

