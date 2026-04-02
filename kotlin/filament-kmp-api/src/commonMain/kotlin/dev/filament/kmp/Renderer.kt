package dev.filament.kmp

/**
 * A Renderer is responsible for rendering one frame into a SwapChain.
 */
expect class Renderer {
    /**
     * Returns whether this Renderer wrapper currently points to a valid native instance.
     */
    val isValid: Boolean

    internal fun invalidate()
}

