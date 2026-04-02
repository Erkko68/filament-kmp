package dev.filament.kmp

/**
 * A <code>SwapChain</code> represents an Operating System's native renderable surface.
 */
expect class SwapChain {
    val isValid: Boolean

    fun getNativeWindow(): Any?

    fun setFrameCompletedCallback(handler: Any, callback: () -> Unit)

    fun setFrameScheduledCallback(handler: Any, callback: () -> Unit)

    fun isFrameScheduledCallbackSet(): Boolean

    fun getNativeObject(): Long

    internal fun invalidate()

    companion object {
        fun isProtectedContentSupported(engine: Engine): Boolean

        fun isSRGBSwapChainSupported(engine: Engine): Boolean

        fun isMSAASwapChainSupported(engine: Engine, samples: Int): Boolean
    }
}

