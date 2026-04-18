package io.github.erkko68.filament

actual class Fence {
    actual fun wait(
        mode: Mode,
        timeout: Long
    ): FenceStatus {
        // JS doesn't support blocking waits. Return satisfied immediately.
        return FenceStatus.CONDITION_SATISFIED
    }

    actual fun getNativeObject(): Long {
        return 0
    }

    actual enum class Mode { FLUSH, DONT_FLUSH }
    actual enum class FenceStatus { ERROR, ALREADY_SIGNALED, TIMEOUT_EXPIRED, CONDITION_SATISFIED }
}