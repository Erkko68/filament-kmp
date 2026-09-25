package io.github.erkko68.filament

import io.github.erkko68.filament.wasm.*

actual class MorphTargetBuffer @InternalFilamentApi constructor(internal var nativeHandle: Int) {
    actual class Builder actual constructor() {
        private val nativeBuilder = FilaMorphTargetBufferBuilder_create()

        actual fun vertexCount(vertexCount: Int): Builder {
            FilaMorphTargetBufferBuilder_vertexCount(nativeBuilder, vertexCount)
            return this
        }

        actual fun count(count: Int): Builder {
            FilaMorphTargetBufferBuilder_count(nativeBuilder, count)
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

    actual val vertexCount: Int get() = FilaMorphTargetBuffer_getVertexCount(nativeHandle).toInt()
    actual val count: Int get() = FilaMorphTargetBuffer_getCount(nativeHandle).toInt()
    actual val hasPositions: Boolean get() = FilaMorphTargetBuffer_hasPositions(nativeHandle)
    actual val hasTangents: Boolean get() = FilaMorphTargetBuffer_hasTangents(nativeHandle)
    actual val isCustomMorphingEnabled: Boolean get() = FilaMorphTargetBuffer_isCustomMorphingEnabled(nativeHandle)

    actual fun setPositionsAt(engine: Engine, targetIndex: Int, positions: FloatArray, count: Int) {
        positions.usePinned { pinned ->
            FilaMorphTargetBuffer_setPositionsAt(
                nativeHandle,
                engine.nativeHandle,
                targetIndex,
                pinned,
                count
            )
        }
    }

    actual fun setTangentsAt(engine: Engine, targetIndex: Int, tangents: ShortArray, count: Int) {
        tangents.usePinned { pinned ->
            FilaMorphTargetBuffer_setTangentsAt(
                nativeHandle,
                engine.nativeHandle,
                targetIndex,
                pinned,
                count
            )
        }
    }
}
