package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.VertexBuffer.VertexAttribute
import io.github.erkko68.filament.interop.*

/**
 * MaterialBuilder compiles Filament material source code into binary packages.
 *
 * MaterialBuilder takes high-level material definitions and generates optimized shaders
 * for multiple backends (OpenGL, Vulkan, Metal, WebGPU). The resulting MaterialPackage
 * can be loaded by Filament's Material system.
 *
 * **Initialization:**
 * Call Filamat.init() before using MaterialBuilder, and Filamat.shutdown() when finished.
 *
 * **Compilation:**
 * Configure material properties using methods like name(), shading(), blendingMode(), etc.,
 * then call build() to generate the package.
 *
 * @see Filamat
 * @see MaterialPackage
 */
class MaterialBuilder() {
    // Setters are recorded and replayed into a C builder that build() frees: no native builder outlives
    // a call, so none needs a finalizer. Replay is safe because filamat copies every string it's given.
    private val ops = ArrayList<(NativePointer) -> Unit>()
    private fun op(block: (NativePointer) -> Unit): MaterialBuilder = apply { ops.add(block) }

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

    /** Compiles the material and returns the resulting package (check `isValid` before use). */
    fun build(): MaterialPackage {
        val builder = FilaMaterialBuilder_create()
        check(builder != NullPointer) { "Failed to create MaterialBuilder" }
        try {
            ops.forEach { it(builder) }
            val pkg = FilaMaterialBuilder_build(builder)
            check(pkg != NullPointer) { "Failed to build material" }
            try {
                val size = FilaPackage_getSize(pkg)
                val data = FilaPackage_getData(pkg)
                val bytes = if (data == NullPointer || size <= 0) ByteArray(0) else readFilamatBytes(data, size)
                return MaterialPackage(bytes, FilaPackage_isValid(pkg))
            } finally {
                FilaPackage_destroy(pkg)
            }
        } finally {
            FilaMaterialBuilder_destroy(builder)
        }
    }

    /** Sets the material's name (shown in tooling and debug output). */
    fun name(name: String): MaterialBuilder = op { b -> name.useFilamatCString { p -> FilaMaterialBuilder_name(b, p) } }

    /** Sets the material domain ([MaterialDomain.SURFACE] by default). */
    fun materialDomain(domain: MaterialDomain): MaterialBuilder = op { FilaMaterialBuilder_materialDomain(it, domain.ordinal) }

    /** Sets the shading model (LIT, UNLIT, SUBSURFACE, CLOTH, SPECULAR_GLOSSINESS). */
    fun shading(shading: Shading): MaterialBuilder = op { FilaMaterialBuilder_shading(it, shading.ordinal) }

    /** Sets the interpolation quality of the shading normal (default: SMOOTH). */
    fun interpolation(interpolation: Interpolation): MaterialBuilder = op { FilaMaterialBuilder_interpolation(it, interpolation.ordinal) }

    /** Declares a uniform parameter of [type] named [name], settable via `MaterialInstance.setParameter`. */
    fun uniformParameter(type: UniformType, name: String): MaterialBuilder = op { b -> name.useFilamatCString { p -> FilaMaterialBuilder_uniformParameter(b, type.ordinal, ParameterPrecision.DEFAULT.ordinal, p) } }

    /** Declares a uniform parameter with an explicit shader [precision]. */
    fun uniformParameter(type: UniformType, precision: ParameterPrecision, name: String): MaterialBuilder = op { b -> name.useFilamatCString { p -> FilaMaterialBuilder_uniformParameter(b, type.ordinal, precision.ordinal, p) } }

    /** Declares a uniform array parameter of [size] elements. */
    fun uniformParameterArray(type: UniformType, size: Int, name: String): MaterialBuilder = op { b -> name.useFilamatCString { p -> FilaMaterialBuilder_uniformParameterArray(b, type.ordinal, size, ParameterPrecision.DEFAULT.ordinal, p) } }

    /** Declares a uniform array parameter with an explicit shader [precision]. */
    fun uniformParameterArray(type: UniformType, size: Int, precision: ParameterPrecision, name: String): MaterialBuilder = op { b -> name.useFilamatCString { p -> FilaMaterialBuilder_uniformParameterArray(b, type.ordinal, size, precision.ordinal, p) } }

