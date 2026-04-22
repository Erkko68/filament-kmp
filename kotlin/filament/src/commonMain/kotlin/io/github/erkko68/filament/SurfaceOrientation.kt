package io.github.erkko68.filament

/**
 * Helper used to populate TANGENTS buffers.
 */
expect class SurfaceOrientation {
    class Builder() {
        fun vertexCount(vertexCount: Int): Builder
        fun normals(buffer: FloatArray, stride: Int = 0): Builder
        fun tangents(buffer: FloatArray, stride: Int = 0): Builder
        fun uvs(buffer: FloatArray, stride: Int = 0): Builder
        fun positions(buffer: FloatArray, stride: Int = 0): Builder
        fun triangleCount(triangleCount: Int): Builder
        fun triangles16(buffer: ShortArray): Builder
        fun triangles32(buffer: IntArray): Builder
        fun build(): SurfaceOrientation
    }

    fun getVertexCount(): Int
    fun getQuatsAsFloat(buffer: FloatArray, count: Int)
    fun getQuatsAsHalf(buffer: ShortArray, count: Int)
    fun getQuatsAsShort(buffer: ShortArray, count: Int)
    fun destroy()
}
