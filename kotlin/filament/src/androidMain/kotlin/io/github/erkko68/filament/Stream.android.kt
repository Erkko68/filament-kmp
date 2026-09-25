package io.github.erkko68.filament

import com.google.android.filament.Stream as AndroidStream

@PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "setDimensions throws — FStream waits on a fence internally, which single-threaded wasm rejects; external video streams have no WebGL source anyway.")
actual class Stream @InternalFilamentApi constructor(internal val nativeStream: AndroidStream) {
    actual enum class StreamType {
        NATIVE,
        ACQUIRED
    }

    actual class Builder actual constructor() {
        private val nativeBuilder = AndroidStream.Builder()

        actual fun width(width: Int): Builder {
            nativeBuilder.width(width)
            return this
        }

        actual fun height(height: Int): Builder {
            nativeBuilder.height(height)
            return this
        }

        actual fun build(engine: Engine): Stream {
            return Stream(nativeBuilder.build(engine.nativeEngine))
        }
    }

    actual val streamType: StreamType get() = StreamType.entries[nativeStream.getStreamType().ordinal]

    actual fun setDimensions(width: Int, height: Int) {
        nativeStream.setDimensions(width, height)
    }

    actual val timestamp: Long get() = nativeStream.timestamp
}