    /** Declares a texture sampler parameter, settable via `MaterialInstance.setParameter`. */
    fun samplerParameter(type: SamplerType, format: SamplerFormat, precision: ParameterPrecision, name: String): MaterialBuilder = op { b -> name.useFilamatCString { p -> FilaMaterialBuilder_samplerParameter(b, type.ordinal, format.ordinal, precision.ordinal, p) } }

    /** Names a custom interpolant ([Variable] slot) passed from the vertex to the fragment stage. */
    fun variable(variable: Variable, name: String): MaterialBuilder = op { b -> name.useFilamatCString { p -> FilaMaterialBuilder_variable(b, variable.ordinal, p) } }

    /** Requires the given vertex [attribute] to be present in rendered geometry (e.g. UV1, COLOR). */
    fun require(attribute: VertexAttribute): MaterialBuilder = op { FilaMaterialBuilder_require(it, attribute.ordinal) }

    /** Sets the fragment-stage material code: a GLSL `void material(inout MaterialInputs)` body. */
    fun material(code: String): MaterialBuilder = op { b -> code.useFilamatCString { p -> FilaMaterialBuilder_material(b, p) } }

    /** Sets the vertex-stage material code: a GLSL `void materialVertex(inout MaterialVertexInputs)` body. */
    fun materialVertex(code: String): MaterialBuilder = op { b -> code.useFilamatCString { p -> FilaMaterialBuilder_materialVertex(b, p) } }

    /** Sets how the material blends with the render target ([BlendingMode.OPAQUE] by default). */
    fun blending(mode: BlendingMode): MaterialBuilder = op { FilaMaterialBuilder_blending(it, mode.ordinal) }

    /** Sets how the post-lighting color blends with the lit result. */
    fun postLightingBlending(mode: BlendingMode): MaterialBuilder = op { FilaMaterialBuilder_postLightingBlending(it, mode.ordinal) }

    /** Sets the coordinate space of the vertex output ([VertexDomain.OBJECT] by default). */
    fun vertexDomain(vertexDomain: VertexDomain): MaterialBuilder = op { FilaMaterialBuilder_vertexDomain(it, vertexDomain.ordinal) }

    /** Sets face culling ([CullingMode.BACK] by default). */
    fun culling(mode: CullingMode): MaterialBuilder = op { FilaMaterialBuilder_culling(it, mode.ordinal) }

    /** Enables/disables writes to the color buffer (default: true). */
    fun colorWrite(enable: Boolean): MaterialBuilder = op { FilaMaterialBuilder_colorWrite(it, enable) }

    /** Enables/disables writes to the depth buffer (default: true, except for blended modes). */
    fun depthWrite(enable: Boolean): MaterialBuilder = op { FilaMaterialBuilder_depthWrite(it, enable) }

    /** Enables/disables depth testing (default: true). */
    fun depthCulling(enable: Boolean): MaterialBuilder = op { FilaMaterialBuilder_depthCulling(it, enable) }

    /** Renders both faces and flips the normal on back faces; implies [CullingMode.NONE]. */
    fun doubleSided(doubleSided: Boolean): MaterialBuilder = op { FilaMaterialBuilder_doubleSided(it, doubleSided) }

    /** Sets the alpha cutoff for [BlendingMode.MASKED] (default: 0.4). */
    fun maskThreshold(threshold: Float): MaterialBuilder = op { FilaMaterialBuilder_maskThreshold(it, threshold) }

    /** Converts fragment alpha to MSAA coverage; smoother [BlendingMode.MASKED] edges under MSAA. */
    fun alphaToCoverage(enable: Boolean): MaterialBuilder = op { FilaMaterialBuilder_alphaToCoverage(it, enable) }

    /** UNLIT only: multiplies the final color by the shadowing factor, for shadow-receiver planes. */
    fun shadowMultiplier(shadowMultiplier: Boolean): MaterialBuilder = op { FilaMaterialBuilder_shadowMultiplier(it, shadowMultiplier) }

    /** Makes this transparent material cast (dithered) transparent shadows. */
    fun transparentShadow(transparentShadow: Boolean): MaterialBuilder = op { FilaMaterialBuilder_transparentShadow(it, transparentShadow) }

    /** Enables colored shadow penumbras for this material's transparent shadows. */
    fun coloredPenumbra(coloredPenumbra: Boolean): MaterialBuilder = op { FilaMaterialBuilder_coloredPenumbra(it, coloredPenumbra) }

