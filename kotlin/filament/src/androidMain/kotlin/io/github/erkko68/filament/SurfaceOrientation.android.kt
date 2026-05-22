package io.github.erkko68.filament

import com.google.android.filament.SurfaceOrientation as AndroidSurfaceOrientation
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import java.nio.ShortBuffer

// The native SurfaceOrientation builder reads through `GetDirectBufferAddress`, which only
// works on direct NIO buffers. `FloatBuffer.wrap(FloatArray)` produces a heap buffer; on
// Android the JNI side silently reads zeros and returned tangent quaternions are bogus —
// every LIT primitive ends up looking flat-unlit. Copy into direct, native-order buffers.

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

actual class SurfaceOrientation internal constructor(val nativeSurfaceOrientation: AndroidSurfaceOrientation) {
    actual class Builder actual constructor() {
        private val nativeBuilder = AndroidSurfaceOrientation.Builder()

        actual fun vertexCount(vertexCount: Int): Builder {
            nativeBuilder.vertexCount(vertexCount)
            return this
        }

        actual fun normals(buffer: FloatArray, stride: Int): Builder {
            nativeBuilder.normals(buffer.toDirectFloatBuffer())
            return this
        }

        actual fun tangents(buffer: FloatArray, stride: Int): Builder {
            nativeBuilder.tangents(buffer.toDirectFloatBuffer())
            return this
        }

        actual fun uvs(buffer: FloatArray, stride: Int): Builder {
            nativeBuilder.uvs(buffer.toDirectFloatBuffer())
            return this
        }

        actual fun positions(buffer: FloatArray, stride: Int): Builder {
            nativeBuilder.positions(buffer.toDirectFloatBuffer())
            return this
        }

        actual fun triangleCount(triangleCount: Int): Builder {
            nativeBuilder.triangleCount(triangleCount)
            return this
        }

        actual fun triangles16(buffer: ShortArray): Builder {
            nativeBuilder.triangles_uint16(buffer.toDirectShortBuffer())
            return this
        }

        actual fun triangles32(buffer: IntArray): Builder {
            nativeBuilder.triangles_uint32(buffer.toDirectIntBuffer())
            return this
        }

        actual fun build(): SurfaceOrientation {
            return SurfaceOrientation(nativeBuilder.build())
        }
    }

    actual val vertexCount: Int get() = nativeSurfaceOrientation.vertexCount

    // Output buffers: allocate direct, hand to the native side, then copy back into the
    // caller's array after the JNI call returns.
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
