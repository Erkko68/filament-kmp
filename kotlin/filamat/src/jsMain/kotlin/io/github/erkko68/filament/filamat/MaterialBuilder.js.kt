package io.github.erkko68.filament.filamat

actual class MaterialBuilder actual constructor() {
    actual fun build(): io.github.erkko68.filament.filamat.MaterialPackage {
        TODO("Not yet implemented")
    }

    actual fun name(name: String): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun materialDomain(domain: MaterialDomain): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun shading(shading: Shading): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun interpolation(interpolation: Interpolation): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun uniformParameter(
        type: UniformType,
        name: String
    ): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun uniformParameter(
        type: UniformType,
        precision: ParameterPrecision,
        name: String
    ): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun uniformParameterArray(
        type: UniformType,
        size: Int,
        name: String
    ): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun uniformParameterArray(
        type: UniformType,
        size: Int,
        precision: ParameterPrecision,
        name: String
    ): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun samplerParameter(
        type: SamplerType,
        format: SamplerFormat,
        precision: ParameterPrecision,
        name: String
    ): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun variable(
        variable: Variable,
        name: String
    ): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun require(attribute: io.github.erkko68.filament.VertexBuffer.VertexAttribute): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun material(code: String): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun materialVertex(code: String): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun blending(mode: BlendingMode): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun postLightingBlending(mode: BlendingMode): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun vertexDomain(vertexDomain: VertexDomain): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun culling(mode: CullingMode): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun colorWrite(enable: Boolean): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun depthWrite(enable: Boolean): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun depthCulling(enable: Boolean): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun doubleSided(doubleSided: Boolean): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun maskThreshold(threshold: Float): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun alphaToCoverage(enable: Boolean): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun shadowMultiplier(shadowMultiplier: Boolean): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun transparentShadow(transparentShadow: Boolean): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun specularAntiAliasing(specularAntiAliasing: Boolean): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun specularAntiAliasingVariance(variance: Float): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun specularAntiAliasingThreshold(threshold: Float): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun refractionMode(mode: RefractionMode): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun reflectionMode(mode: ReflectionMode): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun refractionType(type: RefractionType): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun clearCoatIorChange(clearCoatIorChange: Boolean): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun flipUV(flipUV: Boolean): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun customSurfaceShading(customSurfaceShading: Boolean): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun multiBounceAmbientOcclusion(multiBounceAO: Boolean): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun specularAmbientOcclusion(specularAO: SpecularAmbientOcclusion): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun transparencyMode(mode: TransparencyMode): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun platform(platform: Platform): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun targetApi(api: TargetApi): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun optimization(optimization: Optimization): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun variantFilter(variantFilter: Int): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual fun useLegacyMorphing(): MaterialBuilder {
        TODO("Not yet implemented")
    }

    actual enum class Shading { UNLIT, LIT, SUBSURFACE, CLOTH, SPECULAR_GLOSSINESS }
    actual enum class Interpolation { SMOOTH, FLAT }
    actual enum class UniformType { BOOL, BOOL2, BOOL3, BOOL4, FLOAT, FLOAT2, FLOAT3, FLOAT4, INT, INT2, INT3, INT4, UINT, UINT2, UINT3, UINT4, MAT3, MAT4 }
    actual enum class SamplerType { SAMPLER_2D, SAMPLER_2D_ARRAY, SAMPLER_CUBEMAP, SAMPLER_EXTERNAL, SAMPLER_3D, SAMPLER_CUBEMAP_ARRAY }
    actual enum class SamplerFormat { INT, UINT, FLOAT, SHADOW }
    actual enum class ParameterPrecision { LOW, MEDIUM, HIGH, DEFAULT }
    actual enum class Variable { CUSTOM0, CUSTOM1, CUSTOM2, CUSTOM3, CUSTOM4 }
    actual enum class BlendingMode { OPAQUE, TRANSPARENT, ADD, MASKED, FADE, MULTIPLY, SCREEN, CUSTOM }
    actual enum class VertexDomain { OBJECT, WORLD, VIEW, DEVICE }
    actual enum class CullingMode { NONE, FRONT, BACK, FRONT_AND_BACK }
    actual enum class TransparencyMode { DEFAULT, TWO_PASSES_ONE_SIDE, TWO_PASSES_TWO_SIDES }
    actual enum class MaterialDomain { SURFACE, POST_PROCESS }
    actual enum class SpecularAmbientOcclusion { NONE, SIMPLE, BENT_NORMALS }
    actual enum class RefractionMode { NONE, CUBEMAP, SCREEN_SPACE }
    actual enum class ReflectionMode { DEFAULT, SCREEN_SPACE }
    actual enum class RefractionType { SOLID, THIN }
    actual enum class Platform { DESKTOP, MOBILE, ALL }
    actual enum class TargetApi { OPENGL, VULKAN, METAL, WEBGPU, ALL }
    actual enum class Optimization { NONE, PREPROCESSOR, SIZE, PERFORMANCE }
    actual companion object {
        actual fun init() {
        }

        actual fun shutdown() {
        }
    }
}