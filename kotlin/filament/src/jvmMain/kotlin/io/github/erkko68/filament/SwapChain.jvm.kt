package io.github.erkko68.filament

import io.github.erkko68.filament.jni.SwapChain as JniSwapChain

actual class SwapChain(val nativeSwapChain: JniSwapChain) {
    actual val nativeWindow: Any? get() = nativeSwapChain.nativeWindow
    
    actual fun setFrameCompletedCallback(callback: () -> Unit) {
        nativeSwapChain.setFrameCompletedCallback(Runnable::run, Runnable { callback() })
    }

    actual fun setFrameScheduledCallback(callback: () -> Unit) {
        nativeSwapChain.setFrameScheduledCallback(Runnable::run, Runnable { callback() })
    }

    actual val isFrameScheduledCallbackSet: Boolean get() = nativeSwapChain.isFrameScheduledCallbackSet()

    actual val nativeObject: Long get() = nativeSwapChain.nativeObject

    actual companion object {
        actual fun isProtectedContentSupported(engine: Engine): Boolean = JniSwapChain.isProtectedContentSupported(engine.nativeEngine)
        actual fun isSRGBSwapChainSupported(engine: Engine): Boolean = JniSwapChain.isSRGBSwapChainSupported(engine.nativeEngine)
        actual fun isMSAASwapChainSupported(engine: Engine, samples: Int): Boolean = JniSwapChain.isMSAASwapChainSupported(engine.nativeEngine, samples)
    }
}
