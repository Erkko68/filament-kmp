package dev.filament.kmp

import com.google.android.filament.Stream as AndroidStream

actual class Stream internal constructor(
    internal var androidStream: AndroidStream?,
) {
    actual fun getStreamType(): StreamType {
        val stream = requireNotNull(androidStream) { "Calling method on destroyed Stream" }
        return stream.streamType.toKmp()
    }

    actual fun setAcquiredImage(hwbuffer: Any, handler: Any, callback: () -> Unit) {
        val stream = requireNotNull(androidStream) { "Calling method on destroyed Stream" }
        stream.setAcquiredImage(hwbuffer, handler, Runnable { callback() })
    }

    actual fun setDimensions(width: Int, height: Int) {
        val stream = requireNotNull(androidStream) { "Calling method on destroyed Stream" }
        stream.setDimensions(width, height)
    }

    actual fun getTimestamp(): Long {
        val stream = requireNotNull(androidStream) { "Calling method on destroyed Stream" }
        return stream.timestamp
    }

    actual fun getNativeObject(): Long {
        val stream = requireNotNull(androidStream) { "Calling method on destroyed Stream" }
        return stream.nativeObject
    }

    actual internal fun invalidate() {
        androidStream = null
    }

    actual class Builder {
        private val androidBuilder = AndroidStream.Builder()

        actual fun stream(streamSource: Any): Builder {
            androidBuilder.stream(streamSource)
            return this
        }

        actual fun width(width: Int): Builder {
            androidBuilder.width(width)
            return this
        }

        actual fun height(height: Int): Builder {
            androidBuilder.height(height)
            return this
        }

        actual fun build(engine: Engine): Stream {
            val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
            return Stream(androidBuilder.build(androidEngine))
        }
    }

    actual enum class StreamType {
        NATIVE,
        ACQUIRED,
    }
}

private fun AndroidStream.StreamType.toKmp(): Stream.StreamType = Stream.StreamType.valueOf(name)

