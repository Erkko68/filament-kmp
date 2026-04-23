package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.VertexBuffer.VertexAttribute

// MaterialBuilder is NOT available in JS runtime bindings. Material compilation (filamat) is an offline process.
// This is a stub implementation that accepts all builder methods but does not perform compilation.
// In web applications, materials must be pre-compiled and loaded as binary packages.
actual class MaterialBuilder {
    private val materialCode = StringBuilder()
    private val vertexCode = StringBuilder()

    actual enum class Shading {
        UNLIT,
        LIT,
        SUBSURFACE,
        CLOTH,
        SPECULAR_GLOSSINESS
    }

    actual enum class Interpolation {
        SMOOTH,
        FLAT
    }

    actual enum class UniformType {
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

    actual enum class SamplerType {
        SAMPLER_2D,
        SAMPLER_2D_ARRAY,
        SAMPLER_CUBEMAP,
        SAMPLER_EXTERNAL,
        SAMPLER_3D,
        SAMPLER_CUBEMAP_ARRAY
    }

    actual enum class SamplerFormat {
        INT,
        UINT,
        FLOAT,
        SHADOW
    }

    actual enum class ParameterPrecision {
        LOW,
        MEDIUM,
        HIGH,
        DEFAULT
    }

    actual enum class Variable {
        CUSTOM0,
        CUSTOM1,
        CUSTOM2,
        CUSTOM3,
        CUSTOM4
    }

    actual enum class BlendingMode {
        OPAQUE,
        TRANSPARENT,
        ADD,
        MASKED,
        FADE,
        MULTIPLY,
        SCREEN,
        CUSTOM
    }

    actual enum class VertexDomain {
        OBJECT,
        WORLD,
        VIEW,
        DEVICE
    }

    actual enum class CullingMode {
        NONE,
        FRONT,
        BACK,
        FRONT_AND_BACK
    }

    actual enum class TransparencyMode {
        DEFAULT,
        TWO_PASSES_ONE_SIDE,
        TWO_PASSES_TWO_SIDES
    }

    actual enum class MaterialDomain {
        SURFACE,
        POST_PROCESS
    }

    actual enum class SpecularAmbientOcclusion {
        NONE,
        SIMPLE,
        BENT_NORMALS
    }

    actual enum class RefractionMode {
        NONE,
        CUBEMAP,
        SCREEN_SPACE
    }

    actual enum class ReflectionMode {
        DEFAULT,
        SCREEN_SPACE
    }

    actual enum class RefractionType {
        SOLID,
        THIN
    }

    actual enum class Platform {
        DESKTOP,
        MOBILE,
        ALL
    }

    actual enum class TargetApi {
        OPENGL,
        VULKAN,
        METAL,
        WEBGPU,
        ALL
    }

    actual enum class Optimization {
        NONE,
        PREPROCESSOR,
        SIZE,
        PERFORMANCE
    }

    actual companion object {
        actual fun init() {
            // No initialization needed for JS
        }

        actual fun shutdown() {
            // No shutdown needed for JS
        }
    }

    actual fun build(): MaterialPackage {
        return MaterialPackage(ByteArray(0))
    }

    actual fun name(name: String): MaterialBuilder = this
    actual fun materialDomain(domain: MaterialDomain): MaterialBuilder = this
    actual fun shading(shading: Shading): MaterialBuilder = this
    actual fun interpolation(interpolation: Interpolation): MaterialBuilder = this
    actual fun uniformParameter(type: UniformType, name: String): MaterialBuilder = this
    actual fun uniformParameter(type: UniformType, precision: ParameterPrecision, name: String): MaterialBuilder = this
    actual fun uniformParameterArray(type: UniformType, size: Int, name: String): MaterialBuilder = this
    actual fun uniformParameterArray(type: UniformType, size: Int, precision: ParameterPrecision, name: String): MaterialBuilder = this
    actual fun samplerParameter(type: SamplerType, format: SamplerFormat, precision: ParameterPrecision, name: String): MaterialBuilder = this
    actual fun variable(variable: Variable, name: String): MaterialBuilder = this
    actual fun require(attribute: VertexAttribute): MaterialBuilder = this
    actual fun material(code: String): MaterialBuilder {
        materialCode.append(code)
        return this
    }
    actual fun materialVertex(code: String): MaterialBuilder {
        vertexCode.append(code)
        return this
    }
    actual fun blending(mode: BlendingMode): MaterialBuilder = this
    actual fun postLightingBlending(mode: BlendingMode): MaterialBuilder = this
    actual fun vertexDomain(vertexDomain: VertexDomain): MaterialBuilder = this
    actual fun culling(mode: CullingMode): MaterialBuilder = this
    actual fun colorWrite(enable: Boolean): MaterialBuilder = this
    actual fun depthWrite(enable: Boolean): MaterialBuilder = this
    actual fun depthCulling(enable: Boolean): MaterialBuilder = this
    actual fun doubleSided(doubleSided: Boolean): MaterialBuilder = this
    actual fun maskThreshold(threshold: Float): MaterialBuilder = this
    actual fun alphaToCoverage(enable: Boolean): MaterialBuilder = this
    actual fun shadowMultiplier(shadowMultiplier: Boolean): MaterialBuilder = this
    actual fun transparentShadow(transparentShadow: Boolean): MaterialBuilder = this
    actual fun specularAntiAliasing(specularAntiAliasing: Boolean): MaterialBuilder = this
    actual fun specularAntiAliasingVariance(variance: Float): MaterialBuilder = this
    actual fun specularAntiAliasingThreshold(threshold: Float): MaterialBuilder = this
    actual fun refractionMode(mode: RefractionMode): MaterialBuilder = this
    actual fun reflectionMode(mode: ReflectionMode): MaterialBuilder = this
    actual fun refractionType(type: RefractionType): MaterialBuilder = this
    actual fun clearCoatIorChange(clearCoatIorChange: Boolean): MaterialBuilder = this
    actual fun flipUV(flipUV: Boolean): MaterialBuilder = this
    actual fun customSurfaceShading(customSurfaceShading: Boolean): MaterialBuilder = this
    actual fun multiBounceAmbientOcclusion(multiBounceAO: Boolean): MaterialBuilder = this
    actual fun specularAmbientOcclusion(specularAO: SpecularAmbientOcclusion): MaterialBuilder = this
    actual fun transparencyMode(mode: TransparencyMode): MaterialBuilder = this
    actual fun platform(platform: Platform): MaterialBuilder = this
    actual fun targetApi(api: TargetApi): MaterialBuilder = this
    actual fun optimization(optimization: Optimization): MaterialBuilder = this
    actual fun variantFilter(variantFilter: Int): MaterialBuilder = this
    actual fun useLegacyMorphing(): MaterialBuilder = this
}
