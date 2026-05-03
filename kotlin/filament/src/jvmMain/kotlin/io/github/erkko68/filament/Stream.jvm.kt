package io.github.erkko68.filament

import io.github.erkko68.filament.jni.Stream as JniStream

actual class Stream(val nativeStream: JniStream) {
    actual class Builder actual constructor() {
        private val jni = JniStream.Builder()

        actual fun width(width: Int): Builder {
            jni.width(width)
            return this
        }

        actual fun height(height: Int): Builder {
            jni.height(height)
            return this
        }

        actual fun build(engine: Engine): Stream =
            Stream(jni.build(engine.nativeEngine))
    }

    actual enum class StreamType {
        NATIVE, ACQUIRED
    }

    actual val streamType: StreamType get() = StreamType.values()[nativeStream.streamType.ordinal]

    actual fun setDimensions(width: Int, height: Int) {
        nativeStream.setDimensions(width, height)
    }

    actual val timestamp: Long get() = nativeStream.timestamp
}
