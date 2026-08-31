package io.github.erkko68.filament

import io.github.erkko68.filament.ffm.FilamentC
import io.github.erkko68.filament.ffm.FilaSwapChainFrameCompletedCallback
import io.github.erkko68.filament.ffm.FilaSwapChainFrameScheduledCallback
import java.lang.foreign.Arena
import java.lang.foreign.MemorySegment

actual class SwapChain @InternalFilamentApi constructor(internal var nativeHandle: MemorySegment?) {
    actual enum class FrameRateCompatibility { DEFAULT, FIXED_SOURCE }
    actual enum class ChangeFrameRateStrategy { ONLY_IF_SEAMLESS, ALWAYS }

    actual companion object {
        actual fun isProtectedContentSupported(engine: Engine): Boolean = FilamentC.FilaSwapChain_isProtectedContentSupported(engine.nativeHandle)
        actual fun isSRGBSwapChainSupported(engine: Engine): Boolean = FilamentC.FilaSwapChain_isSRGBSwapChainSupported(engine.nativeHandle)
        actual fun isMSAASwapChainSupported(engine: Engine, samples: Int): Boolean = FilamentC.FilaSwapChain_isMSAASwapChainSupported(engine.nativeHandle, samples)
    }

    actual val nativeWindow: Any? get() = null

    // One stub per swapchain, allocated on first use and freed only when the swapchain is
    // destroyed. Re-setting a callback swaps the field the stub reads rather than reallocating:
    // freeing a stub the backend still holds for an in-flight frame is a use-after-free.
    private var callbackArena: Arena? = Arena.ofShared()
    private var frameCompletedStub: MemorySegment? = null
    private var frameScheduledStub: MemorySegment? = null
    @Volatile private var frameCompleted: (() -> Unit)? = null
    @Volatile private var frameScheduled: (() -> Unit)? = null

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "silent no-op — `filament.js` does not bind setFrameCompletedCallback, and OpenGLDriver implements it as an empty function, so it could not fire on WebGL either.")
    actual fun setFrameCompletedCallback(callback: (() -> Unit)?) {
        frameCompleted = callback
        val arena = callbackArena
        if (callback != null && frameCompletedStub == null && arena != null) {
            frameCompletedStub = FilaSwapChainFrameCompletedCallback.allocate(
                { _, _ -> frameCompleted?.let { upcall(it) } }, arena)
        }
        val stub = if (callback == null) NULL else frameCompletedStub ?: NULL
        FilamentC.FilaSwapChain_setFrameCompletedCallback(nativeHandle, NULL, stub, NULL)
    }

    actual fun setFrameScheduledCallback(callback: (() -> Unit)?) {
        frameScheduled = callback
        val arena = callbackArena
        if (callback != null && frameScheduledStub == null && arena != null) {
            frameScheduledStub = FilaSwapChainFrameScheduledCallback.allocate(
                { _ -> frameScheduled?.let { upcall(it) } }, arena)
        }
        val stub = if (callback == null) NULL else frameScheduledStub ?: NULL
        FilamentC.FilaSwapChain_setFrameScheduledCallback(nativeHandle, NULL, stub, NULL)
    }

    // Called once the swapchain is destroyed and no further frame callbacks can fire.
    internal fun releaseCallbackStubs() {
        frameCompleted = null
        frameScheduled = null
        frameCompletedStub = null
        frameScheduledStub = null
        callbackArena?.close()
        callbackArena = null
    }

    actual val isFrameScheduledCallbackSet: Boolean get() = FilamentC.FilaSwapChain_isFrameScheduledCallbackSet(nativeHandle)

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "returns false — display frame rate switching is not supported on web; pacing is browser-managed.")
    actual val isFrameRateChangeSupported: Boolean get() = FilamentC.FilaSwapChain_isFrameRateChangeSupported(nativeHandle)

    actual fun setFrameRate(frameRate: Float) =
        setFrameRate(frameRate, FrameRateCompatibility.DEFAULT, ChangeFrameRateStrategy.ONLY_IF_SEAMLESS)

    actual fun setFrameRate(frameRate: Float, compatibility: FrameRateCompatibility, strategy: ChangeFrameRateStrategy) {
        FilamentC.FilaSwapChain_setFrameRate(nativeHandle, frameRate, compatibility.ordinal.toByte(), strategy.ordinal.toByte())
    }

    actual val nativeObject: Long get() = nativeHandle?.address() ?: 0L
}
