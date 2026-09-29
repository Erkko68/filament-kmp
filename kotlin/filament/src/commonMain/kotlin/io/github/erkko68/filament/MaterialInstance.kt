package io.github.erkko68.filament

import io.github.erkko68.filament.interop.*

/**
 * MaterialInstance customizes the parameters of a Material for per-object rendering.
 *
 * Each Material can spawn multiple MaterialInstances with different parameter values
 * (colors, textures, numeric uniforms, etc.). Changes to a MaterialInstance only affect
 * renderables using that specific instance, not the Material or other instances.
 *
 * **Creating and destroying:**
 * Create instances via Material.createInstance() and destroy with Engine.destroy(instance).
 * You can duplicate an existing instance using the companion object's duplicate() method.
 *
 * **Setting parameters:**
 * Use setParameter() overloads to set uniforms (booleans, floats, vectors, matrices, textures).
 * Parameter names and types must match those defined in the material. Array parameters are
 * also supported via setParameter(name, type, array, offset, count) variants.
 *
 * **Rendering state customization:**
 * Each MaterialInstance can override per-instance rendering behavior:
 * - Scissor rectangle for pixel-perfect clipping
 * - Polygon offset for depth artifacts
 * - Culling mode (override material's cull setting)
 * - Depth test configuration
 * - Stencil test and operations
 * - Color and depth write masks
 *
 * These settings override the Material's defaults.
 *
 * @see Material
 * @see Material.createInstance
 */
class MaterialInstance @InternalFilamentApi constructor(internal val nativeHandle: NativePointer) {
    /** The native object, for interop with code calling the Fila* C API directly. Read-only: this wrapper owns it. */
    @InternalFilamentApi
    val nativeObject: NativePointer get() = nativeHandle

    /**
     * Element types for boolean parameter arrays.
     *
     * - BOOL: Single boolean (true/false)
     * - BOOL2/BOOL3/BOOL4: 2, 3, or 4 component boolean vectors
     */
    enum class BooleanElement { BOOL, BOOL2, BOOL3, BOOL4 }
    /**
     * Element types for integer parameter arrays.
     *
     * - INT: Single 32-bit signed integer
     * - INT2/INT3/INT4: 2, 3, or 4 component integer vectors
     */
    enum class IntElement { INT, INT2, INT3, INT4 }
    /**
     * Element types for floating-point parameter arrays.
     *
     * - FLOAT: Single 32-bit float
     * - FLOAT2/FLOAT3/FLOAT4: 2, 3, or 4 component float vectors
     * - MAT3/MAT4: 3x3 or 4x4 floating-point matrices
     */
    enum class FloatElement { FLOAT, FLOAT2, FLOAT3, FLOAT4, MAT3, MAT4 }
    
    /**
     * Stencil test operation determines how the stencil buffer is modified.
     *
     * - KEEP: Keep the existing stencil value
     * - ZERO: Clear stencil to 0
     * - REPLACE: Replace with reference value
     * - INCR_CLAMP: Increment and clamp to max
     * - INCR_WRAP: Increment and wrap to 0
     * - DECR_CLAMP: Decrement and clamp to 0
     * - DECR_WRAP: Decrement and wrap to max
     * - INVERT: Bitwise invert stencil value
     */
    enum class StencilOperation { KEEP, ZERO, REPLACE, INCR_CLAMP, INCR_WRAP, DECR_CLAMP, DECR_WRAP, INVERT }
    /**
     * Which face(s) the stencil operation applies to.
     *
     * - FRONT: Front-facing primitives only
     * - BACK: Back-facing primitives only
     * - FRONT_AND_BACK: Both front and back faces
     */
    enum class StencilFace { FRONT, BACK, FRONT_AND_BACK }
 
    companion object {
        /**
         * Create a new MaterialInstance by duplicating an existing one.
         *
         * This is useful for creating instances with the same initial parameters as an
         * existing instance without having to re-set all parameters individually.
         *
         * @param other A MaterialInstance to copy parameter values from.
         * @param name Optional debug name for the new instance (null to use other's name).
         * @return A new MaterialInstance with all parameters copied from other.
         */
        fun duplicate(other: MaterialInstance, name: String? = null): MaterialInstance {
            return MaterialInstance(name.useCString { FilaMaterialInstance_duplicate(other.nativeHandle, it) })
        }
    }

    /**
     * Get the Material this instance is created from.
     *
     * @return The parent Material. The Material owns all instances created from it.
     */
    val material: Material get() = Material(FilaMaterialInstance_getMaterial(nativeHandle))
    /**
     * Get the name of this MaterialInstance.
     *
     * @return Instance name string (useful for debugging and profiling).
     */
    val name: String get() = stringFromInterop(FilaMaterialInstance_getName(nativeHandle)) ?: ""

