@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68

import kotlinx.cinterop.*
import io.github.erkko68.cinterop.*
import cnames.structs.FilaFence

actual class Fence internal constructor(internal var nativeHandle: CPointer<FilaFence>?) {
    actual enum class Mode { FLUSH, DONT_FLUSH }
    actual enum class FenceStatus { ERROR, ALREADY_SIGNALED, TIMEOUT_EXPIRED, CONDITION_SATISFIED }

    actual fun wait(mode: Mode, timeout: Long): FenceStatus {
        val result = FilaFence_wait(nativeHandle, mode.ordinal.toUInt(), timeout.toULong())
        return FenceStatus.values()[result.toInt() + 1] // ERROR is -1, ordinal 0
    }

    actual fun getNativeObject(): Long = nativeHandle?.rawValue?.toLong() ?: 0L
}
