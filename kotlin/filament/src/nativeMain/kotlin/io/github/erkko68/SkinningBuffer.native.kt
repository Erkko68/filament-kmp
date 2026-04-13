@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68

import kotlinx.cinterop.*
import io.github.erkko68.cinterop.*
import cnames.structs.FilaSkinningBuffer

actual class SkinningBuffer internal constructor(internal var nativeHandle: CPointer<FilaSkinningBuffer>?) {
    actual class Builder actual constructor() {
        private val nativeBuilder = FilaSkinningBufferBuilder_create()

        actual fun boneCount(boneCount: Int): Builder {
            FilaSkinningBufferBuilder_boneCount(nativeBuilder, boneCount.toUInt())
            return this
        }

        actual fun initialize(initialize: Boolean): Builder {
            FilaSkinningBufferBuilder_initialize(nativeBuilder, initialize)
            return this
        }

        actual fun build(engine: Engine): SkinningBuffer {
            val handle = FilaSkinningBufferBuilder_build(nativeBuilder, engine.nativeHandle)
            FilaSkinningBufferBuilder_destroy(nativeBuilder)
            return SkinningBuffer(handle)
        }
    }

    actual fun getBoneCount(): Int = FilaSkinningBuffer_getBoneCount(nativeHandle).toInt()

    actual fun setBonesAsMatrices(engine: Engine, matrices: FloatArray, boneCount: Int, offset: Int) {
        matrices.usePinned { pinned ->
            FilaSkinningBuffer_setBonesMat4f(
                nativeHandle,
                engine.nativeHandle,
                pinned.addressOf(0),
                boneCount.toULong(),
                offset.toULong()
            )
        }
    }
}
