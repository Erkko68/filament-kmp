package dev.filament.kmp

import com.google.android.filament.SurfaceOrientation as AndroidSurfaceOrientation
import java.nio.Buffer

actual class SurfaceOrientation internal constructor(val nativeSurfaceOrientation: AndroidSurfaceOrientation) {
    actual class Builder actual constructor() {
        private val nativeBuilder = AndroidSurfaceOrientation.Builder()

        actual fun vertexCount(vertexCount: Int): Builder {
            nativeBuilder.vertexCount(vertexCount)
            return this
        }

        actual fun normals(buffer: Any, stride: Int): Builder {
            nativeBuilder.normals(buffer as Buffer)
            return this
        }

        actual fun tangents(buffer: Any, stride: Int): Builder {
            nativeBuilder.tangents(buffer as Buffer)
            return this
        }

        actual fun uvs(buffer: Any, stride: Int): Builder {
            nativeBuilder.uvs(buffer as Buffer)
            return this
        }

        actual fun positions(buffer: Any, stride: Int): Builder {
            nativeBuilder.positions(buffer as Buffer)
            return this
        }

        actual fun triangleCount(triangleCount: Int): Builder {
            nativeBuilder.triangleCount(triangleCount)
            return this
        }

        actual fun triangles16(buffer: Any): Builder {
            nativeBuilder.triangles_uint16(buffer as Buffer)
            return this
        }

        actual fun triangles32(buffer: Any): Builder {
            nativeBuilder.triangles_uint32(buffer as Buffer)
            return this
        }

        actual fun build(): SurfaceOrientation {
            return SurfaceOrientation(nativeBuilder.build())
        }
    }

    actual fun getVertexCount(): Int = nativeSurfaceOrientation.vertexCount
    
    actual fun getQuatsAsFloat(buffer: Any, count: Int) {
        nativeSurfaceOrientation.getQuatsAsFloat(buffer as Buffer)
    }

    actual fun getQuatsAsHalf(buffer: Any, count: Int) {
        nativeSurfaceOrientation.getQuatsAsHalf(buffer as Buffer)
    }

    actual fun getQuatsAsShort(buffer: Any, count: Int) {
        nativeSurfaceOrientation.getQuatsAsShort(buffer as Buffer)
    }

    actual fun destroy() {
        nativeSurfaceOrientation.destroy()
    }
}
