package dev.filament.kmp

/**
 * <code>Stream</code> is used to attach a native video stream to a filament {@link Texture}.
 *
 * Stream supports two different configurations:
 *
 * <dl>
 * <dt>ACQUIRED</dt>     <dd>connects to an Android AHardwareBuffer</dd>
 * <dt>NATIVE</dt>       <dd>connects to an Android SurfaceTexture</dd>
 * </dl>
 *
 * @see Texture#setExternalStream
 * @see Engine#destroyStream
 */
expect class Stream {
    /**
     * Represents the immutable stream type.
     */
    enum class StreamType {
        /** Not synchronized but copy-free. Good for video. */
        NATIVE,

        /** Synchronized, copy-free, and take a release callback. Good for AR but requires API 26+. */
        ACQUIRED,
    }

    /**
     * Use <code>Builder</code> to construct an Stream object instance.
     */
    class Builder {
        /**
         * Creates a {@link StreamType#NATIVE NATIVE} stream.
         *
         * @param streamSource an opaque native stream handle
         * @return This Builder, for chaining calls.
         */
        fun stream(streamSource: Any): Builder

        /**
         * @param width initial width of the incoming stream.
         * @return This Builder, for chaining calls.
         */
        fun width(width: Int): Builder

        /**
         * @param height initial height of the incoming stream.
         * @return This Builder, for chaining calls.
         */
        fun height(height: Int): Builder

        /**
         * Creates a new <code>Stream</code> object instance.
         */
        fun build(engine: Engine): Stream
    }

    /**
     * Indicates whether this <code>Stream</code> is NATIVE or ACQUIRED.
     */
    fun getStreamType(): StreamType

    /**
     * Updates an <pre>ACQUIRED</pre> stream with an image that is guaranteed to be used in the next frame.
     */
    fun setAcquiredImage(hwbuffer: Any, handler: Any, callback: () -> Unit)

    /**
     * Updates the size of the incoming stream.
     */
    fun setDimensions(width: Int, height: Int)

    /**
     * Returns the presentation time of the currently displayed frame in nanosecond.
     */
    fun getTimestamp(): Long

    fun getNativeObject(): Long

    internal fun invalidate()
}