    /**
     * Sets a boolean parameter.
     * @param name Parameter name as defined in the material
     * @param x Boolean value
     */
    fun setParameter(name: String, x: Boolean) { name.useCString { FilaMaterialInstance_setParameterBool(nativeHandle, it, x) } }
    /**
     * Sets a float parameter.
     * @param name Parameter name as defined in the material
     * @param x Float value
     */
    fun setParameter(name: String, x: Float) { name.useCString { FilaMaterialInstance_setParameterFloat(nativeHandle, it, x) } }
    /**
     * Sets an integer parameter.
     * @param name Parameter name as defined in the material
     * @param x Integer value
     */
    fun setParameter(name: String, x: Int) { name.useCString { FilaMaterialInstance_setParameterInt(nativeHandle, it, x) } }
    /**
     * Returns the boolean value of a material specialization constant.
     * @param name Constant name as defined in the material
     */
    fun getConstantBoolean(name: String): Boolean = name.useCString { FilaMaterialInstance_getConstantBool(nativeHandle, it) }
    /**
     * Returns the float value of a material specialization constant.
     * @param name Constant name as defined in the material
     */
    fun getConstantFloat(name: String): Float = name.useCString { FilaMaterialInstance_getConstantFloat(nativeHandle, it) }
    /**
     * Returns the integer value of a material specialization constant.
     * @param name Constant name as defined in the material
     */
    fun getConstantInt(name: String): Int = name.useCString { FilaMaterialInstance_getConstantInt(nativeHandle, it) }
    /**
     * Sets a 2-component boolean vector parameter.
     * @param name Parameter name as defined in the material
     * @param x First component
     * @param y Second component
     */
    fun setParameter(name: String, x: Boolean, y: Boolean) { name.useCString { FilaMaterialInstance_setParameterBool2(nativeHandle, it, x, y) } }
    /**
     * Sets a 2-component float vector parameter.
     * @param name Parameter name as defined in the material
     * @param x First component
     * @param y Second component
     */
    fun setParameter(name: String, x: Float, y: Float) { name.useCString { FilaMaterialInstance_setParameterFloat2(nativeHandle, it, x, y) } }
    /**
     * Sets a 2-component integer vector parameter.
     * @param name Parameter name as defined in the material
     * @param x First component
     * @param y Second component
     */
    fun setParameter(name: String, x: Int, y: Int) { name.useCString { FilaMaterialInstance_setParameterInt2(nativeHandle, it, x, y) } }
    /**
     * Sets a 3-component boolean vector parameter.
     * @param name Parameter name as defined in the material
     * @param x First component
     * @param y Second component
     * @param z Third component
     */
    fun setParameter(name: String, x: Boolean, y: Boolean, z: Boolean) { name.useCString { FilaMaterialInstance_setParameterBool3(nativeHandle, it, x, y, z) } }
    /**
     * Sets a 3-component float vector parameter.
     * @param name Parameter name as defined in the material
     * @param x First component
     * @param y Second component
     * @param z Third component
     */
    fun setParameter(name: String, x: Float, y: Float, z: Float) { name.useCString { FilaMaterialInstance_setParameterFloat3(nativeHandle, it, x, y, z) } }
    /**
     * Sets a 3-component integer vector parameter.
     * @param name Parameter name as defined in the material
     * @param x First component
     * @param y Second component
     * @param z Third component
     */
    fun setParameter(name: String, x: Int, y: Int, z: Int) { name.useCString { FilaMaterialInstance_setParameterInt3(nativeHandle, it, x, y, z) } }
    /**
     * Sets a 4-component boolean vector parameter.
     * @param name Parameter name as defined in the material
     * @param x First component
     * @param y Second component
     * @param z Third component
     * @param w Fourth component
     */
    fun setParameter(name: String, x: Boolean, y: Boolean, z: Boolean, w: Boolean) { name.useCString { FilaMaterialInstance_setParameterBool4(nativeHandle, it, x, y, z, w) } }
    /**
     * Sets a 4-component float vector parameter.
     * @param name Parameter name as defined in the material
     * @param x First component
     * @param y Second component
     * @param z Third component
     * @param w Fourth component
     */
    fun setParameter(name: String, x: Float, y: Float, z: Float, w: Float) { name.useCString { FilaMaterialInstance_setParameterFloat4(nativeHandle, it, x, y, z, w) } }
    /**
     * Sets a 4-component integer vector parameter.
     * @param name Parameter name as defined in the material
     * @param x First component
     * @param y Second component
     * @param z Third component
     * @param w Fourth component
     */
    fun setParameter(name: String, x: Int, y: Int, z: Int, w: Int) { name.useCString { FilaMaterialInstance_setParameterInt4(nativeHandle, it, x, y, z, w) } }
    
    /**
     * Sets a texture parameter with sampler configuration.
     *
     * Note: Depth textures cannot be sampled with linear filtering unless comparison
     * mode is set to COMPARE_TO_TEXTURE.
     *
     * @param name Parameter name as defined in the material
     * @param texture Texture to bind (can be null to unbind)
     * @param sampler Sampler configuration (filtering, wrapping, comparison function)
     */
    fun setParameter(name: String, texture: Texture, sampler: TextureSampler) {
        name.useCString {
            FilaMaterialInstance_setParameterTexture(
                nativeHandle, it, texture.nativeHandle,
                sampler.minFilter.ordinal, sampler.magFilter.ordinal,
                sampler.wrapModeS.ordinal, sampler.wrapModeT.ordinal, sampler.wrapModeR.ordinal,
                sampler.anisotropy, sampler.compareMode.ordinal, sampler.compareFunction.ordinal,
            )
        }
    }
    
