package io.github.erkko68

/**
 * Helper used to populate TANGENTS buffers.
 */
expect class SurfaceOrientation {
    class Builder() {
        fun vertexCount(vertexCount: Int): Builder
        fun normals(buffer: Any, stride: Int = 0): Builder
        fun tangents(buffer: Any, stride: Int = 0): Builder
        fun uvs(buffer: Any, stride: Int = 0): Builder
        fun positions(buffer: Any, stride: Int = 0): Builder
        fun triangleCount(triangleCount: Int): Builder
        fun triangles16(buffer: Any): Builder
        fun triangles32(buffer: Any): Builder
        fun build(): SurfaceOrientation
    }

    fun getVertexCount(): Int
    fun getQuatsAsFloat(buffer: Any, count: Int)
    fun getQuatsAsHalf(buffer: Any, count: Int)
    fun getQuatsAsShort(buffer: Any, count: Int)
    fun destroy()
}
