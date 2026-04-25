package io.github.erkko68.filament

actual class Stream(internal val jsStream: Any?) {
    actual fun getStreamType(): StreamType {
        return StreamType.NATIVE
    }

    actual fun setDimensions(width: Int, height: Int) {
        // Not exposed in JS bindings
    }

    actual fun getTimestamp(): Long {
        return 0L
    }

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
