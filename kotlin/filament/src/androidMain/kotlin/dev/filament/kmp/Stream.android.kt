package dev.filament.kmp

import com.google.android.filament.Stream as AndroidStream

actual class Stream internal constructor(val nativeStream: AndroidStream) {
    actual enum class StreamType {
        NATIVE,
        ACQUIRED
    }

    actual class Builder actual constructor() {
        private val nativeBuilder = AndroidStream.Builder()

        actual fun stream(streamSource: Any): Builder {
            nativeBuilder.stream(streamSource)
            return this
        }

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

    actual fun getStreamType(): StreamType =
        StreamType.values()[nativeStream.getStreamType().ordinal]

    actual fun setDimensions(width: Int, height: Int) {
        nativeStream.setDimensions(width, height)
    }

    actual fun getTimestamp(): Long = nativeStream.timestamp
}
