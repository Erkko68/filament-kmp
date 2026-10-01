package io.github.erkko68.filament

import io.github.erkko68.filament.capi.*
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
 * also supported via setParameter(name, element, array, offset, count) variants.
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
     * Element types for unsigned integer parameter arrays, passed as [IntArray] bit patterns.
     *
     * - UINT: Single 32-bit unsigned integer
     * - UINT2/UINT3/UINT4: 2, 3, or 4 component unsigned integer vectors
     */
    enum class UIntElement { UINT, UINT2, UINT3, UINT4 }
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
     * - INCR: Increment and clamp to max
     * - INCR_WRAP: Increment and wrap to 0
     * - DECR: Decrement and clamp to 0
     * - DECR_WRAP: Decrement and wrap to max
     * - INVERT: Bitwise invert stencil value
     */
    enum class StencilOperation { KEEP, ZERO, REPLACE, INCR, INCR_WRAP, DECR, DECR_WRAP, INVERT }
    /**
     * Which face(s) the stencil operation applies to.
     *
     * - FRONT: Front-facing primitives only
     * - BACK: Back-facing primitives only
     * - FRONT_AND_BACK: Both front and back faces
     */
    enum class StencilFace(@InternalFilamentApi val value: Int) { FRONT(0x1), BACK(0x2), FRONT_AND_BACK(0x3) }

    companion object {
        /**
         * Creates a new MaterialInstance using another MaterialInstance as a template for initialization.
         * The new MaterialInstance is an instance of the same Material of the template instance and
         * must be destroyed just like any other MaterialInstance.
         *
         * @param other A MaterialInstance to use as a template for initializing a new instance.
         * @param name A name for the new MaterialInstance or null to use the template's name.
         * @return A new MaterialInstance.
         */
        fun duplicate(other: MaterialInstance, name: String? = null): MaterialInstance =
            MaterialInstance(name.useCString { FilaMaterialInstance_duplicate(other.nativeHandle, it) })
    }

    /** The Material associated with this instance. */
    val material: Material get() = Material(FilaMaterialInstance_getMaterial(nativeHandle))
    /** The name associated with this instance. */
    val name: String get() = stringFromInterop(FilaMaterialInstance_getName(nativeHandle)) ?: ""

    /**
     * Sets a boolean parameter.
     * @param name Parameter name as defined in the material
     * @param x Boolean value
     */
    fun setParameter(name: String, x: Boolean) { name.useCString { FilaMaterialInstance_setParameter_bool(nativeHandle, it, x) } }
    /**
     * Sets a float parameter.
     * @param name Parameter name as defined in the material
     * @param x Float value
     */
    fun setParameter(name: String, x: Float) { name.useCString { FilaMaterialInstance_setParameter_float(nativeHandle, it, x) } }
    /**
     * Sets an integer parameter.
     * @param name Parameter name as defined in the material
     * @param x Integer value
     */
    fun setParameter(name: String, x: Int) { name.useCString { FilaMaterialInstance_setParameter_int32_t(nativeHandle, it, x) } }
    /**
     * Sets an unsigned integer parameter.
     * @param name Parameter name as defined in the material
     * @param x Unsigned integer value
     */
    fun setParameter(name: String, x: UInt) { name.useCString { FilaMaterialInstance_setParameter_uint32_t(nativeHandle, it, x.toInt()) } }
    /** Sets a 2-component boolean vector parameter. */
    fun setParameter(name: String, x: Boolean, y: Boolean) = setParameter(name, BooleanElement.BOOL2, booleanArrayOf(x, y), 0, 1)
    /** Sets a 2-component float vector parameter. */
    fun setParameter(name: String, x: Float, y: Float) = setParameter(name, FloatElement.FLOAT2, floatArrayOf(x, y), 0, 1)
    /** Sets a 2-component integer vector parameter. */
    fun setParameter(name: String, x: Int, y: Int) = setParameter(name, IntElement.INT2, intArrayOf(x, y), 0, 1)
    /** Sets a 2-component unsigned integer vector parameter. */
    fun setParameter(name: String, x: UInt, y: UInt) = setParameter(name, UIntElement.UINT2, intArrayOf(x.toInt(), y.toInt()), 0, 1)
    /** Sets a 3-component boolean vector parameter. */
    fun setParameter(name: String, x: Boolean, y: Boolean, z: Boolean) = setParameter(name, BooleanElement.BOOL3, booleanArrayOf(x, y, z), 0, 1)
    /** Sets a 3-component float vector parameter. */
    fun setParameter(name: String, x: Float, y: Float, z: Float) = setParameter(name, FloatElement.FLOAT3, floatArrayOf(x, y, z), 0, 1)
    /** Sets a 3-component integer vector parameter. */
    fun setParameter(name: String, x: Int, y: Int, z: Int) = setParameter(name, IntElement.INT3, intArrayOf(x, y, z), 0, 1)
    /** Sets a 3-component unsigned integer vector parameter. */
    fun setParameter(name: String, x: UInt, y: UInt, z: UInt) =
        setParameter(name, UIntElement.UINT3, intArrayOf(x.toInt(), y.toInt(), z.toInt()), 0, 1)
    /** Sets a 4-component boolean vector parameter. */
    fun setParameter(name: String, x: Boolean, y: Boolean, z: Boolean, w: Boolean) = setParameter(name, BooleanElement.BOOL4, booleanArrayOf(x, y, z, w), 0, 1)
    /** Sets a 4-component float vector parameter. */
    fun setParameter(name: String, x: Float, y: Float, z: Float, w: Float) = setParameter(name, FloatElement.FLOAT4, floatArrayOf(x, y, z, w), 0, 1)
    /** Sets a 4-component integer vector parameter. */
    fun setParameter(name: String, x: Int, y: Int, z: Int, w: Int) = setParameter(name, IntElement.INT4, intArrayOf(x, y, z, w), 0, 1)
    /** Sets a 4-component unsigned integer vector parameter. */
    fun setParameter(name: String, x: UInt, y: UInt, z: UInt, w: UInt) =
        setParameter(name, UIntElement.UINT4, intArrayOf(x.toInt(), y.toInt(), z.toInt(), w.toInt()), 0, 1)

    /**
     * Sets a texture parameter with sampler configuration.
     *
     * Note: Depth textures cannot be sampled with linear filtering unless comparison
     * mode is set to COMPARE_TO_TEXTURE.
     *
     * @param name Parameter name as defined in the material
     * @param texture Texture to bind, or null
     * @param sampler Sampler configuration (filtering, wrapping, comparison function)
     */
    fun setParameter(name: String, texture: Texture?, sampler: TextureSampler) {
        sampler.useNative { s -> name.useCString { FilaMaterialInstance_setParameter_Texture_TextureSampler(nativeHandle, it, texture?.nativeHandle ?: NullPointer, s) } }
    }

    /**
     * Sets a parameter from a boolean array.
     *
     * @param name Parameter name as defined in the material
     * @param element Array element type (BOOL, BOOL2, BOOL3, or BOOL4)
     * @param v Source array, flattened
     * @param offset Index into v to start copying from
     * @param count Number of elements to copy
     */
    fun setParameter(name: String, element: BooleanElement, v: BooleanArray, offset: Int, count: Int) {
        // C bool is one byte.
        val bools = ByteArray(v.size - offset) { if (v[offset + it]) 1 else 0 }
        bools.usePinned { p ->
            name.useCString {
                when (element) {
                    BooleanElement.BOOL -> FilaMaterialInstance_setParameter_bool_size_t(nativeHandle, it, p, count)
                    BooleanElement.BOOL2 -> FilaMaterialInstance_setParameter_bool2_size_t(nativeHandle, it, p, count)
                    BooleanElement.BOOL3 -> FilaMaterialInstance_setParameter_bool3_size_t(nativeHandle, it, p, count)
                    BooleanElement.BOOL4 -> FilaMaterialInstance_setParameter_bool4_size_t(nativeHandle, it, p, count)
                }
            }
        }
    }
    /**
     * Sets a parameter from an integer array.
     *
     * @param name Parameter name as defined in the material
     * @param element Array element type (INT, INT2, INT3, or INT4)
     * @param v Source array, flattened
     * @param offset Index into v to start copying from
     * @param count Number of elements to copy
     */
    fun setParameter(name: String, element: IntElement, v: IntArray, offset: Int, count: Int) {
        v.copyOfRange(offset, v.size).usePinned { p ->
            name.useCString {
                when (element) {
                    IntElement.INT -> FilaMaterialInstance_setParameter_int32_t_size_t(nativeHandle, it, p, count)
                    IntElement.INT2 -> FilaMaterialInstance_setParameter_int2_size_t(nativeHandle, it, p, count)
                    IntElement.INT3 -> FilaMaterialInstance_setParameter_int3_size_t(nativeHandle, it, p, count)
                    IntElement.INT4 -> FilaMaterialInstance_setParameter_int4_size_t(nativeHandle, it, p, count)
                }
            }
        }
    }
    /**
     * Sets a parameter from an unsigned integer array.
     *
     * @param name Parameter name as defined in the material
     * @param element Array element type (UINT, UINT2, UINT3, or UINT4)
     * @param v Source array of bit patterns, flattened
     * @param offset Index into v to start copying from
     * @param count Number of elements to copy
     */
    fun setParameter(name: String, element: UIntElement, v: IntArray, offset: Int, count: Int) {
        v.copyOfRange(offset, v.size).usePinned { p ->
            name.useCString {
                when (element) {
                    UIntElement.UINT -> FilaMaterialInstance_setParameter_uint32_t_size_t(nativeHandle, it, p, count)
                    UIntElement.UINT2 -> FilaMaterialInstance_setParameter_uint2_size_t(nativeHandle, it, p, count)
                    UIntElement.UINT3 -> FilaMaterialInstance_setParameter_uint3_size_t(nativeHandle, it, p, count)
                    UIntElement.UINT4 -> FilaMaterialInstance_setParameter_uint4_size_t(nativeHandle, it, p, count)
                }
            }
        }
    }
    /**
     * Sets a parameter from a float array.
     *
     * @param name Parameter name as defined in the material
     * @param element Array element type (FLOAT, FLOAT2, FLOAT3, FLOAT4, MAT3, or MAT4)
     * @param v Source array, flattened (matrices column-major)
     * @param offset Index into v to start copying from
     * @param count Number of elements to copy
     */
    fun setParameter(name: String, element: FloatElement, v: FloatArray, offset: Int, count: Int) {
        v.copyOfRange(offset, v.size).usePinned { p ->
            name.useCString {
                when (element) {
                    FloatElement.FLOAT -> FilaMaterialInstance_setParameter_float_size_t(nativeHandle, it, p, count)
                    FloatElement.FLOAT2 -> FilaMaterialInstance_setParameter_float2_size_t(nativeHandle, it, p, count)
                    FloatElement.FLOAT3 -> FilaMaterialInstance_setParameter_float3_size_t(nativeHandle, it, p, count)
                    FloatElement.FLOAT4 -> FilaMaterialInstance_setParameter_float4_size_t(nativeHandle, it, p, count)
                    FloatElement.MAT3 -> FilaMaterialInstance_setParameter_mat3f_size_t(nativeHandle, it, p, count)
                    FloatElement.MAT4 -> FilaMaterialInstance_setParameter_mat4f_size_t(nativeHandle, it, p, count)
                }
            }
        }
    }

    /**
     * Sets an RGB color parameter, converted to linear from [type]'s space.
     *
     * @param name Parameter name as defined in the material
     * @param type Whether color is in linear or sRGB space
     * @param r Red channel [0, 1]
     * @param g Green channel [0, 1]
     * @param b Blue channel [0, 1]
     */
    fun setParameter(name: String, type: RgbType, r: Float, g: Float, b: Float) {
        floatArrayOf(r, g, b).usePinned { c -> name.useCString { FilaMaterialInstance_setParameter_RgbType_float3(nativeHandle, it, type.ordinal, c) } }
    }
    /**
     * Sets an RGBA color parameter, converted to linear from [type]'s space.
     *
     * @param name Parameter name as defined in the material
     * @param type Whether color is in linear or sRGB space, and whether alpha is premultiplied
     * @param r Red channel [0, 1]
     * @param g Green channel [0, 1]
     * @param b Blue channel [0, 1]
     * @param a Alpha channel [0, 1]
     */
    fun setParameter(name: String, type: RgbaType, r: Float, g: Float, b: Float, a: Float) {
        floatArrayOf(r, g, b, a).usePinned { c -> name.useCString { FilaMaterialInstance_setParameter_RgbaType_float4(nativeHandle, it, type.ordinal, c) } }
    }

    /**
     * Gets the value of a float parameter.
     *
     * @param name Parameter name as defined in the material
     * @param element Parameter type (FLOAT, FLOAT2, FLOAT3, FLOAT4, MAT3, or MAT4)
     * @return The value, flattened (matrices column-major)
     */
    fun getParameter(name: String, element: FloatElement): FloatArray = name.useCString {
        when (element) {
            FloatElement.FLOAT -> floatArrayOf(FilaMaterialInstance_getParameter_float(nativeHandle, it))
            FloatElement.FLOAT2 -> FloatArray(2).apply { usePinned { o -> FilaMaterialInstance_getParameter_float2(nativeHandle, it, o) } }
            FloatElement.FLOAT3 -> FloatArray(3).apply { usePinned { o -> FilaMaterialInstance_getParameter_float3(nativeHandle, it, o) } }
            FloatElement.FLOAT4 -> FloatArray(4).apply { usePinned { o -> FilaMaterialInstance_getParameter_float4(nativeHandle, it, o) } }
            FloatElement.MAT3 -> FloatArray(9).apply { usePinned { o -> FilaMaterialInstance_getParameter_mat3f(nativeHandle, it, o) } }
            FloatElement.MAT4 -> FloatArray(16).apply { usePinned { o -> FilaMaterialInstance_getParameter_mat4f(nativeHandle, it, o) } }
        }
    }
    /**
     * Gets the value of an integer parameter.
     *
     * @param name Parameter name as defined in the material
     * @param element Parameter type (INT, INT2, INT3, or INT4)
     * @return The value's components
     */
    fun getParameter(name: String, element: IntElement): IntArray = name.useCString {
        when (element) {
            IntElement.INT -> intArrayOf(FilaMaterialInstance_getParameter_int32_t(nativeHandle, it))
            IntElement.INT2 -> IntArray(2).apply { usePinned { o -> FilaMaterialInstance_getParameter_int2(nativeHandle, it, o) } }
            IntElement.INT3 -> IntArray(3).apply { usePinned { o -> FilaMaterialInstance_getParameter_int3(nativeHandle, it, o) } }
            IntElement.INT4 -> IntArray(4).apply { usePinned { o -> FilaMaterialInstance_getParameter_int4(nativeHandle, it, o) } }
        }
    }
    /**
     * Gets the value of an unsigned integer parameter.
     *
     * @param name Parameter name as defined in the material
     * @param element Parameter type (UINT, UINT2, UINT3, or UINT4)
     * @return The value's components, as bit patterns
     */
    fun getParameter(name: String, element: UIntElement): IntArray = name.useCString {
        when (element) {
            UIntElement.UINT -> intArrayOf(FilaMaterialInstance_getParameter_uint32_t(nativeHandle, it))
            UIntElement.UINT2 -> IntArray(2).apply { usePinned { o -> FilaMaterialInstance_getParameter_uint2(nativeHandle, it, o) } }
            UIntElement.UINT3 -> IntArray(3).apply { usePinned { o -> FilaMaterialInstance_getParameter_uint3(nativeHandle, it, o) } }
            UIntElement.UINT4 -> IntArray(4).apply { usePinned { o -> FilaMaterialInstance_getParameter_uint4(nativeHandle, it, o) } }
        }
    }

    /**
     * Sets the value of a specialization constant, overriding the Material's. Panics if the constant doesn't
     * exist or has another type. Compiles new programs when the value changes, so prefer
     * [Material.Builder.constant] where the value is known up front.
     *
     * @param name Constant name as defined in the material
     * @param value Value of the constant
     */
    fun setConstant(name: String, value: Int) { name.useCString { FilaMaterialInstance_setConstant_int32_t(nativeHandle, it, value) } }
    /** @see setConstant */
    fun setConstant(name: String, value: Float) { name.useCString { FilaMaterialInstance_setConstant_float(nativeHandle, it, value) } }
    /** @see setConstant */
    fun setConstant(name: String, value: Boolean) { name.useCString { FilaMaterialInstance_setConstant_bool(nativeHandle, it, value) } }
    /**
     * Returns the boolean value of a specialization constant.
     * @param name Constant name as defined in the material
     */
    fun getConstantBoolean(name: String): Boolean = name.useCString { FilaMaterialInstance_getConstant_bool(nativeHandle, it) }
    /**
     * Returns the float value of a specialization constant.
     * @param name Constant name as defined in the material
     */
    fun getConstantFloat(name: String): Float = name.useCString { FilaMaterialInstance_getConstant_float(nativeHandle, it) }
    /**
     * Returns the integer value of a specialization constant.
     * @param name Constant name as defined in the material
     */
    fun getConstantInt(name: String): Int = name.useCString { FilaMaterialInstance_getConstant_int32_t(nativeHandle, it) }

    /**
     * Asynchronously ensures that a subset of this MaterialInstance's variants are compiled, taking its
     * specialization constants into account.
     *
     * @param priority Which priority queue to use (CRITICAL, HIGH, or LOW).
     * @param variants Variants to compile (a mask of [UserVariantFilterBit] values).
     * @param callback Optional callback invoked on the main thread with this instance when compilation completes.
     *
     * @see Material.compile
     */
    fun compile(priority: Material.CompilerPriorityQueue, variants: Int = UserVariantFilterBit.ALL, callback: ((MaterialInstance) -> Unit)? = null) {
        val userData = if (callback != null) Callbacks.register(once = true) { _ -> callback(this) } else NullPointer
        FilaMaterialInstance_compile_UserVariantFilterMask_CallbackHandler_Invocable(
            nativeHandle, priority.ordinal, variants, NullPointer, if (callback != null) Callbacks.argUser else NullPointer, userData,
        )
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
     * Gets/sets the face culling mode, for both the color and shadow passes.
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
        FilaMaterialInstance_setCullingMode_CullingMode(nativeHandle, colorPassCullingMode.ordinal, shadowPassCullingMode.ordinal)
    }

    /** The face culling mode for the shadow passes. */
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
    var depthFunc: TextureSampler.CompareFunc
        get() = TextureSampler.CompareFunc.entries[FilaMaterialInstance_getDepthFunc(nativeHandle)]
        set(value) { FilaMaterialInstance_setDepthFunc(nativeHandle, value.ordinal) }

    /**
     * Sets the stencil comparison function (default is A, always).
     *
     * It's possible to set separate stencil comparison functions; one for front-facing polygons,
     * and one for back-facing polygons. The face parameter determines the comparison function(s)
     * updated by this call.
     *
     * @param func Comparison function
     * @param face Which face(s) this applies to
     */
    fun setStencilCompareFunction(func: TextureSampler.CompareFunc, face: StencilFace = StencilFace.FRONT_AND_BACK) {
        FilaMaterialInstance_setStencilCompareFunction(nativeHandle, func.ordinal, face.value)
    }
    /**
     * Sets the stencil fail operation (default is KEEP).
     *
     * The stencil fail operation is performed to update values in the stencil buffer when the
     * stencil test fails.
     *
     * @param op Operation to apply
     * @param face Which face(s) this applies to
     */
    fun setStencilOpStencilFail(op: StencilOperation, face: StencilFace = StencilFace.FRONT_AND_BACK) {
        FilaMaterialInstance_setStencilOpStencilFail(nativeHandle, op.ordinal, face.value)
    }
    /**
     * Sets the depth fail operation (default is KEEP).
     *
     * The depth fail operation is performed to update values in the stencil buffer when the depth
     * test fails.
     *
     * @param op Operation to apply
     * @param face Which face(s) this applies to
     */
    fun setStencilOpDepthFail(op: StencilOperation, face: StencilFace = StencilFace.FRONT_AND_BACK) {
        FilaMaterialInstance_setStencilOpDepthFail(nativeHandle, op.ordinal, face.value)
    }
    /**
     * Sets the depth-stencil pass operation (default is KEEP).
     *
     * The depth-stencil pass operation is performed to update values in the stencil buffer when
     * both the stencil test and depth test pass.
     *
     * @param op Operation to apply
     * @param face Which face(s) this applies to
     */
    fun setStencilOpDepthStencilPass(op: StencilOperation, face: StencilFace = StencilFace.FRONT_AND_BACK) {
        FilaMaterialInstance_setStencilOpDepthStencilPass(nativeHandle, op.ordinal, face.value)
    }
    /**
     * Sets the stencil reference value (default is 0).
     *
     * @param value Reference value [0, 255]
     * @param face Which face(s) this applies to
     */
    fun setStencilReferenceValue(value: Int, face: StencilFace = StencilFace.FRONT_AND_BACK) {
        FilaMaterialInstance_setStencilReferenceValue(nativeHandle, value, face.value)
    }
    /**
     * Sets the stencil read mask (default is 0xFF).
     *
     * @param readMask Bitmask [0, 255]; only masked bits participate in comparison
     * @param face Which face(s) this applies to
     */
    fun setStencilReadMask(readMask: Int, face: StencilFace = StencilFace.FRONT_AND_BACK) {
        FilaMaterialInstance_setStencilReadMask(nativeHandle, readMask, face.value)
    }
    /**
     * Sets the stencil write mask (default is 0xFF).
     *
     * @param writeMask Bitmask [0, 255]; only masked bits can be modified
     * @param face Which face(s) this applies to
     */
    fun setStencilWriteMask(writeMask: Int, face: StencilFace = StencilFace.FRONT_AND_BACK) {
        FilaMaterialInstance_setStencilWriteMask(nativeHandle, writeMask, face.value)
    }

    /**
     * Uploads this instance's pending parameter changes. The engine does this for every instance it renders;
     * call it for instances only used outside of rendering, e.g. by compute.
     *
     * @param engine Engine this instance belongs to
     */
    fun commit(engine: Engine) { FilaMaterialInstance_commit(nativeHandle, engine.nativeHandle) }
}
