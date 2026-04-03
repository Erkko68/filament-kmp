package dev.filament.kmp

actual class SurfaceOrientation {
    actual fun getNativeObject(): Long = TODO("Not yet implemented")

    actual fun getVertexCount(): Int = TODO("Not yet implemented")

    actual fun getQuatsAsFloat(buffer: Any) {
        TODO("Not yet implemented")
    }

    actual fun getQuatsAsHalf(buffer: Any) {
        TODO("Not yet implemented")
    }

    actual fun getQuatsAsShort(buffer: Any) {
        TODO("Not yet implemented")
    }

    actual fun destroy() {
        TODO("Not yet implemented")
    }

    actual class Builder {
        actual fun vertexCount(vertexCount: Int): Builder = TODO("Not yet implemented")

        actual fun normals(buffer: Any): Builder = TODO("Not yet implemented")

        actual fun tangents(buffer: Any): Builder = TODO("Not yet implemented")

        actual fun uvs(buffer: Any): Builder = TODO("Not yet implemented")

        actual fun positions(buffer: Any): Builder = TODO("Not yet implemented")

        actual fun triangleCount(triangleCount: Int): Builder = TODO("Not yet implemented")

        actual fun triangles_uint16(buffer: Any): Builder = TODO("Not yet implemented")

        actual fun triangles_uint32(buffer: Any): Builder = TODO("Not yet implemented")

        actual fun build(): SurfaceOrientation = TODO("Not yet implemented")
    }
}

