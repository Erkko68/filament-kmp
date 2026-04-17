package io.github.erkko68.filament

actual class MorphTargetBuffer {
    actual fun getVertexCount(): Int {
        TODO("Not yet implemented")
    }

    actual fun getCount(): Int {
        TODO("Not yet implemented")
    }

    actual fun hasPositions(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun hasTangents(): Boolean {
        TODO("Not yet implemented")
    }

    actual fun isCustomMorphingEnabled(): Boolean {
        TODO("Not yet implemented")
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
            TODO("Not yet implemented")
        }

        actual fun count(count: Int): Builder {
            TODO("Not yet implemented")
        }

        actual fun withPositions(enabled: Boolean): Builder {
            TODO("Not yet implemented")
        }

        actual fun withTangents(enabled: Boolean): Builder {
            TODO("Not yet implemented")
        }

        actual fun enableCustomMorphing(enabled: Boolean): Builder {
            TODO("Not yet implemented")
        }

        actual fun build(engine: Engine): MorphTargetBuffer {
            TODO("Not yet implemented")
        }
    }
}