@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68.filament

import kotlinx.cinterop.*
import io.github.erkko68.filament.cinterop.*
import cnames.structs.FilaSurfaceOrientation
import cnames.structs.FilaSurfaceOrientationBuilder

actual class SurfaceOrientation internal constructor(internal val nativeHandle: CPointer<FilaSurfaceOrientation>) {
    actual class Builder actual constructor() {
        private val nativeBuilder = FilaSurfaceOrientationBuilder_create()!!

        actual fun vertexCount(vertexCount: Int): Builder {
            FilaSurfaceOrientationBuilder_vertexCount(nativeBuilder, vertexCount.toUInt())
            return this
        }

        actual fun normals(buffer: Any, stride: Int): Builder {
            (buffer as FloatArray).usePinned { pinned ->
                FilaSurfaceOrientationBuilder_normals(nativeBuilder, pinned.addressOf(0), stride.toUInt())
            }
            return this
        }

        actual fun tangents(buffer: Any, stride: Int): Builder {
            (buffer as FloatArray).usePinned { pinned ->
                FilaSurfaceOrientationBuilder_tangents(nativeBuilder, pinned.addressOf(0), stride.toUInt())
            }
            return this
        }

        actual fun uvs(buffer: Any, stride: Int): Builder {
            (buffer as FloatArray).usePinned { pinned ->
                FilaSurfaceOrientationBuilder_uvs(nativeBuilder, pinned.addressOf(0), stride.toUInt())
            }
            return this
        }

        actual fun positions(buffer: Any, stride: Int): Builder {
            (buffer as FloatArray).usePinned { pinned ->
                FilaSurfaceOrientationBuilder_positions(nativeBuilder, pinned.addressOf(0), stride.toUInt())
            }
            return this
        }

        actual fun triangleCount(triangleCount: Int): Builder {
            FilaSurfaceOrientationBuilder_triangleCount(nativeBuilder, triangleCount.toUInt())
            return this
        }

        actual fun triangles16(buffer: Any): Builder {
            (buffer as ShortArray).usePinned { pinned ->
                val ptr: CPointer<UShortVar> = pinned.addressOf(0).reinterpret()
                FilaSurfaceOrientationBuilder_triangles16(nativeBuilder, ptr)
            }
            return this
        }

        actual fun triangles32(buffer: Any): Builder {
            (buffer as IntArray).usePinned { pinned ->
                val ptr: CPointer<UIntVar> = pinned.addressOf(0).reinterpret()
                FilaSurfaceOrientationBuilder_triangles32(nativeBuilder, ptr)
            }
            return this
        }

        actual fun build(): SurfaceOrientation {
            val handle = FilaSurfaceOrientationBuilder_build(nativeBuilder)!!
            FilaSurfaceOrientationBuilder_destroy(nativeBuilder)
            return SurfaceOrientation(handle)
        }
    }

    actual fun getVertexCount(): Int = FilaSurfaceOrientation_getVertexCount(nativeHandle).toInt()

    actual fun getQuatsAsFloat(buffer: Any, count: Int) {
        (buffer as FloatArray).usePinned { pinned ->
            FilaSurfaceOrientation_getQuatsAsFloat(nativeHandle, pinned.addressOf(0), count.toUInt())
        }
    }

    actual fun getQuatsAsHalf(buffer: Any, count: Int) {
        (buffer as ShortArray).usePinned { pinned ->
            val ptr: CPointer<UShortVar> = pinned.addressOf(0).reinterpret()
            FilaSurfaceOrientation_getQuatsAsHalf(nativeHandle, ptr, count.toUInt())
        }
    }

    actual fun getQuatsAsShort(buffer: Any, count: Int) {
        (buffer as ShortArray).usePinned { pinned ->
            FilaSurfaceOrientation_getQuatsAsShort(nativeHandle, pinned.addressOf(0), count.toUInt())
        }
    }

    actual fun destroy() {
        FilaSurfaceOrientation_destroy(nativeHandle)
    }
}
