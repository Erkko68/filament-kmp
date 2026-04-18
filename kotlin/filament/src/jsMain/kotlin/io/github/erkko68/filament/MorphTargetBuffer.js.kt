package io.github.erkko68.filament

actual class MorphTargetBuffer(internal val jsMorphTargetBuffer: Any? = null) {
    actual fun getVertexCount(): Int {
        return 0
    }

    actual fun getCount(): Int {
        return 0
    }

    actual fun hasPositions(): Boolean {
        return false
    }

    actual fun hasTangents(): Boolean {
        return false
    }

    actual fun isCustomMorphingEnabled(): Boolean {
        return false
    }

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