@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package io.github.erkko68

import kotlinx.cinterop.*
import io.github.erkko68.cinterop.*
import cnames.structs.FilaMorphTargetBuffer

actual class MorphTargetBuffer internal constructor(internal var nativeHandle: CPointer<FilaMorphTargetBuffer>?) {
    actual class Builder actual constructor() {
        private val nativeBuilder = FilaMorphTargetBufferBuilder_create()

        actual fun vertexCount(vertexCount: Int): Builder {
            FilaMorphTargetBufferBuilder_vertexCount(nativeBuilder, vertexCount.toULong())
            return this
        }

        actual fun count(count: Int): Builder {
            FilaMorphTargetBufferBuilder_count(nativeBuilder, count.toULong())
            return this
        }

        actual fun withPositions(enabled: Boolean): Builder {
            FilaMorphTargetBufferBuilder_withPositions(nativeBuilder, enabled)
            return this
        }

        actual fun withTangents(enabled: Boolean): Builder {
            FilaMorphTargetBufferBuilder_withTangents(nativeBuilder, enabled)
            return this
        }

        actual fun enableCustomMorphing(enabled: Boolean): Builder {
            FilaMorphTargetBufferBuilder_enableCustomMorphing(nativeBuilder, enabled)
            return this
        }

        actual fun build(engine: Engine): MorphTargetBuffer {
            val handle = FilaMorphTargetBufferBuilder_build(nativeBuilder, engine.nativeHandle)
            FilaMorphTargetBufferBuilder_destroy(nativeBuilder)
            return MorphTargetBuffer(handle)
        }
    }

    actual fun getVertexCount(): Int = FilaMorphTargetBuffer_getVertexCount(nativeHandle).toInt()
    actual fun getCount(): Int = FilaMorphTargetBuffer_getCount(nativeHandle).toInt()
    actual fun hasPositions(): Boolean = FilaMorphTargetBuffer_hasPositions(nativeHandle)
    actual fun hasTangents(): Boolean = FilaMorphTargetBuffer_hasTangents(nativeHandle)
    actual fun isCustomMorphingEnabled(): Boolean = FilaMorphTargetBuffer_isCustomMorphingEnabled(nativeHandle)

    actual fun setPositionsAt(engine: Engine, targetIndex: Int, positions: FloatArray, count: Int) {
        positions.usePinned { pinned ->
            FilaMorphTargetBuffer_setPositionsAt(
                nativeHandle,
                engine.nativeHandle,
                targetIndex.toULong(),
                pinned.addressOf(0),
                count.toULong()
            )
        }
    }

    actual fun setTangentsAt(engine: Engine, targetIndex: Int, tangents: ShortArray, count: Int) {
        tangents.usePinned { pinned ->
            FilaMorphTargetBuffer_setTangentsAt(
                nativeHandle,
                engine.nativeHandle,
                targetIndex.toULong(),
                pinned.addressOf(0),
                count.toULong()
            )
        }
    }
}
