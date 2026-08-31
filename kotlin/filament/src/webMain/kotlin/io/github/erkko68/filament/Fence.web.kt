package io.github.erkko68.filament

import io.github.erkko68.filament.web.Fence_Mode
import io.github.erkko68.filament.web.Fence as JSFence
import io.github.erkko68.filament.web.FenceStatus as JSFenceStatus

actual class Fence(internal val jsFence: JSFence) {
    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "WebGL cannot block the calling thread, so the timeout is clamped to 0 — wait() is a non-blocking poll of the fence state.")
    actual fun wait(
        mode: Mode,
        timeout: Long
    ): FenceStatus {
        val jsMode = when (mode) {
            Mode.FLUSH -> Fence_Mode.FLUSH
            Mode.DONT_FLUSH -> Fence_Mode.DONT_FLUSH
        }
        return when (jsFence.wait(jsMode, 0.0)) {
            JSFenceStatus.CONDITION_SATISFIED -> FenceStatus.CONDITION_SATISFIED
            JSFenceStatus.TIMEOUT_EXPIRED -> FenceStatus.TIMEOUT_EXPIRED
            else -> FenceStatus.ERROR
        }
    }

    actual val nativeObject: Long get() = 1L

    actual enum class Mode { FLUSH, DONT_FLUSH }
    actual enum class FenceStatus { ERROR, ALREADY_SIGNALED, TIMEOUT_EXPIRED, CONDITION_SATISFIED }

    actual companion object {
        actual fun waitAndDestroy(fence: Fence, mode: Mode): FenceStatus {
            return fence.wait(mode, 0L)
        }
    }
}
