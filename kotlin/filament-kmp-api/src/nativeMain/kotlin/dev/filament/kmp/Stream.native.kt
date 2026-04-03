package dev.filament.kmp

actual class Stream {
    actual fun getStreamType(): StreamType = TODO("Not yet implemented")

    actual fun setAcquiredImage(hwbuffer: Any, handler: Any, callback: () -> Unit) {
        TODO("Not yet implemented")
    }

    actual fun setDimensions(width: Int, height: Int) {
        TODO("Not yet implemented")
    }

    actual fun getTimestamp(): Long = TODO("Not yet implemented")

    actual fun getNativeObject(): Long = TODO("Not yet implemented")

    actual internal fun invalidate() {
    }

    actual class Builder {
        actual fun stream(streamSource: Any): Builder = TODO("Not yet implemented")

        actual fun width(width: Int): Builder = TODO("Not yet implemented")

        actual fun height(height: Int): Builder = TODO("Not yet implemented")

        actual fun build(engine: Engine): Stream = TODO("Not yet implemented")
    }

    actual enum class StreamType {
        NATIVE,
        ACQUIRED,
    }
}

