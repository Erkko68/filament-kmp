package dev.filament.kmp

import com.google.android.filament.MorphTargetBuffer as AndroidMorphTargetBuffer

actual class MorphTargetBuffer internal constructor(
    internal var androidMorphTargetBuffer: AndroidMorphTargetBuffer?,
) {
    actual fun setPositionsAt(engine: Engine, targetIndex: Int, positions: FloatArray, count: Int) {
        val morphTargetBuffer = requireNotNull(androidMorphTargetBuffer) { "Calling method on destroyed MorphTargetBuffer" }
        val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
        morphTargetBuffer.setPositionsAt(androidEngine, targetIndex, positions, count)
    }

    actual fun setTangentsAt(engine: Engine, targetIndex: Int, tangents: ShortArray, count: Int) {
        val morphTargetBuffer = requireNotNull(androidMorphTargetBuffer) { "Calling method on destroyed MorphTargetBuffer" }
        val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
        morphTargetBuffer.setTangentsAt(androidEngine, targetIndex, tangents, count)
    }

    actual val vertexCount: Int
        get() {
        val morphTargetBuffer = requireNotNull(androidMorphTargetBuffer) { "Calling method on destroyed MorphTargetBuffer" }
        return morphTargetBuffer.vertexCount
    }

    actual val count: Int
        get() {
        val morphTargetBuffer = requireNotNull(androidMorphTargetBuffer) { "Calling method on destroyed MorphTargetBuffer" }
        return morphTargetBuffer.count
    }

    actual fun hasPositions(): Boolean {
        val morphTargetBuffer = requireNotNull(androidMorphTargetBuffer) { "Calling method on destroyed MorphTargetBuffer" }
        return morphTargetBuffer.hasPositions()
    }

    actual fun hasTangents(): Boolean {
        val morphTargetBuffer = requireNotNull(androidMorphTargetBuffer) { "Calling method on destroyed MorphTargetBuffer" }
        return morphTargetBuffer.hasTangents()
    }

    actual fun isCustomMorphingEnabled(): Boolean {
        val morphTargetBuffer = requireNotNull(androidMorphTargetBuffer) { "Calling method on destroyed MorphTargetBuffer" }
        return morphTargetBuffer.isCustomMorphingEnabled()
    }

    actual val nativeObject: Long
        get() {
        val morphTargetBuffer = requireNotNull(androidMorphTargetBuffer) { "Calling method on destroyed MorphTargetBuffer" }
        return morphTargetBuffer.nativeObject
    }

    actual internal fun invalidate() {
        androidMorphTargetBuffer = null
    }

    actual class Builder {
        private val androidBuilder = AndroidMorphTargetBuffer.Builder()

        actual fun vertexCount(vertexCount: Int): Builder {
            androidBuilder.vertexCount(vertexCount)
            return this
        }

        actual fun count(count: Int): Builder {
            androidBuilder.count(count)
            return this
        }

        actual fun withPositions(enabled: Boolean): Builder {
            androidBuilder.withPositions(enabled)
            return this
        }

        actual fun withTangents(enabled: Boolean): Builder {
            androidBuilder.withTangents(enabled)
            return this
        }

        actual fun enableCustomMorphing(enabled: Boolean): Builder {
            androidBuilder.enableCustomMorphing(enabled)
            return this
        }

        actual fun build(engine: Engine): MorphTargetBuffer {
            val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
            return MorphTargetBuffer(androidBuilder.build(androidEngine))
        }
    }
}

