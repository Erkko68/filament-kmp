package io.github.erkko68.filament

import io.github.erkko68.filament.jni.SurfaceOrientation as JniSurfaceOrientation
import java.nio.Buffer

actual class SurfaceOrientation(val nativeSurfaceOrientation: JniSurfaceOrientation) {
    actual class Builder actual constructor() {
        private val jni = JniSurfaceOrientation.Builder()

        actual fun vertexCount(vertexCount: Int): Builder {
            jni.vertexCount(vertexCount)
            return this
        }

        actual fun normals(buffer: Any, stride: Int): Builder {
            jni.normals(buffer as Buffer)
            return this
        }

        actual fun tangents(buffer: Any, stride: Int): Builder {
            jni.tangents(buffer as Buffer)
            return this
        }

        actual fun uvs(buffer: Any, stride: Int): Builder {
            jni.uvs(buffer as Buffer)
            return this
        }

        actual fun positions(buffer: Any, stride: Int): Builder {
            jni.positions(buffer as Buffer)
            return this
        }

        actual fun triangleCount(triangleCount: Int): Builder {
            jni.triangleCount(triangleCount)
            return this
        }

        actual fun triangles16(buffer: Any): Builder {
            jni.triangles_uint16(buffer as Buffer)
            return this
        }

        actual fun triangles32(buffer: Any): Builder {
            jni.triangles_uint32(buffer as Buffer)
            return this
        }

        actual fun build(): SurfaceOrientation =
            SurfaceOrientation(jni.build())
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