    /** Reduces specular shimmering/aliasing on curved geometry (LIT models only). */
    fun specularAntiAliasing(specularAntiAliasing: Boolean): MaterialBuilder = op { FilaMaterialBuilder_specularAntiAliasing(it, specularAntiAliasing) }

    /** Screen-space variance of the specular AA filter kernel, in `[0, 1]` (default: 0.15). */
    fun specularAntiAliasingVariance(variance: Float): MaterialBuilder = op { FilaMaterialBuilder_specularAntiAliasingVariance(it, variance) }

    /** Clamping threshold of the specular AA roughness increase, in `[0, 1]` (default: 0.2). */
    fun specularAntiAliasingThreshold(threshold: Float): MaterialBuilder = op { FilaMaterialBuilder_specularAntiAliasingThreshold(it, threshold) }

    /** Sets where refracted light is sampled from ([RefractionMode.NONE] by default). */
    fun refractionMode(mode: RefractionMode): MaterialBuilder = op { FilaMaterialBuilder_refractionMode(it, mode.ordinal) }

    /** Sets where reflections are sampled from ([ReflectionMode.DEFAULT] by default). */
    fun reflectionMode(mode: ReflectionMode): MaterialBuilder = op { FilaMaterialBuilder_reflectionMode(it, mode.ordinal) }

    /** Sets the refraction geometry model ([RefractionType.SOLID] by default). */
    fun refractionType(type: RefractionType): MaterialBuilder = op { FilaMaterialBuilder_refractionType(it, type.ordinal) }

    /** Makes the clear coat layer's IOR affect the base layer (physically correct; default: true). */
    fun clearCoatIorChange(clearCoatIorChange: Boolean): MaterialBuilder = op { FilaMaterialBuilder_clearCoatIorChange(it, clearCoatIorChange) }

    /** Flips the V texture coordinate at compile time (default: true, matching Filament's convention). */
    fun flipUV(flipUV: Boolean): MaterialBuilder = op { FilaMaterialBuilder_flipUV(it, flipUV) }

    /** Enables custom surface shading: the material provides its own `surfaceShading()` function. */
    fun customSurfaceShading(customSurfaceShading: Boolean): MaterialBuilder = op { FilaMaterialBuilder_customSurfaceShading(it, customSurfaceShading) }

    /** Simulates extra light bounces in occluded areas to reduce over-darkening from AO. */
    fun multiBounceAmbientOcclusion(multiBounceAO: Boolean): MaterialBuilder = op { FilaMaterialBuilder_multiBounceAmbientOcclusion(it, multiBounceAO) }

    /** Sets how AO is applied to specular lighting ([SpecularAmbientOcclusion.NONE] by default). */
    fun specularAmbientOcclusion(specularAO: SpecularAmbientOcclusion): MaterialBuilder = op { FilaMaterialBuilder_specularAmbientOcclusion(it, specularAO.ordinal) }

    /** Sets the transparency rendering strategy ([TransparencyMode.DEFAULT] by default). */
    fun transparencyMode(mode: TransparencyMode): MaterialBuilder = op { FilaMaterialBuilder_transparencyMode(it, mode.ordinal) }

    /** Selects the platform class to generate shaders for ([Platform.ALL] to cover everything). */
    fun platform(platform: Platform): MaterialBuilder = op { FilaMaterialBuilder_platform(it, platform.ordinal) }

    /** Selects the graphics API(s) to generate shaders for; fewer APIs → smaller package. */
    fun targetApi(api: TargetApi): MaterialBuilder {
        val apiNative = when (api) {
            TargetApi.OPENGL -> 0x01
            TargetApi.VULKAN -> 0x02
            TargetApi.METAL -> 0x04
            TargetApi.WEBGPU -> 0x08
            TargetApi.ALL -> 0x07 // OpenGL | Vulkan | Metal
        }
        return op { FilaMaterialBuilder_targetApi(it, apiNative) }
    }

    /** Sets the shader optimization level ([Optimization.PERFORMANCE] by default). */
    fun optimization(optimization: Optimization): MaterialBuilder = op { FilaMaterialBuilder_optimization(it, optimization.ordinal) }

    /** Bitmask of shader variants to exclude from compilation, shrinking the package. */
    fun variantFilter(variantFilter: Int): MaterialBuilder = op { FilaMaterialBuilder_variantFilter(it, variantFilter) }

