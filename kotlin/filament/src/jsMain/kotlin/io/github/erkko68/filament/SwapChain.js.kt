package io.github.erkko68.filament

import io.github.erkko68.filament.js.SwapChain as JSSwapChain

actual class SwapChain(internal val jsSwapChain: JSSwapChain) {
    actual val nativeWindow: Any? get() = null

    // SwapChain callback hooks aren't bound in upstream jsbindings.cpp (v1.71.4).
    // Track locally so the common API reflects the callbacks being installed.
    private var frameScheduledCallback: (() -> Unit)? = null

    actual fun setFrameCompletedCallback(callback: () -> Unit) {
    }

    actual fun setFrameScheduledCallback(callback: () -> Unit) {
        frameScheduledCallback = callback
    }

    actual val isFrameScheduledCallbackSet: Boolean get() = frameScheduledCallback != null

    // Not bound upstream; return a non-zero sentinel so callers checking
    // `nativeObject != 0` treat the swap chain as live.
    actual val nativeObject: Long get() = 1L

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