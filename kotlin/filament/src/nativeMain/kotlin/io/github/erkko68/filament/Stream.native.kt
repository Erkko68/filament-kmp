@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament

import kotlinx.cinterop.*
import io.github.erkko68.filament.cinterop.*
import cnames.structs.FilaStream

actual class Stream internal constructor(internal var nativeHandle: CPointer<FilaStream>?) {
    actual enum class StreamType {
        NATIVE,
        ACQUIRED
    }

    actual class Builder actual constructor() {
        private val nativeBuilder = FilaStreamBuilder_create()

        actual fun width(width: Int): Builder {
            FilaStreamBuilder_width(nativeBuilder, width.toUInt())
            return this
        }

        actual fun height(height: Int): Builder {
            FilaStreamBuilder_height(nativeBuilder, height.toUInt())
            return this
        }

        actual fun build(engine: Engine): Stream {
            val handle = FilaStreamBuilder_build(nativeBuilder, engine.nativeHandle)
            FilaStreamBuilder_destroy(nativeBuilder)
            return Stream(handle)
        }
    }

    actual val streamType: StreamType get() = StreamType.values()[FilaStream_getStreamType(nativeHandle).toInt()]

    actual fun setDimensions(width: Int, height: Int) {
        FilaStream_setDimensions(nativeHandle, width.toUInt(), height.toUInt())
    }

    actual val timestamp: Long get() = FilaStream_getTimestamp(nativeHandle)
}
