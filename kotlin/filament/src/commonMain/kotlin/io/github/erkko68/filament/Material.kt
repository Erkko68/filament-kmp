package io.github.erkko68.filament

/**
 * A Filament Material defines the visual appearance of an object. Materials function as a
 * templates from which [MaterialInstance]s can be spawned.
 */
expect class Material {
    enum class Shading { UNLIT, LIT, SUBSURFACE, CLOTH, SPECULAR_GLOSSINESS }
    enum class Interpolation { SMOOTH, FLAT }
    enum class BlendingMode { OPAQUE, TRANSPARENT, ADD, MASKED, FADE, MULTIPLY, SCREEN }
    enum class TransparencyMode { DEFAULT, TWO_PASSES_ONE_SIDE, TWO_PASSES_TWO_SIDES }
    enum class RefractionMode { NONE, CUBEMAP, SCREEN_SPACE }
    enum class RefractionType { SOLID, THIN }
    enum class ReflectionMode { DEFAULT, SCREEN_SPACE }
    enum class VertexDomain { OBJECT, WORLD, VIEW, DEVICE }
    enum class CullingMode { NONE, FRONT, BACK, FRONT_AND_BACK }
    enum class CompilerPriorityQueue { CRITICAL, HIGH, LOW }
    enum class UboBatchingMode { DEFAULT, DISABLED }
    
    class UserVariantFilterBit {
        companion object {
            val DIRECTIONAL_LIGHTING: Int
            val DYNAMIC_LIGHTING: Int
            val SHADOW_RECEIVER: Int
            val SKINNING: Int
            val FOG: Int
            val VSM: Int
            val SSR: Int
            val STE: Int
            val ALL: Int
        }
    }

    class Parameter(
        name: String,
        type: Type,
        precision: Precision,
        count: Int
    ) {
        val name: String
        val type: Type
        val precision: Precision
        val count: Int

        enum class Type {
            BOOL, BOOL2, BOOL3, BOOL4,
            FLOAT, FLOAT2, FLOAT3, FLOAT4,
            INT, INT2, INT3, INT4,
            UINT, UINT2, UINT3, UINT4,
            MAT3, MAT4,
            SAMPLER_2D, SAMPLER_2D_ARRAY, SAMPLER_CUBEMAP, SAMPLER_EXTERNAL, SAMPLER_3D,
            SUBPASS_INPUT
        }

        enum class Precision { LOW, MEDIUM, HIGH, DEFAULT }
    }

    class Builder() {
        enum class ShadowSamplingQuality { HARD, LOW }
        fun payload(data: ByteArray): Builder
        fun sphericalHarmonicsBandCount(shBandCount: Int): Builder
        fun shadowSamplingQuality(quality: ShadowSamplingQuality): Builder
        fun uboBatching(mode: UboBatchingMode): Builder
        fun build(engine: Engine): Material
    }

    fun compile(priority: CompilerPriorityQueue, variants: Int, callback: (() -> Unit)? = null)
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
    fun getFeatureLevel(): Engine.FeatureLevel
    
    fun getParameterCount(): Int
    fun getParameters(): List<Parameter>
    
    fun getRequiredAttributes(): Set<VertexBuffer.VertexAttribute>
}
