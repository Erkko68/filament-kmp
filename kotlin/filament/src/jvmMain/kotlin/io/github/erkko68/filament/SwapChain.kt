package io.github.erkko68.filament

import io.github.erkko68.filament.jni.SwapChain as JniSwapChain

actual class SwapChain(val nativeSwapChain: JniSwapChain) {
    actual fun getNativeWindow(): Any? = nativeSwapChain.nativeWindow
    
    actual fun setFrameCompletedCallback(handler: Any?, callback: () -> Unit) {
        nativeSwapChain.setFrameCompletedCallback(handler ?: Any(), Runnable { callback() })
    }

    actual fun setFrameScheduledCallback(handler: Any?, callback: () -> Unit) {
        nativeSwapChain.setFrameScheduledCallback(handler ?: Any(), Runnable { callback() })
    }

    actual fun isFrameScheduledCallbackSet(): Boolean = nativeSwapChain.isFrameScheduledCallbackSet()

    actual fun getNativeObject(): Long = nativeSwapChain.nativeObject

    actual companion object {
        actual fun isProtectedContentSupported(engine: Engine): Boolean = JniSwapChain.isProtectedContentSupported(engine.nativeEngine)
        actual fun isSRGBSwapChainSupported(engine: Engine): Boolean = JniSwapChain.isSRGBSwapChainSupported(engine.nativeEngine)
        actual fun isMSAASwapChainSupported(engine: Engine, samples: Int): Boolean = JniSwapChain.isMSAASwapChainSupported(engine.nativeEngine, samples)
    }
}