    /**
     * Sets a parameter from a boolean array.
     *
     * @param name Parameter name as defined in the material
     * @param type Array element type (BOOL, BOOL2, BOOL3, or BOOL4)
     * @param v Source array
     * @param offset Index into v to start copying from
     * @param count Number of elements to copy
     */
    fun setParameter(name: String, type: BooleanElement, v: BooleanArray, offset: Int, count: Int) {
        // C bool is one byte.
        val bools = ByteArray(v.size - offset) { if (v[offset + it]) 1 else 0 }
        bools.usePinned { p -> name.useCString { FilaMaterialInstance_setBooleanParameterArray(nativeHandle, it, type.ordinal + 1, p, count) } }
    }
    /**
     * Sets a parameter from an integer array.
     *
     * @param name Parameter name as defined in the material
     * @param type Array element type (INT, INT2, INT3, or INT4)
     * @param v Source array
     * @param offset Index into v to start copying from
     * @param count Number of elements to copy
     */
    fun setParameter(name: String, type: IntElement, v: IntArray, offset: Int, count: Int) {
        v.copyOfRange(offset, v.size).usePinned { p ->
            name.useCString { FilaMaterialInstance_setIntParameterArray(nativeHandle, it, type.ordinal + 1, p, count) }
        }
    }
    /**
     * Sets a parameter from a float array.
     *
     * @param name Parameter name as defined in the material
     * @param type Array element type (FLOAT, FLOAT2, FLOAT3, FLOAT4, MAT3, or MAT4)
     * @param v Source array
     * @param offset Index into v to start copying from
     * @param count Number of elements to copy
     */
    fun setParameter(name: String, type: FloatElement, v: FloatArray, offset: Int, count: Int) {
        val elementSize = when (type) {
            FloatElement.FLOAT -> 1
            FloatElement.FLOAT2 -> 2
            FloatElement.FLOAT3 -> 3
            FloatElement.FLOAT4 -> 4
            FloatElement.MAT3 -> 9
            FloatElement.MAT4 -> 16
        }
        v.copyOfRange(offset, v.size).usePinned { p ->
            name.useCString { FilaMaterialInstance_setFloatParameterArray(nativeHandle, it, elementSize, p, count) }
        }
    }
    
    /**
     * Sets an RGB color parameter.
     *
     * The color is converted based on the specified type (Linear or sRGB).
     *
     * @param name Parameter name as defined in the material
     * @param type Whether color is in Linear or sRGB space
     * @param r Red channel [0, 1]
     * @param g Green channel [0, 1]
     * @param b Blue channel [0, 1]
     */
    fun setParameter(name: String, type: RgbType, r: Float, g: Float, b: Float) {
        val linear = Color.toLinear(type, floatArrayOf(r, g, b))
        name.useCString { FilaMaterialInstance_setParameterFloat3(nativeHandle, it, linear[0], linear[1], linear[2]) }
    }
    /**
     * Sets an RGBA color parameter.
     *
     * The color is converted based on the specified type (Linear or sRGB).
     *
     * @param name Parameter name as defined in the material
     * @param type Whether color is in Linear or sRGB space
     * @param r Red channel [0, 1]
     * @param g Green channel [0, 1]
     * @param b Blue channel [0, 1]
     * @param a Alpha channel [0, 1]
     */
    fun setParameter(name: String, type: RgbaType, r: Float, g: Float, b: Float, a: Float) {
        val linear = Color.toLinear(type, floatArrayOf(r, g, b, a))
        name.useCString { FilaMaterialInstance_setParameterFloat4(nativeHandle, it, linear[0], linear[1], linear[2], linear[3]) }
    }

    /**
     * Set-up a custom scissor rectangle; by default it is disabled.
     *
     * The scissor rectangle gets clipped by the View's viewport, in other words, the scissor
     * cannot affect fragments outside of the View's Viewport.
     *
     * Currently the scissor is not compatible with dynamic resolution and should always be
     * disabled when dynamic resolution is used.
     *
     * @param left left coordinate of the scissor box relative to the viewport
     * @param bottom bottom coordinate of the scissor box relative to the viewport
     * @param width width of the scissor box
     * @param height height of the scissor box
     *
     * @see unsetScissor
     * @see View.setViewport
     * @see View.setDynamicResolutionOptions
     */
    fun setScissor(left: Int, bottom: Int, width: Int, height: Int) {
        FilaMaterialInstance_setScissor(nativeHandle, left, bottom, width, height)
    }
    /**
     * Disables the scissor box test; rendering is not restricted to any region.
     */
    fun unsetScissor() { FilaMaterialInstance_unsetScissor(nativeHandle) }
    
