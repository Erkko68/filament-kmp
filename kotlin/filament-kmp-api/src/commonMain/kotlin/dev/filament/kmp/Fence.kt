package dev.filament.kmp

expect class Fence {
    enum class Mode {
        FLUSH,
        DONT_FLUSH,
    }

    enum class FenceStatus {
        ERROR,
        CONDITION_SATISFIED,
        TIMEOUT_EXPIRED,
    }

    /**
     * Blocks the current thread until the Fence signals.
     */
    fun wait(mode: Mode, timeoutNanoSeconds: Long): FenceStatus

    val nativeObject: Long

    companion object {
        val WAIT_FOR_EVER: Long

        fun waitAndDestroy(fence: Fence, mode: Mode): FenceStatus
    }
}

