package dev.filament.kmp

expect class MaterialInstance {
    enum class StencilOperation {
        KEEP,
        ZERO,
        REPLACE,
        INCR_CLAMP,
        INCR_WRAP,
        DECR_CLAMP,
        DECR_WRAP,
        INVERT,
    }

    enum class StencilFace {
        FRONT,
        BACK,
        FRONT_AND_BACK,
    }

    /**
     * Creates a new {@link #MaterialInstance} using another {@link #MaterialInstance} as a template for initialization.
     * The new {@link #MaterialInstance} is an instance of the same {@link Material} of the template instance and
     * must be destroyed just like any other {@link #MaterialInstance}.
     *
     * @param other A {@link #MaterialInstance} to use as a template for initializing a new instance
     * @param name  A name for the new {@link #MaterialInstance} or nullptr to use the template's name
     * @return      A new {@link #MaterialInstance}
     */
    companion object {
        fun duplicate(other: MaterialInstance, name: String?): MaterialInstance
    }

    /** @return the {@link Material} associated with this instance */
    fun getMaterial(): Material

    /** @return the name associated with this instance */
    fun getName(): String

    /**
     * Sets the value of a float parameter.
     *
     * @param name the name of the material parameter
     * @param x    the value of the material parameter
     */
    fun setParameter(name: String, x: Float)

    /**
     * Sets the value of an int parameter.
     *
     * @param name the name of the material parameter
     * @param x    the value of the material parameter
     */
    fun setParameter(name: String, x: Int)

    /**
     * Sets the value of a float2 parameter.
     *
     * @param name the name of the material parameter
     * @param x    the value of the first component
     * @param y    the value of the second component
     */
    fun setParameter(name: String, x: Float, y: Float)

    /**
     * Sets the value of an int2 parameter.
     *
     * @param name the name of the material parameter
     * @param x    the value of the first component
     * @param y    the value of the second component
     */
    fun setParameter(name: String, x: Int, y: Int)

    /**
     * Sets the value of a float3 parameter.
     *
     * @param name the name of the material parameter
     * @param x    the value of the first component
     * @param y    the value of the second component
     * @param z    the value of the third component
     */
    fun setParameter(name: String, x: Float, y: Float, z: Float)

    /**
     * Sets the value of a int3 parameter.
     *
     * @param name the name of the material parameter
     * @param x    the value of the first component
     * @param y    the value of the second component
     * @param z    the value of the third component
     */
    fun setParameter(name: String, x: Int, y: Int, z: Int)

    /**
     * Sets the value of a float4 parameter.
     *
     * @param name the name of the material parameter
     * @param x    the value of the first component
     * @param y    the value of the second component
     * @param z    the value of the third component
     * @param w    the value of the fourth component
     */
    fun setParameter(name: String, x: Float, y: Float, z: Float, w: Float)

    /**
     * Sets the value of a int4 parameter.
     *
     * @param name the name of the material parameter
     * @param x    the value of the first component
     * @param y    the value of the second component
     * @param z    the value of the third component
     * @param w    the value of the fourth component
     */
    fun setParameter(name: String, x: Int, y: Int, z: Int, w: Int)

    /**
     * Sets the value of a color parameter.
     *
     * @param name the name of the material parameter
     * @param type the type of the color parameter
     * @param r    the value of the red component
     * @param g    the value of the green component
     * @param b    the value of the blue component
     * @param a    the value of the alpha component
     */
    fun setParameter(name: String, type: Colors.RgbaType, r: Float, g: Float, b: Float, a: Float)

    /**
     * Sets a texture and sampler parameter on this material's default instance.
     * <p>
     * Note: Depth textures can't be sampled with a linear filter unless the comparison mode is set
     *       to COMPARE_TO_TEXTURE.
     * </p>
     *
     * @param name The name of the material texture parameter
     * @param texture The texture to set as parameter
     * @param sampler The sampler to be used with this texture
     */
    fun setParameter(name: String, texture: Texture, sampler: TextureSampler)

    /**
     * Set-up a custom scissor rectangle; by default it is disabled.
     *
     * <p>
     * The scissor rectangle gets clipped by the View's viewport, in other words, the scissor
     * cannot affect fragments outside of the View's Viewport.
     * </p>
     *
     * <p>
     * Currently the scissor is not compatible with dynamic resolution and should always be
     * disabled when dynamic resolution is used.
     * </p>
     *
     * @param left      left coordinate of the scissor box relative to the viewport
     * @param bottom    bottom coordinate of the scissor box relative to the viewport
     * @param width     width of the scissor box
     * @param height    height of the scissor box
     *
     * @see #unsetScissor
     * @see View#setViewport
     * @see View#setDynamicResolutionOptions
     */
    fun setScissor(left: Int, bottom: Int, width: Int, height: Int)

    /**
     * Returns the scissor rectangle to its default disabled setting.
     * <p>
     * Currently the scissor is not compatible with dynamic resolution and should always be
     * disabled when dynamic resolution is used.
     * </p>
     * @see View#setDynamicResolutionOptions
     */
    fun unsetScissor()

    /**
     * Sets a polygon offset that will be applied to all renderables drawn with this material
     * instance.
     *
     *  The value of the offset is scale * dz + r * constant, where dz is the change in depth
     *  relative to the screen area of the triangle, and r is the smallest value that is guaranteed
     *  to produce a resolvable offset for a given implementation. This offset is added before the
     *  depth test.
     *
     *  Warning: using a polygon offset other than zero has a significant negative performance
     *  impact, as most implementations have to disable early depth culling. DO NOT USE unless
     *  absolutely necessary.
     *
     * @param scale scale factor used to create a variable depth offset for each triangle
     * @param constant scale factor used to create a constant depth offset for each triangle
     */
    fun setPolygonOffset(scale: Float, constant: Float)

    /**
     * Overrides the minimum alpha value a fragment must have to not be discarded when the blend
     * mode is MASKED. Defaults to 0.4 if it has not been set in the parent Material. The specified
     * value should be between 0 and 1 and will be clamped if necessary.
     *
     * @see
     * <a href="https://google.github.io/filament/Materials.html#materialdefinitions/materialblock/blendingandtransparency:maskthreshold">
     * Blending and transparency: maskThreshold</a>
     */
    fun setMaskThreshold(threshold: Float)

    /**
     * Gets the minimum alpha value a fragment must have to not be discarded when the blend
     * mode is MASKED
     */
    fun getMaskThreshold(): Float

    /**
     * Enables or disables double-sided lighting if the parent Material has double-sided capability,
     * otherwise prints a warning. If double-sided lighting is enabled, backface culling is
     * automatically disabled.
     *
     * @see
     * <a href="https://google.github.io/filament/Materials.html#materialdefinitions/materialblock/rasterization:doublesided">
     * Rasterization: doubleSided</a>
     */
    fun setDoubleSided(doubleSided: Boolean)

    /**
     * Returns true if double-sided lighting is enabled.
     */
    fun isDoubleSided(): Boolean

    /**
     * Sets the transparency mode for this material instance.
     * @see Material.TransparencyMode
     */
    fun setTransparencyMode(mode: Material.TransparencyMode)

    /**
     * Returns the transparency mode for this material instance.
     * @see Material.TransparencyMode
     */
    fun getTransparencyMode(): Material.TransparencyMode

    /**
     * Overrides the default triangle culling state that was set on the material.
     *
     * @see
     * <a href="https://google.github.io/filament/Materials.html#materialdefinitions/materialblock/rasterization:culling">
     * Rasterization: culling</a>
     */
    fun setCullingMode(mode: Material.CullingMode)

    fun setCullingMode(colorPassCullingMode: Material.CullingMode, shadowPassCullingMode: Material.CullingMode)

    /**
     * Returns the triangle culling state for this material instance.
     *
     * @see Material.CullingMode
     */
    fun getCullingMode(): Material.CullingMode

    /**
     * Returns the shadow triangle culling state for this material instance.
     *
     * @see Material.CullingMode
     */
    fun getShadowCullingMode(): Material.CullingMode

    /**
     * Enables or disables color writing.
     *
     * @param enable true to enable color writing, false to disable
     */
    fun setColorWrite(enable: Boolean)

    /**
     * Returns true if color writing is enabled.
     */
    fun isColorWriteEnabled(): Boolean

    /**
     * Enables or disables depth writing.
     *
     * @param enable true to enable depth writing, false to disable
     */
    fun setDepthWrite(enable: Boolean)

    /**
     * Returns true if depth writing is enabled.
     */
    fun isDepthWriteEnabled(): Boolean

    /**
     * Enables or disables stencil writing.
     *
     * @param enable true to enable stencil writing, false to disable
     */
    fun setStencilWrite(enable: Boolean)

    /**
     * Returns true if stencil writing is enabled.
     */
    fun isStencilWriteEnabled(): Boolean

    /**
     * Enables or disables depth culling.
     *
     * @param enable true to enable depth culling, false to disable
     */
    fun setDepthCulling(enable: Boolean)

    /**
     * Returns true if depth culling is enabled.
     */
    fun isDepthCullingEnabled(): Boolean

    /**
     * Sets the depth comparison function.
     *
     * @param func the depth comparison function
     */
    fun setDepthFunc(func: TextureSampler.CompareFunction)

    /**
     * Returns the depth comparison function.
     */
    fun getDepthFunc(): TextureSampler.CompareFunction

    /**
     * Sets the stencil comparison function for a specific face.
     *
     * @param func the stencil comparison function
     * @param face the stencil face
     */
    fun setStencilCompareFunction(func: TextureSampler.CompareFunction, face: StencilFace)

    /**
     * Sets the stencil comparison function for both faces.
     *
     * @param func the stencil comparison function
     */
    fun setStencilCompareFunction(func: TextureSampler.CompareFunction)

    /**
     * Sets the stencil operation for stencil fail cases.
     *
     * @param op the stencil operation
     * @param face the stencil face
     */
    fun setStencilOpStencilFail(op: StencilOperation, face: StencilFace)

    /**
     * Sets the stencil operation for stencil fail cases for both faces.
     *
     * @param op the stencil operation
     */
    fun setStencilOpStencilFail(op: StencilOperation)

    /**
     * Sets the stencil operation for depth fail cases.
     *
     * @param op the stencil operation
     * @param face the stencil face
     */
    fun setStencilOpDepthFail(op: StencilOperation, face: StencilFace)

    /**
     * Sets the stencil operation for depth fail cases for both faces.
     *
     * @param op the stencil operation
     */
    fun setStencilOpDepthFail(op: StencilOperation)

    /**
     * Sets the stencil operation for depth and stencil pass cases.
     *
     * @param op the stencil operation
     * @param face the stencil face
     */
    fun setStencilOpDepthStencilPass(op: StencilOperation, face: StencilFace)

    /**
     * Sets the stencil operation for depth and stencil pass cases for both faces.
     *
     * @param op the stencil operation
     */
    fun setStencilOpDepthStencilPass(op: StencilOperation)

    /**
     * Sets the stencil reference value for a specific face.
     *
     * @param value the stencil reference value
     * @param face the stencil face
     */
    fun setStencilReferenceValue(value: Int, face: StencilFace)

    /**
     * Sets the stencil reference value for both faces.
     *
     * @param value the stencil reference value
     */
    fun setStencilReferenceValue(value: Int)

    /**
     * Sets the stencil read mask for a specific face.
     *
     * @param readMask the stencil read mask
     * @param face the stencil face
     */
    fun setStencilReadMask(readMask: Int, face: StencilFace)

    /**
     * Sets the stencil read mask for both faces.
     *
     * @param readMask the stencil read mask
     */
    fun setStencilReadMask(readMask: Int)

    /**
     * Sets the stencil write mask for a specific face.
     *
     * @param writeMask the stencil write mask
     * @param face the stencil face
     */
    fun setStencilWriteMask(writeMask: Int, face: StencilFace)

    /**
     * Sets the stencil write mask for both faces.
     *
     * @param writeMask the stencil write mask
     */
    fun setStencilWriteMask(writeMask: Int)

    fun getNativeObject(): Long

    internal fun invalidate()
}