    /**
     * Sets a polygon offset that will be applied to all renderables drawn with this material instance.
     *
     * The value of the offset is scale * dz + r * constant, where dz is the change in depth
     * relative to the screen area of the triangle, and r is the smallest value that is guaranteed
     * to produce a resolvable offset for a given implementation. This offset is added before the
     * depth test.
     *
     * @warning Using a polygon offset other than zero has a significant negative performance
     * impact, as most implementations have to disable early depth culling. DO NOT USE unless
     * absolutely necessary.
     *
     * @param scale Scale factor used to create a variable depth offset for each triangle
     * @param constant Scale factor used to create a constant depth offset for each triangle
     */
    fun setPolygonOffset(scale: Float, constant: Float) { FilaMaterialInstance_setPolygonOffset(nativeHandle, scale, constant) }
    /**
     * Gets/sets the alpha mask threshold for masked blending mode.
     *
     * Overrides the minimum alpha value a fragment must have to not be discarded when the blend
     * mode is MASKED. Defaults to 0.4 if it has not been set in the parent Material. The specified
     * value should be between 0 and 1 and will be clamped if necessary.
     *
     * @see Material.BlendingMode.MASKED
     */
    var maskThreshold: Float
        get() = FilaMaterialInstance_getMaskThreshold(nativeHandle)
        set(value) { FilaMaterialInstance_setMaskThreshold(nativeHandle, value) }
    /**
     * Gets/sets the screen space variance of the filter kernel used when applying specular
     * anti-aliasing.
     *
     * The default value is set to 0.15. The specified value should be between 0 and 1
     * and will be clamped if necessary.
     */
    var specularAntiAliasingVariance: Float
        get() = FilaMaterialInstance_getSpecularAntiAliasingVariance(nativeHandle)
        set(value) { FilaMaterialInstance_setSpecularAntiAliasingVariance(nativeHandle, value) }
    /**
     * Gets/sets the clamping threshold used to suppress estimation errors when applying specular
     * anti-aliasing.
     *
     * The default value is set to 0.2. The specified value should be between 0 and 1
     * and will be clamped if necessary.
     */
    var specularAntiAliasingThreshold: Float
        get() = FilaMaterialInstance_getSpecularAntiAliasingThreshold(nativeHandle)
        set(value) { FilaMaterialInstance_setSpecularAntiAliasingThreshold(nativeHandle, value) }
    /**
     * Gets/sets whether double-sided lighting is enabled.
     *
     * Enables or disables double-sided lighting if the parent Material has double-sided capability,
     * otherwise prints a warning. If double-sided lighting is enabled, backface culling is
     * automatically disabled.
     */
    var isDoubleSided: Boolean
        get() = FilaMaterialInstance_isDoubleSided(nativeHandle)
        set(value) { FilaMaterialInstance_setDoubleSided(nativeHandle, value) }
    
    /**
     * Gets/sets the transparency rendering mode.
     *
     * Specifies how transparent objects should be rendered (default is DEFAULT).
     *
     * @see Material.TransparencyMode
     */
    var transparencyMode: Material.TransparencyMode
        get() = Material.TransparencyMode.entries[FilaMaterialInstance_getTransparencyMode(nativeHandle)]
        set(value) { FilaMaterialInstance_setTransparencyMode(nativeHandle, value.ordinal) }
    
    /**
     * Gets/sets the face culling mode.
     *
     * Overrides the default triangle culling state that was set on the material.
     *
     * @see Material.CullingMode
     */
    var cullingMode: Material.CullingMode
        get() = Material.CullingMode.entries[FilaMaterialInstance_getCullingMode(nativeHandle)]
        set(value) { FilaMaterialInstance_setCullingMode(nativeHandle, value.ordinal) }

    /**
     * Sets different culling modes for color and shadow passes.
     *
     * Overrides the default triangle culling state that was set on the material separately for the
     * color and shadow passes.
     *
     * @param colorPassCullingMode Culling mode for color rendering
     * @param shadowPassCullingMode Culling mode for shadow pass rendering
     */
    fun setCullingMode(colorPassCullingMode: Material.CullingMode, shadowPassCullingMode: Material.CullingMode) {
        FilaMaterialInstance_setCullingModeSeparate(nativeHandle, colorPassCullingMode.ordinal, shadowPassCullingMode.ordinal)
    }

    /**
     * Returns the face culling mode for the shadow passes.
     *
     * @return Culling mode used when rendering shadow maps
     */
    val shadowCullingMode: Material.CullingMode get() = Material.CullingMode.entries[FilaMaterialInstance_getShadowCullingMode(nativeHandle)]
    
