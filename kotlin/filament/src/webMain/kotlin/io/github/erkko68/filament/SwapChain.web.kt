package io.github.erkko68.filament

import io.github.erkko68.filament.web.SwapChain as JSSwapChain

actual class SwapChain @InternalFilamentApi constructor(internal val jsSwapChain: JSSwapChain) {
    actual enum class FrameRateCompatibility { DEFAULT, FIXED_SOURCE }
    actual enum class ChangeFrameRateStrategy { ONLY_IF_SEAMLESS, ALWAYS }

    actual val nativeWindow: Any? get() = null

    // SwapChain callback hooks aren't bound in upstream jsbindings.cpp (v1.76.0).
    // Track locally so the common API reflects the callbacks being installed.
    private var frameScheduledCallback: (() -> Unit)? = null

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "silent no-op — `filament.js` does not bind setFrameCompletedCallback, and OpenGLDriver implements it as an empty function, so it could not fire on WebGL either.")
    actual fun setFrameCompletedCallback(callback: (() -> Unit)?) {
    }

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "state is only tracked locally — filament.js binds no frame-scheduled callback, so it never fires; isFrameScheduledCallbackSet reports what was set here.")
    actual fun setFrameScheduledCallback(callback: (() -> Unit)?) {
        frameScheduledCallback = callback
    }

    actual val isFrameScheduledCallbackSet: Boolean get() = frameScheduledCallback != null

    // TODO(js): nativeObject not bound upstream; return a non-zero sentinel so
    // callers checking `nativeObject != 0` treat the swap chain as live.
    actual val nativeObject: Long get() = 1L

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "returns false — display frame rate switching is not supported on web; pacing is browser-managed.")
    actual val isFrameRateChangeSupported: Boolean get() = false

    actual fun setFrameRate(frameRate: Float) {
    }

    actual fun setFrameRate(frameRate: Float, compatibility: FrameRateCompatibility, strategy: ChangeFrameRateStrategy) {
    }

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