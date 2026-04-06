@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import dev.filament.kmp.cinterop.*
import cnames.structs.FilaSwapChain

actual class SwapChain internal constructor(internal var nativeHandle: CPointer<FilaSwapChain>?) {
    actual fun getNativeObject(): Long = nativeHandle?.rawValue?.toLong() ?: 0L
}
