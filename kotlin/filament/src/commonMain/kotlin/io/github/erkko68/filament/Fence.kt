package io.github.erkko68.filament

expect class Fence {
    enum class Mode { FLUSH, DONT_FLUSH }
    enum class FenceStatus { ERROR, ALREADY_SIGNALED, TIMEOUT_EXPIRED, CONDITION_SATISFIED }

    fun wait(mode: Mode, timeout: Long): FenceStatus
    val nativeObject: Long

    companion object {
        fun waitAndDestroy(fence: Fence, mode: Mode): FenceStatus
    }
}