    /** Uses the legacy (non-CPU-skinning-aware) morph target implementation. */
    fun useLegacyMorphing(): MaterialBuilder = op { FilaMaterialBuilder_useLegacyMorphing(it) }
}

@ExternalSymbolName("FilaMaterialBuilder_create")
private external fun FilaMaterialBuilder_create(): NativePointer

@ExternalSymbolName("FilaMaterialBuilder_destroy")
private external fun FilaMaterialBuilder_destroy(builder: NativePointer)

@ExternalSymbolName("FilaMaterialBuilder_build")
private external fun FilaMaterialBuilder_build(builder: NativePointer): NativePointer

@ExternalSymbolName("FilaMaterialBuilder_name")
private external fun FilaMaterialBuilder_name(builder: NativePointer, name: NativePointer)

@ExternalSymbolName("FilaMaterialBuilder_materialDomain")
private external fun FilaMaterialBuilder_materialDomain(builder: NativePointer, domain: Int)

@ExternalSymbolName("FilaMaterialBuilder_shading")
private external fun FilaMaterialBuilder_shading(builder: NativePointer, shading: Int)

@ExternalSymbolName("FilaMaterialBuilder_interpolation")
private external fun FilaMaterialBuilder_interpolation(builder: NativePointer, interpolation: Int)

@ExternalSymbolName("FilaMaterialBuilder_uniformParameter")
private external fun FilaMaterialBuilder_uniformParameter(builder: NativePointer, type: Int, precision: Int, name: NativePointer)

@ExternalSymbolName("FilaMaterialBuilder_uniformParameterArray")
private external fun FilaMaterialBuilder_uniformParameterArray(builder: NativePointer, type: Int, size: Int, precision: Int, name: NativePointer)

@ExternalSymbolName("FilaMaterialBuilder_samplerParameter")
private external fun FilaMaterialBuilder_samplerParameter(builder: NativePointer, type: Int, format: Int, precision: Int, name: NativePointer)

@ExternalSymbolName("FilaMaterialBuilder_variable")
private external fun FilaMaterialBuilder_variable(builder: NativePointer, variable: Int, name: NativePointer)

@ExternalSymbolName("FilaMaterialBuilder_require")
private external fun FilaMaterialBuilder_require(builder: NativePointer, attribute: Int)

@ExternalSymbolName("FilaMaterialBuilder_material")
private external fun FilaMaterialBuilder_material(builder: NativePointer, code: NativePointer)

@ExternalSymbolName("FilaMaterialBuilder_materialVertex")
private external fun FilaMaterialBuilder_materialVertex(builder: NativePointer, code: NativePointer)

@ExternalSymbolName("FilaMaterialBuilder_blending")
private external fun FilaMaterialBuilder_blending(builder: NativePointer, mode: Int)

@ExternalSymbolName("FilaMaterialBuilder_postLightingBlending")
private external fun FilaMaterialBuilder_postLightingBlending(builder: NativePointer, mode: Int)

@ExternalSymbolName("FilaMaterialBuilder_vertexDomain")
private external fun FilaMaterialBuilder_vertexDomain(builder: NativePointer, domain: Int)

@ExternalSymbolName("FilaMaterialBuilder_culling")
private external fun FilaMaterialBuilder_culling(builder: NativePointer, mode: Int)

@ExternalSymbolName("FilaMaterialBuilder_colorWrite")
private external fun FilaMaterialBuilder_colorWrite(builder: NativePointer, enable: Boolean)

@ExternalSymbolName("FilaMaterialBuilder_depthWrite")
private external fun FilaMaterialBuilder_depthWrite(builder: NativePointer, enable: Boolean)

@ExternalSymbolName("FilaMaterialBuilder_depthCulling")
private external fun FilaMaterialBuilder_depthCulling(builder: NativePointer, enable: Boolean)

@ExternalSymbolName("FilaMaterialBuilder_doubleSided")
private external fun FilaMaterialBuilder_doubleSided(builder: NativePointer, doubleSided: Boolean)

@ExternalSymbolName("FilaMaterialBuilder_maskThreshold")
private external fun FilaMaterialBuilder_maskThreshold(builder: NativePointer, threshold: Float)

