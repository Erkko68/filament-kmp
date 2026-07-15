package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.VertexBuffer.VertexAttribute
import io.github.erkko68.filament.FilamentPlatform
import io.github.erkko68.filament.PlatformGap

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
@PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "throws UnsupportedOperationException on construction — runtime material compilation is not available on web; precompile .filamat assets instead.")
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

    /** Coordinate space the vertex shader's `material()` output is expressed in. */
    enum class VertexDomain {
        /** Vertices are in object/model space (default). */
        OBJECT,
        /** Vertices are in world space; the object transform is ignored. */
        WORLD,
        /** Vertices are in view/eye space. */
        VIEW,
        /** Vertices are in normalized device space; view and object transforms are ignored. */
        DEVICE
    }

    /** Which triangle faces are culled before rasterization. */
    enum class CullingMode {
        /** No culling; both faces are rendered. */
        NONE,
        /** Front faces are culled. */
        FRONT,
        /** Back faces are culled (default). */
        BACK,
        /** Both faces are culled; geometry-less rendering. */
        FRONT_AND_BACK
    }

    /** How transparent objects are rendered ([BlendingMode.TRANSPARENT]/[BlendingMode.FADE] only). */
    enum class TransparencyMode {
        /** Object is rendered in one pass; can self-overlap visibly (default). */
        DEFAULT,
        /** Two passes: depth pre-pass then color; only the front-most surface shows. */
        TWO_PASSES_ONE_SIDE,
        /** Two passes: back faces first, then front faces; approximates two-layer transparency. */
        TWO_PASSES_TWO_SIDES
    }

    /** Which pipeline stage the material targets. */
    enum class MaterialDomain {
        /** Regular surface shading in the scene (default). */
        SURFACE,
        /** Full-screen post-processing effect. */
        POST_PROCESS
    }

    /** How ambient occlusion is applied to specular indirect lighting. */
    enum class SpecularAmbientOcclusion {
        /** AO does not affect specular lighting. */
        NONE,
        /** Cheap approximation from the AO term (default on high quality). */
        SIMPLE,
        /** Higher-quality occlusion using bent normals. */
        BENT_NORMALS
    }

    /** Source of refracted light for refractive materials. */
    enum class RefractionMode {
        /** No refraction (default). */
        NONE,
        /** Refraction samples the IBL cubemap; cheap, world-independent. */
        CUBEMAP,
        /** Refraction samples the opaque scene render; requires screen-space refraction on the View. */
        SCREEN_SPACE
    }

    /** Source of reflections for the material. */
    enum class ReflectionMode {
        /** Reflections come from the IBL/environment (default). */
        DEFAULT,
        /** Reflections come from screen-space ray marching on the View. */
        SCREEN_SPACE
    }

    /** Geometry model used to compute refraction. */
    enum class RefractionType {
        /** Object is a solid volume (e.g. glass sphere); refraction bends twice (default). */
        SOLID,
        /** Object is a thin shell (e.g. window pane, bubble); refraction bends once. */
        THIN
    }

    /** Platform class to generate shaders for. */
    enum class Platform {
        /** Desktop-class GPUs. */
        DESKTOP,
        /** Mobile-class GPUs (default on device builds). */
        MOBILE,
        /** Both desktop and mobile shader sets. */
        ALL
    }

    /** Graphics API(s) to generate shaders for. */
    enum class TargetApi {
        /** OpenGL / OpenGL ES (GLSL). */
        OPENGL,
        /** Vulkan (SPIR-V). */
        VULKAN,
        /** Metal (MSL). */
        METAL,
        /** WebGPU (WGSL). */
        WEBGPU,
        /** Every supported API; largest package. */
        ALL
    }

    /** Shader optimization level applied at compile time. */
    enum class Optimization {
        /** No optimization; fastest compile, best for debugging. */
        NONE,
        /** Only run the preprocessor. */
        PREPROCESSOR,
        /** Optimize for smallest shader size. */
        SIZE,
        /** Optimize for runtime performance (default). */
        PERFORMANCE
    }

    companion object {
        /** Initializes the material compiler's global state. Call once before building materials. */
        fun init()

        /** Releases the material compiler's global state. Call when done building materials. */
        fun shutdown()
    }

    /** Compiles the material and returns the resulting package (check `isValid` before use). */
    fun build(): MaterialPackage

    /** Sets the material's name (shown in tooling and debug output). */
    fun name(name: String): MaterialBuilder

    /** Sets the material domain ([MaterialDomain.SURFACE] by default). */
    fun materialDomain(domain: MaterialDomain): MaterialBuilder

    /** Sets the shading model (LIT, UNLIT, SUBSURFACE, CLOTH, SPECULAR_GLOSSINESS). */
    fun shading(shading: Shading): MaterialBuilder

    /** Sets the interpolation quality of the shading normal (default: SMOOTH). */
    fun interpolation(interpolation: Interpolation): MaterialBuilder

    /** Declares a uniform parameter of [type] named [name], settable via `MaterialInstance.setParameter`. */
    fun uniformParameter(type: UniformType, name: String): MaterialBuilder

    /** Declares a uniform parameter with an explicit shader [precision]. */
    fun uniformParameter(type: UniformType, precision: ParameterPrecision, name: String): MaterialBuilder

    /** Declares a uniform array parameter of [size] elements. */
    fun uniformParameterArray(type: UniformType, size: Int, name: String): MaterialBuilder

    /** Declares a uniform array parameter with an explicit shader [precision]. */
    fun uniformParameterArray(type: UniformType, size: Int, precision: ParameterPrecision, name: String): MaterialBuilder

    /** Declares a texture sampler parameter, settable via `MaterialInstance.setParameter`. */
    fun samplerParameter(type: SamplerType, format: SamplerFormat, precision: ParameterPrecision, name: String): MaterialBuilder

    /** Names a custom interpolant ([Variable] slot) passed from the vertex to the fragment stage. */
    fun variable(variable: Variable, name: String): MaterialBuilder

    /** Requires the given vertex [attribute] to be present in rendered geometry (e.g. UV1, COLOR). */
    fun require(attribute: VertexAttribute): MaterialBuilder

    /** Sets the fragment-stage material code: a GLSL `void material(inout MaterialInputs)` body. */
    fun material(code: String): MaterialBuilder

    /** Sets the vertex-stage material code: a GLSL `void materialVertex(inout MaterialVertexInputs)` body. */
    fun materialVertex(code: String): MaterialBuilder

    /** Sets how the material blends with the render target ([BlendingMode.OPAQUE] by default). */
    fun blending(mode: BlendingMode): MaterialBuilder

    /** Sets how the post-lighting color blends with the lit result. */
    fun postLightingBlending(mode: BlendingMode): MaterialBuilder

    /** Sets the coordinate space of the vertex output ([VertexDomain.OBJECT] by default). */
    fun vertexDomain(vertexDomain: VertexDomain): MaterialBuilder

    /** Sets face culling ([CullingMode.BACK] by default). */
    fun culling(mode: CullingMode): MaterialBuilder

    /** Enables/disables writes to the color buffer (default: true). */
    fun colorWrite(enable: Boolean): MaterialBuilder

    /** Enables/disables writes to the depth buffer (default: true, except for blended modes). */
    fun depthWrite(enable: Boolean): MaterialBuilder

    /** Enables/disables depth testing (default: true). */
    fun depthCulling(enable: Boolean): MaterialBuilder

    /** Renders both faces and flips the normal on back faces; implies [CullingMode.NONE]. */
    fun doubleSided(doubleSided: Boolean): MaterialBuilder

    /** Sets the alpha cutoff for [BlendingMode.MASKED] (default: 0.4). */
    fun maskThreshold(threshold: Float): MaterialBuilder

    /** Converts fragment alpha to MSAA coverage; smoother [BlendingMode.MASKED] edges under MSAA. */
    fun alphaToCoverage(enable: Boolean): MaterialBuilder

    /** UNLIT only: multiplies the final color by the shadowing factor, for shadow-receiver planes. */
    fun shadowMultiplier(shadowMultiplier: Boolean): MaterialBuilder

    /** Makes this transparent material cast (dithered) transparent shadows. */
    fun transparentShadow(transparentShadow: Boolean): MaterialBuilder

    /** Enables colored shadow penumbras for this material's transparent shadows. */
    fun coloredPenumbra(coloredPenumbra: Boolean): MaterialBuilder

    /** Reduces specular shimmering/aliasing on curved geometry (LIT models only). */
    fun specularAntiAliasing(specularAntiAliasing: Boolean): MaterialBuilder

    /** Screen-space variance of the specular AA filter kernel, in `[0, 1]` (default: 0.15). */
    fun specularAntiAliasingVariance(variance: Float): MaterialBuilder

    /** Clamping threshold of the specular AA roughness increase, in `[0, 1]` (default: 0.2). */
    fun specularAntiAliasingThreshold(threshold: Float): MaterialBuilder

    /** Sets where refracted light is sampled from ([RefractionMode.NONE] by default). */
    fun refractionMode(mode: RefractionMode): MaterialBuilder

    /** Sets where reflections are sampled from ([ReflectionMode.DEFAULT] by default). */
    fun reflectionMode(mode: ReflectionMode): MaterialBuilder

    /** Sets the refraction geometry model ([RefractionType.SOLID] by default). */
    fun refractionType(type: RefractionType): MaterialBuilder

    /** Makes the clear coat layer's IOR affect the base layer (physically correct; default: true). */
    fun clearCoatIorChange(clearCoatIorChange: Boolean): MaterialBuilder

    /** Flips the V texture coordinate at compile time (default: true, matching Filament's convention). */
    fun flipUV(flipUV: Boolean): MaterialBuilder

    /** Enables custom surface shading: the material provides its own `surfaceShading()` function. */
    fun customSurfaceShading(customSurfaceShading: Boolean): MaterialBuilder

    /** Simulates extra light bounces in occluded areas to reduce over-darkening from AO. */
    fun multiBounceAmbientOcclusion(multiBounceAO: Boolean): MaterialBuilder

    /** Sets how AO is applied to specular lighting ([SpecularAmbientOcclusion.NONE] by default). */
    fun specularAmbientOcclusion(specularAO: SpecularAmbientOcclusion): MaterialBuilder

    /** Sets the transparency rendering strategy ([TransparencyMode.DEFAULT] by default). */
    fun transparencyMode(mode: TransparencyMode): MaterialBuilder

    /** Selects the platform class to generate shaders for ([Platform.ALL] to cover everything). */
    fun platform(platform: Platform): MaterialBuilder

    /** Selects the graphics API(s) to generate shaders for; fewer APIs → smaller package. */
    fun targetApi(api: TargetApi): MaterialBuilder

    /** Sets the shader optimization level ([Optimization.PERFORMANCE] by default). */
    fun optimization(optimization: Optimization): MaterialBuilder

    /** Bitmask of shader variants to exclude from compilation, shrinking the package. */
    fun variantFilter(variantFilter: Int): MaterialBuilder

    /** Uses the legacy (non-CPU-skinning-aware) morph target implementation. */
    fun useLegacyMorphing(): MaterialBuilder
}
