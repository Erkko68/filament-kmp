package dev.filament.kmp.filamat

import dev.filament.kmp.VertexBuffer.VertexAttribute

expect class MaterialBuilder() {
    enum class Shading {
        UNLIT,
        LIT,
        SUBSURFACE,
        CLOTH,
        SPECULAR_GLOSSINESS
    }

    enum class Interpolation {
        SMOOTH,
        FLAT
    }

    enum class UniformType {
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
        MAT4
    }

    enum class SamplerType {
        SAMPLER_2D,
        SAMPLER_2D_ARRAY,
        SAMPLER_CUBEMAP,
        SAMPLER_EXTERNAL,
        SAMPLER_3D
    }

    enum class SamplerFormat {
        INT,
        UINT,
        FLOAT,
        SHADOW
    }

    enum class ParameterPrecision {
        LOW,
        MEDIUM,
        HIGH,
        DEFAULT
    }

    enum class Variable {
        CUSTOM0,
        CUSTOM1,
        CUSTOM2,
        CUSTOM3
    }

    enum class BlendingMode {
        OPAQUE,
        TRANSPARENT,
        ADD,
        MASKED,
        FADE,
        MULTIPLY,
        SCREEN
    }

    enum class VertexDomain {
        OBJECT,
        WORLD,
        VIEW,
        DEVICE
    }

    enum class CullingMode {
        NONE,
        FRONT,
        BACK,
        FRONT_AND_BACK
    }

    enum class TransparencyMode {
        DEFAULT,
        TWO_PASSES_ONE_SIDE,
        TWO_PASSES_TWO_SIDES
    }

    enum class MaterialDomain {
        SURFACE,
        POST_PROCESS
    }

    enum class SpecularAmbientOcclusion {
        NONE,
        SIMPLE,
        BENT_NORMALS
    }

    enum class RefractionMode {
        NONE,
        CUBEMAP,
        SCREEN_SPACE
    }

    enum class ReflectionMode {
        DEFAULT,
        SCREEN_SPACE
    }

    enum class RefractionType {
        SOLID,
        THIN
    }

    enum class Platform {
        DESKTOP,
        MOBILE,
        ALL
    }

    enum class TargetApi {
        OPENGL,
        VULKAN,
        METAL,
        WEBGPU,
        ALL
    }

    enum class Optimization {
        NONE,
        PREPROCESSOR,
        SIZE,
        PERFORMANCE
    }

    companion object {
        fun init()
        fun shutdown()
    }

    fun build(): MaterialPackage
    fun name(name: String): MaterialBuilder
    fun materialDomain(domain: MaterialDomain): MaterialBuilder
    fun shading(shading: Shading): MaterialBuilder
    fun interpolation(interpolation: Interpolation): MaterialBuilder
    fun uniformParameter(type: UniformType, name: String): MaterialBuilder
    fun uniformParameter(type: UniformType, precision: ParameterPrecision, name: String): MaterialBuilder
    fun uniformParameterArray(type: UniformType, size: Int, name: String): MaterialBuilder
    fun uniformParameterArray(type: UniformType, size: Int, precision: ParameterPrecision, name: String): MaterialBuilder
    fun samplerParameter(type: SamplerType, format: SamplerFormat, precision: ParameterPrecision, name: String): MaterialBuilder
    fun variable(variable: Variable, name: String): MaterialBuilder
    fun require(attribute: VertexAttribute): MaterialBuilder
    fun material(code: String): MaterialBuilder
    fun materialVertex(code: String): MaterialBuilder
    fun blending(mode: BlendingMode): MaterialBuilder
    fun postLightingBlending(mode: BlendingMode): MaterialBuilder
    fun vertexDomain(vertexDomain: VertexDomain): MaterialBuilder
    fun culling(mode: CullingMode): MaterialBuilder
    fun colorWrite(enable: Boolean): MaterialBuilder
    fun depthWrite(enable: Boolean): MaterialBuilder
    fun depthCulling(enable: Boolean): MaterialBuilder
    fun doubleSided(doubleSided: Boolean): MaterialBuilder
    fun maskThreshold(threshold: Float): MaterialBuilder
    fun alphaToCoverage(enable: Boolean): MaterialBuilder
    fun shadowMultiplier(shadowMultiplier: Boolean): MaterialBuilder
    fun transparentShadow(transparentShadow: Boolean): MaterialBuilder
    fun specularAntiAliasing(specularAntiAliasing: Boolean): MaterialBuilder
    fun specularAntiAliasingVariance(variance: Float): MaterialBuilder
    fun specularAntiAliasingThreshold(threshold: Float): MaterialBuilder
    fun refractionMode(mode: RefractionMode): MaterialBuilder
    fun reflectionMode(mode: ReflectionMode): MaterialBuilder
    fun refractionType(type: RefractionType): MaterialBuilder
    fun clearCoatIorChange(clearCoatIorChange: Boolean): MaterialBuilder
    fun flipUV(flipUV: Boolean): MaterialBuilder
    fun customSurfaceShading(customSurfaceShading: Boolean): MaterialBuilder
    fun multiBounceAmbientOcclusion(multiBounceAO: Boolean): MaterialBuilder
    fun specularAmbientOcclusion(specularAO: SpecularAmbientOcclusion): MaterialBuilder
    fun transparencyMode(mode: TransparencyMode): MaterialBuilder
    fun platform(platform: Platform): MaterialBuilder
    fun targetApi(api: TargetApi): MaterialBuilder
    fun optimization(optimization: Optimization): MaterialBuilder
    fun variantFilter(variantFilter: Int): MaterialBuilder
    fun useLegacyMorphing(): MaterialBuilder
}
