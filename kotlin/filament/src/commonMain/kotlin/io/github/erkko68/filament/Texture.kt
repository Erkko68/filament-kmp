package io.github.erkko68.filament

/**
 * A GPU texture supporting 2D, 3D, cubemap, and mipmap data.
 *
 * Textures are created using [Builder] and destroyed via [Engine.destroy].
 * The Texture class supports:
 * - 2D textures
 * - 3D textures
 * - Cube maps
 * - Mip mapping
 */
expect class Texture {
    /**
     * Builder for creating Texture instances.
     *
     * All builder methods return this Builder for method chaining. Sensible defaults
     * are applied: width=1, height=1, depth=1, levels=1, format=RGBA8.
     */
    class Builder() {
        /**
         * Specifies the width in texels. Does not need to be a power of two.
         * @param width Width in texels (default: 1)
         * @return This Builder
         */
        fun width(width: Int): Builder

        /**
         * Specifies the height in texels. Does not need to be a power of two.
         * @param height Height in texels (default: 1)
         * @return This Builder
         */
        fun height(height: Int): Builder

        /**
         * Specifies the depth in texels for 3D textures or layer count for 2D arrays.
         * Does not need to be a power of two. Values greater than 1 effectively create
         * a 3D texture when used with [Sampler.SAMPLER_3D] or a 2D array with
         * [Sampler.SAMPLER_2D_ARRAY].
         * @param depth Depth in texels (default: 1)
         * @return This Builder
         */
        fun depth(depth: Int): Builder

        /**
         * Specifies the number of mip map levels. Creates a mip-map pyramid where the
         * maximum number of levels is: max(width, height, depth) / 2^MAX_LEVELS = 1
         * @param levels Number of mipmap levels
         * @return This Builder
         */
        fun levels(levels: Int): Builder

        /**
         * Specifies the number of samples for MSAA (Multisample Anti-Aliasing).
         *
         * Calling this implicitly indicates the texture is used as a render target.
         * Should not be used with methods like [setImage] that are semantically conflicting.
         *
         * For array textures, indicates the texture is used for multiview.
         * @param samples Number of samples (default: 1)
         * @return This Builder
         */
        fun samples(samples: Int): Builder

        /**
         * Specifies the type of sampler to use.
         * @param target Sampler type
         * @return This Builder
         */
        fun sampler(target: Sampler): Builder

        /**
         * Specifies the internal format (how texels are stored in memory).
         *
         * The internal format specifies the color components and data type, which may
         * differ from the format specified in [setImage].
         * @param format Format of the texture's texels
         * @return This Builder
         */
        fun format(format: InternalFormat): Builder

        /**
         * Specifies if the texture will be used as a render target attachment.
         *
         * If the texture is potentially rendered into, it may require a different
         * memory layout, which must be known during construction.
         * @param usage Defaults to [Usage.DEFAULT]; use [Usage.COLOR_ATTACHMENT], etc.
         * @return This Builder
         */
        fun usage(usage: Int): Builder

        /**
         * Specifies how a texture's channels map to color components (only if
         * [isTextureSwizzleSupported] returns true).
         * @param r texture channel for red component
         * @param g texture channel for green component
         * @param b texture channel for blue component
         * @param a texture channel for alpha component
         * @return This Builder
         */
        fun swizzle(r: Swizzle, g: Swizzle, b: Swizzle, a: Swizzle): Builder

        /**
         * Import a native platform texture as a Filament texture.
         *
         * Backend-specific texture ID:
         * - OpenGL: GLuint texture ID
         * - Metal: id<MTLTexture> (cast to Long via CFBridgingRetain for ownership transfer)
         *
         * Filament takes ownership of Metal textures and releases them when the
         * Filament texture is destroyed.
         * @param id Backend-specific texture identifier
         * @return This Builder
         */
        fun importTexture(id: Long): Builder

        /**
         * Creates an external texture. Content must be set using [setExternalImage] or [setExternalStream].
         * The sampler can be [Sampler.SAMPLER_EXTERNAL] or [Sampler.SAMPLER_2D] depending
         * on the format. Generally YUV formats require [Sampler.SAMPLER_EXTERNAL].
         * @return This Builder
         */
        fun external(): Builder

        /**
         * Builds and returns the Texture instance.
         * @param engine Engine to associate this texture with
         * @return The newly created Texture
         */
        fun build(engine: Engine): Texture
    }

    /** Sampler type for texture addressing. */
    enum class Sampler { SAMPLER_2D, SAMPLER_2D_ARRAY, SAMPLER_CUBEMAP, SAMPLER_EXTERNAL, SAMPLER_3D, SAMPLER_CUBEMAP_ARRAY }

    /** Internal format specifying how texels are stored in memory. */
    enum class InternalFormat {
        R8, R8_SNORM, R8UI, R8I, STENCIL8,
        R16F, R16UI, R16I,
        RG8, RG8_SNORM, RG8UI, RG8I,
        RGB565, RGB9_E5, RGB5_A1,
        RGBA4,
        DEPTH16,
        RGB8, SRGB8, RGB8_SNORM, RGB8UI, RGB8I,
        DEPTH24,
        R32F, R32UI, R32I,
        RG16F, RG16UI, RG16I,
        R11F_G11F_B10F,
        RGBA8, SRGB8_A8, RGBA8_SNORM,
        UNUSED,
        RGB10_A2, RGBA8UI, RGBA8I,
        DEPTH32F, DEPTH24_STENCIL8, DEPTH32F_STENCIL8,
        RGB16F, RGB16UI, RGB16I,
        RG32F, RG32UI, RG32I,
        RGBA16F, RGBA16UI, RGBA16I,
        RGB32F, RGB32UI, RGB32I,
        RGBA32F, RGBA32UI, RGBA32I,
        EAC_R11, EAC_R11_SIGNED, EAC_RG11, EAC_RG11_SIGNED,
        ETC2_RGB8, ETC2_SRGB8,
        ETC2_RGB8_A1, ETC2_SRGB8_A1,
        ETC2_EAC_RGBA8, ETC2_EAC_SRGBA8,
        DXT1_RGB, DXT1_RGBA, DXT3_RGBA, DXT5_RGBA,
        DXT1_SRGB, DXT1_SRGBA, DXT3_SRGBA, DXT5_SRGBA,
        RGBA_ASTC_4x4, RGBA_ASTC_5x4, RGBA_ASTC_5x5, RGBA_ASTC_6x5, RGBA_ASTC_6x6,
        RGBA_ASTC_8x5, RGBA_ASTC_8x6, RGBA_ASTC_8x8,
        RGBA_ASTC_10x5, RGBA_ASTC_10x6, RGBA_ASTC_10x8, RGBA_ASTC_10x10,
        RGBA_ASTC_12x10, RGBA_ASTC_12x12,
        SRGB8_ALPHA8_ASTC_4x4, SRGB8_ALPHA8_ASTC_5x4, SRGB8_ALPHA8_ASTC_5x5,
        SRGB8_ALPHA8_ASTC_6x5, SRGB8_ALPHA8_ASTC_6x6,
        SRGB8_ALPHA8_ASTC_8x5, SRGB8_ALPHA8_ASTC_8x6, SRGB8_ALPHA8_ASTC_8x8,
        SRGB8_ALPHA8_ASTC_10x5, SRGB8_ALPHA8_ASTC_10x6, SRGB8_ALPHA8_ASTC_10x8,
        SRGB8_ALPHA8_ASTC_10x10, SRGB8_ALPHA8_ASTC_12x10, SRGB8_ALPHA8_ASTC_12x12,
        RED_RGTC1, SIGNED_RED_RGTC1, RED_GREEN_RGTC2, SIGNED_RED_GREEN_RGTC2,
        RGB_BPTC_SIGNED_FLOAT, RGB_BPTC_UNSIGNED_FLOAT, RGBA_BPTC_UNORM, SRGB_ALPHA_BPTC_UNORM
    }

    /** Cubemap faces: +X, -X, +Y, -Y, +Z, -Z. */
    enum class CubemapFace { POSITIVE_X, NEGATIVE_X, POSITIVE_Y, NEGATIVE_Y, POSITIVE_Z, NEGATIVE_Z }

    /** Pixel color format (layout of input data in [setImage]). */
    enum class Format { R, R_INTEGER, RG, RG_INTEGER, RGB, RGB_INTEGER, RGBA, RGBA_INTEGER, UNUSED, DEPTH_COMPONENT, DEPTH_STENCIL, ALPHA }

    /** Pixel data type (component size/signedness in [setImage]). */
    enum class Type { UBYTE, BYTE, USHORT, SHORT, UINT, INT, HALF, FLOAT, COMPRESSED, UINT_10F_11F_11F_REV, USHORT_565 }

    /** Texture channel swizzle (how channels map to RGBA components). */
    enum class Swizzle { SUBSTITUTE_ZERO, SUBSTITUTE_ONE, CHANNEL_0, CHANNEL_1, CHANNEL_2, CHANNEL_3 }

    /** Texture usage flags affecting memory layout and rendering attachment compatibility. */
    class Usage {
        companion object {
            /** Texture is usable as a color attachment. */
            val COLOR_ATTACHMENT: Int
            /** Texture is usable as a depth attachment. */
            val DEPTH_ATTACHMENT: Int
            /** Texture is usable as a stencil attachment. */
            val STENCIL_ATTACHMENT: Int
            /** Texture can have data uploaded via [setImage]. */
            val UPLOADABLE: Int
            /** Texture can be sampled in shaders. */
            val SAMPLEABLE: Int
            /** Texture is usable as a subpass input. */
            val SUBPASS_INPUT: Int
            /** Texture is usable as a blit source. */
            val BLIT_SRC: Int
            /** Texture is usable as a blit destination. */
            val BLIT_DST: Int
            /** Texture is protected (secure content). */
            val PROTECTED: Int
            /** Texture can have mipmaps generated via [generateMipmaps]. */
            val GEN_MIPMAPPABLE: Int
            /** Default usage (rendering and sampling). */
            val DEFAULT: Int
        }
    }

    /**
     * Describes pixel buffer data for uploading to a texture via [setImage].
     *
     * The callback is invoked after the driver has consumed the data, allowing
     * safe deallocation or reuse of the storage.
     *
     * @param storage Client-side byte buffer containing pixel data
     * @param sizeInBytes Total size of buffer in bytes
     * @param format Pixel color format (data layout)
     * @param type Pixel data type (component size/signedness)
     * @param alignment Alignment in bytes (default: 1)
     * @param left Left offset in pixels (default: 0)
     * @param top Top offset in pixels (default: 0)
     * @param stride Stride in pixels; 0 means tightly packed (default: 0)
     * @param callback Optional callback invoked when data is consumed (default: null)
     */
    class PixelBufferDescriptor(
        storage: ByteArray,
        sizeInBytes: Int,
        format: Format,
        type: Type,
        alignment: Int = 1,
        left: Int = 0,
        top: Int = 0,
        stride: Int = 0,
        callback: (() -> Unit)? = null
    ) {
        val storage: ByteArray
        val sizeInBytes: Int
        val format: Format
        val type: Type
        val alignment: Int
        val left: Int
        val top: Int
        val stride: Int
        val callback: (() -> Unit)?
    }

    /**
     * Returns the width of a texture level in texels, clamped to 1.
     *
     * For external textures ([Sampler.SAMPLER_EXTERNAL]), the dimension is unknown
     * and returns whatever was set in [Builder.width].
     * @param level Texture level (default: 0)
     * @return Width in texels
     */
    fun getWidth(level: Int = 0): Int

    /**
     * Returns the height of a texture level in texels, clamped to 1.
     *
     * For external textures ([Sampler.SAMPLER_EXTERNAL]), the dimension is unknown
     * and returns whatever was set in [Builder.height].
     * @param level Texture level (default: 0)
     * @return Height in texels
     */
    fun getHeight(level: Int = 0): Int

    /**
     * Returns the depth of a texture level in texels, clamped to 1.
     *
     * For external textures ([Sampler.SAMPLER_EXTERNAL]), the dimension is unknown
     * and returns whatever was set in [Builder.depth].
     * @param level Texture level (default: 0)
     * @return Depth in texels
     */
    fun getDepth(level: Int = 0): Int

    /**
     * Returns the maximum number of levels this texture can have.
     *
     * For external textures ([Sampler.SAMPLER_EXTERNAL]), the dimension is unknown
     * and returns whatever was set in [Builder.levels].
     * @return Number of mipmap levels
     */
    fun getLevels(): Int

    /**
     * Returns this texture's sampler as set by [Builder.sampler].
     * @return Sampler type
     */
    fun getTarget(): Sampler

    /**
     * Returns this texture's internal format as set by [Builder.format].
     * @return Internal format
     */
    fun getFormat(): InternalFormat

    /**
     * Updates a 2D texture level with image data from a buffer.
     *
     * The descriptor's callback is invoked when the driver has consumed the data.
     *
     * @param engine Engine this texture is associated with
     * @param level Mipmap level to update (must be < [getLevels])
     * @param descriptor Pixel buffer containing the image data
     */
    fun setImage(engine: Engine, level: Int, descriptor: PixelBufferDescriptor)

    /**
     * Updates a rectangular sub-region of a 2D texture level.
     *
     * The descriptor's callback is invoked when the driver has consumed the data.
     *
     * @param engine Engine this texture is associated with
     * @param level Mipmap level to update (must be < [getLevels])
     * @param xoffset Left offset in pixels
     * @param yoffset Bottom offset in pixels
     * @param width Width of sub-region in pixels
     * @param height Height of sub-region in pixels
     * @param descriptor Pixel buffer containing the image data
     */
    fun setImage(engine: Engine, level: Int, xoffset: Int, yoffset: Int, width: Int, height: Int, descriptor: PixelBufferDescriptor)

    /**
     * Updates a sub-region of a 3D texture or 2D texture array. Cubemaps are treated
     * like a 2D array of six layers.
     *
     * The descriptor's callback is invoked when the driver has consumed the data.
     *
     * @param engine Engine this texture is associated with
     * @param level Mipmap level to update (must be < [getLevels])
     * @param xoffset Left offset in pixels
     * @param yoffset Bottom offset in pixels
     * @param zoffset Depth offset (layer for arrays, face for cubemaps)
     * @param width Width of sub-region in pixels
     * @param height Height of sub-region in pixels
     * @param depth Depth of sub-region in layers/faces
     * @param descriptor Pixel buffer containing the image data
     */
    fun setImage(engine: Engine, level: Int, xoffset: Int, yoffset: Int, zoffset: Int, width: Int, height: Int, depth: Int, descriptor: PixelBufferDescriptor)

    /**
     * Associates an external stream with this texture. Typically the external stream
     * is OS-specific and can be a video or camera stream.
     *
     * There are many restrictions when using an external stream:
     * - Only level 0 (lod 0) can be specified
     * - Only nearest or linear filtering is supported
     * - Size and format are defined by the external stream
     * - Only CLAMP_TO_EDGE wrap mode is supported
     *
     * This texture must use [Sampler.SAMPLER_EXTERNAL] for this to have an effect.
     *
     * @param engine Engine this texture is associated with
     * @param stream A Stream object, or null to clear
     *
     * @see Stream
     */
    fun setExternalStream(engine: Engine, stream: Stream)

    /**
     * Generates all mipmap levels automatically.
     *
     * Requires the texture to have a color-renderable format and usage set to
     * BLIT_SRC | BLIT_DST. If unspecified, usage bits are set automatically.
     *
     * This texture must not use [Sampler.SAMPLER_3D].
     *
     * @param engine Engine this texture is associated with
     */
    fun generateMipmaps(engine: Engine)

    companion object {
        /**
         * Queries whether a backend supports a particular format.
         * @param engine Engine to query
         * @param format Format to check
         * @return true if the format is supported
         */
        fun isTextureFormatSupported(engine: Engine, format: InternalFormat): Boolean

        /**
         * Queries whether a backend supports mipmapping of a particular format.
         * @param engine Engine to query
         * @param format Format to check
         * @return true if the format supports mipmapping
         */
        fun isTextureFormatMipmappable(engine: Engine, format: InternalFormat): Boolean

        /**
         * Queries whether the backend supports texture swizzling.
         * @param engine Engine to query
         * @return true if texture swizzling is supported
         */
        fun isTextureSwizzleSupported(engine: Engine): Boolean

        /**
         * Validates whether a combination of internal format, pixel format, and pixel
         * data type is valid.
         * @param internalFormat Internal storage format
         * @param pixelDataFormat Pixel color format
         * @param pixelDataType Pixel data type
         * @return true if the combination is valid
         */
        fun validatePixelFormatAndType(internalFormat: InternalFormat, pixelDataFormat: Format, pixelDataType: Type): Boolean

        /**
         * Returns the maximum size in texels of a texture of the given type.
         * Guarantees at least 2048 for 2D textures, 256 for 3D textures.
         * @param engine Engine to query
         * @param type Sampler type
         * @return Maximum size in texels
         */
        fun getMaxTextureSize(engine: Engine, type: Sampler): Int

        /**
         * Returns the maximum number of layers supported by texture arrays.
         * Guarantees at least 256.
         * @param engine Engine to query
         * @return Maximum layer count
         */
        fun getMaxArrayTextureLayers(engine: Engine): Int

        /**
         * Computes the required buffer size for pixel data given format, type, stride,
         * and alignment parameters.
         * @param format Pixel color format
         * @param type Pixel data type
         * @param stride Stride in pixels (0 = tightly packed)
         * @param height Height in pixels
         * @param alignment Alignment in bytes
         * @return Required buffer size in bytes
         */
        fun computeDataSize(format: Format, type: Type, stride: Int, height: Int, alignment: Int): Int
    }
}
