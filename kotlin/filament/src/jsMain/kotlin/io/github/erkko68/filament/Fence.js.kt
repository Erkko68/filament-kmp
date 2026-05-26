package io.github.erkko68.filament

actual class Fence {
    actual fun wait(
        mode: Mode,
        timeout: Long
    ): FenceStatus {
        // JS doesn't support blocking waits. Return satisfied immediately.
        return FenceStatus.CONDITION_SATISFIED
    }

    // Fence isn't bound in upstream jsbindings.cpp (v1.71.4). Return a non-zero
    // sentinel so callers checking `nativeObject != 0` treat the fence as live.
    actual val nativeObject: Long get() = 1L

    actual enum class Mode { FLUSH, DONT_FLUSH }
    actual enum class FenceStatus { ERROR, ALREADY_SIGNALED, TIMEOUT_EXPIRED, CONDITION_SATISFIED }

    actual companion object {
        actual fun waitAndDestroy(fence: Fence, mode: Mode): FenceStatus {
            // JS doesn't support blocking waits
            return FenceStatus.CONDITION_SATISFIED
        }
    }
}