@ExternalSymbolName("FilaMaterialBuilder_alphaToCoverage")
private external fun FilaMaterialBuilder_alphaToCoverage(builder: NativePointer, enable: Boolean)

@ExternalSymbolName("FilaMaterialBuilder_shadowMultiplier")
private external fun FilaMaterialBuilder_shadowMultiplier(builder: NativePointer, enable: Boolean)

@ExternalSymbolName("FilaMaterialBuilder_transparentShadow")
private external fun FilaMaterialBuilder_transparentShadow(builder: NativePointer, enable: Boolean)

@ExternalSymbolName("FilaMaterialBuilder_coloredPenumbra")
private external fun FilaMaterialBuilder_coloredPenumbra(builder: NativePointer, enable: Boolean)

@ExternalSymbolName("FilaMaterialBuilder_specularAntiAliasing")
private external fun FilaMaterialBuilder_specularAntiAliasing(builder: NativePointer, enable: Boolean)

@ExternalSymbolName("FilaMaterialBuilder_specularAntiAliasingVariance")
private external fun FilaMaterialBuilder_specularAntiAliasingVariance(builder: NativePointer, variance: Float)

@ExternalSymbolName("FilaMaterialBuilder_specularAntiAliasingThreshold")
private external fun FilaMaterialBuilder_specularAntiAliasingThreshold(builder: NativePointer, threshold: Float)

@ExternalSymbolName("FilaMaterialBuilder_clearCoatIorChange")
private external fun FilaMaterialBuilder_clearCoatIorChange(builder: NativePointer, enable: Boolean)

@ExternalSymbolName("FilaMaterialBuilder_flipUV")
private external fun FilaMaterialBuilder_flipUV(builder: NativePointer, enable: Boolean)

@ExternalSymbolName("FilaMaterialBuilder_customSurfaceShading")
private external fun FilaMaterialBuilder_customSurfaceShading(builder: NativePointer, enable: Boolean)

@ExternalSymbolName("FilaMaterialBuilder_multiBounceAmbientOcclusion")
private external fun FilaMaterialBuilder_multiBounceAmbientOcclusion(builder: NativePointer, enable: Boolean)

@ExternalSymbolName("FilaMaterialBuilder_specularAmbientOcclusion")
private external fun FilaMaterialBuilder_specularAmbientOcclusion(builder: NativePointer, mode: Int)

@ExternalSymbolName("FilaMaterialBuilder_refractionMode")
private external fun FilaMaterialBuilder_refractionMode(builder: NativePointer, mode: Int)

@ExternalSymbolName("FilaMaterialBuilder_reflectionMode")
private external fun FilaMaterialBuilder_reflectionMode(builder: NativePointer, mode: Int)

@ExternalSymbolName("FilaMaterialBuilder_refractionType")
private external fun FilaMaterialBuilder_refractionType(builder: NativePointer, type: Int)

@ExternalSymbolName("FilaMaterialBuilder_transparencyMode")
private external fun FilaMaterialBuilder_transparencyMode(builder: NativePointer, mode: Int)

@ExternalSymbolName("FilaMaterialBuilder_platform")
private external fun FilaMaterialBuilder_platform(builder: NativePointer, platform: Int)

@ExternalSymbolName("FilaMaterialBuilder_targetApi")
private external fun FilaMaterialBuilder_targetApi(builder: NativePointer, targetApi: Int)

@ExternalSymbolName("FilaMaterialBuilder_optimization")
private external fun FilaMaterialBuilder_optimization(builder: NativePointer, optimization: Int)

@ExternalSymbolName("FilaMaterialBuilder_variantFilter")
private external fun FilaMaterialBuilder_variantFilter(builder: NativePointer, variantFilter: Int)

@ExternalSymbolName("FilaMaterialBuilder_useLegacyMorphing")
private external fun FilaMaterialBuilder_useLegacyMorphing(builder: NativePointer)

@ExternalSymbolName("FilaPackage_destroy")
private external fun FilaPackage_destroy(pkg: NativePointer)

@ExternalSymbolName("FilaPackage_isValid")
private external fun FilaPackage_isValid(pkg: NativePointer): Boolean

@ExternalSymbolName("FilaPackage_getData")
private external fun FilaPackage_getData(pkg: NativePointer): NativePointer

@ExternalSymbolName("FilaPackage_getSize")
private external fun FilaPackage_getSize(pkg: NativePointer): Int
