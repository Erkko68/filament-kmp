package io.github.erkko68.filament

expect class Stream {
    enum class StreamType {
        NATIVE,
        ACQUIRED
    }

    class Builder() {
        fun stream(streamSource: Any): Builder
        fun width(width: Int): Builder
        fun height(height: Int): Builder
        fun build(engine: Engine): Stream
    }

    fun getStreamType(): StreamType
    fun setDimensions(width: Int, height: Int)
    fun getTimestamp(): Long
}
