@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import dev.filament.kmp.cinterop.*
import cnames.structs.FilaSwapChain

actual class SwapChain internal constructor(internal var nativeHandle: CPointer<FilaSwapChain>?) {
    actual companion object {
        actual fun isProtectedContentSupported(engine: Engine): Boolean = FilaSwapChain_isProtectedContentSupported(engine.nativeHandle)
        actual fun isSRGBSwapChainSupported(engine: Engine): Boolean = FilaSwapChain_isSRGBSwapChainSupported(engine.nativeHandle)
        actual fun isMSAASwapChainSupported(engine: Engine, samples: Int): Boolean = FilaSwapChain_isMSAASwapChainSupported(engine.nativeHandle, samples)
    }

    actual fun getNativeWindow(): Any? = null
    
    actual fun setFrameCompletedCallback(handler: Any?, callback: () -> Unit) {
        // Callback bridging not yet implemented for Native
        FilaSwapChain_setFrameCompletedCallback(nativeHandle, null, null, null)
    }

    actual fun setFrameScheduledCallback(handler: Any?, callback: () -> Unit) {
        // Callback bridging not yet implemented for Native
        FilaSwapChain_setFrameScheduledCallback(nativeHandle, null, null, null)
    }

    actual fun isFrameScheduledCallbackSet(): Boolean = FilaSwapChain_isFrameScheduledCallbackSet(nativeHandle)
    
    actual fun getNativeObject(): Long = nativeHandle?.rawValue?.toLong() ?: 0L
}
