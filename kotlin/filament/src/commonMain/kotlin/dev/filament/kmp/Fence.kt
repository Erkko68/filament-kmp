package dev.filament.kmp

expect class Fence {
    enum class Mode { FLUSH, DONT_FLUSH }
    enum class FenceStatus { ERROR, ALREADY_SIGNALED, TIMEOUT_EXPIRED, CONDITION_SATISFIED }

    fun wait(mode: Mode, timeout: Long): FenceStatus
    fun getNativeObject(): Long
}
