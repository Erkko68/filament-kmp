package dev.filament.kmp

import kotlinx.cinterop.*
import filament.*

actual class SwapChain internal constructor(
    internal val nativeObject: CPointer<FilaSwapChain>
) {
    actual companion object {
        actual const val CONFIG_TRANSPARENT: Long = 0x1
        actual const val CONFIG_READABLE: Long = 0x2
        actual const val CONFIG_ENABLE_X11_ERROR_HANDLER: Long = 0x4
        actual const val CONFIG_SRGB: Long = 0x8
        actual const val CONFIG_PROTECTED_CONTENT: Long = 0x10

        actual fun isSRGBSwapChainSupported(engine: Engine): Boolean = FilaSwapChain_isSRGBSwapChainSupported(engine.nativeObject)
        actual fun isMSAASwapChainSupported(engine: Engine, samples: Int): Boolean = FilaSwapChain_isMSAASwapChainSupported(engine.nativeObject, samples)
        actual fun isProtectedContentSupported(engine: Engine): Boolean = FilaSwapChain_isProtectedContentSupported(engine.nativeObject)
    }

    actual fun setFrameCompletedCallback(handler: Any?, callback: Runnable?) {
        // ... handled via C-wrapper if needed
    }

    actual fun setFrameScheduledCallback(handler: Any?, callback: Runnable?) {
        // ... handled via C-wrapper if needed
    }

    actual fun isFrameScheduledCallbackSet(): Boolean = FilaSwapChain_isFrameScheduledCallbackSet(nativeObject)

    actual val nativeObject: Long get() = nativeObject.toLong()
}
