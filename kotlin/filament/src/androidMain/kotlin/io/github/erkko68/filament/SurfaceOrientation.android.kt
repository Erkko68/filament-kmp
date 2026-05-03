package io.github.erkko68.filament

import com.google.android.filament.SurfaceOrientation as AndroidSurfaceOrientation
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import java.nio.IntBuffer

actual class SurfaceOrientation internal constructor(val nativeSurfaceOrientation: AndroidSurfaceOrientation) {
    actual class Builder actual constructor() {
        private val nativeBuilder = AndroidSurfaceOrientation.Builder()

        actual fun vertexCount(vertexCount: Int): Builder {
            nativeBuilder.vertexCount(vertexCount)
            return this
        }

        actual fun normals(buffer: FloatArray, stride: Int): Builder {
            nativeBuilder.normals(FloatBuffer.wrap(buffer))
            return this
        }

        actual fun tangents(buffer: FloatArray, stride: Int): Builder {
            nativeBuilder.tangents(FloatBuffer.wrap(buffer))
            return this
        }

        actual fun uvs(buffer: FloatArray, stride: Int): Builder {
            nativeBuilder.uvs(FloatBuffer.wrap(buffer))
            return this
        }

        actual fun positions(buffer: FloatArray, stride: Int): Builder {
            nativeBuilder.positions(FloatBuffer.wrap(buffer))
            return this
        }

        actual fun triangleCount(triangleCount: Int): Builder {
            nativeBuilder.triangleCount(triangleCount)
            return this
        }

        actual fun triangles16(buffer: ShortArray): Builder {
            nativeBuilder.triangles_uint16(ShortBuffer.wrap(buffer))
            return this
        }

        actual fun triangles32(buffer: IntArray): Builder {
            nativeBuilder.triangles_uint32(IntBuffer.wrap(buffer))
            return this
        }

        actual fun build(): SurfaceOrientation {
            return SurfaceOrientation(nativeBuilder.build())
        }
    }

    actual val vertexCount: Int get() = nativeSurfaceOrientation.vertexCount

    actual fun getQuatsAsFloat(buffer: FloatArray, count: Int) {
        nativeSurfaceOrientation.getQuatsAsFloat(FloatBuffer.wrap(buffer))
    }

    actual fun getQuatsAsHalf(buffer: ShortArray, count: Int) {
        nativeSurfaceOrientation.getQuatsAsHalf(ShortBuffer.wrap(buffer))
    }

    actual fun getQuatsAsShort(buffer: ShortArray, count: Int) {
        nativeSurfaceOrientation.getQuatsAsShort(ShortBuffer.wrap(buffer))
    }

    actual fun destroy() {
        nativeSurfaceOrientation.destroy()
    }
}
