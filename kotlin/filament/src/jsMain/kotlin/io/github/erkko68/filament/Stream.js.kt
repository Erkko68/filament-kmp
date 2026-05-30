package io.github.erkko68.filament

actual class Stream(internal val jsStream: Any?) {
    actual val streamType: StreamType get() = StreamType.NATIVE

    actual fun setDimensions(width: Int, height: Int) {
        // TODO(js): Stream not bound in jsbindings.cpp — no-op on web
    }

    actual val timestamp: Long get() = 0L

    actual enum class StreamType { NATIVE, ACQUIRED }
    actual class Builder {
        actual fun width(width: Int): Builder {
            return this
        }

        actual fun height(height: Int): Builder {
            return this
        }

        actual fun build(engine: Engine): Stream {
            return Stream(null)
        }
    }
}
