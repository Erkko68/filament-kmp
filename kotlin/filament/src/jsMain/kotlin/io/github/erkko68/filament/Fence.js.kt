package io.github.erkko68.filament

actual class Fence {
    actual fun wait(
        mode: Mode,
        timeout: Long
    ): FenceStatus {
        TODO("Not yet implemented")
    }

    actual fun getNativeObject(): Long {
        TODO("Not yet implemented")
    }

    actual enum class Mode { FLUSH, DONT_FLUSH }
    actual enum class FenceStatus { ERROR, ALREADY_SIGNALED, TIMEOUT_EXPIRED, CONDITION_SATISFIED }
}