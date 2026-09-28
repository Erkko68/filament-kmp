package io.github.erkko68.filament

import io.github.erkko68.filament.jni.*

actual class SwapChain @InternalFilamentApi constructor(internal var nativeHandle: Long, private var window: Long = 0) {
    actual enum class FrameRateCompatibility { DEFAULT, FIXED_SOURCE }
    actual enum class ChangeFrameRateStrategy { ONLY_IF_SEAMLESS, ALWAYS }

    actual companion object {
        actual fun isProtectedContentSupported(engine: Engine): Boolean = FilaSwapChain_isProtectedContentSupported(engine.nativeHandle)
        actual fun isSRGBSwapChainSupported(engine: Engine): Boolean = FilaSwapChain_isSRGBSwapChainSupported(engine.nativeHandle)
        actual fun isMSAASwapChainSupported(engine: Engine, samples: Int): Boolean = FilaSwapChain_isMSAASwapChainSupported(engine.nativeHandle, samples)
    }

    actual val nativeWindow: Any? get() = window.takeIf { it != 0L }

    internal fun releaseWindow() {
        if (window != 0L) FilaJni.releaseWindow(window)
        window = 0
    }

    // One registry entry per callback kind, released only when the swapchain is destroyed:
    // releasing what the backend may still call for an in-flight frame would drop the callback.
    private var completed: (() -> Unit)? = null
    private var scheduled: (() -> Unit)? = null
    private var completedId = 0L
    private var scheduledId = 0L

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "never fires — OpenGLDriver (the WebGL backend) implements the frame-completed callback as a no-op.")
    actual fun setFrameCompletedCallback(callback: (() -> Unit)?) {
        completed = callback
        if (callback == null) {
            FilaSwapChain_setFrameCompletedCallback(nativeHandle, 0, 0, 0)
            return
        }
        if (completedId == 0L) completedId = Callbacks.register(once = false) { _, _ -> completed?.invoke() }
        FilaSwapChain_setFrameCompletedCallback(nativeHandle, 0, Callbacks.argUser, completedId)
    }

    actual fun setFrameScheduledCallback(callback: (() -> Unit)?) {
        scheduled = callback
        if (callback == null) {
            FilaSwapChain_setFrameScheduledCallback(nativeHandle, 0, 0, 0)
            return
        }
        if (scheduledId == 0L) scheduledId = Callbacks.register(once = false) { _, _ -> scheduled?.invoke() }
        FilaSwapChain_setFrameScheduledCallback(nativeHandle, 0, Callbacks.userOnly, scheduledId)
    }

    // Called once the swapchain is destroyed and no further frame callbacks can fire.
    internal fun releaseCallbackStubs() {
        completed = null
        scheduled = null
        if (completedId != 0L) Callbacks.release(completedId)
        if (scheduledId != 0L) Callbacks.release(scheduledId)
        completedId = 0
        scheduledId = 0
    }

    actual val isFrameScheduledCallbackSet: Boolean get() = FilaSwapChain_isFrameScheduledCallbackSet(nativeHandle)

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "returns false — display frame rate switching is not supported on web; pacing is browser-managed.")
    actual val isFrameRateChangeSupported: Boolean get() = FilaSwapChain_isFrameRateChangeSupported(nativeHandle)

    actual fun setFrameRate(frameRate: Float) =
        setFrameRate(frameRate, FrameRateCompatibility.DEFAULT, ChangeFrameRateStrategy.ONLY_IF_SEAMLESS)

    actual fun setFrameRate(frameRate: Float, compatibility: FrameRateCompatibility, strategy: ChangeFrameRateStrategy) {
        FilaSwapChain_setFrameRate(nativeHandle, frameRate, compatibility.ordinal, strategy.ordinal)
    }

    actual val nativeObject: Long get() = nativeHandle
}
