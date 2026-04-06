package dev.filament.kmp

/**
 * A Filament Material defines the visual appearance of an object. Materials function as a
 * templates from which [MaterialInstance]s can be spawned. Use [Builder] to construct
 * a Material object.
 *
 * @see <a href="https://google.github.io/filament/Materials.html">Filament Materials Guide</a>
 */
expect class Material {

    /** Supported shading models */
    enum class Shading {
        UNLIT,
        LIT,
        SUBSURFACE,
        CLOTH,
        SPECULAR_GLOSSINESS
    }

    /** Attribute interpolation types in the fragment shader */
    enum class Interpolation {
        SMOOTH,
        FLAT
    }

    /** Supported blending modes */
    enum class BlendingMode {
        OPAQUE,
        TRANSPARENT,
        ADD,
        MASKED,
        FADE,
        MULTIPLY,
        SCREEN,
    }

    /** How transparent objects are handled */
    enum class TransparencyMode {
        DEFAULT,
        TWO_PASSES_ONE_SIDE,
        TWO_PASSES_TWO_SIDES
    }

    /** Supported refraction modes */
    enum class RefractionMode {
        NONE,
        CUBEMAP,
        SCREEN_SPACE
    }

    /** Supported refraction types */
    enum class RefractionType {
        SOLID,
        THIN
    }

    /** Supported reflection modes */
    enum class ReflectionMode {
        DEFAULT,
        SCREEN_SPACE
    }

    /** Supported types of vertex domains */
    enum class VertexDomain {
        OBJECT,
        WORLD,
        VIEW,
        DEVICE
    }

    /** Face culling Mode */
    enum class CullingMode {
        NONE,
        FRONT,
        BACK,
        FRONT_AND_BACK
    }

    /** Shader compiler priority queue */
    enum class CompilerPriorityQueue {
        CRITICAL,
        HIGH,
        LOW
    }

    /** Defines whether a material instance should use UBO batching or not. */
    enum class UboBatchingMode {
        DEFAULT,
        DISABLED
    }

    class UserVariantFilterBit {
        companion object {
            const val DIRECTIONAL_LIGHTING: Int
            const val DYNAMIC_LIGHTING: Int
            const val SHADOW_RECEIVER: Int
            const val SKINNING: Int
            const val FOG: Int
            const val VSM: Int
            const val SSR: Int
            const val STE: Int
            const val ALL: Int
        }
    }

    class Parameter {
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
            SUBPASS_INPUT
        }

        enum class Precision {
            LOW,
            MEDIUM,
            HIGH,
            DEFAULT
        }

        val name: String
        val type: Type
        val precision: Precision
        val count: Int
    }

    class Builder() {
        enum class ShadowSamplingQuality {
            HARD,
            LOW,
        }

        fun payload(buffer: Buffer, size: Int): Builder
        fun sphericalHarmonicsBandCount(shBandCount: Int): Builder
        fun shadowSamplingQuality(quality: ShadowSamplingQuality): Builder
        fun uboBatching(mode: UboBatchingMode): Builder
        fun build(engine: Engine): Material
    }

    fun compile(priority: CompilerPriorityQueue, variants: Int, handler: Any?, callback: Runnable?)
    fun createInstance(): MaterialInstance
    fun createInstance(name: String): MaterialInstance
    fun getDefaultInstance(): MaterialInstance
    fun getName(): String
    fun getShading(): Shading
    fun getInterpolation(): Interpolation
    fun getBlendingMode(): BlendingMode
    fun getTransparencyMode(): TransparencyMode
    fun getRefractionMode(): RefractionMode
    fun getRefractionType(): RefractionType
    fun getReflectionMode(): ReflectionMode
    fun getFeatureLevel(): Engine.FeatureLevel
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
    fun setDefaultParameter(name: String, x: Boolean, y: Boolean)
    fun setDefaultParameter(name: String, x: Float, y: Float)
    fun setDefaultParameter(name: String, x: Int, y: Int)
    fun setDefaultParameter(name: String, x: Boolean, y: Boolean, z: Boolean)
    fun setDefaultParameter(name: String, x: Float, y: Float, z: Float)
    fun setDefaultParameter(name: String, x: Int, y: Int, z: Int)
    fun setDefaultParameter(name: String, x: Boolean, y: Boolean, z: Boolean, w: Boolean)
    fun setDefaultParameter(name: String, x: Float, y: Float, z: Float, w: Float)
    fun setDefaultParameter(name: String, x: Int, y: Int, z: Int, w: Int)
    fun setDefaultParameter(name: String, type: MaterialInstance.BooleanElement, v: BooleanArray, offset: Int, count: Int)
    fun setDefaultParameter(name: String, type: MaterialInstance.IntElement, v: IntArray, offset: Int, count: Int)
    fun setDefaultParameter(name: String, type: MaterialInstance.FloatElement, v: FloatArray, offset: Int, count: Int)
    fun setDefaultParameter(name: String, type: Colors.RgbType, r: Float, g: Float, b: Float)
    fun setDefaultParameter(name: String, type: Colors.RgbaType, r: Float, g: Float, b: Float, a: Float)
    fun setDefaultParameter(name: String, texture: Texture, sampler: TextureSampler)
}
