package io.github.erkko68.filament

expect class MorphTargetBuffer {
    class Builder() {
        fun vertexCount(vertexCount: Int): Builder
        fun count(count: Int): Builder
        fun withPositions(enabled: Boolean = true): Builder
        fun withTangents(enabled: Boolean = true): Builder
        fun enableCustomMorphing(enabled: Boolean = true): Builder
        fun build(engine: Engine): MorphTargetBuffer
    }

    fun getVertexCount(): Int
    fun getCount(): Int
    fun hasPositions(): Boolean
    fun hasTangents(): Boolean
    fun isCustomMorphingEnabled(): Boolean

    fun setPositionsAt(engine: Engine, targetIndex: Int, positions: FloatArray, count: Int)
    fun setTangentsAt(engine: Engine, targetIndex: Int, tangents: ShortArray, count: Int)
}
