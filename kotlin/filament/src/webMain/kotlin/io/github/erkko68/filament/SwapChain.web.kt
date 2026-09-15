package io.github.erkko68.filament

import io.github.erkko68.filament.web.SwapChain as JSSwapChain

actual class SwapChain @InternalFilamentApi constructor(internal val jsSwapChain: JSSwapChain) {
    actual enum class FrameRateCompatibility { DEFAULT, FIXED_SOURCE }
    actual enum class ChangeFrameRateStrategy { ONLY_IF_SEAMLESS, ALWAYS }

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "returns null — SwapChain wraps an HTML5 canvas on web, not an OS native window handle.")
    actual val nativeWindow: Any? get() = null

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "silent no-op — `filament.js` does not bind setFrameCompletedCallback, and OpenGLDriver implements it as an empty function, so it could not fire on WebGL either.")
    actual fun setFrameCompletedCallback(callback: (() -> Unit)?) {
    }

    actual fun setFrameScheduledCallback(callback: (() -> Unit)?) {
        jsSwapChain.setFrameScheduledCallback(callback)
    }

    actual val isFrameScheduledCallbackSet: Boolean get() = jsSwapChain.isFrameScheduledCallbackSet()

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "returns sentinel value 1L — SwapChain handle is not exposed as a numeric pointer on web.")
    actual val nativeObject: Long get() = 1L

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "returns false — display frame rate switching is not supported on web; pacing is browser-managed.")
    actual val isFrameRateChangeSupported: Boolean get() = false

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "silent no-op — display frame rate switching is not supported on web; pacing is browser-managed.")
    actual fun setFrameRate(frameRate: Float) {
    }

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "silent no-op — display frame rate switching is not supported on web; pacing is browser-managed.")
    actual fun setFrameRate(frameRate: Float, compatibility: FrameRateCompatibility, strategy: ChangeFrameRateStrategy) {
    }

    actual companion object {
        actual fun isProtectedContentSupported(engine: Engine): Boolean {
            return JSSwapChain.isProtectedContentSupported(engine.jsEngine)
        }

        actual fun isSRGBSwapChainSupported(engine: Engine): Boolean {
            return JSSwapChain.isSRGBSwapChainSupported(engine.jsEngine)
        }

        actual fun isMSAASwapChainSupported(
            engine: Engine,
            samples: Int
        ): Boolean {
            return JSSwapChain.isMSAASwapChainSupported(engine.jsEngine, samples.toDouble())
        }
    }
}