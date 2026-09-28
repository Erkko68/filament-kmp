package io.github.erkko68.filament

import io.github.erkko68.filament.jni.*

actual class Fence @InternalFilamentApi constructor(internal var nativeHandle: Long) {
    actual enum class Mode { FLUSH, DONT_FLUSH }
    actual enum class FenceStatus { ERROR, ALREADY_SIGNALED, TIMEOUT_EXPIRED, CONDITION_SATISFIED }

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "the timeout is clamped to 0 — wasm is single-threaded, so wait() is a non-blocking poll (a FLUSH has already executed every command).")
    actual fun wait(mode: Mode, timeout: Long): FenceStatus {
        val result = FilaFence_wait(nativeHandle, mode.ordinal, timeout)
        return FenceStatus.entries[result + 1] // ERROR is -1, ordinal 0
    }

    actual val nativeObject: Long get() = nativeHandle

    actual companion object {
        actual fun waitAndDestroy(fence: Fence, mode: Mode): FenceStatus {
            val result = FilaFence_waitAndDestroy(fence.nativeHandle, mode.ordinal)
            fence.nativeHandle = 0
            return FenceStatus.entries[result + 1]
        }
    }
}