    /**
     * Gets/sets whether color write is enabled.
     *
     * Overrides the default color-buffer write state that was set on the material.
     */
    var isColorWriteEnabled: Boolean
        get() = FilaMaterialInstance_isColorWriteEnabled(nativeHandle)
        set(value) { FilaMaterialInstance_setColorWrite(nativeHandle, value) }
    /**
     * Gets/sets whether depth write is enabled.
     *
     * Overrides the default depth-buffer write state that was set on the material.
     */
    var isDepthWriteEnabled: Boolean
        get() = FilaMaterialInstance_isDepthWriteEnabled(nativeHandle)
        set(value) { FilaMaterialInstance_setDepthWrite(nativeHandle, value) }
    /**
     * Gets/sets whether stencil write is enabled.
     *
     * Overrides the default stencil-buffer write state that was set on the material.
     */
    var isStencilWriteEnabled: Boolean
        get() = FilaMaterialInstance_isStencilWriteEnabled(nativeHandle)
        set(value) { FilaMaterialInstance_setStencilWrite(nativeHandle, value) }
    
    /**
     * Gets/sets whether depth culling (depth testing) is enabled.
     *
     * Overrides the default depth testing state that was set on the material.
     */
    var isDepthCullingEnabled: Boolean
        get() = FilaMaterialInstance_isDepthCullingEnabled(nativeHandle)
        set(value) { FilaMaterialInstance_setDepthCulling(nativeHandle, value) }

    /**
     * Gets/sets the depth function.
     *
     * Overrides the default depth function state that was set on the material.
     */
    var depthFunc: TextureSampler.CompareFunction
        get() = TextureSampler.CompareFunction.entries[FilaMaterialInstance_getDepthFunc(nativeHandle)]
        set(value) { FilaMaterialInstance_setDepthFunc(nativeHandle, value.ordinal) }
    
    /**
     * Sets the stencil comparison function (default is ALWAYS).
     *
     * It's possible to set separate stencil comparison functions; one for front-facing polygons,
     * and one for back-facing polygons. The face parameter determines the comparison function(s)
     * updated by this call.
     *
     * @param func Comparison function
     * @param face Which face(s) this applies to (FRONT, BACK, or FRONT_AND_BACK)
     */
    fun setStencilCompareFunction(func: TextureSampler.CompareFunction, face: StencilFace) {
        FilaMaterialInstance_setStencilCompareFunction(nativeHandle, func.ordinal, face.native)
    }
    /**
     * Sets the stencil comparison function for both front and back faces (default is ALWAYS).
     *
     * @param func Comparison function
     */
    fun setStencilCompareFunction(func: TextureSampler.CompareFunction) {
        FilaMaterialInstance_setStencilCompareFunction(nativeHandle, func.ordinal, StencilFace.FRONT_AND_BACK.native)
    }
    /**
     * Sets the stencil fail operation (default is KEEP).
     *
     * The stencil fail operation is performed to update values in the stencil buffer when the
     * stencil test fails.
     *
     * It's possible to set separate stencil fail operations; one for front-facing polygons, and one
     * for back-facing polygons. The face parameter determines the stencil fail operation(s) updated
     * by this call.
     *
     * @param op Operation to apply
     * @param face Which face(s) this applies to (FRONT, BACK, or FRONT_AND_BACK)
     */
    fun setStencilOpStencilFail(op: StencilOperation, face: StencilFace) {
        FilaMaterialInstance_setStencilOpStencilFail(nativeHandle, op.ordinal, face.native)
    }
    /**
     * Sets the stencil fail operation for both front and back faces (default is KEEP).
     *
     * @param op Operation to apply
     */
    fun setStencilOpStencilFail(op: StencilOperation) {
        FilaMaterialInstance_setStencilOpStencilFail(nativeHandle, op.ordinal, StencilFace.FRONT_AND_BACK.native)
    }
    /**
     * Sets the depth fail operation (default is KEEP).
     *
     * The depth fail operation is performed to update values in the stencil buffer when the depth
     * test fails.
     *
     * It's possible to set separate depth fail operations; one for front-facing polygons, and one
     * for back-facing polygons. The face parameter determines the depth fail operation(s) updated
     * by this call.
     *
     * @param op Operation to apply
     * @param face Which face(s) this applies to (FRONT, BACK, or FRONT_AND_BACK)
     */
    fun setStencilOpDepthFail(op: StencilOperation, face: StencilFace) {
        FilaMaterialInstance_setStencilOpDepthFail(nativeHandle, op.ordinal, face.native)
    }
    /**
     * Sets the depth fail operation for both front and back faces (default is KEEP).
     *
     * @param op Operation to apply
     */
    fun setStencilOpDepthFail(op: StencilOperation) {
        FilaMaterialInstance_setStencilOpDepthFail(nativeHandle, op.ordinal, StencilFace.FRONT_AND_BACK.native)
    }
    /**
     * Sets the depth-stencil pass operation (default is KEEP).
     *
     * The depth-stencil pass operation is performed to update values in the stencil buffer when
     * both the stencil test and depth test pass.
     *
     * It's possible to set separate depth-stencil pass operations; one for front-facing polygons,
     * and one for back-facing polygons. The face parameter determines the depth-stencil pass
     * operation(s) updated by this call.
     *
     * @param op Operation to apply
     * @param face Which face(s) this applies to (FRONT, BACK, or FRONT_AND_BACK)
     */
    fun setStencilOpDepthStencilPass(op: StencilOperation, face: StencilFace) {
        FilaMaterialInstance_setStencilOpDepthStencilPass(nativeHandle, op.ordinal, face.native)
    }
    /**
     * Sets the depth-stencil pass operation for both front and back faces (default is KEEP).
     *
     * @param op Operation to apply
     */
    fun setStencilOpDepthStencilPass(op: StencilOperation) {
        FilaMaterialInstance_setStencilOpDepthStencilPass(nativeHandle, op.ordinal, StencilFace.FRONT_AND_BACK.native)
    }
    
