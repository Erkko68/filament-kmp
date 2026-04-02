package dev.filament.kmp

import com.google.android.filament.SwapChain as AndroidSwapChain

actual class SwapChain internal constructor(
    internal var androidSwapChain: AndroidSwapChain?,
) {
    actual val isValid: Boolean
        get() = androidSwapChain != null

    actual fun getNativeWindow(): Any? = androidSwapChain?.nativeWindow

    actual fun setFrameCompletedCallback(handler: Any, callback: () -> Unit) {
        val swapChain = requireNotNull(androidSwapChain) { "Calling method on destroyed SwapChain" }
        swapChain.setFrameCompletedCallback(handler, Runnable { callback() })
    }

    actual fun setFrameScheduledCallback(handler: Any, callback: () -> Unit) {
        val swapChain = requireNotNull(androidSwapChain) { "Calling method on destroyed SwapChain" }
        swapChain.setFrameScheduledCallback(handler, Runnable { callback() })
    }

    actual fun isFrameScheduledCallbackSet(): Boolean {
        val swapChain = requireNotNull(androidSwapChain) { "Calling method on destroyed SwapChain" }
        return swapChain.isFrameScheduledCallbackSet
    }

    actual fun getNativeObject(): Long {
        val swapChain = requireNotNull(androidSwapChain) { "Calling method on destroyed SwapChain" }
        return swapChain.nativeObject
    }

    actual internal fun invalidate() {
        androidSwapChain = null
    }

    actual companion object {
        actual fun isProtectedContentSupported(engine: Engine): Boolean {
            val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
            return AndroidSwapChain.isProtectedContentSupported(androidEngine)
        }

        actual fun isSRGBSwapChainSupported(engine: Engine): Boolean {
            val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
            return AndroidSwapChain.isSRGBSwapChainSupported(androidEngine)
        }

        actual fun isMSAASwapChainSupported(engine: Engine, samples: Int): Boolean {
            val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
            return AndroidSwapChain.isMSAASwapChainSupported(androidEngine, samples)
        }
    }
}

