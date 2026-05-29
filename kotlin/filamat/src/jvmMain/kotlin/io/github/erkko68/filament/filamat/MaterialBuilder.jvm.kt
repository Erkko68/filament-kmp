package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.VertexBuffer.VertexAttribute
import io.github.erkko68.filament.confined
import io.github.erkko68.filament.cstr
import io.github.erkko68.filament.ffm.FilamentC
import java.lang.foreign.MemorySegment

actual class MaterialBuilder actual constructor() {
    private var nativeHandle: MemorySegment? =
        FilamentC.FilaMaterialBuilder_create() ?: throw IllegalStateException("Failed to create MaterialBuilder")

    actual enum class Shading {
        UNLIT, LIT, SUBSURFACE, CLOTH, SPECULAR_GLOSSINESS
    }

    actual enum class Interpolation {
        SMOOTH, FLAT
    }

    actual enum class UniformType {
        BOOL, BOOL2, BOOL3, BOOL4, FLOAT, FLOAT2, FLOAT3, FLOAT4, INT, INT2, INT3, INT4, UINT, UINT2, UINT3, UINT4, MAT3, MAT4
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
        OPENGL, VULKAN, METAL, WEBGPU, ALL
    }

    actual enum class Optimization {
        NONE, PREPROCESSOR, SIZE, PERFORMANCE
    }

    actual companion object {
        actual fun init() = Filamat.init()
        actual fun shutdown() = FilamentC.FilaMaterialBuilder_shutdown()
    }

    actual fun build(): MaterialPackage {
        val pkg = FilamentC.FilaMaterialBuilder_build(nativeHandle)
            ?: throw IllegalStateException("Failed to build material")
        return MaterialPackage(pkg)
    }

    actual fun name(name: String): MaterialBuilder = apply {
        confined { a -> FilamentC.FilaMaterialBuilder_name(nativeHandle, a.cstr(name)) }
    }

    actual fun materialDomain(domain: MaterialDomain): MaterialBuilder = apply {
        FilamentC.FilaMaterialBuilder_materialDomain(nativeHandle, domain.ordinal)
    }

    actual fun shading(shading: Shading): MaterialBuilder = apply {
        FilamentC.FilaMaterialBuilder_shading(nativeHandle, shading.ordinal)
    }

    actual fun interpolation(interpolation: Interpolation): MaterialBuilder = apply {
        FilamentC.FilaMaterialBuilder_interpolation(nativeHandle, interpolation.ordinal)
    }

    actual fun uniformParameter(type: UniformType, name: String): MaterialBuilder = apply {
        confined { a ->
            FilamentC.FilaMaterialBuilder_uniformParameter(
                nativeHandle, type.ordinal, ParameterPrecision.DEFAULT.ordinal, a.cstr(name),
            )
        }
    }

    actual fun uniformParameter(type: UniformType, precision: ParameterPrecision, name: String): MaterialBuilder = apply {
        confined { a ->
            FilamentC.FilaMaterialBuilder_uniformParameter(nativeHandle, type.ordinal, precision.ordinal, a.cstr(name))
        }
    }

    actual fun uniformParameterArray(type: UniformType, size: Int, name: String): MaterialBuilder = apply {
        confined { a ->
            FilamentC.FilaMaterialBuilder_uniformParameterArray(
                nativeHandle, type.ordinal, size.toLong(), ParameterPrecision.DEFAULT.ordinal, a.cstr(name),
            )
        }
    }

    actual fun uniformParameterArray(type: UniformType, size: Int, precision: ParameterPrecision, name: String): MaterialBuilder = apply {
        confined { a ->
            FilamentC.FilaMaterialBuilder_uniformParameterArray(
                nativeHandle, type.ordinal, size.toLong(), precision.ordinal, a.cstr(name),
            )
        }
    }

    actual fun samplerParameter(type: SamplerType, format: SamplerFormat, precision: ParameterPrecision, name: String): MaterialBuilder = apply {
        confined { a ->
            FilamentC.FilaMaterialBuilder_samplerParameter(
                nativeHandle, type.ordinal, format.ordinal, precision.ordinal, a.cstr(name),
            )
        }
    }

    actual fun variable(variable: Variable, name: String): MaterialBuilder = apply {
        confined { a -> FilamentC.FilaMaterialBuilder_variable(nativeHandle, variable.ordinal, a.cstr(name)) }
    }

    actual fun require(attribute: VertexAttribute): MaterialBuilder = apply {
        FilamentC.FilaMaterialBuilder_require(nativeHandle, attribute.ordinal)
    }

    actual fun material(code: String): MaterialBuilder = apply {
        confined { a -> FilamentC.FilaMaterialBuilder_material(nativeHandle, a.cstr(code)) }
    }

    actual fun materialVertex(code: String): MaterialBuilder = apply {
        confined { a -> FilamentC.FilaMaterialBuilder_materialVertex(nativeHandle, a.cstr(code)) }
    }

    actual fun blending(mode: BlendingMode): MaterialBuilder = apply {
        FilamentC.FilaMaterialBuilder_blending(nativeHandle, mode.ordinal)
    }

    actual fun postLightingBlending(mode: BlendingMode): MaterialBuilder = apply {
        FilamentC.FilaMaterialBuilder_postLightingBlending(nativeHandle, mode.ordinal)
    }

    actual fun vertexDomain(vertexDomain: VertexDomain): MaterialBuilder = apply {
        FilamentC.FilaMaterialBuilder_vertexDomain(nativeHandle, vertexDomain.ordinal)
    }

    actual fun culling(mode: CullingMode): MaterialBuilder = apply {
        FilamentC.FilaMaterialBuilder_culling(nativeHandle, mode.ordinal)
    }

    actual fun colorWrite(enable: Boolean): MaterialBuilder = apply {
        FilamentC.FilaMaterialBuilder_colorWrite(nativeHandle, enable)
    }

    actual fun depthWrite(enable: Boolean): MaterialBuilder = apply {
        FilamentC.FilaMaterialBuilder_depthWrite(nativeHandle, enable)
    }

    actual fun depthCulling(enable: Boolean): MaterialBuilder = apply {
        FilamentC.FilaMaterialBuilder_depthCulling(nativeHandle, enable)
    }

    actual fun doubleSided(doubleSided: Boolean): MaterialBuilder = apply {
        FilamentC.FilaMaterialBuilder_doubleSided(nativeHandle, doubleSided)
    }

    actual fun maskThreshold(threshold: Float): MaterialBuilder = apply {
        FilamentC.FilaMaterialBuilder_maskThreshold(nativeHandle, threshold)
    }

    actual fun alphaToCoverage(enable: Boolean): MaterialBuilder = apply {
        FilamentC.FilaMaterialBuilder_alphaToCoverage(nativeHandle, enable)
    }

    actual fun shadowMultiplier(shadowMultiplier: Boolean): MaterialBuilder = apply {
        FilamentC.FilaMaterialBuilder_shadowMultiplier(nativeHandle, shadowMultiplier)
    }

    actual fun transparentShadow(transparentShadow: Boolean): MaterialBuilder = apply {
        FilamentC.FilaMaterialBuilder_transparentShadow(nativeHandle, transparentShadow)
    }

    actual fun specularAntiAliasing(specularAntiAliasing: Boolean): MaterialBuilder = apply {
        FilamentC.FilaMaterialBuilder_specularAntiAliasing(nativeHandle, specularAntiAliasing)
    }

    actual fun specularAntiAliasingVariance(variance: Float): MaterialBuilder = apply {
        FilamentC.FilaMaterialBuilder_specularAntiAliasingVariance(nativeHandle, variance)
    }

    actual fun specularAntiAliasingThreshold(threshold: Float): MaterialBuilder = apply {
        FilamentC.FilaMaterialBuilder_specularAntiAliasingThreshold(nativeHandle, threshold)
    }

    actual fun refractionMode(mode: RefractionMode): MaterialBuilder = apply {
        FilamentC.FilaMaterialBuilder_refractionMode(nativeHandle, mode.ordinal)
    }

    actual fun reflectionMode(mode: ReflectionMode): MaterialBuilder = apply {
        FilamentC.FilaMaterialBuilder_reflectionMode(nativeHandle, mode.ordinal)
    }

    actual fun refractionType(type: RefractionType): MaterialBuilder = apply {
        FilamentC.FilaMaterialBuilder_refractionType(nativeHandle, type.ordinal)
    }

    actual fun clearCoatIorChange(clearCoatIorChange: Boolean): MaterialBuilder = apply {
        FilamentC.FilaMaterialBuilder_clearCoatIorChange(nativeHandle, clearCoatIorChange)
    }

    actual fun flipUV(flipUV: Boolean): MaterialBuilder = apply {
        FilamentC.FilaMaterialBuilder_flipUV(nativeHandle, flipUV)
    }

    actual fun customSurfaceShading(customSurfaceShading: Boolean): MaterialBuilder = apply {
        FilamentC.FilaMaterialBuilder_customSurfaceShading(nativeHandle, customSurfaceShading)
    }

    actual fun multiBounceAmbientOcclusion(multiBounceAO: Boolean): MaterialBuilder = apply {
        FilamentC.FilaMaterialBuilder_multiBounceAmbientOcclusion(nativeHandle, multiBounceAO)
    }

    actual fun specularAmbientOcclusion(specularAO: SpecularAmbientOcclusion): MaterialBuilder = apply {
        FilamentC.FilaMaterialBuilder_specularAmbientOcclusion(nativeHandle, specularAO.ordinal)
    }

    actual fun transparencyMode(mode: TransparencyMode): MaterialBuilder = apply {
        FilamentC.FilaMaterialBuilder_transparencyMode(nativeHandle, mode.ordinal)
    }

    actual fun platform(platform: Platform): MaterialBuilder = apply {
        FilamentC.FilaMaterialBuilder_platform(nativeHandle, platform.ordinal)
    }

    actual fun targetApi(api: TargetApi): MaterialBuilder = apply {
        // Mirrors MaterialBuilder.native.kt: the C wrapper takes the filamat bitmask, not the ordinal.
        val apiNative = when (api) {
            TargetApi.OPENGL -> 0x01
            TargetApi.VULKAN -> 0x02
            TargetApi.METAL -> 0x04
            TargetApi.WEBGPU -> 0x08
            TargetApi.ALL -> 0x07 // OpenGL | Vulkan | Metal
        }
        FilamentC.FilaMaterialBuilder_targetApi(nativeHandle, apiNative)
    }

    actual fun optimization(optimization: Optimization): MaterialBuilder = apply {
        FilamentC.FilaMaterialBuilder_optimization(nativeHandle, optimization.ordinal)
    }

    actual fun variantFilter(variantFilter: Int): MaterialBuilder = apply {
        FilamentC.FilaMaterialBuilder_variantFilter(nativeHandle, variantFilter)
    }

    actual fun useLegacyMorphing(): MaterialBuilder = apply {
        FilamentC.FilaMaterialBuilder_useLegacyMorphing(nativeHandle)
    }

    protected fun finalize() {
        nativeHandle?.let { FilamentC.FilaMaterialBuilder_destroy(it) }
        nativeHandle = null
    }
}
