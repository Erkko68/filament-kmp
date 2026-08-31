package io.github.erkko68.filament

import org.khronos.webgl.set
import io.github.erkko68.filament.web.MorphTargetBuffer as JSMorphTargetBuffer

actual class MorphTargetBuffer internal constructor(
    internal val jsMorphTargetBuffer: JSMorphTargetBuffer,
) {
    actual val vertexCount: Int get() = jsMorphTargetBuffer.getVertexCount().toInt()
    actual val count: Int get() = jsMorphTargetBuffer.getCount().toInt()
    actual val hasPositions: Boolean get() = jsMorphTargetBuffer.hasPositions()
    actual val hasTangents: Boolean get() = jsMorphTargetBuffer.hasTangents()
    actual val isCustomMorphingEnabled: Boolean get() = jsMorphTargetBuffer.isCustomMorphingEnabled()

    actual fun setPositionsAt(
        engine: Engine,
        targetIndex: Int,
        positions: FloatArray,
        count: Int
    ) {
        val typed = org.khronos.webgl.Float32Array(positions.size).also { arr ->
            positions.forEachIndexed { i, v -> arr[i] = v }
        }
        jsMorphTargetBuffer.setPositionsAt(engine.jsEngine, targetIndex.toDouble(), typed, count.toDouble(), 0.0)
    }

    actual fun setTangentsAt(
        engine: Engine,
        targetIndex: Int,
        tangents: ShortArray,
        count: Int
    ) {
        val typed = org.khronos.webgl.Int16Array(tangents.size).also { arr ->
            tangents.forEachIndexed { i, v -> arr[i] = v }
        }
        jsMorphTargetBuffer.setTangentsAt(engine.jsEngine, targetIndex.toDouble(), typed, count.toDouble(), 0.0)
    }

    actual class Builder {
        private val jsBuilder = JSMorphTargetBuffer.Builder()

        actual fun vertexCount(vertexCount: Int): Builder {
            jsBuilder.vertexCount(vertexCount.toDouble())
            return this
        }

        actual fun count(count: Int): Builder {
            jsBuilder.count(count.toDouble())
            return this
        }

        actual fun withPositions(enabled: Boolean): Builder {
            jsBuilder.withPositions(enabled)
            return this
        }

        actual fun withTangents(enabled: Boolean): Builder {
            jsBuilder.withTangents(enabled)
            return this
        }

        actual fun enableCustomMorphing(enabled: Boolean): Builder {
            jsBuilder.enableCustomMorphing(enabled)
            return this
        }

        actual fun build(engine: Engine): MorphTargetBuffer =
            MorphTargetBuffer(jsBuilder.build(engine.jsEngine))
    }
}
