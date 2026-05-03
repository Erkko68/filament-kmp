package io.github.erkko68.filament

actual class MorphTargetBuffer(internal val jsMorphTargetBuffer: Any? = null) {
    actual val vertexCount: Int get() = 0 // not exposed in JS bindings
    actual val count: Int get() = 0 // not exposed in JS bindings
    actual val hasPositions: Boolean get() = false // not exposed in JS bindings
    actual val hasTangents: Boolean get() = false // not exposed in JS bindings
    actual val isCustomMorphingEnabled: Boolean get() = false // not exposed in JS bindings

    actual fun setPositionsAt(
        engine: Engine,
        targetIndex: Int,
        positions: FloatArray,
        count: Int
    ) {
    }

    actual fun setTangentsAt(
        engine: Engine,
        targetIndex: Int,
        tangents: ShortArray,
        count: Int
    ) {
    }

    actual class Builder {
        actual fun vertexCount(vertexCount: Int): Builder {
            return this
        }

        actual fun count(count: Int): Builder {
            return this
        }

        actual fun withPositions(enabled: Boolean): Builder {
            return this
        }

        actual fun withTangents(enabled: Boolean): Builder {
            return this
        }

        actual fun enableCustomMorphing(enabled: Boolean): Builder {
            return this
        }

        actual fun build(engine: Engine): MorphTargetBuffer {
            return MorphTargetBuffer()
        }
    }
}