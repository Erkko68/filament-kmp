package io.github.erkko68.filament

import io.github.erkko68.filament.jni.MorphTargetBuffer as JniMorphTargetBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

actual class MorphTargetBuffer(val nativeMorphTargetBuffer: JniMorphTargetBuffer) {
    actual class Builder actual constructor() {
        private val jni = JniMorphTargetBuffer.Builder()

        actual fun vertexCount(vertexCount: Int): Builder {
            jni.vertexCount(vertexCount)
            return this
        }

        actual fun count(count: Int): Builder {
            jni.count(count)
            return this
        }

        actual fun withPositions(enabled: Boolean): Builder {
            jni.withPositions(enabled)
            return this
        }

        actual fun withTangents(enabled: Boolean): Builder {
            jni.withTangents(enabled)
            return this
        }

        actual fun enableCustomMorphing(enabled: Boolean): Builder {
            jni.enableCustomMorphing(enabled)
            return this
        }

        actual fun build(engine: Engine): MorphTargetBuffer =
            MorphTargetBuffer(jni.build(engine.nativeEngine))
    }

    actual val vertexCount: Int get() = nativeMorphTargetBuffer.vertexCount
    actual val count: Int get() = nativeMorphTargetBuffer.count
    actual val hasPositions: Boolean get() = nativeMorphTargetBuffer.hasPositions()
    actual val hasTangents: Boolean get() = nativeMorphTargetBuffer.hasTangents()
    actual val isCustomMorphingEnabled: Boolean get() = nativeMorphTargetBuffer.isCustomMorphingEnabled()

    actual fun setPositionsAt(engine: Engine, targetIndex: Int, positions: FloatArray, count: Int) {
        val buffer = ByteBuffer.allocateDirect(positions.size * 4).order(ByteOrder.nativeOrder()).asFloatBuffer()
        buffer.put(positions)
        buffer.rewind()
        nativeMorphTargetBuffer.setPositionsAt(engine.nativeEngine, targetIndex, buffer, 0)
    }

    actual fun setTangentsAt(engine: Engine, targetIndex: Int, tangents: ShortArray, count: Int) {
        val buffer = ByteBuffer.allocateDirect(tangents.size * 2).order(ByteOrder.nativeOrder()).asShortBuffer()
        buffer.put(tangents)
        buffer.rewind()
        nativeMorphTargetBuffer.setTangentsAt(engine.nativeEngine, targetIndex, buffer, 0)
    }
}
