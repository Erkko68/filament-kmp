package io.github.erkko68.filament

expect class Stream {
    enum class StreamType {
        NATIVE,
        ACQUIRED
    }

    class Builder() {
        fun width(width: Int): Builder
        fun height(height: Int): Builder
        fun build(engine: Engine): Stream
    }

    val streamType: StreamType
    val timestamp: Long
    fun setDimensions(width: Int, height: Int)
}
