package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.VertexBuffer.VertexAttribute
import io.github.erkko68.filament.filamat.wasm.*
import io.github.erkko68.filament.wasm.readBytes

actual class MaterialBuilder actual constructor() {
    // No finalizers on web: setters are recorded and replayed into a C builder that build() frees.
    // Replay is safe because filamat copies every string it's given.
    private val ops = ArrayList<(Int) -> Unit>()
    private fun op(block: (Int) -> Unit): MaterialBuilder = apply { ops.add(block) }

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

    actual fun build(): MaterialPackage {
        val builder = FilaMaterialBuilder_create()
        check(builder != 0) { "Failed to create MaterialBuilder" }
        try {
            ops.forEach { it(builder) }
            val pkg = FilaMaterialBuilder_build(builder)
            check(pkg != 0) { "Failed to build material" }
            try {
                val size = FilaPackage_getSize(pkg)
                val data = FilaPackage_getData(pkg)
                val bytes = if (data == 0 || size <= 0) ByteArray(0) else filamatWasm.readBytes(data, size)
                return MaterialPackage(bytes, FilaPackage_isValid(pkg))
            } finally {
                FilaPackage_destroy(pkg)
            }
        } finally {
            FilaMaterialBuilder_destroy(builder)
        }
    }

    actual fun name(name: String): MaterialBuilder {
        return op { FilaMaterialBuilder_name(it, name) }
    }

    actual fun materialDomain(domain: MaterialDomain): MaterialBuilder {
        return op { FilaMaterialBuilder_materialDomain(it, domain.ordinal) }
    }

    actual fun shading(shading: Shading): MaterialBuilder {
        return op { FilaMaterialBuilder_shading(it, shading.ordinal) }
    }

    actual fun interpolation(interpolation: Interpolation): MaterialBuilder {
        return op { FilaMaterialBuilder_interpolation(it, interpolation.ordinal) }
    }

    actual fun uniformParameter(type: UniformType, name: String): MaterialBuilder {
        return op { FilaMaterialBuilder_uniformParameter(it, type.ordinal, ParameterPrecision.DEFAULT.ordinal, name) }
    }

    actual fun uniformParameter(type: UniformType, precision: ParameterPrecision, name: String): MaterialBuilder {
        return op { FilaMaterialBuilder_uniformParameter(it, type.ordinal, precision.ordinal, name) }
    }

    actual fun uniformParameterArray(type: UniformType, size: Int, name: String): MaterialBuilder {
        return op { FilaMaterialBuilder_uniformParameterArray(it, type.ordinal, size, ParameterPrecision.DEFAULT.ordinal, name) }
    }

    actual fun uniformParameterArray(type: UniformType, size: Int, precision: ParameterPrecision, name: String): MaterialBuilder {
        return op { FilaMaterialBuilder_uniformParameterArray(it, type.ordinal, size, precision.ordinal, name) }
    }

    actual fun samplerParameter(type: SamplerType, format: SamplerFormat, precision: ParameterPrecision, name: String): MaterialBuilder {
        return op { FilaMaterialBuilder_samplerParameter(it, type.ordinal, format.ordinal, precision.ordinal, name) }
    }

    actual fun variable(variable: Variable, name: String): MaterialBuilder {
        return op { FilaMaterialBuilder_variable(it, variable.ordinal, name) }
    }

    actual fun require(attribute: io.github.erkko68.filament.VertexBuffer.VertexAttribute): MaterialBuilder {
        return op { FilaMaterialBuilder_require(it, attribute.ordinal) }
    }

    actual fun material(code: String): MaterialBuilder {
        return op { FilaMaterialBuilder_material(it, code) }
    }

    actual fun materialVertex(code: String): MaterialBuilder {
        return op { FilaMaterialBuilder_materialVertex(it, code) }
    }

    actual fun blending(mode: BlendingMode): MaterialBuilder {
        return op { FilaMaterialBuilder_blending(it, mode.ordinal) }
    }

    actual fun postLightingBlending(mode: BlendingMode): MaterialBuilder {
        return op { FilaMaterialBuilder_postLightingBlending(it, mode.ordinal) }
    }

    actual fun vertexDomain(vertexDomain: VertexDomain): MaterialBuilder {
        return op { FilaMaterialBuilder_vertexDomain(it, vertexDomain.ordinal) }
    }

    actual fun culling(mode: CullingMode): MaterialBuilder {
        return op { FilaMaterialBuilder_culling(it, mode.ordinal) }
    }

    actual fun colorWrite(enable: Boolean): MaterialBuilder {
        return op { FilaMaterialBuilder_colorWrite(it, enable) }
    }

    actual fun depthWrite(enable: Boolean): MaterialBuilder {
        return op { FilaMaterialBuilder_depthWrite(it, enable) }
    }

    actual fun depthCulling(enable: Boolean): MaterialBuilder {
        return op { FilaMaterialBuilder_depthCulling(it, enable) }
    }

    actual fun doubleSided(doubleSided: Boolean): MaterialBuilder {
        return op { FilaMaterialBuilder_doubleSided(it, doubleSided) }
    }

    actual fun maskThreshold(threshold: Float): MaterialBuilder {
        return op { FilaMaterialBuilder_maskThreshold(it, threshold) }
    }

    actual fun alphaToCoverage(enable: Boolean): MaterialBuilder {
        return op { FilaMaterialBuilder_alphaToCoverage(it, enable) }
    }

    actual fun shadowMultiplier(shadowMultiplier: Boolean): MaterialBuilder {
        return op { FilaMaterialBuilder_shadowMultiplier(it, shadowMultiplier) }
    }

    actual fun transparentShadow(transparentShadow: Boolean): MaterialBuilder {
        return op { FilaMaterialBuilder_transparentShadow(it, transparentShadow) }
    }

    actual fun coloredPenumbra(coloredPenumbra: Boolean): MaterialBuilder {
        return op { FilaMaterialBuilder_coloredPenumbra(it, coloredPenumbra) }
    }

    actual fun specularAntiAliasing(specularAntiAliasing: Boolean): MaterialBuilder {
        return op { FilaMaterialBuilder_specularAntiAliasing(it, specularAntiAliasing) }
    }

    actual fun specularAntiAliasingVariance(variance: Float): MaterialBuilder {
        return op { FilaMaterialBuilder_specularAntiAliasingVariance(it, variance) }
    }

    actual fun specularAntiAliasingThreshold(threshold: Float): MaterialBuilder {
        return op { FilaMaterialBuilder_specularAntiAliasingThreshold(it, threshold) }
    }

    actual fun refractionMode(mode: RefractionMode): MaterialBuilder {
        // pattern from Material.native.kt: direct ordinal mapping if byValue is missing
        return op { FilaMaterialBuilder_refractionMode(it, mode.ordinal) }
    }

    actual fun reflectionMode(mode: ReflectionMode): MaterialBuilder {
        return op { FilaMaterialBuilder_reflectionMode(it, mode.ordinal) }
    }

    actual fun refractionType(type: RefractionType): MaterialBuilder {
        return op { FilaMaterialBuilder_refractionType(it, type.ordinal) }
    }

    actual fun clearCoatIorChange(clearCoatIorChange: Boolean): MaterialBuilder {
        return op { FilaMaterialBuilder_clearCoatIorChange(it, clearCoatIorChange) }
    }

    actual fun flipUV(flipUV: Boolean): MaterialBuilder {
        return op { FilaMaterialBuilder_flipUV(it, flipUV) }
    }

    actual fun customSurfaceShading(customSurfaceShading: Boolean): MaterialBuilder {
        return op { FilaMaterialBuilder_customSurfaceShading(it, customSurfaceShading) }
    }

    actual fun multiBounceAmbientOcclusion(multiBounceAO: Boolean): MaterialBuilder {
        return op { FilaMaterialBuilder_multiBounceAmbientOcclusion(it, multiBounceAO) }
    }

    actual fun specularAmbientOcclusion(specularAO: SpecularAmbientOcclusion): MaterialBuilder {
        return op { FilaMaterialBuilder_specularAmbientOcclusion(it, specularAO.ordinal) }
    }

    actual fun transparencyMode(mode: TransparencyMode): MaterialBuilder {
        return op { FilaMaterialBuilder_transparencyMode(it, mode.ordinal) }
    }

    actual fun platform(platform: Platform): MaterialBuilder {
        return op { FilaMaterialBuilder_platform(it, platform.ordinal) }
    }

    actual fun targetApi(api: TargetApi): MaterialBuilder {
        val apiNative = when (api) {
            TargetApi.OPENGL -> 0x01
            TargetApi.VULKAN -> 0x02
            TargetApi.METAL -> 0x04
            TargetApi.WEBGPU -> 0x08
            TargetApi.ALL -> 0x07 // OpenGL | Vulkan | Metal
        }
        return op { FilaMaterialBuilder_targetApi(it, apiNative) }
    }

    actual fun optimization(optimization: Optimization): MaterialBuilder {
        return op { FilaMaterialBuilder_optimization(it, optimization.ordinal) }
    }

    actual fun variantFilter(variantFilter: Int): MaterialBuilder {
        return op { FilaMaterialBuilder_variantFilter(it, variantFilter) }
    }

    actual fun useLegacyMorphing(): MaterialBuilder = op { FilaMaterialBuilder_useLegacyMorphing(it) }

}
