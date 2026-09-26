package io.github.erkko68.filament

import io.github.erkko68.filament.wasm.*

@PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "setDimensions throws — FStream waits on a fence internally, which single-threaded wasm rejects; external video streams have no WebGL source anyway.")
actual class Stream @InternalFilamentApi constructor(internal var nativeHandle: Int) {
    actual enum class StreamType {
        NATIVE,
        ACQUIRED
    }

    actual class Builder actual constructor() {
        private val nativeBuilder = FilaStreamBuilder_create()

        actual fun width(width: Int): Builder {
            FilaStreamBuilder_width(nativeBuilder, width)
            return this
        }

        actual fun height(height: Int): Builder {
            FilaStreamBuilder_height(nativeBuilder, height)
            return this
        }

        actual fun build(engine: Engine): Stream {
            val handle = FilaStreamBuilder_build(nativeBuilder, engine.nativeHandle)
            FilaStreamBuilder_destroy(nativeBuilder)
            return Stream(handle)
        }
    }

    actual val streamType: StreamType get() = StreamType.entries[FilaStream_getStreamType(nativeHandle).toInt()]

    actual fun setDimensions(width: Int, height: Int) {
        FilaStream_setDimensions(nativeHandle, width, height)
    }

    actual val timestamp: Long get() = FilaStream_getTimestamp(nativeHandle)
}
