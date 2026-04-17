package io.github.erkko68.filament

actual class SurfaceOrientation {
    actual fun getVertexCount(): Int {
        TODO("Not yet implemented")
    }

    actual fun getQuatsAsFloat(buffer: Any, count: Int) {
    }

    actual fun getQuatsAsHalf(buffer: Any, count: Int) {
    }

    actual fun getQuatsAsShort(buffer: Any, count: Int) {
    }

    actual fun destroy() {
    }

    actual class Builder {
        actual fun vertexCount(vertexCount: Int): Builder {
            TODO("Not yet implemented")
        }

        actual fun normals(
            buffer: Any,
            stride: Int
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun tangents(
            buffer: Any,
            stride: Int
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun uvs(buffer: Any, stride: Int): Builder {
            TODO("Not yet implemented")
        }

        actual fun positions(
            buffer: Any,
            stride: Int
        ): Builder {
            TODO("Not yet implemented")
        }

        actual fun triangleCount(triangleCount: Int): Builder {
            TODO("Not yet implemented")
        }

        actual fun triangles16(buffer: Any): Builder {
            TODO("Not yet implemented")
        }

        actual fun triangles32(buffer: Any): Builder {
            TODO("Not yet implemented")
        }

        actual fun build(): SurfaceOrientation {
            TODO("Not yet implemented")
        }
    }
}