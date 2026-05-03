package io.github.erkko68.filament

expect class SwapChain {
    companion object {
        fun isProtectedContentSupported(engine: Engine): Boolean
        fun isSRGBSwapChainSupported(engine: Engine): Boolean
        fun isMSAASwapChainSupported(engine: Engine, samples: Int): Boolean
    }

    val nativeWindow: Any?
    val nativeObject: Long
    val isFrameScheduledCallbackSet: Boolean
    fun setFrameCompletedCallback(callback: () -> Unit)
    fun setFrameScheduledCallback(callback: () -> Unit)
}
