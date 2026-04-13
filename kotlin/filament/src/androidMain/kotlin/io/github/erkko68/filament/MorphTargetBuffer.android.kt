package io.github.erkko68.filament

import com.google.android.filament.MorphTargetBuffer as AndroidMorphTargetBuffer

actual class MorphTargetBuffer internal constructor(val nativeMorphTargetBuffer: AndroidMorphTargetBuffer) {
    actual class Builder actual constructor() {
        private val nativeBuilder = AndroidMorphTargetBuffer.Builder()

        actual fun vertexCount(vertexCount: Int): Builder {
            nativeBuilder.vertexCount(vertexCount)
            return this
        }

        actual fun count(count: Int): Builder {
            nativeBuilder.count(count)
            return this
        }

        actual fun withPositions(enabled: Boolean): Builder {
            nativeBuilder.withPositions(enabled)
            return this
        }

        actual fun withTangents(enabled: Boolean): Builder {
            nativeBuilder.withTangents(enabled)
            return this
        }

        actual fun enableCustomMorphing(enabled: Boolean): Builder {
            nativeBuilder.enableCustomMorphing(enabled)
            return this
        }

        actual fun build(engine: Engine): MorphTargetBuffer {
            return MorphTargetBuffer(nativeBuilder.build(engine.nativeEngine))
        }
    }

    actual fun getVertexCount(): Int = nativeMorphTargetBuffer.vertexCount
    actual fun getCount(): Int = nativeMorphTargetBuffer.count
    actual fun hasPositions(): Boolean = nativeMorphTargetBuffer.hasPositions()
    actual fun hasTangents(): Boolean = nativeMorphTargetBuffer.hasTangents()
    actual fun isCustomMorphingEnabled(): Boolean = nativeMorphTargetBuffer.isCustomMorphingEnabled

    actual fun setPositionsAt(engine: Engine, targetIndex: Int, positions: FloatArray, count: Int) {
        nativeMorphTargetBuffer.setPositionsAt(engine.nativeEngine, targetIndex, positions, count)
    }

    actual fun setTangentsAt(engine: Engine, targetIndex: Int, tangents: ShortArray, count: Int) {
        nativeMorphTargetBuffer.setTangentsAt(engine.nativeEngine, targetIndex, tangents, count)
    }
}