    /**
     * Sets the stencil reference value (default is 0).
     *
     * It's possible to set separate stencil reference values; one for front-facing polygons, and one
     * for back-facing polygons. The face parameter determines the reference value(s) updated
     * by this call.
     *
     * @param value Reference value [0, 255]
     * @param face Which face(s) this applies to (FRONT, BACK, or FRONT_AND_BACK)
     */
    fun setStencilReferenceValue(value: Int, face: StencilFace) {
        FilaMaterialInstance_setStencilReferenceValue(nativeHandle, value, face.native)
    }
    /**
     * Sets the stencil reference value for both front and back faces (default is 0).
     *
     * @param value Reference value [0, 255]
     */
    fun setStencilReferenceValue(value: Int) {
        FilaMaterialInstance_setStencilReferenceValue(nativeHandle, value, StencilFace.FRONT_AND_BACK.native)
    }
    /**
     * Sets the stencil read mask (default is 0xFF / 255 / all bits).
     *
     * It's possible to set separate stencil read masks; one for front-facing polygons, and one
     * for back-facing polygons. The face parameter determines the read mask(s) updated by this call.
     *
     * @param readMask Bitmask [0, 255]; only masked bits participate in comparison
     * @param face Which face(s) this applies to (FRONT, BACK, or FRONT_AND_BACK)
     */
    fun setStencilReadMask(readMask: Int, face: StencilFace) {
        FilaMaterialInstance_setStencilReadMask(nativeHandle, readMask, face.native)
    }
    /**
     * Sets the stencil read mask for both front and back faces (default is 0xFF / 255 / all bits).
     *
     * @param readMask Bitmask [0, 255]; only masked bits participate in comparison
     */
    fun setStencilReadMask(readMask: Int) {
        FilaMaterialInstance_setStencilReadMask(nativeHandle, readMask, StencilFace.FRONT_AND_BACK.native)
    }
    /**
     * Sets the stencil write mask (default is 0xFF / 255 / all bits).
     *
     * It's possible to set separate stencil write masks; one for front-facing polygons, and one
     * for back-facing polygons. The face parameter determines the write mask(s) updated by this call.
     *
     * @param writeMask Bitmask [0, 255]; only masked bits can be modified
     * @param face Which face(s) this applies to (FRONT, BACK, or FRONT_AND_BACK)
     */
    fun setStencilWriteMask(writeMask: Int, face: StencilFace) {
        FilaMaterialInstance_setStencilWriteMask(nativeHandle, writeMask, face.native)
    }
    /**
     * Sets the stencil write mask for both front and back faces (default is 0xFF / 255 / all bits).
     *
     * @param writeMask Bitmask [0, 255]; only masked bits can be modified
     */
    fun setStencilWriteMask(writeMask: Int) {
        FilaMaterialInstance_setStencilWriteMask(nativeHandle, writeMask, StencilFace.FRONT_AND_BACK.native)
    }
}

private val MaterialInstance.StencilFace.native: Int
    get() = when (this) {
        MaterialInstance.StencilFace.FRONT -> 1
        MaterialInstance.StencilFace.BACK -> 2
        MaterialInstance.StencilFace.FRONT_AND_BACK -> 3
    }

@ExternalSymbolName("FilaMaterialInstance_duplicate")
private external fun FilaMaterialInstance_duplicate(other: NativePointer, name: NativePointer): NativePointer

@ExternalSymbolName("FilaMaterialInstance_getConstantBool")
private external fun FilaMaterialInstance_getConstantBool(instance: NativePointer, name: NativePointer): Boolean

@ExternalSymbolName("FilaMaterialInstance_getConstantFloat")
private external fun FilaMaterialInstance_getConstantFloat(instance: NativePointer, name: NativePointer): Float

@ExternalSymbolName("FilaMaterialInstance_getConstantInt")
private external fun FilaMaterialInstance_getConstantInt(instance: NativePointer, name: NativePointer): Int

@ExternalSymbolName("FilaMaterialInstance_getCullingMode")
private external fun FilaMaterialInstance_getCullingMode(instance: NativePointer): Int

@ExternalSymbolName("FilaMaterialInstance_getDepthFunc")
private external fun FilaMaterialInstance_getDepthFunc(instance: NativePointer): Int

@ExternalSymbolName("FilaMaterialInstance_getMaskThreshold")
private external fun FilaMaterialInstance_getMaskThreshold(instance: NativePointer): Float

