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

    private var frameCompletedRef: StableRef<() -> Unit>? = null
    private var frameScheduledRef: StableRef<() -> Unit>? = null

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "silent no-op — frame callbacks are only supported on the Metal backend; on web, frame presentation is managed by the browser.")
    actual fun setFrameCompletedCallback(callback: () -> Unit) {
        frameCompletedRef?.dispose()
        val stableRef = StableRef.create(callback)
        frameCompletedRef = stableRef
        val callbackWrapper = staticCFunction { _: CPointer<FilaSwapChain>?, user: COpaquePointer? ->
            val ref = user!!.asStableRef<() -> Unit>()
            ref.get().invoke()
        }
        FilaSwapChain_setFrameCompletedCallback(nativeHandle, null, callbackWrapper, stableRef.asCPointer())
    }

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "tracked locally only — frame callbacks are only supported on the Metal backend; on web, frame presentation is managed by the browser.")
    actual fun setFrameScheduledCallback(callback: () -> Unit) {
        frameScheduledRef?.dispose()
        val stableRef = StableRef.create(callback)
        frameScheduledRef = stableRef
        val callbackWrapper = staticCFunction { user: COpaquePointer? ->
            val ref = user!!.asStableRef<() -> Unit>()
            ref.get().invoke()
        }
        FilaSwapChain_setFrameScheduledCallback(nativeHandle, null, callbackWrapper, stableRef.asCPointer())
    }

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "tracked locally only — frame callbacks are only supported on the Metal backend; on web, frame presentation is managed by the browser.")
    actual val isFrameScheduledCallbackSet: Boolean get() = FilaSwapChain_isFrameScheduledCallbackSet(nativeHandle)

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "returns false — display frame rate switching is not supported on web; pacing is browser-managed.")
    actual fun isFrameRateChangeSupported(): Boolean = FilaSwapChain_isFrameRateChangeSupported(nativeHandle)

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
