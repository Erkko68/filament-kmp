@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68

import kotlinx.cinterop.*
import io.github.erkko68.cinterop.*
import cnames.structs.FilaSwapChain

actual class SwapChain internal constructor(internal var nativeHandle: CPointer<FilaSwapChain>?) {
    actual companion object {
        actual fun isProtectedContentSupported(engine: Engine): Boolean = FilaSwapChain_isProtectedContentSupported(engine.nativeHandle)
        actual fun isSRGBSwapChainSupported(engine: Engine): Boolean = FilaSwapChain_isSRGBSwapChainSupported(engine.nativeHandle)
        actual fun isMSAASwapChainSupported(engine: Engine, samples: Int): Boolean = FilaSwapChain_isMSAASwapChainSupported(engine.nativeHandle, samples)
    }

    actual fun getNativeWindow(): Any? = null

    private var frameCompletedRef: StableRef<() -> Unit>? = null
    private var frameScheduledRef: StableRef<() -> Unit>? = null

    actual fun setFrameCompletedCallback(handler: Any?, callback: () -> Unit) {
        frameCompletedRef?.dispose()
        val stableRef = StableRef.create(callback)
        frameCompletedRef = stableRef
        val callbackWrapper = staticCFunction { _: CPointer<FilaSwapChain>?, user: COpaquePointer? ->
            val ref = user!!.asStableRef<() -> Unit>()
            ref.get().invoke()
        }
        FilaSwapChain_setFrameCompletedCallback(nativeHandle, null, callbackWrapper, stableRef.asCPointer())
    }

    actual fun setFrameScheduledCallback(handler: Any?, callback: () -> Unit) {
        frameScheduledRef?.dispose()
        val stableRef = StableRef.create(callback)
        frameScheduledRef = stableRef
        val callbackWrapper = staticCFunction { user: COpaquePointer? ->
            val ref = user!!.asStableRef<() -> Unit>()
            ref.get().invoke()
        }
        FilaSwapChain_setFrameScheduledCallback(nativeHandle, null, callbackWrapper, stableRef.asCPointer())
    }

    actual fun isFrameScheduledCallbackSet(): Boolean = FilaSwapChain_isFrameScheduledCallbackSet(nativeHandle)
    
    actual fun getNativeObject(): Long = nativeHandle?.rawValue?.toLong() ?: 0L
}
