package io.github.erkko68.filament

import io.github.erkko68.filament.jni.SurfaceOrientation as JniSurfaceOrientation
import java.nio.FloatBuffer
import java.nio.ShortBuffer
import java.nio.IntBuffer

actual class SurfaceOrientation(val nativeSurfaceOrientation: JniSurfaceOrientation) {
    actual class Builder actual constructor() {
        private val jni = JniSurfaceOrientation.Builder()

        actual fun vertexCount(vertexCount: Int): Builder {
            jni.vertexCount(vertexCount)
            return this
        }

        actual fun normals(buffer: FloatArray, stride: Int): Builder {
            jni.normals(FloatBuffer.wrap(buffer))
            return this
        }

        actual fun tangents(buffer: FloatArray, stride: Int): Builder {
            jni.tangents(FloatBuffer.wrap(buffer))
            return this
        }

        actual fun uvs(buffer: FloatArray, stride: Int): Builder {
            jni.uvs(FloatBuffer.wrap(buffer))
            return this
        }

        actual fun positions(buffer: FloatArray, stride: Int): Builder {
            jni.positions(FloatBuffer.wrap(buffer))
            return this
        }

        actual fun triangleCount(triangleCount: Int): Builder {
            jni.triangleCount(triangleCount)
            return this
        }

        actual fun triangles16(buffer: ShortArray): Builder {
            jni.triangles_uint16(ShortBuffer.wrap(buffer))
            return this
        }

        actual fun triangles32(buffer: IntArray): Builder {
            jni.triangles_uint32(IntBuffer.wrap(buffer))
            return this
        }

        actual fun build(): SurfaceOrientation =
            SurfaceOrientation(jni.build())
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
