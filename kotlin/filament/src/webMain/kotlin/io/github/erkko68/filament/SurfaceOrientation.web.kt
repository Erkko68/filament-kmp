package io.github.erkko68.filament

import io.github.erkko68.filament.wasm.*

actual class SurfaceOrientation @InternalFilamentApi constructor(internal val nativeHandle: Int) : AutoCloseable {
    actual class Builder actual constructor() {
        private val nativeBuilder = FilaSurfaceOrientationBuilder_create()
        // The C++ builder keeps the array pointers until build(), so the heap copies live until then.
        private val heap = HeapScope(fila)

        actual fun vertexCount(vertexCount: Int): Builder {
            FilaSurfaceOrientationBuilder_vertexCount(nativeBuilder, vertexCount)
            return this
        }

        actual fun normals(buffer: FloatArray, stride: Int): Builder {
            FilaSurfaceOrientationBuilder_normals(nativeBuilder, heap.floats(buffer), stride)
            return this
        }

        actual fun tangents(buffer: FloatArray, stride: Int): Builder {
            FilaSurfaceOrientationBuilder_tangents(nativeBuilder, heap.floats(buffer), stride)
            return this
        }

        actual fun uvs(buffer: FloatArray, stride: Int): Builder {
            FilaSurfaceOrientationBuilder_uvs(nativeBuilder, heap.floats(buffer), stride)
            return this
        }

        actual fun positions(buffer: FloatArray, stride: Int): Builder {
            FilaSurfaceOrientationBuilder_positions(nativeBuilder, heap.floats(buffer), stride)
            return this
        }

        actual fun triangleCount(triangleCount: Int): Builder {
            FilaSurfaceOrientationBuilder_triangleCount(nativeBuilder, triangleCount)
            return this
        }

        actual fun triangles16(buffer: ShortArray): Builder {
            FilaSurfaceOrientationBuilder_triangles16(nativeBuilder, heap.shorts(buffer))
            return this
        }

        actual fun triangles32(buffer: IntArray): Builder {
            FilaSurfaceOrientationBuilder_triangles32(nativeBuilder, heap.ints(buffer))
            return this
        }

        actual fun build(): SurfaceOrientation {
            val handle = FilaSurfaceOrientationBuilder_build(nativeBuilder)
            FilaSurfaceOrientationBuilder_destroy(nativeBuilder)
            heap.freeAll()
            return SurfaceOrientation(handle)
        }
    }

    actual val vertexCount: Int get() = FilaSurfaceOrientation_getVertexCount(nativeHandle).toInt()

    actual fun getQuatsAsFloat(buffer: FloatArray, count: Int) {
        buffer.usePinned { pinned ->
            FilaSurfaceOrientation_getQuatsAsFloat(nativeHandle, pinned, count)
        }
    }

    actual fun getQuatsAsHalf(buffer: ShortArray, count: Int) {
        buffer.usePinned { pinned ->
            val ptr: Int = pinned
            FilaSurfaceOrientation_getQuatsAsHalf(nativeHandle, ptr, count)
        }
    }

    actual fun getQuatsAsShort(buffer: ShortArray, count: Int) {
        buffer.usePinned { pinned ->
            FilaSurfaceOrientation_getQuatsAsShort(nativeHandle, pinned, count)
        }
    }

    actual override fun close() = destroy()


    actual fun destroy() {
        FilaSurfaceOrientation_destroy(nativeHandle)
    }
}