@ExternalSymbolName("FilaMaterialInstance_getMaterial")
private external fun FilaMaterialInstance_getMaterial(instance: NativePointer): NativePointer

@ExternalSymbolName("FilaMaterialInstance_getName")
private external fun FilaMaterialInstance_getName(instance: NativePointer): NativePointer

@ExternalSymbolName("FilaMaterialInstance_getShadowCullingMode")
private external fun FilaMaterialInstance_getShadowCullingMode(instance: NativePointer): Int

@ExternalSymbolName("FilaMaterialInstance_getSpecularAntiAliasingThreshold")
private external fun FilaMaterialInstance_getSpecularAntiAliasingThreshold(instance: NativePointer): Float

@ExternalSymbolName("FilaMaterialInstance_getSpecularAntiAliasingVariance")
private external fun FilaMaterialInstance_getSpecularAntiAliasingVariance(instance: NativePointer): Float

@ExternalSymbolName("FilaMaterialInstance_getTransparencyMode")
private external fun FilaMaterialInstance_getTransparencyMode(instance: NativePointer): Int

@ExternalSymbolName("FilaMaterialInstance_isColorWriteEnabled")
private external fun FilaMaterialInstance_isColorWriteEnabled(instance: NativePointer): Boolean

@ExternalSymbolName("FilaMaterialInstance_isDepthCullingEnabled")
private external fun FilaMaterialInstance_isDepthCullingEnabled(instance: NativePointer): Boolean

@ExternalSymbolName("FilaMaterialInstance_isDepthWriteEnabled")
private external fun FilaMaterialInstance_isDepthWriteEnabled(instance: NativePointer): Boolean

@ExternalSymbolName("FilaMaterialInstance_isDoubleSided")
private external fun FilaMaterialInstance_isDoubleSided(instance: NativePointer): Boolean

@ExternalSymbolName("FilaMaterialInstance_isStencilWriteEnabled")
private external fun FilaMaterialInstance_isStencilWriteEnabled(instance: NativePointer): Boolean

@ExternalSymbolName("FilaMaterialInstance_setBooleanParameterArray")
private external fun FilaMaterialInstance_setBooleanParameterArray(instance: NativePointer, name: NativePointer, elementSize: Int, v: NativePointer, count: Int)

@ExternalSymbolName("FilaMaterialInstance_setColorWrite")
private external fun FilaMaterialInstance_setColorWrite(instance: NativePointer, enable: Boolean)

@ExternalSymbolName("FilaMaterialInstance_setCullingMode")
private external fun FilaMaterialInstance_setCullingMode(instance: NativePointer, cullingMode: Int)

@ExternalSymbolName("FilaMaterialInstance_setCullingModeSeparate")
private external fun FilaMaterialInstance_setCullingModeSeparate(instance: NativePointer, colorPassCullingMode: Int, shadowPassCullingMode: Int)

@ExternalSymbolName("FilaMaterialInstance_setDepthCulling")
private external fun FilaMaterialInstance_setDepthCulling(instance: NativePointer, enable: Boolean)

@ExternalSymbolName("FilaMaterialInstance_setDepthFunc")
private external fun FilaMaterialInstance_setDepthFunc(instance: NativePointer, func: Int)

@ExternalSymbolName("FilaMaterialInstance_setDepthWrite")
private external fun FilaMaterialInstance_setDepthWrite(instance: NativePointer, enable: Boolean)

@ExternalSymbolName("FilaMaterialInstance_setDoubleSided")
private external fun FilaMaterialInstance_setDoubleSided(instance: NativePointer, doubleSided: Boolean)

@ExternalSymbolName("FilaMaterialInstance_setFloatParameterArray")
private external fun FilaMaterialInstance_setFloatParameterArray(instance: NativePointer, name: NativePointer, elementSize: Int, v: NativePointer, count: Int)

@ExternalSymbolName("FilaMaterialInstance_setIntParameterArray")
private external fun FilaMaterialInstance_setIntParameterArray(instance: NativePointer, name: NativePointer, elementSize: Int, v: NativePointer, count: Int)

@ExternalSymbolName("FilaMaterialInstance_setMaskThreshold")
private external fun FilaMaterialInstance_setMaskThreshold(instance: NativePointer, threshold: Float)

@ExternalSymbolName("FilaMaterialInstance_setParameterBool")
private external fun FilaMaterialInstance_setParameterBool(instance: NativePointer, name: NativePointer, x: Boolean)

@ExternalSymbolName("FilaMaterialInstance_setParameterBool2")
private external fun FilaMaterialInstance_setParameterBool2(instance: NativePointer, name: NativePointer, x: Boolean, y: Boolean)

@ExternalSymbolName("FilaMaterialInstance_setParameterBool3")
private external fun FilaMaterialInstance_setParameterBool3(instance: NativePointer, name: NativePointer, x: Boolean, y: Boolean, z: Boolean)

