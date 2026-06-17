package io.github.erkko68.filament

/**
 * MaterialInstance allows you to customize the parameters of a Material per-object.
 *
 * Each Material can spawn multiple MaterialInstances with different parameter values
 * (colors, textures, numeric uniforms). Changes to a MaterialInstance only affect
 * renderables using that specific instance, not the Material or other instances.
 *
 * **Creating and destroying instances:**
 * Create instances via [Material.createInstance] and destroy them with [Engine.destroy].
 * You can also duplicate an existing instance with [duplicate].
 *
 * **Setting parameters:**
 * Use [setParameter] overloads to set uniforms (floats, vectors, matrices, textures, colors).
 * Parameter names and types must match those defined in the material. Arrays are also supported.
 *
 * **Rendering state:**
 * Customize per-instance rendering behavior: scissor test, polygon offset, culling mode,
 * depth test, stencil test, color/depth write masks. These override material defaults.
 */
expect class MaterialInstance {
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
         * Creates a new MaterialInstance using another as a template.
         *
         * Useful for creating instances with the same initial parameters as an existing one.
         *
         * @param other A MaterialInstance to copy parameter values from
         * @param name Optional name for the new instance (null to use template's name)
         * @return A new MaterialInstance with copied parameters
         */
        fun duplicate(other: MaterialInstance, name: String? = null): MaterialInstance
    }

    /**
     * Gets the Material this instance is created from.
     * @return The parent Material
     */
    val material: Material

    /**
     * Gets the name of this MaterialInstance.
     * @return Instance name (useful for debugging)
     */
    val name: String

    /**
     * Sets a boolean parameter.
     * @param name Parameter name as defined in the material
     * @param x Boolean value
     */
    fun setParameter(name: String, x: Boolean)

    /**
     * Sets a float parameter.
     * @param name Parameter name as defined in the material
     * @param x Float value
     */
    fun setParameter(name: String, x: Float)

    /**
     * Sets an integer parameter.
     * @param name Parameter name as defined in the material
     * @param x Integer value
     */
    fun setParameter(name: String, x: Int)

    /**
     * Sets a 2-component boolean vector parameter.
     * @param name Parameter name as defined in the material
     * @param x First component
     * @param y Second component
     */
    fun setParameter(name: String, x: Boolean, y: Boolean)

    /**
     * Sets a 2-component float vector parameter.
     * @param name Parameter name as defined in the material
     * @param x First component
     * @param y Second component
     */
    fun setParameter(name: String, x: Float, y: Float)

    /**
     * Sets a 2-component integer vector parameter.
     * @param name Parameter name as defined in the material
     * @param x First component
     * @param y Second component
     */
    fun setParameter(name: String, x: Int, y: Int)

    /**
     * Sets a 3-component boolean vector parameter.
     * @param name Parameter name as defined in the material
     * @param x First component
     * @param y Second component
     * @param z Third component
     */
    fun setParameter(name: String, x: Boolean, y: Boolean, z: Boolean)

    /**
     * Sets a 3-component float vector parameter.
     * @param name Parameter name as defined in the material
     * @param x First component
     * @param y Second component
     * @param z Third component
     */
    fun setParameter(name: String, x: Float, y: Float, z: Float)

    /**
     * Sets a 3-component integer vector parameter.
     * @param name Parameter name as defined in the material
     * @param x First component
     * @param y Second component
     * @param z Third component
     */
    fun setParameter(name: String, x: Int, y: Int, z: Int)

    /**
     * Sets a 4-component boolean vector parameter.
     * @param name Parameter name as defined in the material
     * @param x First component
     * @param y Second component
     * @param z Third component
     * @param w Fourth component
     */
    fun setParameter(name: String, x: Boolean, y: Boolean, z: Boolean, w: Boolean)

    /**
     * Sets a 4-component float vector parameter.
     * @param name Parameter name as defined in the material
     * @param x First component
     * @param y Second component
     * @param z Third component
     * @param w Fourth component
     */
    fun setParameter(name: String, x: Float, y: Float, z: Float, w: Float)

    /**
     * Sets a 4-component integer vector parameter.
     * @param name Parameter name as defined in the material
     * @param x First component
     * @param y Second component
     * @param z Third component
     * @param w Fourth component
     */
    fun setParameter(name: String, x: Int, y: Int, z: Int, w: Int)

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
    fun setParameter(name: String, texture: Texture, sampler: TextureSampler)

    /**
     * Sets a parameter from a boolean array.
     *
     * @param name Parameter name as defined in the material
     * @param type Array element type (BOOL, BOOL2, BOOL3, or BOOL4)
     * @param v Source array
     * @param offset Index into v to start copying from
     * @param count Number of elements to copy
     */
    fun setParameter(name: String, type: BooleanElement, v: BooleanArray, offset: Int, count: Int)

    /**
     * Sets a parameter from an integer array.
     *
     * @param name Parameter name as defined in the material
     * @param type Array element type (INT, INT2, INT3, or INT4)
     * @param v Source array
     * @param offset Index into v to start copying from
     * @param count Number of elements to copy
     */
    fun setParameter(name: String, type: IntElement, v: IntArray, offset: Int, count: Int)

    /**
     * Sets a parameter from a float array.
     *
     * @param name Parameter name as defined in the material
     * @param type Array element type (FLOAT, FLOAT2, FLOAT3, FLOAT4, MAT3, or MAT4)
     * @param v Source array
     * @param offset Index into v to start copying from
     * @param count Number of elements to copy
     */
    fun setParameter(name: String, type: FloatElement, v: FloatArray, offset: Int, count: Int)

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
    fun setParameter(name: String, type: Colors.RgbType, r: Float, g: Float, b: Float)

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
    fun setParameter(name: String, type: Colors.RgbaType, r: Float, g: Float, b: Float, a: Float)

    /**
     * Specifies a scissor box to restrict rendering to a rectangular region.
     *
     * Pixels outside the scissor box are discarded. Useful for HUD elements, minimaps, etc.
     *
     * @param left Left edge of scissor box in pixels
     * @param bottom Bottom edge of scissor box in pixels
     * @param width Width of scissor box in pixels
     * @param height Height of scissor box in pixels
     */
    fun setScissor(left: Int, bottom: Int, width: Int, height: Int)

    /**
     * Disables the scissor box test; rendering is not restricted to any region.
     */
    fun unsetScissor()

    /**
     * Applies a constant and scale offset to the depth value (polygon offset / depth bias).
     *
     * Useful for preventing z-fighting between nearby surfaces. Negative values move
     * geometry closer to the camera.
     *
     * @param scale Scale factor applied to the fragment's slope
     * @param constant Constant offset in depth units
     */
    fun setPolygonOffset(scale: Float, constant: Float)

    /**
     * Gets/sets the alpha mask threshold for masked blending mode.
     *
     * Pixels with alpha < threshold are discarded; >= threshold are opaque.
     * Default: material's mask threshold.
     *
     * @see Material.BlendingMode.MASKED
     */
    var maskThreshold: Float

    /**
     * Gets/sets the specular anti-aliasing variance for this instance.
     *
     * Higher values reduce specular aliasing but may blur highlights. Range: [0, 1].
     * Default: material's variance.
     */
    var specularAntiAliasingVariance: Float

    /**
     * Gets/sets the specular anti-aliasing threshold for this instance.
     *
     * Clamps the amount of anti-aliasing applied to specular highlights. Range: [0, 1].
     * Default: material's threshold.
     */
    var specularAntiAliasingThreshold: Float

    /**
     * Gets/sets whether this instance is double-sided.
     *
     * If true, both front and back faces are rendered. If false, back faces are culled.
     * Default: material's double-sided setting.
     */
    var isDoubleSided: Boolean

    /**
     * Gets/sets the transparency rendering mode for this instance.
     *
     * Controls how transparent pixels are rendered (immediate, multi-pass, etc).
     * Default: material's transparency mode.
     *
     * @see Material.TransparencyMode
     */
    var transparencyMode: Material.TransparencyMode

    /**
     * Gets/sets the culling mode for color pass rendering.
     *
     * Default: material's culling mode.
     *
     * @see Material.CullingMode
     */
    var cullingMode: Material.CullingMode

    /**
     * Sets different culling modes for color and shadow passes.
     *
     * Allows fine-grained control over what's rendered in each pass. For example,
     * you might cull backfaces in the color pass but render everything in shadows.
     *
     * @param colorPassCullingMode Culling mode for color rendering
     * @param shadowPassCullingMode Culling mode for shadow map rendering
     */
    fun setCullingMode(colorPassCullingMode: Material.CullingMode, shadowPassCullingMode: Material.CullingMode)

    /**
     * Gets the culling mode for shadow pass rendering.
     * @return Culling mode used when rendering shadow maps
     */
    val shadowCullingMode: Material.CullingMode

    /**
     * Gets/sets whether this instance writes to the color buffer (enabled by default).
     * @return true if color writes are enabled
     */
    var isColorWriteEnabled: Boolean

    /**
     * Gets/sets whether this instance writes to the depth buffer (enabled by default).
     * @return true if depth writes are enabled
     */
    var isDepthWriteEnabled: Boolean

    /**
     * Gets/sets whether this instance writes to the stencil buffer.
     * Default: false.
     */
    var isStencilWriteEnabled: Boolean

    /**
     * Gets/sets whether this instance performs depth testing (enabled by default).
     * @return true if depth testing is enabled
     */
    var isDepthCullingEnabled: Boolean

    /**
     * Gets/sets the depth comparison function for this instance.
     *
     * Controls how pixels are tested against the depth buffer (less, greater, equal, etc).
     * Default: material's depth function.
     */
    var depthFunc: TextureSampler.CompareFunction

    /**
     * Sets the stencil comparison function for both front and back faces.
     *
     * The comparison function determines when pixels pass the stencil test.
     *
     * @param func Comparison function (e.g., LESS, EQUAL, GREATER)
     * @param face Which face(s) this applies to (FRONT, BACK, or FRONT_AND_BACK)
     */
    fun setStencilCompareFunction(func: TextureSampler.CompareFunction, face: StencilFace)

    /**
     * Sets the stencil comparison function for both front and back faces.
     *
     * @param func Comparison function (e.g., LESS, EQUAL, GREATER)
     */
    fun setStencilCompareFunction(func: TextureSampler.CompareFunction)

    /**
     * Sets the stencil operation when the stencil test fails.
     *
     * @param op Operation to apply when stencil test fails (KEEP, ZERO, REPLACE, etc)
     * @param face Which face(s) this applies to (FRONT, BACK, or FRONT_AND_BACK)
     */
    fun setStencilOpStencilFail(op: StencilOperation, face: StencilFace)

    /**
     * Sets the stencil operation when the stencil test fails for both faces.
     *
     * @param op Operation to apply (KEEP, ZERO, REPLACE, etc)
     */
    fun setStencilOpStencilFail(op: StencilOperation)

    /**
     * Sets the stencil operation when the stencil test passes but depth test fails.
     *
     * @param op Operation to apply when depth test fails (KEEP, ZERO, REPLACE, etc)
     * @param face Which face(s) this applies to (FRONT, BACK, or FRONT_AND_BACK)
     */
    fun setStencilOpDepthFail(op: StencilOperation, face: StencilFace)

    /**
     * Sets the stencil operation when the stencil test passes but depth test fails for both faces.
     *
     * @param op Operation to apply (KEEP, ZERO, REPLACE, etc)
     */
    fun setStencilOpDepthFail(op: StencilOperation)

    /**
     * Sets the stencil operation when both stencil and depth tests pass.
     *
     * @param op Operation to apply when both tests pass (KEEP, ZERO, REPLACE, etc)
     * @param face Which face(s) this applies to (FRONT, BACK, or FRONT_AND_BACK)
     */
    fun setStencilOpDepthStencilPass(op: StencilOperation, face: StencilFace)

    /**
     * Sets the stencil operation when both stencil and depth tests pass for both faces.
     *
     * @param op Operation to apply (KEEP, ZERO, REPLACE, etc)
     */
    fun setStencilOpDepthStencilPass(op: StencilOperation)

    /**
     * Sets the stencil reference value used in comparisons.
     *
     * @param value Reference value [0, 255]
     * @param face Which face(s) this applies to (FRONT, BACK, or FRONT_AND_BACK)
     */
    fun setStencilReferenceValue(value: Int, face: StencilFace)

    /**
     * Sets the stencil reference value for both front and back faces.
     *
     * @param value Reference value [0, 255]
     */
    fun setStencilReferenceValue(value: Int)

    /**
     * Sets the stencil read mask (which bits are compared).
     *
     * @param readMask Bitmask [0, 255]; only masked bits participate in comparison
     * @param face Which face(s) this applies to (FRONT, BACK, or FRONT_AND_BACK)
     */
    fun setStencilReadMask(readMask: Int, face: StencilFace)

    /**
     * Sets the stencil read mask for both front and back faces.
     *
     * @param readMask Bitmask [0, 255]; only masked bits participate in comparison
     */
    fun setStencilReadMask(readMask: Int)

    /**
     * Sets the stencil write mask (which bits can be written).
     *
     * @param writeMask Bitmask [0, 255]; only masked bits can be modified
     * @param face Which face(s) this applies to (FRONT, BACK, or FRONT_AND_BACK)
     */
    fun setStencilWriteMask(writeMask: Int, face: StencilFace)

    /**
     * Sets the stencil write mask for both front and back faces.
     *
     * @param writeMask Bitmask [0, 255]; only masked bits can be modified
     */
    fun setStencilWriteMask(writeMask: Int)
}
