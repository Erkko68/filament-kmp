package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.VertexBuffer.VertexAttribute

/**
 * MaterialBuilder compiles Filament material source code into binary packages.
 *
 * MaterialBuilder takes high-level material definitions and generates optimized shaders
 * for multiple backends (OpenGL, Vulkan, Metal, WebGPU). The resulting MaterialPackage
 * can be loaded by Filament's Material system.
 *
 * **Initialization:**
 * Call Filamat.init() before using MaterialBuilder. Call shutdown() when finished.
 *
 * **Compilation:**
 * Configure material properties using methods like name(), shading(), blendingMode(), etc.,
 * then call build() to generate the package.
 *
 * @see Filamat
 * @see MaterialPackage
 */
expect class MaterialBuilder() {
    /**
     * Shading model determines how light interacts with the material surface.
     */
    enum class Shading {
        /** No lighting; emissive only. Useful for UI, billboards, light sources. */
        UNLIT,
        /** Standard physically-based lighting model (default). */
        LIT,
        /** Subsurface scattering for thin/translucent materials (skin, leaves, paper). */
        SUBSURFACE,
        /** Cloth-specific lighting model for fabric appearance. */
        CLOTH,
        /** Legacy specular/glossiness model (not recommended for new materials). */
        SPECULAR_GLOSSINESS
    }

    /**
     * Vertex attribute interpolation in the fragment shader.
     */
    enum class Interpolation {
        /** Smooth Gouraud interpolation across the primitive (default). */
        SMOOTH,
        /** Flat interpolation; values are constant per primitive. */
        FLAT
    }

    /**
     * Uniform variable types in material parameters.
     */
    enum class UniformType {
        /** Boolean scalar and vector types. */
        BOOL, BOOL2, BOOL3, BOOL4,
        /** Floating-point scalar and vector types. */
        FLOAT, FLOAT2, FLOAT3, FLOAT4,
        /** Signed integer scalar and vector types. */
        INT, INT2, INT3, INT4,
        /** Unsigned integer scalar and vector types. */
        UINT, UINT2, UINT3, UINT4,
        /** Floating-point 3x3 and 4x4 matrices. */
        MAT3, MAT4
    }

    /**
     * Sampler types for texture parameters.
     */
    enum class SamplerType {
        /** 2D texture sampler. */
        SAMPLER_2D,
        /** 2D array texture sampler (multiple 2D textures). */
        SAMPLER_2D_ARRAY,
        /** Cubemap sampler (6 faces). */
        SAMPLER_CUBEMAP,
        /** External texture sampler (platform-specific, e.g., camera stream). */
        SAMPLER_EXTERNAL,
        /** 3D volume texture sampler. */
        SAMPLER_3D,
        /** Cubemap array sampler (multiple cubemaps). */
        SAMPLER_CUBEMAP_ARRAY
    }

    /**
     * Data format for sampler parameters.
     */
    enum class SamplerFormat {
        /** Signed integer data format. */
        INT,
        /** Unsigned integer data format. */
        UINT,
        /** Floating-point data format. */
        FLOAT,
        /** Depth comparison format (for shadow mapping). */
        SHADOW
    }

    /**
     * Precision level for numeric parameters.
     */
    enum class ParameterPrecision {
        /** Low precision; may reduce quality but improve performance. */
        LOW,
        /** Medium precision. */
        MEDIUM,
        /** High precision. */
        HIGH,
        /** Use engine default precision. */
        DEFAULT
    }

    /**
     * Custom vertex attribute variable slots.
     */
    enum class Variable {
        /** Custom variable slot 0. */
        CUSTOM0,
        /** Custom variable slot 1. */
        CUSTOM1,
        /** Custom variable slot 2. */
        CUSTOM2,
        /** Custom variable slot 3. */
        CUSTOM3,
        /** Custom variable slot 4. */
        CUSTOM4
    }

    /**
     * Blending modes determine how material color combines with background color.
     */
    enum class BlendingMode {
        /** Opaque material; no blending (default). */
        OPAQUE,
        /** Transparent with alpha pre-multiplication; affects diffuse only. */
        TRANSPARENT,
        /** Additive blending; brightens background. Used for glows, holograms. */
        ADD,
        /** Alpha-tested; either fully opaque or fully transparent per pixel. */
        MASKED,
        /** Transparent with alpha pre-multiplication; affects specular. */
        FADE,
        /** Multiplicative blending; darkens background. */
        MULTIPLY,
        /** Screen blending; brightens with color. */
        SCREEN,
        /** Custom blending using backend-specific blend function. */
        CUSTOM
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
    fun coloredPenumbra(coloredPenumbra: Boolean): MaterialBuilder
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
