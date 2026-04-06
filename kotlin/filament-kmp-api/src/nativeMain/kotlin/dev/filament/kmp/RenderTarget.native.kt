@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp

import kotlinx.cinterop.*
import dev.filament.kmp.cinterop.*
import cnames.structs.FilaRenderTarget

actual class RenderTarget internal constructor(internal var nativeHandle: CPointer<FilaRenderTarget>?) {
    actual fun getNativeObject(): Long = nativeHandle?.rawValue?.toLong() ?: 0L
}
