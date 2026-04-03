package dev.filament.kmp

expect class SkinningBuffer {
    class Builder {
        /**
         * Size of the skinning buffer in bones.
         *
         * @param boneCount Number of bones the skinning buffer can hold.
         * @return this <code>Builder</code> object for chaining calls
         */
        fun boneCount(boneCount: Int): Builder

        /**
         * The new buffer is created with identity bones.
         */
        fun initialize(initialize: Boolean): Builder

        /**
         * Creates and returns the <code>SkinningBuffer</code> object.
         */
        fun build(engine: Engine): SkinningBuffer
    }

    /**
     * Updates the bone transforms in the range [offset, offset + boneCount).
     */
    fun setBonesAsMatrices(engine: Engine, matrices: Any, boneCount: Int, offset: Int)

    /**
     * Updates the bone transforms in the range [offset, offset + boneCount).
     */
    fun setBonesAsQuaternions(engine: Engine, quaternions: Any, boneCount: Int, offset: Int)

    /**
     * @return number of bones in this {@link SkinningBuffer}
     */
    fun getBoneCount(): Int

    fun getNativeObject(): Long

    internal fun invalidate()
}

