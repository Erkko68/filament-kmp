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
    
    // Upstream's nSetFrameCompletedCallback/nSetFrameScheduledCallback always install a
    // JniCallback, and both Java overloads are @NonNull, so there is no way to hand the engine
    // the empty callback it unsets on. Null therefore installs a Runnable that does nothing,
    // and [isFrameScheduledCallbackSet] answers from what was set here rather than from the
    // engine — otherwise it would keep reporting true after a clear.
    private var frameScheduled: (() -> Unit)? = null

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "silent no-op — `filament.js` does not bind setFrameCompletedCallback, and OpenGLDriver implements it as an empty function, so it could not fire on WebGL either.")
    actual fun setFrameCompletedCallback(callback: (() -> Unit)?) {
        nativeSwapChain.setFrameCompletedCallback(Runnable::run, Runnable { callback?.invoke() })
    }

    actual fun setFrameScheduledCallback(callback: (() -> Unit)?) {
        frameScheduled = callback
        nativeSwapChain.setFrameScheduledCallback(Runnable::run, Runnable { callback?.invoke() })
    }

    actual val isFrameScheduledCallbackSet: Boolean get() = frameScheduled != null

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
