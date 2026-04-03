package dev.filament.kmp

expect class MorphTargetBuffer {
    class Builder {
        /**
         * Size of the morph targets in vertex counts.
         *
         * @param vertexCount Number of vertex counts the morph targets can hold.
         * @return this <code>Builder</code> object for chaining calls
         */
        fun vertexCount(vertexCount: Int): Builder

        /**
         * Size of the morph targets in targets.
         *
         * @param count Number of targets the morph targets can hold.
         * @return this <code>Builder</code> object for chaining calls
         */
        fun count(count: Int): Builder

        /**
         * Use this method to enable or disable the built-in position morphing buffer.
         * Default is true.
         */
        fun withPositions(enabled: Boolean): Builder

        /**
         * Use this method to enable or disable the built-in tangent morphing buffer.
         * Default is true.
         */
        fun withTangents(enabled: Boolean): Builder

        /**
         * Use this method to enable or disable custom morphing.
         * Default is false.
         */
        fun enableCustomMorphing(enabled: Boolean): Builder

        /**
         * Creates and returns the <code>MorphTargetBuffer</code> object.
         */
        fun build(engine: Engine): MorphTargetBuffer
    }

    /**
     * Updates float4 positions for the given morph target.
     */
    fun setPositionsAt(engine: Engine, targetIndex: Int, positions: FloatArray, count: Int)

    /**
     * Updates tangents for the given morph target.
     */
    fun setTangentsAt(engine: Engine, targetIndex: Int, tangents: ShortArray, count: Int)

    /**
     * @return number of vertices in this {@link MorphTargetBuffer}
     */
    fun getVertexCount(): Int

    /**
     * @return number of morph targets in this {@link MorphTargetBuffer}
     */
    fun getCount(): Int

    /**
     * @return true if this MorphTargetBuffer has a position buffer.
     */
    fun hasPositions(): Boolean

    /**
     * @return true if this MorphTargetBuffer has a tangent buffer.
     */
    fun hasTangents(): Boolean

    /**
     * @return true if custom morphing is enabled.
     */
    fun isCustomMorphingEnabled(): Boolean

    fun getNativeObject(): Long

    internal fun invalidate()
}

