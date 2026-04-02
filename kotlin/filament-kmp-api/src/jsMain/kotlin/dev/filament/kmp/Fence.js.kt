package dev.filament.kmp

actual class Fence internal constructor() {
    actual fun wait(mode: Mode, timeoutNanoSeconds: Long): FenceStatus {
        TODO("Not yet implemented")
    }

    actual fun getNativeObject(): Long {
        TODO("Not yet implemented")
    }

    actual companion object {
        actual val WAIT_FOR_EVER: Long = -1L

        actual fun waitAndDestroy(fence: Fence, mode: Mode): FenceStatus {
            TODO("Not yet implemented")
        }
    }

    actual enum class Mode {
        FLUSH,
        DONT_FLUSH,
    }

    actual enum class FenceStatus {
        ERROR,
        CONDITION_SATISFIED,
        TIMEOUT_EXPIRED,
    }
}

