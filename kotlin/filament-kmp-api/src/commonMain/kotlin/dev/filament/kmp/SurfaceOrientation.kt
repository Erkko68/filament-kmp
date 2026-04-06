package dev.filament.kmp

/**
 * Helper used to populate <code>TANGENTS</code> buffers.
 */
expect class SurfaceOrientation {
    /**
     * Constructs an immutable surface orientation helper.
     */
    class Builder {
        fun vertexCount(vertexCount: Int): Builder

        fun normals(buffer: Any): Builder

        fun tangents(buffer: Any): Builder

        fun uvs(buffer: Any): Builder

        fun positions(buffer: Any): Builder

        fun triangleCount(triangleCount: Int): Builder

        fun triangles_uint16(buffer: Any): Builder

        fun triangles_uint32(buffer: Any): Builder

        /**
         * Consumes the input data, produces quaternions, and destroys the native builder.
         */
        fun build(): SurfaceOrientation
    }

    val nativeObject: Long

    val vertexCount: Int

    fun getQuatsAsFloat(buffer: Any)

    fun getQuatsAsHalf(buffer: Any)

    fun getQuatsAsShort(buffer: Any)

    fun destroy()
}

