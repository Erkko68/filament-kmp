package io.github.erkko68.filament

import com.google.android.filament.SwapChain as AndroidSwapChain

actual class SwapChain @InternalFilamentApi constructor(internal val nativeSwapChain: AndroidSwapChain) {
    actual enum class FrameRateCompatibility { DEFAULT, FIXED_SOURCE }
    actual enum class ChangeFrameRateStrategy { ONLY_IF_SEAMLESS, ALWAYS }

    actual companion object {
        actual fun isProtectedContentSupported(engine: Engine): Boolean = AndroidSwapChain.isProtectedContentSupported(engine.nativeEngine)
        actual fun isSRGBSwapChainSupported(engine: Engine): Boolean = AndroidSwapChain.isSRGBSwapChainSupported(engine.nativeEngine)
        actual fun isMSAASwapChainSupported(engine: Engine, samples: Int): Boolean = AndroidSwapChain.isMSAASwapChainSupported(engine.nativeEngine, samples)
    }

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "returns null — SwapChain wraps an HTML5 canvas on web, not an OS native window handle.")
    actual val nativeWindow: Any? get() = nativeSwapChain.nativeWindow
    
    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "silent no-op — frame callbacks are only supported on the Metal backend; on web, frame presentation is managed by the browser.")
    actual fun setFrameCompletedCallback(callback: (() -> Unit)?) {
        nativeSwapChain.setFrameCompletedCallback(Runnable::run, Runnable { callback?.invoke() })
    }

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "tracked locally only — frame callbacks are only supported on the Metal backend; on web, frame presentation is managed by the browser.")
    actual fun setFrameScheduledCallback(callback: (() -> Unit)?) {
        nativeSwapChain.setFrameScheduledCallback(Runnable::run, Runnable { callback?.invoke() })
    }

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "tracked locally only — frame callbacks are only supported on the Metal backend; on web, frame presentation is managed by the browser.")
    actual val isFrameScheduledCallbackSet: Boolean get() = nativeSwapChain.isFrameScheduledCallbackSet

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "returns false — display frame rate switching is not supported on web; pacing is browser-managed.")
    actual val isFrameRateChangeSupported: Boolean get() = nativeSwapChain.isFrameRateChangeSupported()

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "silent no-op — display frame rate switching is not supported on web; pacing is browser-managed.")
    actual fun setFrameRate(frameRate: Float) = nativeSwapChain.setFrameRate(frameRate)

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "silent no-op — display frame rate switching is not supported on web; pacing is browser-managed.")
    actual fun setFrameRate(frameRate: Float, compatibility: FrameRateCompatibility, strategy: ChangeFrameRateStrategy) =
        nativeSwapChain.setFrameRate(
            frameRate,
            AndroidSwapChain.FrameRateCompatibility.entries[compatibility.ordinal],
            AndroidSwapChain.ChangeFrameRateStrategy.entries[strategy.ordinal]
        )

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "returns sentinel value 1L — SwapChain handle is not exposed as a numeric pointer on web.")
    actual val nativeObject: Long get() = nativeSwapChain.nativeObject
}
