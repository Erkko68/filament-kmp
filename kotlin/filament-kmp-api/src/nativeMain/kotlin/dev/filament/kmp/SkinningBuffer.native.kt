@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package dev.filament.kmp

import dev.filament.kmp.cinterop.*
import kotlinx.cinterop.*

actual class SkinningBuffer(private var nativeInternal: Long) {
    actual class Builder {
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
            val result = FilaSkinningBufferBuilder_build(nativeBuilder, engine.nativeObject.toCPointer())
                ?: throw IllegalStateException("Failed to build SkinningBuffer")
            return SkinningBuffer(result.toLong())
        }
    }

    actual fun setBonesAsMatrices(engine: Engine, matrices: Any, boneCount: Int, offset: Int) {
        if (matrices !is FloatArray) throw IllegalArgumentException("matrices must be FloatArray")
        matrices.usePinned { pinned ->
            FilaSkinningBuffer_setBonesMat4f(
                nativeObject.toCPointer(),
                engine.nativeObject.toCPointer(),
                pinned.addressOf(0),
                boneCount.toULong(),
                offset.toULong()
            )
        }
    }

    actual fun setBonesAsQuaternions(engine: Engine, quaternions: Any, boneCount: Int, offset: Int) {
        if (quaternions !is FloatArray) throw IllegalArgumentException("quaternions must be FloatArray")
        quaternions.usePinned { pinned ->
            FilaSkinningBuffer_setBonesQuaternions(
                nativeObject.toCPointer(),
                engine.nativeObject.toCPointer(),
                pinned.addressOf(0),
                boneCount.toULong(),
                offset.toULong()
            )
        }
    }

    actual fun getBoneCount(): Int {
        return FilaSkinningBuffer_getBoneCount(nativeObject.toCPointer()).toInt()
    }

    actual val nativeObject: Long
        get() = nativeInternal

    actual internal fun invalidate() {
        nativeInternal = 0
    }
}
