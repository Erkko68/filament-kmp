package dev.filament.kmp

expect class SwapChain {
    companion object {
        fun isProtectedContentSupported(engine: Engine): Boolean
        fun isSRGBSwapChainSupported(engine: Engine): Boolean
        fun isMSAASwapChainSupported(engine: Engine, samples: Int): Boolean
    }

    fun getNativeWindow(): Any?
    fun setFrameCompletedCallback(handler: Any?, callback: () -> Unit)
    fun setFrameScheduledCallback(handler: Any?, callback: () -> Unit)
    fun isFrameScheduledCallbackSet(): Boolean
    
    fun getNativeObject(): Long
}
