@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament

import kotlinx.cinterop.*
import io.github.erkko68.filament.cinterop.*
import cnames.structs.FilaSwapChain

actual class SwapChain @InternalFilamentApi constructor(internal var nativeHandle: CPointer<FilaSwapChain>?) {
    actual enum class FrameRateCompatibility { DEFAULT, FIXED_SOURCE }
    actual enum class ChangeFrameRateStrategy { ONLY_IF_SEAMLESS, ALWAYS }

    actual companion object {
        actual fun isProtectedContentSupported(engine: Engine): Boolean = FilaSwapChain_isProtectedContentSupported(engine.nativeHandle)
        actual fun isSRGBSwapChainSupported(engine: Engine): Boolean = FilaSwapChain_isSRGBSwapChainSupported(engine.nativeHandle)
        actual fun isMSAASwapChainSupported(engine: Engine, samples: Int): Boolean = FilaSwapChain_isMSAASwapChainSupported(engine.nativeHandle, samples)
    }

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "returns null — SwapChain wraps an HTML5 canvas on web, not an OS native window handle.")
    actual val nativeWindow: Any? get() = null

    // One StableRef per swapchain, disposed only when the swapchain is destroyed. Re-setting a
    // callback swaps a field on the holder rather than disposing the ref: freeing what the
    // backend still holds for an in-flight frame is a use-after-free. `staticCFunction` on a
    // non-capturing lambda is a compile-time constant, so the wrappers cost nothing to rebuild.
    private class FrameCallbacks {
        var completed: (() -> Unit)? = null
        var scheduled: (() -> Unit)? = null
    }

    private val callbacks = FrameCallbacks()
    private var callbacksRef: StableRef<FrameCallbacks>? = StableRef.create(callbacks)

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "silent no-op — `filament.js` does not bind setFrameCompletedCallback, and OpenGLDriver implements it as an empty function, so it could not fire on WebGL either.")
    actual fun setFrameCompletedCallback(callback: (() -> Unit)?) {
        callbacks.completed = callback
        val ref = callbacksRef
        if (callback == null || ref == null) {
            FilaSwapChain_setFrameCompletedCallback(nativeHandle, null, null, null)
            return
        }
        val wrapper = staticCFunction { _: CPointer<FilaSwapChain>?, user: COpaquePointer? ->
            upcall { user!!.asStableRef<FrameCallbacks>().get().completed?.invoke() }
        }
        FilaSwapChain_setFrameCompletedCallback(nativeHandle, null, wrapper, ref.asCPointer())
    }

    actual fun setFrameScheduledCallback(callback: (() -> Unit)?) {
        callbacks.scheduled = callback
        val ref = callbacksRef
        if (callback == null || ref == null) {
            FilaSwapChain_setFrameScheduledCallback(nativeHandle, null, null, null)
            return
        }
        val wrapper = staticCFunction { user: COpaquePointer? ->
            upcall { user!!.asStableRef<FrameCallbacks>().get().scheduled?.invoke() }
        }
        FilaSwapChain_setFrameScheduledCallback(nativeHandle, null, wrapper, ref.asCPointer())
    }

    // Called once the swapchain is destroyed and no further frame callbacks can fire.
    internal fun releaseCallbackStubs() {
        callbacks.completed = null
        callbacks.scheduled = null
        callbacksRef?.dispose()
        callbacksRef = null
    }

    actual val isFrameScheduledCallbackSet: Boolean get() = FilaSwapChain_isFrameScheduledCallbackSet(nativeHandle)

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "returns false — display frame rate switching is not supported on web; pacing is browser-managed.")
    actual val isFrameRateChangeSupported: Boolean get() = FilaSwapChain_isFrameRateChangeSupported(nativeHandle)

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "silent no-op — display frame rate switching is not supported on web; pacing is browser-managed.")
    actual fun setFrameRate(frameRate: Float) =
        setFrameRate(frameRate, FrameRateCompatibility.DEFAULT, ChangeFrameRateStrategy.ONLY_IF_SEAMLESS)

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "silent no-op — display frame rate switching is not supported on web; pacing is browser-managed.")
    actual fun setFrameRate(frameRate: Float, compatibility: FrameRateCompatibility, strategy: ChangeFrameRateStrategy) {
        FilaSwapChain_setFrameRate(nativeHandle, frameRate, compatibility.ordinal.toUByte(), strategy.ordinal.toUByte())
    }

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "returns sentinel value 1L — SwapChain handle is not exposed as a numeric pointer on web.")
    actual val nativeObject: Long get() = nativeHandle?.rawValue?.toLong() ?: 0L
}
