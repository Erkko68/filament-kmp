package io.github.erkko68.filament

actual class Stream {
    actual fun getStreamType(): StreamType {
        TODO("Not yet implemented")
    }

    actual fun setDimensions(width: Int, height: Int) {
    }

    actual fun getTimestamp(): Long {
        TODO("Not yet implemented")
    }

    actual enum class StreamType { NATIVE, ACQUIRED }
    actual class Builder {
        actual fun stream(streamSource: Any): Builder {
            TODO("Not yet implemented")
        }

        actual fun width(width: Int): Builder {
            TODO("Not yet implemented")
        }

        actual fun height(height: Int): Builder {
            TODO("Not yet implemented")
        }

        actual fun build(engine: Engine): Stream {
            TODO("Not yet implemented")
        }
    }
}