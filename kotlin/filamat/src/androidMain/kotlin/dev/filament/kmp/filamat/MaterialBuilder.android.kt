package dev.filament.kmp.filamat

import dev.filament.kmp.VertexBuffer.VertexAttribute
import com.google.android.filament.filamat.MaterialBuilder as AndroidMaterialBuilder

actual class MaterialBuilder actual constructor() {
    private val builder = AndroidMaterialBuilder()

    actual enum class Shading {
        UNLIT, LIT, SUBSURFACE, CLOTH, SPECULAR_GLOSSINESS
    }

    actual enum class Interpolation {
        SMOOTH, FLAT
    }

    actual enum class UniformType {
        BOOL, BOOL2, BOOL3, BOOL4, FLOAT, FLOAT2, FLOAT3, FLOAT4,
        INT, INT2, INT3, INT4, UINT, UINT2, UINT3, UINT4, MAT3, MAT4
    }

    actual enum class SamplerType {
        SAMPLER_2D, SAMPLER_2D_ARRAY, SAMPLER_CUBEMAP, SAMPLER_EXTERNAL, SAMPLER_3D
    }

    actual enum class SamplerFormat {
        INT, UINT, FLOAT, SHADOW
    }

    actual enum class ParameterPrecision {
        LOW, MEDIUM, HIGH, DEFAULT
    }

    actual enum class Variable {
        CUSTOM0, CUSTOM1, CUSTOM2, CUSTOM3
    }

    actual enum class BlendingMode {
        OPAQUE, TRANSPARENT, ADD, MASKED, FADE, MULTIPLY, SCREEN
    }

    actual enum class VertexDomain {
        OBJECT, WORLD, VIEW, DEVICE
    }

    actual enum class CullingMode {
        NONE, FRONT, BACK, FRONT_AND_BACK
    }

    actual enum class TransparencyMode {
        DEFAULT, TWO_PASSES_ONE_SIDE, TWO_PASSES_TWO_SIDES
    }

    actual enum class MaterialDomain {
        SURFACE, POST_PROCESS
    }

    actual enum class SpecularAmbientOcclusion {
        NONE, SIMPLE, BENT_NORMALS
    }

    actual enum class RefractionMode {
        NONE, CUBEMAP, SCREEN_SPACE
    }

    actual enum class ReflectionMode {
        DEFAULT, SCREEN_SPACE
    }

    actual enum class RefractionType {
        SOLID, THIN
    }

    actual enum class Platform {
        DESKTOP, MOBILE, ALL
    }

    actual enum class TargetApi {
        OPENGL,
        VULKAN,
        METAL,
        WEBGPU,
        ALL
    }

    actual enum class Optimization {
        NONE, PREPROCESSOR, SIZE, PERFORMANCE
    }

    actual companion object {
        actual fun init() = AndroidMaterialBuilder.init()
        actual fun shutdown() = AndroidMaterialBuilder.shutdown()
    }

    actual fun build(): MaterialPackage {
        val pkg = builder.build()
        return MaterialPackage(pkg)
    }

    actual fun name(name: String): MaterialBuilder {
        builder.name(name)
        return this
    }

    actual fun materialDomain(domain: MaterialDomain): MaterialBuilder {
        builder.materialDomain(AndroidMaterialBuilder.MaterialDomain.values()[domain.ordinal])
        return this
    }

    actual fun shading(shading: Shading): MaterialBuilder {
        builder.shading(AndroidMaterialBuilder.Shading.values()[shading.ordinal])
        return this
    }

    actual fun interpolation(interpolation: Interpolation): MaterialBuilder {
        builder.interpolation(AndroidMaterialBuilder.Interpolation.values()[interpolation.ordinal])
        return this
    }

    actual fun uniformParameter(type: UniformType, name: String): MaterialBuilder {
        builder.uniformParameter(AndroidMaterialBuilder.UniformType.values()[type.ordinal], name)
        return this
    }

    actual fun uniformParameter(type: UniformType, precision: ParameterPrecision, name: String): MaterialBuilder {
        builder.uniformParameter(AndroidMaterialBuilder.UniformType.values()[type.ordinal], AndroidMaterialBuilder.ParameterPrecision.values()[precision.ordinal], name)
        return this
    }

    actual fun uniformParameterArray(type: UniformType, size: Int, name: String): MaterialBuilder {
        builder.uniformParameterArray(AndroidMaterialBuilder.UniformType.values()[type.ordinal], size, name)
        return this
    }

    actual fun uniformParameterArray(type: UniformType, size: Int, precision: ParameterPrecision, name: String): MaterialBuilder {
        builder.uniformParameterArray(AndroidMaterialBuilder.UniformType.values()[type.ordinal], size, AndroidMaterialBuilder.ParameterPrecision.values()[precision.ordinal], name)
        return this
    }

    actual fun samplerParameter(type: SamplerType, format: SamplerFormat, precision: ParameterPrecision, name: String): MaterialBuilder {
        builder.samplerParameter(
            AndroidMaterialBuilder.SamplerType.values()[type.ordinal],
            AndroidMaterialBuilder.SamplerFormat.values()[format.ordinal],
            AndroidMaterialBuilder.ParameterPrecision.values()[precision.ordinal],
            name
        )
        return this
    }

    actual fun variable(variable: Variable, name: String): MaterialBuilder {
        builder.variable(AndroidMaterialBuilder.Variable.values()[variable.ordinal], name)
        return this
    }

    actual fun require(attribute: VertexAttribute): MaterialBuilder {
        builder.require(AndroidMaterialBuilder.VertexAttribute.values()[attribute.ordinal])
        return this
    }

    actual fun material(code: String): MaterialBuilder {
        builder.material(code)
        return this
    }

    actual fun materialVertex(code: String): MaterialBuilder {
        builder.materialVertex(code)
        return this
    }

    actual fun blending(mode: BlendingMode): MaterialBuilder {
        builder.blending(AndroidMaterialBuilder.BlendingMode.values()[mode.ordinal])
        return this
    }

    actual fun postLightingBlending(mode: BlendingMode): MaterialBuilder {
        builder.postLightingBlending(AndroidMaterialBuilder.BlendingMode.values()[mode.ordinal])
        return this
    }

    actual fun vertexDomain(vertexDomain: VertexDomain): MaterialBuilder {
        builder.vertexDomain(AndroidMaterialBuilder.VertexDomain.values()[vertexDomain.ordinal])
        return this
    }

    actual fun culling(mode: CullingMode): MaterialBuilder {
        builder.culling(AndroidMaterialBuilder.CullingMode.values()[mode.ordinal])
        return this
    }

    actual fun colorWrite(enable: Boolean): MaterialBuilder {
        builder.colorWrite(enable)
        return this
    }

    actual fun depthWrite(enable: Boolean): MaterialBuilder {
        builder.depthWrite(enable)
        return this
    }

    actual fun depthCulling(enable: Boolean): MaterialBuilder {
        builder.depthCulling(enable)
        return this
    }

    actual fun doubleSided(doubleSided: Boolean): MaterialBuilder {
        builder.doubleSided(doubleSided)
        return this
    }

    actual fun maskThreshold(threshold: Float): MaterialBuilder {
        builder.maskThreshold(threshold)
        return this
    }

    actual fun alphaToCoverage(enable: Boolean): MaterialBuilder {
        builder.alphaToCoverage(enable)
        return this
    }

    actual fun shadowMultiplier(shadowMultiplier: Boolean): MaterialBuilder {
        builder.shadowMultiplier(shadowMultiplier)
        return this
    }

    actual fun transparentShadow(transparentShadow: Boolean): MaterialBuilder {
        builder.transparentShadow(transparentShadow)
        return this
    }

    actual fun specularAntiAliasing(specularAntiAliasing: Boolean): MaterialBuilder {
        builder.specularAntiAliasing(specularAntiAliasing)
        return this
    }

    actual fun specularAntiAliasingVariance(variance: Float): MaterialBuilder {
        builder.specularAntiAliasingVariance(variance)
        return this
    }

    actual fun specularAntiAliasingThreshold(threshold: Float): MaterialBuilder {
        builder.specularAntiAliasingThreshold(threshold)
        return this
    }

    actual fun refractionMode(mode: RefractionMode): MaterialBuilder {
        builder.refractionMode(AndroidMaterialBuilder.RefractionMode.values()[mode.ordinal])
        return this
    }

    actual fun reflectionMode(mode: ReflectionMode): MaterialBuilder {
        builder.reflectionMode(AndroidMaterialBuilder.ReflectionMode.values()[mode.ordinal])
        return this
    }

    actual fun refractionType(type: RefractionType): MaterialBuilder {
        builder.refractionType(AndroidMaterialBuilder.RefractionType.values()[type.ordinal])
        return this
    }

    actual fun clearCoatIorChange(clearCoatIorChange: Boolean): MaterialBuilder {
        builder.clearCoatIorChange(clearCoatIorChange)
        return this
    }

    actual fun flipUV(flipUV: Boolean): MaterialBuilder {
        builder.flipUV(flipUV)
        return this
    }

    actual fun customSurfaceShading(customSurfaceShading: Boolean): MaterialBuilder {
        builder.customSurfaceShading(customSurfaceShading)
        return this
    }

    actual fun multiBounceAmbientOcclusion(multiBounceAO: Boolean): MaterialBuilder {
        builder.multiBounceAmbientOcclusion(multiBounceAO)
        return this
    }

    actual fun specularAmbientOcclusion(specularAO: SpecularAmbientOcclusion): MaterialBuilder {
        builder.specularAmbientOcclusion(AndroidMaterialBuilder.SpecularAmbientOcclusion.values()[specularAO.ordinal])
        return this
    }

    actual fun transparencyMode(mode: TransparencyMode): MaterialBuilder {
        builder.transparencyMode(AndroidMaterialBuilder.TransparencyMode.values()[mode.ordinal])
        return this
    }

    actual fun platform(platform: Platform): MaterialBuilder {
        builder.platform(AndroidMaterialBuilder.Platform.values()[platform.ordinal])
        return this
    }

    actual fun targetApi(api: TargetApi): MaterialBuilder {
        builder.targetApi(AndroidMaterialBuilder.TargetApi.values()[api.ordinal])
        return this
    }

    actual fun optimization(optimization: Optimization): MaterialBuilder {
        builder.optimization(AndroidMaterialBuilder.Optimization.values()[optimization.ordinal])
        return this
    }

    actual fun variantFilter(variantFilter: Int): MaterialBuilder {
        builder.variantFilter(variantFilter)
        return this
    }

    actual fun useLegacyMorphing(): MaterialBuilder {
        builder.useLegacyMorphing()
        return this
    }
}
