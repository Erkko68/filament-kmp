package io.github.erkko68.filament

expect class SkinningBuffer {
    class Builder() {
        fun boneCount(boneCount: Int): Builder
        fun initialize(initialize: Boolean): Builder
        fun build(engine: Engine): SkinningBuffer
    }

    fun getBoneCount(): Int
    fun setBonesAsMatrices(engine: Engine, matrices: FloatArray, boneCount: Int, offset: Int)
}
