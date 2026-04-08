@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)
package dev.filament.kmp.filamat

import dev.filament.kmp.VertexBuffer.VertexAttribute
import dev.filament.kmp.filamat.MaterialPackage
import dev.filament.kmp.filamat.cinterop.*
import kotlinx.cinterop.*

actual class MaterialBuilder actual constructor() {
    private val nativeHandle = FilaMaterialBuilder_create() ?: throw IllegalStateException("Failed to create MaterialBuilder")

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
        SAMPLER_2D, SAMPLER_2D_ARRAY, SAMPLER_CUBEMAP, SAMPLER_EXTERNAL, SAMPLER_3D, SAMPLER_CUBEMAP_ARRAY
    }

    actual enum class SamplerFormat {
        INT, UINT, FLOAT, SHADOW
    }

    actual enum class ParameterPrecision {
        LOW, MEDIUM, HIGH, DEFAULT
    }

    actual enum class Variable {
        CUSTOM0, CUSTOM1, CUSTOM2, CUSTOM3, CUSTOM4
    }

    actual enum class BlendingMode {
        OPAQUE, TRANSPARENT, ADD, MASKED, FADE, MULTIPLY, SCREEN, CUSTOM
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

    actual enum class ShaderQuality {
        DEFAULT, LOW, NORMAL, HIGH
    }

    actual enum class FeatureLevel {
        LEVEL_0, LEVEL_1, LEVEL_2, LEVEL_3
    }

    actual companion object {
        actual fun init() = FilaMaterialBuilder_init()
        actual fun shutdown() = FilaMaterialBuilder_shutdown()
    }

    actual fun build(): MaterialPackage {
        val pkgHandle = FilaMaterialBuilder_build(nativeHandle) ?: throw IllegalStateException("Failed to build material")
        return MaterialPackage(pkgHandle)
    }

    actual fun name(name: String): MaterialBuilder {
        FilaMaterialBuilder_name(nativeHandle, name)
        return this
    }

    actual fun materialDomain(domain: MaterialDomain): MaterialBuilder {
        FilaMaterialBuilder_materialDomain(nativeHandle, domain.ordinal.toUInt())
        return this
    }

    actual fun shading(shading: Shading): MaterialBuilder {
        FilaMaterialBuilder_shading(nativeHandle, shading.ordinal.toUInt())
        return this
    }

    actual fun interpolation(interpolation: Interpolation): MaterialBuilder {
        FilaMaterialBuilder_interpolation(nativeHandle, interpolation.ordinal.toUInt())
        return this
    }

    actual fun uniformParameter(type: UniformType, name: String): MaterialBuilder {
        FilaMaterialBuilder_uniformParameter(
            nativeHandle,
            type.ordinal.toUInt(),
            ParameterPrecision.DEFAULT.ordinal.toUInt(),
            name
        )
        return this
    }

    actual fun uniformParameter(type: UniformType, precision: ParameterPrecision, name: String): MaterialBuilder {
        FilaMaterialBuilder_uniformParameter(
            nativeHandle,
            type.ordinal.toUInt(),
            precision.ordinal.toUInt(),
            name
        )
        return this
    }

    actual fun uniformParameterArray(type: UniformType, size: Int, name: String): MaterialBuilder {
        FilaMaterialBuilder_uniformParameterArray(
            nativeHandle,
            type.ordinal.toUInt(),
            size.toULong(),
            ParameterPrecision.DEFAULT.ordinal.toUInt(),
            name
        )
        return this
    }

    actual fun uniformParameterArray(type: UniformType, size: Int, precision: ParameterPrecision, name: String): MaterialBuilder {
        FilaMaterialBuilder_uniformParameterArray(
            nativeHandle,
            type.ordinal.toUInt(),
            size.toULong(),
            precision.ordinal.toUInt(),
            name
        )
        return this
    }

    actual fun samplerParameter(type: SamplerType, format: SamplerFormat, precision: ParameterPrecision, name: String): MaterialBuilder {
        FilaMaterialBuilder_samplerParameter(
            nativeHandle,
            type.ordinal.toUInt(),
            format.ordinal.toUInt(),
            precision.ordinal.toUInt(),
            name
        )
        return this
    }

    actual fun variable(variable: Variable, name: String): MaterialBuilder {
        FilaMaterialBuilder_variable(nativeHandle, variable.ordinal.toUInt(), name)
        return this
    }

    actual fun require(attribute: dev.filament.kmp.VertexBuffer.VertexAttribute): MaterialBuilder {
        FilaMaterialBuilder_require(nativeHandle, attribute.ordinal.toUInt())
        return this
    }

    actual fun material(code: String): MaterialBuilder {
        FilaMaterialBuilder_material(nativeHandle, code)
        return this
    }

    actual fun materialVertex(code: String): MaterialBuilder {
        FilaMaterialBuilder_materialVertex(nativeHandle, code)
        return this
    }

    actual fun blending(mode: BlendingMode): MaterialBuilder {
        FilaMaterialBuilder_blending(nativeHandle, mode.ordinal.toUInt())
        return this
    }

    actual fun postLightingBlending(mode: BlendingMode): MaterialBuilder {
        FilaMaterialBuilder_postLightingBlending(nativeHandle, mode.ordinal.toUInt())
        return this
    }

    actual fun vertexDomain(vertexDomain: VertexDomain): MaterialBuilder {
        FilaMaterialBuilder_vertexDomain(nativeHandle, vertexDomain.ordinal.toUInt())
        return this
    }

    actual fun culling(mode: CullingMode): MaterialBuilder {
        FilaMaterialBuilder_culling(nativeHandle, mode.ordinal.toUInt())
        return this
    }

    actual fun colorWrite(enable: Boolean): MaterialBuilder {
        FilaMaterialBuilder_colorWrite(nativeHandle, enable)
        return this
    }

    actual fun depthWrite(enable: Boolean): MaterialBuilder {
        FilaMaterialBuilder_depthWrite(nativeHandle, enable)
        return this
    }

    actual fun depthCulling(enable: Boolean): MaterialBuilder {
        FilaMaterialBuilder_depthCulling(nativeHandle, enable)
        return this
    }

    actual fun doubleSided(doubleSided: Boolean): MaterialBuilder {
        FilaMaterialBuilder_doubleSided(nativeHandle, doubleSided)
        return this
    }

    actual fun maskThreshold(threshold: Float): MaterialBuilder {
        FilaMaterialBuilder_maskThreshold(nativeHandle, threshold)
        return this
    }

    actual fun alphaToCoverage(enable: Boolean): MaterialBuilder {
        FilaMaterialBuilder_alphaToCoverage(nativeHandle, enable)
        return this
    }

    actual fun shadowMultiplier(shadowMultiplier: Boolean): MaterialBuilder {
        FilaMaterialBuilder_shadowMultiplier(nativeHandle, shadowMultiplier)
        return this
    }

    actual fun transparentShadow(transparentShadow: Boolean): MaterialBuilder {
        FilaMaterialBuilder_transparentShadow(nativeHandle, transparentShadow)
        return this
    }

    actual fun specularAntiAliasing(specularAntiAliasing: Boolean): MaterialBuilder {
        FilaMaterialBuilder_specularAntiAliasing(nativeHandle, specularAntiAliasing)
        return this
    }

    actual fun specularAntiAliasingVariance(variance: Float): MaterialBuilder {
        FilaMaterialBuilder_specularAntiAliasingVariance(nativeHandle, variance)
        return this
    }

    actual fun specularAntiAliasingThreshold(threshold: Float): MaterialBuilder {
        FilaMaterialBuilder_specularAntiAliasingThreshold(nativeHandle, threshold)
        return this
    }

    actual fun refractionMode(mode: RefractionMode): MaterialBuilder {
        // pattern from Material.native.kt: direct ordinal mapping if byValue is missing
        FilaMaterialBuilder_refractionMode(nativeHandle, mode.ordinal.toUInt())
        return this
    }

    actual fun reflectionMode(mode: ReflectionMode): MaterialBuilder {
        FilaMaterialBuilder_reflectionMode(nativeHandle, mode.ordinal.toUInt())
        return this
    }

    actual fun refractionType(type: RefractionType): MaterialBuilder {
        FilaMaterialBuilder_refractionType(nativeHandle, type.ordinal.toUInt())
        return this
    }

    actual fun clearCoatIorChange(clearCoatIorChange: Boolean): MaterialBuilder {
        FilaMaterialBuilder_clearCoatIorChange(nativeHandle, clearCoatIorChange)
        return this
    }

    actual fun flipUV(flipUV: Boolean): MaterialBuilder {
        FilaMaterialBuilder_flipUV(nativeHandle, flipUV)
        return this
    }

    actual fun customSurfaceShading(customSurfaceShading: Boolean): MaterialBuilder {
        FilaMaterialBuilder_customSurfaceShading(nativeHandle, customSurfaceShading)
        return this
    }

    actual fun multiBounceAmbientOcclusion(multiBounceAO: Boolean): MaterialBuilder {
        FilaMaterialBuilder_multiBounceAmbientOcclusion(nativeHandle, multiBounceAO)
        return this
    }

    actual fun specularAmbientOcclusion(specularAO: SpecularAmbientOcclusion): MaterialBuilder {
        FilaMaterialBuilder_specularAmbientOcclusion(nativeHandle, specularAO.ordinal.toUInt())
        return this
    }

    actual fun transparencyMode(mode: TransparencyMode): MaterialBuilder {
        FilaMaterialBuilder_transparencyMode(nativeHandle, mode.ordinal.toUInt())
        return this
    }

    actual fun platform(platform: Platform): MaterialBuilder {
        FilaMaterialBuilder_platform(nativeHandle, platform.ordinal.toUInt())
        return this
    }

    actual fun targetApi(api: TargetApi): MaterialBuilder {
        val apiNative = when (api) {
            TargetApi.OPENGL -> 0x01u
            TargetApi.VULKAN -> 0x02u
            TargetApi.METAL -> 0x04u
            TargetApi.WEBGPU -> 0x08u
            TargetApi.ALL -> 0x0Fu
        }
        FilaMaterialBuilder_targetApi(nativeHandle, apiNative)
        return this
    }

    actual fun optimization(optimization: Optimization): MaterialBuilder {
        FilaMaterialBuilder_optimization(nativeHandle, optimization.ordinal.toUInt())
        return this
    }

    actual fun variantFilter(variantFilter: Int): MaterialBuilder {
        FilaMaterialBuilder_variantFilter(nativeHandle, variantFilter.toUInt())
        return this
    }

    actual fun useLegacyMorphing(): MaterialBuilder {
        FilaMaterialBuilder_useLegacyMorphing(nativeHandle)
        return this
    }

    actual fun shaderDefine(name: String, value: String): MaterialBuilder {
        FilaMaterialBuilder_shaderDefine(nativeHandle, name, value)
        return this
    }

    actual fun quality(quality: ShaderQuality): MaterialBuilder {
        FilaMaterialBuilder_quality(nativeHandle, quality.ordinal.toInt())
        return this
    }

    actual fun featureLevel(level: FeatureLevel): MaterialBuilder {
        FilaMaterialBuilder_featureLevel(nativeHandle, level.ordinal.toUByte())
        return this
    }

    actual fun instanced(enabled: Boolean): MaterialBuilder {
        FilaMaterialBuilder_instanced(nativeHandle, enabled)
        return this
    }

    actual fun linearFog(enabled: Boolean): MaterialBuilder {
        FilaMaterialBuilder_linearFog(nativeHandle, enabled)
        return this
    }

    actual fun shadowFarAttenuation(enabled: Boolean): MaterialBuilder {
        FilaMaterialBuilder_shadowFarAttenuation(nativeHandle, enabled)
        return this
    }

    actual fun useDefaultDepthVariant(): MaterialBuilder {
        FilaMaterialBuilder_useDefaultDepthVariant(nativeHandle)
        return this
    }

    protected fun finalize() {
        FilaMaterialBuilder_destroy(nativeHandle)
    }
}
