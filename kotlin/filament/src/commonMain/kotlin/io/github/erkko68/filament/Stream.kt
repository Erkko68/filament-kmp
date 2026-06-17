package io.github.erkko68.filament

/**
 * Stream represents an external video or camera stream that can be bound to a texture.
 *
 * Streams allow rendering of video or camera feeds by binding them to textures with
 * [Sampler.SAMPLER_EXTERNAL]. The stream provides timing and dimension information.
 *
 * **Usage:**
 * Create a Stream with the Builder, use it with a Texture set to SAMPLER_EXTERNAL,
 * and destroy with [Engine.destroy]. The stream dimensions must match the texture dimensions.
 *
 * @see Texture
 * @see TextureSampler
 */
expect class Stream {
    /**
     * Type of stream source.
     *
     * - NATIVE: Native OS stream (camera, video device)
     * - ACQUIRED: Stream acquired from external source
     */
    enum class StreamType {
        NATIVE,
        ACQUIRED
    }

    /**
     * Builder for creating Stream instances.
     *
     * Configure the stream dimensions (width and height) before building.
     */
    class Builder() {
        /**
         * Sets the width of the stream in pixels.
         *
         * @param width Stream width (must match associated texture width)
         * @return This Builder, for chaining calls
         */
        fun width(width: Int): Builder

        /**
         * Sets the height of the stream in pixels.
         *
         * @param height Stream height (must match associated texture height)
         * @return This Builder, for chaining calls
         */
        fun height(height: Int): Builder

        /**
         * Creates the Stream object.
         *
         * @param engine Engine to associate this Stream with
         * @return The newly created Stream
         */
        fun build(engine: Engine): Stream
    }

    /**
     * Gets the type of this stream (NATIVE or ACQUIRED).
     * @return The StreamType
     */
    val streamType: StreamType

    /**
     * Gets the timestamp of the most recent frame in nanoseconds.
     * @return Frame timestamp (monotonically increasing)
     */
    val timestamp: Long

    /**
     * Updates the stream dimensions.
     *
     * Must match the texture dimensions of any texture using this stream.
     *
     * @param width New width in pixels
     * @param height New height in pixels
     */
    fun setDimensions(width: Int, height: Int)
}
