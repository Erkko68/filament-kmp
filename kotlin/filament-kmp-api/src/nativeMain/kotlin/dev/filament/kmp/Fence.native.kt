@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import dev.filament.kmp.cinterop.*
import cnames.structs.FilaFence

actual class Fence internal constructor(internal var nativeHandle: CPointer<FilaFence>?) {
    actual fun getNativeObject(): Long = nativeHandle?.rawValue?.toLong() ?: 0L
}
