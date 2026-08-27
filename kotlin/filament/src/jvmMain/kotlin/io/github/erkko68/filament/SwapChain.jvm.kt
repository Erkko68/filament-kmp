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

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "returns null — SwapChain wraps an HTML5 canvas on web, not an OS native window handle.")
    actual val nativeWindow: Any? get() = null

    // Persistent upcall stubs: the arena must outlive the swapchain, so it is replaced (and the
    // old one closed) on each re-set. The lambda is captured directly — userData stays NULL.
    private var frameCompletedArena: Arena? = null
    private var frameScheduledArena: Arena? = null

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "silent no-op — frame callbacks are only supported on the Metal backend; on web, frame presentation is managed by the browser.")
    actual fun setFrameCompletedCallback(callback: () -> Unit) {
        frameCompletedArena?.close()
        val arena = Arena.ofShared()
        frameCompletedArena = arena
        val cb = FilaSwapChainFrameCompletedCallback.allocate({ _, _ -> callback() }, arena)
        FilamentC.FilaSwapChain_setFrameCompletedCallback(nativeHandle, NULL, cb, NULL)
    }

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "tracked locally only — frame callbacks are only supported on the Metal backend; on web, frame presentation is managed by the browser.")
    actual fun setFrameScheduledCallback(callback: () -> Unit) {
        frameScheduledArena?.close()
        val arena = Arena.ofShared()
        frameScheduledArena = arena
        val cb = FilaSwapChainFrameScheduledCallback.allocate({ _ -> callback() }, arena)
        FilamentC.FilaSwapChain_setFrameScheduledCallback(nativeHandle, NULL, cb, NULL)
    }

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "tracked locally only — frame callbacks are only supported on the Metal backend; on web, frame presentation is managed by the browser.")
    actual val isFrameScheduledCallbackSet: Boolean get() = FilamentC.FilaSwapChain_isFrameScheduledCallbackSet(nativeHandle)

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "returns false — display frame rate switching is not supported on web; pacing is browser-managed.")
    actual fun isFrameRateChangeSupported(): Boolean = FilamentC.FilaSwapChain_isFrameRateChangeSupported(nativeHandle)

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "silent no-op — display frame rate switching is not supported on web; pacing is browser-managed.")
    actual fun setFrameRate(frameRate: Float) =
        setFrameRate(frameRate, FrameRateCompatibility.DEFAULT, ChangeFrameRateStrategy.ONLY_IF_SEAMLESS)

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "silent no-op — display frame rate switching is not supported on web; pacing is browser-managed.")
    actual fun setFrameRate(frameRate: Float, compatibility: FrameRateCompatibility, strategy: ChangeFrameRateStrategy) {
        FilamentC.FilaSwapChain_setFrameRate(nativeHandle, frameRate, compatibility.ordinal.toByte(), strategy.ordinal.toByte())
    }

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "returns sentinel value 1L — SwapChain handle is not exposed as a numeric pointer on web.")
    actual val nativeObject: Long get() = nativeHandle?.address() ?: 0L
}
