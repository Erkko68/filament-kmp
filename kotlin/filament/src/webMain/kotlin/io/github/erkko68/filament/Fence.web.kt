package io.github.erkko68.filament

import io.github.erkko68.filament.wasm.*

// Single-threaded wasm: Filament rejects a non-zero wait timeout ("requires threads"), and a FLUSH
// runs every command synchronously anyway, so waits are a timeout-0 poll.
actual class Fence @InternalFilamentApi constructor(
    internal var nativeHandle: Int,
    private val engine: Int = 0,
) {
    actual enum class Mode { FLUSH, DONT_FLUSH }
    actual enum class FenceStatus { ERROR, ALREADY_SIGNALED, TIMEOUT_EXPIRED, CONDITION_SATISFIED }

    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "the timeout is clamped to 0 — wasm is single-threaded, so wait() is a non-blocking poll (a FLUSH has already executed every command).")
    actual fun wait(mode: Mode, timeout: Long): FenceStatus {
        val result = FilaFence_wait(nativeHandle, mode.ordinal, 0L)
        return FenceStatus.entries[result + 1] // ERROR is -1, ordinal 0
    }

    actual val nativeObject: Long get() = nativeHandle.toLong()

    actual companion object {
        actual fun waitAndDestroy(fence: Fence, mode: Mode): FenceStatus {
            val status = fence.wait(mode, 0L)
            FilaEngine_destroyFence(fence.engine, fence.nativeHandle)
            fence.nativeHandle = 0
            return status
        }
    }
}
