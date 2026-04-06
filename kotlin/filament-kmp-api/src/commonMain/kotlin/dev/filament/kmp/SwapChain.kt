package dev.filament.kmp

/**
 * A SwapChain represents an Operating System's native renderable surface.
 */
expect class SwapChain {

    /**
     * @return the native Object this SwapChain was created from or null for a headless SwapChain.
     */
    fun getNativeWindow(): Any?

    /**
     * FrameCompletedCallback is a callback function that notifies an application when a frame's
     * contents have completed rendering on the GPU.
     */
    fun setFrameCompletedCallback(handler: Any, callback: Runnable)

    /**
     * FrameScheduledCallback is a callback function that notifies an application about the status
     * of a frame after Filament has finished its processing.
     */
    fun setFrameScheduledCallback(handler: Any, callback: Runnable)

    /**
     * Returns whether this SwapChain currently has a FrameScheduledCallback set.
     */
    fun isFrameScheduledCallbackSet(): Boolean

    companion object {
        /**
         * Return whether createSwapChain supports the CONFIG_PROTECTED_CONTENT flag.
         */
        fun isProtectedContentSupported(engine: Engine): Boolean

        /**
         * Return whether createSwapChain supports the CONFIG_SRGB_COLORSPACE flag.
         */
        fun isSRGBSwapChainSupported(engine: Engine): Boolean

        /**
         * Return whether createSwapChain supports the CONFIG_MSAA_*_SAMPLES flag.
         */
        fun isMSAASwapChainSupported(engine: Engine, samples: Int): Boolean
    }
}
