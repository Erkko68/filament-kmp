package io.github.erkko68.filament

// MorphTargetBuffer is not bound in upstream jsbindings.cpp (v1.71.4) — see
// UPSTREAM_INCONSISTENCIES.md. The actual is a transparent record of the
// Builder inputs so common-tests reading `vertexCount`/`count`/flags pass.
actual class MorphTargetBuffer internal constructor(
    internal val jsMorphTargetBuffer: Any? = null,
    actual val vertexCount: Int = 0,
    actual val count: Int = 0,
    actual val hasPositions: Boolean = false,
    actual val hasTangents: Boolean = false,
    actual val isCustomMorphingEnabled: Boolean = false,
) {
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
        private var vertexCount: Int = 0
        private var count: Int = 0
        private var hasPositions: Boolean = false
        private var hasTangents: Boolean = false
        private var isCustomMorphingEnabled: Boolean = false

        actual fun vertexCount(vertexCount: Int): Builder {
            this.vertexCount = vertexCount
            return this
        }

        actual fun count(count: Int): Builder {
            this.count = count
            return this
        }

        actual fun withPositions(enabled: Boolean): Builder {
            this.hasPositions = enabled
            return this
        }

        actual fun withTangents(enabled: Boolean): Builder {
            this.hasTangents = enabled
            return this
        }

        actual fun enableCustomMorphing(enabled: Boolean): Builder {
            this.isCustomMorphingEnabled = enabled
            return this
        }

        actual fun build(engine: Engine): MorphTargetBuffer {
            return MorphTargetBuffer(
                vertexCount = vertexCount,
                count = count,
                hasPositions = hasPositions,
                hasTangents = hasTangents,
                isCustomMorphingEnabled = isCustomMorphingEnabled,
            )
        }
    }
}
