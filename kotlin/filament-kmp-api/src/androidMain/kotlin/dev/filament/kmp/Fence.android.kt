package dev.filament.kmp

import com.google.android.filament.Fence as AndroidFence

actual class Fence internal constructor(
    internal var androidFence: AndroidFence?,
) {
    /**
     * Blocks the current thread until the Fence signals.
     */
    actual fun wait(mode: Mode, timeoutNanoSeconds: Long): FenceStatus {
        val fence = requireNotNull(androidFence) { "Calling method on destroyed Fence" }
        return fence.wait(mode.toAndroid(), timeoutNanoSeconds).toKmp()
    }

    actual fun getNativeObject(): Long {
        val fence = requireNotNull(androidFence) { "Calling method on destroyed Fence" }
        return fence.nativeObject
    }

    internal fun clearNativeObject() {
        androidFence = null
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

    actual companion object {
        actual val WAIT_FOR_EVER: Long = AndroidFence.WAIT_FOR_EVER

        actual fun waitAndDestroy(fence: Fence, mode: Mode): FenceStatus {
            val handle = requireNotNull(fence.androidFence) { "Calling method on destroyed Fence" }
            val result = AndroidFence.waitAndDestroy(handle, mode.toAndroid())
            fence.clearNativeObject()
            return result.toKmp()
        }
    }
}

private fun Fence.Mode.toAndroid(): AndroidFence.Mode = when (this) {
    Fence.Mode.FLUSH -> AndroidFence.Mode.FLUSH
    Fence.Mode.DONT_FLUSH -> AndroidFence.Mode.DONT_FLUSH
}

private fun AndroidFence.FenceStatus.toKmp(): Fence.FenceStatus = when (this) {
    AndroidFence.FenceStatus.ERROR -> Fence.FenceStatus.ERROR
    AndroidFence.FenceStatus.CONDITION_SATISFIED -> Fence.FenceStatus.CONDITION_SATISFIED
    AndroidFence.FenceStatus.TIMEOUT_EXPIRED -> Fence.FenceStatus.TIMEOUT_EXPIRED
}

