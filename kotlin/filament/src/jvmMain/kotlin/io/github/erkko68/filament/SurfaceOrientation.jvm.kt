package io.github.erkko68.filament

import io.github.erkko68.filament.jni.SurfaceOrientation as JniSurfaceOrientation
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer

// The native SurfaceOrientation builder reads through `GetDirectBufferAddress`, which only
// works on direct NIO buffers — `FloatBuffer.wrap(FloatArray)` produces a heap buffer that the
// native side either rejects or reads with unaligned access (SIGBUS BUS_ADRALN on aarch64).
// All input buffers are copied into direct, native-order NIO buffers here.

private fun FloatArray.toDirectFloatBuffer(): FloatBuffer =
    ByteBuffer.allocateDirect(size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer().also {
        it.put(this); it.flip()
    }

private fun ShortArray.toDirectShortBuffer(): ShortBuffer =
    ByteBuffer.allocateDirect(size * 2).order(ByteOrder.nativeOrder()).asShortBuffer().also {
        it.put(this); it.flip()
    }

private fun IntArray.toDirectIntBuffer(): IntBuffer =
    ByteBuffer.allocateDirect(size * 4).order(ByteOrder.nativeOrder()).asIntBuffer().also {
        it.put(this); it.flip()
    }

actual class SurfaceOrientation(val nativeSurfaceOrientation: JniSurfaceOrientation) {
    actual class Builder actual constructor() {
        private val jni = JniSurfaceOrientation.Builder()

        actual fun vertexCount(vertexCount: Int): Builder {
            jni.vertexCount(vertexCount)
            return this
        }

        actual fun normals(buffer: FloatArray, stride: Int): Builder {
            jni.normals(buffer.toDirectFloatBuffer())
            return this
        }

        actual fun tangents(buffer: FloatArray, stride: Int): Builder {
            jni.tangents(buffer.toDirectFloatBuffer())
            return this
        }

        actual fun uvs(buffer: FloatArray, stride: Int): Builder {
            jni.uvs(buffer.toDirectFloatBuffer())
            return this
        }

        actual fun positions(buffer: FloatArray, stride: Int): Builder {
            jni.positions(buffer.toDirectFloatBuffer())
            return this
        }

        actual fun triangleCount(triangleCount: Int): Builder {
            jni.triangleCount(triangleCount)
            return this
        }

        actual fun triangles16(buffer: ShortArray): Builder {
            jni.triangles_uint16(buffer.toDirectShortBuffer())
            return this
        }

        actual fun triangles32(buffer: IntArray): Builder {
            jni.triangles_uint32(buffer.toDirectIntBuffer())
            return this
        }

        actual fun build(): SurfaceOrientation =
            SurfaceOrientation(jni.build())
    }

    actual val vertexCount: Int get() = nativeSurfaceOrientation.vertexCount

    // Output buffers are written by the native side: allocate direct, copy back into the
    // caller's array after the native call returns.
    actual fun getQuatsAsFloat(buffer: FloatArray, count: Int) {
        val direct = ByteBuffer.allocateDirect(buffer.size * 4)
            .order(ByteOrder.nativeOrder()).asFloatBuffer()
        nativeSurfaceOrientation.getQuatsAsFloat(direct)
        direct.position(0); direct.get(buffer)
    }

    actual fun getQuatsAsHalf(buffer: ShortArray, count: Int) {
        val direct = ByteBuffer.allocateDirect(buffer.size * 2)
            .order(ByteOrder.nativeOrder()).asShortBuffer()
        nativeSurfaceOrientation.getQuatsAsHalf(direct)
        direct.position(0); direct.get(buffer)
    }

    actual fun getQuatsAsShort(buffer: ShortArray, count: Int) {
        val direct = ByteBuffer.allocateDirect(buffer.size * 2)
            .order(ByteOrder.nativeOrder()).asShortBuffer()
        nativeSurfaceOrientation.getQuatsAsShort(direct)
        direct.position(0); direct.get(buffer)
    }

    actual fun destroy() {
        nativeSurfaceOrientation.destroy()
    }
}
