package io.github.erkko68.filament

expect class SkinningBuffer {
    class Builder() {
        fun boneCount(boneCount: Int): Builder
        fun initialize(initialize: Boolean): Builder
        fun build(engine: Engine): SkinningBuffer
    }

    val boneCount: Int
    fun setBonesAsMatrices(engine: Engine, matrices: FloatArray, boneCount: Int, offset: Int)
    fun setBonesAsQuaternions(engine: Engine, bones: FloatArray, boneCount: Int, offset: Int)
}
