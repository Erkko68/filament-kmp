package io.github.erkko68.filament

import com.google.android.filament.Fence as AndroidFence

actual class Fence internal constructor(val nativeFence: AndroidFence) {
    actual enum class Mode { FLUSH, DONT_FLUSH }
    actual enum class FenceStatus { ERROR, ALREADY_SIGNALED, TIMEOUT_EXPIRED, CONDITION_SATISFIED }

    actual fun wait(mode: Mode, timeout: Long): FenceStatus {
        val androidMode = AndroidFence.Mode.values()[mode.ordinal]
        val result = nativeFence.wait(androidMode, timeout)
        return when (result) {
            AndroidFence.FenceStatus.ERROR -> FenceStatus.ERROR
            AndroidFence.FenceStatus.CONDITION_SATISFIED -> FenceStatus.CONDITION_SATISFIED
            AndroidFence.FenceStatus.TIMEOUT_EXPIRED -> FenceStatus.TIMEOUT_EXPIRED
        }
    }

    actual fun getNativeObject(): Long = nativeFence.nativeObject

    actual companion object {
        actual fun waitAndDestroy(fence: Fence, mode: Mode): FenceStatus {
            val androidMode = AndroidFence.Mode.values()[mode.ordinal]
            return when (AndroidFence.waitAndDestroy(fence.nativeFence, androidMode)) {
                AndroidFence.FenceStatus.ERROR -> FenceStatus.ERROR
                AndroidFence.FenceStatus.CONDITION_SATISFIED -> FenceStatus.CONDITION_SATISFIED
                AndroidFence.FenceStatus.TIMEOUT_EXPIRED -> FenceStatus.TIMEOUT_EXPIRED
            }
        }
    }
}
