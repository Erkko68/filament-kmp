package dev.filament.kmp

expect class MaterialInstance {
    /**
     * Creates a new MaterialInstance using another MaterialInstance as a template.
     */
    companion object {
        fun duplicate(other: MaterialInstance, name: String?): MaterialInstance
    }

    /** @return the Material associated with this instance */
    fun getMaterial(): Material

    /** @return the name associated with this instance */
    fun getName(): String

    /** Sets the value of a float parameter. */
    fun setParameter(name: String, x: Float)

    /** Sets the value of an int parameter. */
    fun setParameter(name: String, x: Int)

    /** Sets the value of a float2 parameter. */
    fun setParameter(name: String, x: Float, y: Float)

    /** Sets the value of an int2 parameter. */
    fun setParameter(name: String, x: Int, y: Int)

    /** Sets the value of a float3 parameter. */
    fun setParameter(name: String, x: Float, y: Float, z: Float)

    /** Sets the value of an int3 parameter. */
    fun setParameter(name: String, x: Int, y: Int, z: Int)

    /** Sets the value of a float4 parameter. */
    fun setParameter(name: String, x: Float, y: Float, z: Float, w: Float)

    /** Sets the value of an int4 parameter. */
    fun setParameter(name: String, x: Int, y: Int, z: Int, w: Int)

    /** Sets a texture and sampler parameter. */
    fun setParameter(name: String, texture: Texture, sampler: TextureSampler)

    /** Sets a custom scissor rectangle. */
    fun setScissor(left: Int, bottom: Int, width: Int, height: Int)

    /** Clears custom scissor rectangle. */
    fun unsetScissor()

    /** Sets polygon offset. */
    fun setPolygonOffset(scale: Float, constant: Float)

    fun setMaskThreshold(threshold: Float)

    fun getMaskThreshold(): Float

    fun setDoubleSided(doubleSided: Boolean)

    fun setTransparencyMode(mode: Material.TransparencyMode)

    fun setCullingMode(mode: Material.CullingMode)

    fun getNativeObject(): Long

    internal fun invalidate()
}