@ExternalSymbolName("FilaMaterialInstance_setParameterBool4")
private external fun FilaMaterialInstance_setParameterBool4(instance: NativePointer, name: NativePointer, x: Boolean, y: Boolean, z: Boolean, w: Boolean)

@ExternalSymbolName("FilaMaterialInstance_setParameterFloat")
private external fun FilaMaterialInstance_setParameterFloat(instance: NativePointer, name: NativePointer, x: Float)

@ExternalSymbolName("FilaMaterialInstance_setParameterFloat2")
private external fun FilaMaterialInstance_setParameterFloat2(instance: NativePointer, name: NativePointer, x: Float, y: Float)

@ExternalSymbolName("FilaMaterialInstance_setParameterFloat3")
private external fun FilaMaterialInstance_setParameterFloat3(instance: NativePointer, name: NativePointer, x: Float, y: Float, z: Float)

@ExternalSymbolName("FilaMaterialInstance_setParameterFloat4")
private external fun FilaMaterialInstance_setParameterFloat4(instance: NativePointer, name: NativePointer, x: Float, y: Float, z: Float, w: Float)

@ExternalSymbolName("FilaMaterialInstance_setParameterInt")
private external fun FilaMaterialInstance_setParameterInt(instance: NativePointer, name: NativePointer, x: Int)

@ExternalSymbolName("FilaMaterialInstance_setParameterInt2")
private external fun FilaMaterialInstance_setParameterInt2(instance: NativePointer, name: NativePointer, x: Int, y: Int)

@ExternalSymbolName("FilaMaterialInstance_setParameterInt3")
private external fun FilaMaterialInstance_setParameterInt3(instance: NativePointer, name: NativePointer, x: Int, y: Int, z: Int)

@ExternalSymbolName("FilaMaterialInstance_setParameterInt4")
private external fun FilaMaterialInstance_setParameterInt4(instance: NativePointer, name: NativePointer, x: Int, y: Int, z: Int, w: Int)

@ExternalSymbolName("FilaMaterialInstance_setParameterTexture")
private external fun FilaMaterialInstance_setParameterTexture(instance: NativePointer, name: NativePointer, texture: NativePointer, minFilter: Int, magFilter: Int, wrapS: Int, wrapT: Int, wrapR: Int, anisotropy: Float, compareMode: Int, compareFunc: Int)

@ExternalSymbolName("FilaMaterialInstance_setPolygonOffset")
private external fun FilaMaterialInstance_setPolygonOffset(instance: NativePointer, scale: Float, constant: Float)

@ExternalSymbolName("FilaMaterialInstance_setScissor")
private external fun FilaMaterialInstance_setScissor(instance: NativePointer, left: Int, bottom: Int, width: Int, height: Int)

@ExternalSymbolName("FilaMaterialInstance_setSpecularAntiAliasingThreshold")
private external fun FilaMaterialInstance_setSpecularAntiAliasingThreshold(instance: NativePointer, threshold: Float)

@ExternalSymbolName("FilaMaterialInstance_setSpecularAntiAliasingVariance")
private external fun FilaMaterialInstance_setSpecularAntiAliasingVariance(instance: NativePointer, variance: Float)

@ExternalSymbolName("FilaMaterialInstance_setStencilCompareFunction")
private external fun FilaMaterialInstance_setStencilCompareFunction(instance: NativePointer, func: Int, face: Int)

@ExternalSymbolName("FilaMaterialInstance_setStencilOpDepthFail")
private external fun FilaMaterialInstance_setStencilOpDepthFail(instance: NativePointer, op: Int, face: Int)

@ExternalSymbolName("FilaMaterialInstance_setStencilOpDepthStencilPass")
private external fun FilaMaterialInstance_setStencilOpDepthStencilPass(instance: NativePointer, op: Int, face: Int)

@ExternalSymbolName("FilaMaterialInstance_setStencilOpStencilFail")
private external fun FilaMaterialInstance_setStencilOpStencilFail(instance: NativePointer, op: Int, face: Int)

@ExternalSymbolName("FilaMaterialInstance_setStencilReadMask")
private external fun FilaMaterialInstance_setStencilReadMask(instance: NativePointer, readMask: Int, face: Int)

@ExternalSymbolName("FilaMaterialInstance_setStencilReferenceValue")
private external fun FilaMaterialInstance_setStencilReferenceValue(instance: NativePointer, value: Int, face: Int)

@ExternalSymbolName("FilaMaterialInstance_setStencilWrite")
private external fun FilaMaterialInstance_setStencilWrite(instance: NativePointer, enable: Boolean)

@ExternalSymbolName("FilaMaterialInstance_setStencilWriteMask")
private external fun FilaMaterialInstance_setStencilWriteMask(instance: NativePointer, writeMask: Int, face: Int)

@ExternalSymbolName("FilaMaterialInstance_setTransparencyMode")
private external fun FilaMaterialInstance_setTransparencyMode(instance: NativePointer, mode: Int)

@ExternalSymbolName("FilaMaterialInstance_unsetScissor")
private external fun FilaMaterialInstance_unsetScissor(instance: NativePointer)
