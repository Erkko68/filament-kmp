package io.github.erkko68.filament

import io.github.erkko68.filament.js.SwapChain as JSSwapChain

actual class SwapChain(internal val jsSwapChain: JSSwapChain) {
    actual fun getNativeWindow(): Any? {
        return null
    }

    actual fun setFrameCompletedCallback(handler: Any?, callback: () -> Unit) {
    }

    actual fun setFrameScheduledCallback(handler: Any?, callback: () -> Unit) {
    }

    actual fun isFrameScheduledCallbackSet(): Boolean {
        return false
    }

    actual fun getNativeObject(): Long {
        return 0
    }

    actual companion object {
        actual fun isProtectedContentSupported(engine: Engine): Boolean {
            return false
        }

        actual fun isSRGBSwapChainSupported(engine: Engine): Boolean {
            return true
        }

        actual fun isMSAASwapChainSupported(
            engine: Engine,
            samples: Int
        ): Boolean {
            return false // WebGL depth/stencil MSAA is tricky
        }
    }
}