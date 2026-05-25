package io.github.erkko68.filament

import io.github.erkko68.filament.js.SwapChain as JSSwapChain

actual class SwapChain(internal val jsSwapChain: JSSwapChain) {
    actual val nativeWindow: Any? get() = null

    actual fun setFrameCompletedCallback(callback: () -> Unit) {
    }

    actual fun setFrameScheduledCallback(callback: () -> Unit) {
    }

    actual val isFrameScheduledCallbackSet: Boolean get() = false

    actual val nativeObject: Long get() = 0

    actual companion object {
        actual fun isProtectedContentSupported(engine: Engine): Boolean {
            return false
        }

        actual fun isSRGBSwapChainSupported(engine: Engine): Boolean {
            return JSSwapChain.isSRGBSwapChainSupported(engine.jsEngine)
        }

        actual fun isMSAASwapChainSupported(
            engine: Engine,
            samples: Int
        ): Boolean {
            return false // WebGL depth/stencil MSAA is tricky
        }
    }
}