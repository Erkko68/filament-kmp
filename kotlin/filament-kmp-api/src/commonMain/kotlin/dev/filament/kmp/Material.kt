package dev.filament.kmp

/**
 * A Filament Material defines the visual appearance of an object. Materials function as a
 * templates from which {@link MaterialInstance}s can be spawned. Use {@link Builder} to construct
 * a Material object.
 */
expect class Material {
    enum class Shading {
        UNLIT,
        LIT,
        SUBSURFACE,
        CLOTH,
        SPECULAR_GLOSSINESS,
    }

    enum class Interpolation {
        SMOOTH,
        FLAT,
    }

    enum class BlendingMode {
        OPAQUE,
        TRANSPARENT,
        ADD,
        MASKED,
        FADE,
        MULTIPLY,
        SCREEN,
    }

    enum class TransparencyMode {
        DEFAULT,
        TWO_PASSES_ONE_SIDE,
        TWO_PASSES_TWO_SIDES,
    }

    enum class RefractionMode {
        NONE,
        CUBEMAP,
        SCREEN_SPACE,
    }

    enum class RefractionType {
        SOLID,
        THIN,
    }

    enum class ReflectionMode {
        DEFAULT,
        SCREEN_SPACE,
    }

    enum class VertexDomain {
        OBJECT,
        WORLD,
        VIEW,
        DEVICE,
    }

    enum class CullingMode {
        NONE,
        FRONT,
        BACK,
        FRONT_AND_BACK,
    }

    enum class CompilerPriorityQueue {
        CRITICAL,
        HIGH,
        LOW,
    }

    enum class UboBatchingMode {
        DEFAULT,
        DISABLED,
    }

    class Parameter(
        name: String,
        type: Type,
        precision: Precision,
        count: Int,
    ) {
        enum class Type {
            BOOL,
            BOOL2,
            BOOL3,
            BOOL4,
            FLOAT,
            FLOAT2,
            FLOAT3,
            FLOAT4,
            INT,
            INT2,
            INT3,
            INT4,
            UINT,
            UINT2,
            UINT3,
            UINT4,
            MAT3,
            MAT4,
            SAMPLER_2D,
            SAMPLER_2D_ARRAY,
            SAMPLER_CUBEMAP,
            SAMPLER_EXTERNAL,
            SAMPLER_3D,
            SUBPASS_INPUT,
        }

        enum class Precision {
            LOW,
            MEDIUM,
            HIGH,
            DEFAULT,
        }

        val name: String
        val type: Type
        val precision: Precision
        val count: Int
    }

    class Builder {
        enum class ShadowSamplingQuality {
            HARD,
            LOW,
        }

        /**
         * Specifies the material data. The material data is a binary blob produced by
         * libfilamat or by matc.
         *
         * @param buffer  buffer containing material data
         * @param size    size of the material data in bytes
         */
        fun payload(buffer: Any, size: Int): Builder

        fun sphericalHarmonicsBandCount(shBandCount: Int): Builder

        fun shadowSamplingQuality(quality: ShadowSamplingQuality): Builder

        fun uboBatching(mode: UboBatchingMode): Builder

        /**
         * Creates and returns the Material object.
         *
         * @param engine reference to the Engine instance to associate this Material with
         *
         * @return the newly created object
         *
         * @exception IllegalStateException if the material could not be created
         */
        fun build(engine: Engine): Material
    }

    fun compile(priority: CompilerPriorityQueue, variants: Int, handler: Any?, callback: (() -> Unit)?)

    /**
     * Creates a new instance of this material. Material instances should be freed using
     * {@link Engine#destroyMaterialInstance(MaterialInstance)}.
     *
     * @return the new instance
     */
    fun createInstance(): MaterialInstance

    /**
     * Creates a new instance of this material with a specified name. Material instances should be
     * freed using {@link Engine#destroyMaterialInstance(MaterialInstance)}.
     *
     * @param name arbitrary label to associate with the given material instance
     *
     * @return the new instance
     */
    fun createInstance(name: String): MaterialInstance

    /** Returns the material's default instance. */
    fun getDefaultInstance(): MaterialInstance

    /**
     * Returns the name of this material. The material name is used for debugging purposes.
     */
    fun getName(): String

    fun getShading(): Shading

    fun getInterpolation(): Interpolation

    fun getBlendingMode(): BlendingMode

    fun getTransparencyMode(): TransparencyMode

    fun getRefractionMode(): RefractionMode

    fun getRefractionType(): RefractionType

    fun getReflectionMode(): ReflectionMode

    fun getFeatureLevel(): Int

    fun getVertexDomain(): VertexDomain

    fun getCullingMode(): CullingMode

    fun isColorWriteEnabled(): Boolean

    fun isDepthWriteEnabled(): Boolean

    fun isDepthCullingEnabled(): Boolean

    fun isDoubleSided(): Boolean

    fun isAlphaToCoverageEnabled(): Boolean

    fun getMaskThreshold(): Float

    fun getSpecularAntiAliasingVariance(): Float

    fun getSpecularAntiAliasingThreshold(): Float

    fun getRequiredAttributes(): Set<VertexBuffer.VertexAttribute>

    fun getParameterCount(): Int

    fun getParameters(): List<Parameter>

    fun hasParameter(name: String): Boolean

    fun getParameterTransformName(samplerName: String): String

    fun setDefaultParameter(name: String, x: Boolean)

    fun setDefaultParameter(name: String, x: Float)

    fun setDefaultParameter(name: String, x: Int)

    fun setDefaultParameter(name: String, x: Float, y: Float)

    fun setDefaultParameter(name: String, x: Float, y: Float, z: Float)

    fun setDefaultParameter(name: String, x: Float, y: Float, z: Float, w: Float)

    fun setDefaultParameter(name: String, type: Any, x: Float, y: Float, z: Float, w: Float)

    fun getNativeObject(): Long

    internal fun invalidate()
}

