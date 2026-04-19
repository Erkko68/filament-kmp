package io.github.erkko68.filament

import com.google.android.filament.SwapChain as AndroidSwapChain

actual class SwapChain internal constructor(val nativeSwapChain: AndroidSwapChain) {
    actual companion object {
        actual fun isProtectedContentSupported(engine: Engine): Boolean = AndroidSwapChain.isProtectedContentSupported(engine.nativeEngine)
        actual fun isSRGBSwapChainSupported(engine: Engine): Boolean = AndroidSwapChain.isSRGBSwapChainSupported(engine.nativeEngine)
        actual fun isMSAASwapChainSupported(engine: Engine, samples: Int): Boolean = AndroidSwapChain.isMSAASwapChainSupported(engine.nativeEngine, samples)
    }

    actual fun getNativeWindow(): Any? = nativeSwapChain.nativeWindow
    
    actual fun setFrameCompletedCallback(callback: () -> Unit) {
        nativeSwapChain.setFrameCompletedCallback(Runnable::run, Runnable { callback() })
    }

    actual fun setFrameScheduledCallback(callback: () -> Unit) {
        nativeSwapChain.setFrameScheduledCallback(Runnable::run, Runnable { callback() })
    }

    actual fun isFrameScheduledCallbackSet(): Boolean = nativeSwapChain.isFrameScheduledCallbackSet
    
    actual fun getNativeObject(): Long = nativeSwapChain.nativeObject
}
