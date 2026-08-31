@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament

import kotlinx.cinterop.*
import io.github.erkko68.filament.cinterop.*
import cnames.structs.FilaFence

actual class Fence internal constructor(internal var nativeHandle: CPointer<FilaFence>?) {
    actual enum class Mode { FLUSH, DONT_FLUSH }
    actual enum class FenceStatus { ERROR, ALREADY_SIGNALED, TIMEOUT_EXPIRED, CONDITION_SATISFIED }

    actual fun wait(mode: Mode, timeout: Long): FenceStatus {
        val result = FilaFence_wait(nativeHandle, mode.ordinal.toUInt(), timeout.toULong())
        return FenceStatus.values()[result + 1] // ERROR is -1, ordinal 0
    }

    actual val nativeObject: Long get() = nativeHandle?.rawValue?.toLong() ?: 0L

    actual companion object {
        actual fun waitAndDestroy(fence: Fence, mode: Mode): FenceStatus {
            val result = FilaFence_waitAndDestroy(fence.nativeHandle, mode.ordinal.toUInt())
            fence.nativeHandle = null
            return FenceStatus.values()[result + 1]
        }
    }
}
