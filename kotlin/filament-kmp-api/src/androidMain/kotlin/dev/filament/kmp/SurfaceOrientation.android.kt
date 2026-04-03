package dev.filament.kmp

import com.google.android.filament.SurfaceOrientation as AndroidSurfaceOrientation
import java.nio.Buffer

actual class SurfaceOrientation internal constructor(
    internal var androidSurfaceOrientation: AndroidSurfaceOrientation?,
) {
    actual fun getNativeObject(): Long {
        val surfaceOrientation = requireNotNull(androidSurfaceOrientation) { "Calling method on destroyed SurfaceOrientation" }
        return surfaceOrientation.nativeObject
    }

    actual fun getVertexCount(): Int {
        val surfaceOrientation = requireNotNull(androidSurfaceOrientation) { "Calling method on destroyed SurfaceOrientation" }
        return surfaceOrientation.vertexCount
    }

    actual fun getQuatsAsFloat(buffer: Any) {
        val surfaceOrientation = requireNotNull(androidSurfaceOrientation) { "Calling method on destroyed SurfaceOrientation" }
        surfaceOrientation.getQuatsAsFloat(buffer as Buffer)
    }

    actual fun getQuatsAsHalf(buffer: Any) {
        val surfaceOrientation = requireNotNull(androidSurfaceOrientation) { "Calling method on destroyed SurfaceOrientation" }
        surfaceOrientation.getQuatsAsHalf(buffer as Buffer)
    }

    actual fun getQuatsAsShort(buffer: Any) {
        val surfaceOrientation = requireNotNull(androidSurfaceOrientation) { "Calling method on destroyed SurfaceOrientation" }
        surfaceOrientation.getQuatsAsShort(buffer as Buffer)
    }

    actual fun destroy() {
        val surfaceOrientation = requireNotNull(androidSurfaceOrientation) { "Calling method on destroyed SurfaceOrientation" }
        surfaceOrientation.destroy()
        androidSurfaceOrientation = null
    }

    actual class Builder {
        private val androidBuilder = AndroidSurfaceOrientation.Builder()

        actual fun vertexCount(vertexCount: Int): Builder {
            androidBuilder.vertexCount(vertexCount)
            return this
        }

        actual fun normals(buffer: Any): Builder {
            androidBuilder.normals(buffer as Buffer)
            return this
        }

        actual fun tangents(buffer: Any): Builder {
            androidBuilder.tangents(buffer as Buffer)
            return this
        }

        actual fun uvs(buffer: Any): Builder {
            androidBuilder.uvs(buffer as Buffer)
            return this
        }

        actual fun positions(buffer: Any): Builder {
            androidBuilder.positions(buffer as Buffer)
            return this
        }

        actual fun triangleCount(triangleCount: Int): Builder {
            androidBuilder.triangleCount(triangleCount)
            return this
        }

        actual fun triangles_uint16(buffer: Any): Builder {
            androidBuilder.triangles_uint16(buffer as Buffer)
            return this
        }

        actual fun triangles_uint32(buffer: Any): Builder {
            androidBuilder.triangles_uint32(buffer as Buffer)
            return this
        }

        actual fun build(): SurfaceOrientation = SurfaceOrientation(androidBuilder.build())
    }
}

