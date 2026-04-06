@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package dev.filament.kmp

import dev.filament.kmp.cinterop.*
import kotlinx.cinterop.*

actual class MorphTargetBuffer(private var nativeInternal: Long) {
    actual class Builder {
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
            val result = FilaMorphTargetBufferBuilder_build(nativeBuilder, engine.nativeObject.toCPointer())
                ?: throw IllegalStateException("Failed to build MorphTargetBuffer")
            return MorphTargetBuffer(result.toLong())
        }
    }

    actual fun setPositionsAt(engine: Engine, targetIndex: Int, positions: FloatArray, count: Int) {
        positions.usePinned { pinned ->
            FilaMorphTargetBuffer_setPositionsAt(
                nativeObject.toCPointer(),
                engine.nativeObject.toCPointer(),
                targetIndex.toULong(),
                pinned.addressOf(0),
                count.toULong()
            )
        }
    }

    actual fun setTangentsAt(engine: Engine, targetIndex: Int, tangents: ShortArray, count: Int) {
        tangents.usePinned { pinned ->
            FilaMorphTargetBuffer_setTangentsAt(
                nativeObject.toCPointer(),
                engine.nativeObject.toCPointer(),
                targetIndex.toULong(),
                pinned.addressOf(0),
                count.toULong()
            )
        }
    }

    actual val vertexCount: Int
        get() = FilaMorphTargetBuffer_getVertexCount(nativeObject.toCPointer()).toInt()

    actual val count: Int
        get() = FilaMorphTargetBuffer_getCount(nativeObject.toCPointer()).toInt()

    actual fun hasPositions(): Boolean {
        return FilaMorphTargetBuffer_hasPositions(nativeObject.toCPointer())
    }

    actual fun hasTangents(): Boolean {
        return FilaMorphTargetBuffer_hasTangents(nativeObject.toCPointer())
    }

    actual fun isCustomMorphingEnabled(): Boolean {
        return FilaMorphTargetBuffer_isCustomMorphingEnabled(nativeObject.toCPointer())
    }

    actual val nativeObject: Long
        get() = nativeInternal

    actual internal fun invalidate() {
        nativeInternal = 0
    }
}
