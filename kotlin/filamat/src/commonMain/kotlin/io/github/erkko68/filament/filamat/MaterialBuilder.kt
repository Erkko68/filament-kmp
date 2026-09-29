package io.github.erkko68.filament.filamat

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Filament
import io.github.erkko68.filament.VertexBuffer.VertexAttribute
import io.github.erkko68.filament.filamat.capi.*
import io.github.erkko68.filament.interop.*

/**
 * MaterialBuilder compiles Filament material source code into binary packages.
 *
 * MaterialBuilder takes high-level material definitions and generates optimized shaders
 * for multiple backends (OpenGL, Vulkan, Metal, WebGPU). The resulting MaterialPackage
 * can be loaded by Filament's Material system.
 *
 * **Initialization:**
 * Call [MaterialBuilder.init] before building any material, and [MaterialBuilder.shutdown] when finished.
 *
 * **Compilation:**
 * Configure material properties using methods like name(), shading(), blendingMode(), etc.,
 * then call build() to generate the package.
 *
 * @see MaterialPackage
 */
class MaterialBuilder() {
    // Setters are recorded and replayed into a C builder that build() frees: no native builder outlives
    // a call, so none needs a finalizer. Replay is safe because filamat copies every string it's given.
    private val ops = ArrayList<(NativePointer) -> Unit>()
    private fun op(block: (NativePointer) -> Unit): MaterialBuilder = apply { ops.add(block) }

    // filamat keeps the view of this one until build(), so build() passes it last.
    private var materialSource: String? = null

    companion object {
        /** Initializes the compiler; call before building any material. Calls nest: pair each with [shutdown]. */
        fun init() {
            // Loads the native library: this may be the process's first call into it.
            Filament.init()
            FilaFilamatMaterialBuilderBase_init()
        }

        /** Releases the compiler's resources; [init] must precede another build. */
        fun shutdown() = FilaFilamatMaterialBuilderBase_shutdown()

        /** The vertex attributes materials can [require], with the names their shaders see them by. */
        fun getAttributeDatabase(): List<Attribute> {
            val count = FilaFilamatMaterialBuilder_getAttributeDatabase(NullPointer, 0)
            val handles = List(count) { FilaFilamatMaterialBuilderAttribute_create() }
            try {
                handles.useFilamatPointers { FilaFilamatMaterialBuilder_getAttributeDatabase(it, count) }
                return handles.map { a ->
                    Attribute(
                        name = filamatString(FilaFilamatMaterialBuilderAttribute_getName(a)).orEmpty(),
                        type = UniformType.entries[FilaFilamatMaterialBuilderAttribute_getType(a)],
                        location = FilaFilamatMaterialBuilderAttribute_getLocation(a).let { l -> VertexAttribute.entries.first { it.value == l } },
                        attributeName = copiedString { out, capacity -> FilaFilamatMaterialBuilderAttribute_getAttributeName(a, out, capacity) },
                        defineName = copiedString { out, capacity -> FilaFilamatMaterialBuilderAttribute_getDefineName(a, out, capacity) },
                    )
                }
            } finally {
                handles.forEach(::FilaFilamatMaterialBuilderAttribute_destroy)
            }
        }

        /** A string C copies out: asked for its length first, then copied. */
        private fun copiedString(copy: (NativePointer, Int) -> Int): String {
            val bytes = ByteArray(copy(NullPointer, 0))
            bytes.useFilamatPinned { copy(it, bytes.size) }
            return bytes.decodeToString()
        }
    }

    /**
     * A vertex attribute a material can [require]: its [name], shader type and slot, and the names
     * shaders use for it ([attributeName], e.g. `mesh_uv0`, and [defineName], e.g. `HAS_ATTRIBUTE_UV0`).
     */
    data class Attribute(
        val name: String,
        val type: UniformType,
        val location: VertexAttribute,
        val attributeName: String,
        val defineName: String,
    )

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
    enum class TargetApi(internal val value: Int) {
        /** OpenGL / OpenGL ES (GLSL). */
        OPENGL(0x01),
        /** Vulkan (SPIR-V). */
        VULKAN(0x02),
        /** Metal (MSL). */
        METAL(0x04),
        /** WebGPU (WGSL). */
        WEBGPU(0x08),
        /** OpenGL, Vulkan and Metal (not WebGPU). */
        ALL(0x07)
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

    /** Shader quality: lower trades accuracy for speed. [DEFAULT] picks per platform. */
    enum class ShaderQuality { DEFAULT, LOW, NORMAL, HIGH }

    /** A blend factor, for [customBlendFunctions]. */
    enum class BlendFunction {
        ZERO, ONE, SRC_COLOR, ONE_MINUS_SRC_COLOR, DST_COLOR, ONE_MINUS_DST_COLOR,
        SRC_ALPHA, ONE_MINUS_SRC_ALPHA, DST_ALPHA, ONE_MINUS_DST_ALPHA, SRC_ALPHA_SATURATE
    }

    /** Type of a specialization [constant]. */
    enum class ConstantType { INT, FLOAT, BOOL }

    /** A shader stage, for the stages a sampler [parameter] is visible to. */
    enum class ShaderStage { VERTEX, FRAGMENT, COMPUTE }

    /** Qualifier of a post-process [output]. */
    enum class VariableQualifier { OUT }

    /** Attachment a post-process [output] writes. */
    enum class OutputTarget { COLOR, DEPTH }

    /** Type of a post-process [output]. */
    enum class OutputType { FLOAT, FLOAT2, FLOAT3, FLOAT4, INT, INT2, INT3, INT4, UINT, UINT2, UINT3, UINT4 }

    /** Driver workarounds baked into the shaders. */
    enum class Workarounds(internal val value: Long) { NONE(0L), ALL(-1L) }

    /** Compiles the material and returns the resulting package (check [MaterialPackage.isValid] before use). */
    fun build(): MaterialPackage {
        val builder = FilaFilamatMaterialBuilder_create()
        check(builder != NullPointer) { "Failed to create MaterialBuilder" }
        try {
            ops.forEach { it(builder) }
            val source = materialSource
            val pkg = if (source == null) FilaFilamatMaterialBuilder_build(builder)
                else source.useFilamatCString { p -> FilaFilamatMaterialBuilder_materialSource(builder, p); FilaFilamatMaterialBuilder_build(builder) }
            check(pkg != NullPointer) { "Failed to build material" }
            try {
                val size = FilaFilamatPackage_getSize(pkg)
                val data = FilaFilamatPackage_getData(pkg)
                val bytes = if (data == NullPointer || size <= 0) ByteArray(0) else readFilamatBytes(data, size)
                return MaterialPackage(bytes).apply { isValid = FilaFilamatPackage_isValid(pkg) }
            } finally {
                FilaFilamatPackage_destroy(pkg)
            }
        } finally {
            FilaFilamatMaterialBuilder_destroy(builder)
        }
    }

    private fun string(value: String, call: (NativePointer, NativePointer) -> Unit): MaterialBuilder =
        op { b -> value.useFilamatCString { call(b, it) } }

    /** Skips validating samplers against the feature level's limits. */
    fun noSamplerValidation(enabled: Boolean): MaterialBuilder = op { FilaFilamatMaterialBuilder_noSamplerValidation(it, enabled) }

    /** Also generates ESSL 1.0 shaders, for feature level 0. */
    fun includeEssl1(enabled: Boolean): MaterialBuilder = op { FilaFilamatMaterialBuilder_includeEssl1(it, enabled) }

    /** Sets the material's name (shown in tooling and debug output). */
    fun name(name: String): MaterialBuilder = string(name) { b, p -> FilaFilamatMaterialBuilder_name(b, p) }

    /** Records the compiler parameters (matc's command line) in the package. */
    fun compilationParameters(params: String): MaterialBuilder = string(params) { b, p -> FilaFilamatMaterialBuilder_compilationParameters(b, p) }

    /** Sets the shading model (LIT, UNLIT, SUBSURFACE, CLOTH, SPECULAR_GLOSSINESS). */
    fun shading(shading: Shading): MaterialBuilder = op { FilaFilamatMaterialBuilder_shading(it, shading.ordinal) }

    /** Sets the interpolation of the shading normal (default: SMOOTH). */
    fun interpolation(interpolation: Interpolation): MaterialBuilder = op { FilaFilamatMaterialBuilder_interpolation(it, interpolation.ordinal) }

    /** Declares a uniform parameter, settable via `MaterialInstance.setParameter`. */
    fun parameter(name: String, type: UniformType, precision: ParameterPrecision = ParameterPrecision.DEFAULT): MaterialBuilder =
        string(name) { b, p -> FilaFilamatMaterialBuilder_parameter_UniformType_ParameterPrecision(b, p, type.ordinal, precision.ordinal) }

    /** Declares a uniform array parameter of [size] elements. */
    fun parameter(name: String, size: Int, type: UniformType, precision: ParameterPrecision = ParameterPrecision.DEFAULT): MaterialBuilder =
        string(name) { b, p -> FilaFilamatMaterialBuilder_parameter_size_t_UniformType_ParameterPrecision(b, p, size, type.ordinal, precision.ordinal) }

    /** Declares a specialization constant with its default value, settable per material instance. */
    fun constant(name: String, type: ConstantType, defaultValue: Int): MaterialBuilder =
        string(name) { b, p -> FilaFilamatMaterialBuilder_constant_int32_t(b, p, type.ordinal, defaultValue) }

    /** Declares a specialization constant with its default value, settable per material instance. */
    fun constant(name: String, type: ConstantType, defaultValue: Float): MaterialBuilder =
        string(name) { b, p -> FilaFilamatMaterialBuilder_constant_float(b, p, type.ordinal, defaultValue) }

    /** Declares a specialization constant with its default value, settable per material instance. */
    fun constant(name: String, type: ConstantType, defaultValue: Boolean): MaterialBuilder =
        string(name) { b, p -> FilaFilamatMaterialBuilder_constant_bool(b, p, type.ordinal, defaultValue) }

    /**
     * Declares a texture sampler parameter, settable via `MaterialInstance.setParameter`. [transformName]
     * names a mat3 uniform transforming its UVs; [stages] limits the shader stages that see it (all when null).
     */
    fun parameter(
        name: String,
        samplerType: SamplerType,
        format: SamplerFormat = SamplerFormat.FLOAT,
        precision: ParameterPrecision = ParameterPrecision.DEFAULT,
        filterable: Boolean = true,
        multisample: Boolean = false,
        transformName: String = "",
        stages: Set<ShaderStage>? = null,
    ): MaterialBuilder = op { b ->
        name.useFilamatCString { p ->
            transformName.useFilamatCString { t ->
                val flags = stages?.let { set -> intArrayOf(set.sumOf { 1 shl it.ordinal }) }
                // NULL stages is std::nullopt.
                fun call(s: NativePointer) = FilaFilamatMaterialBuilder_parameter_SamplerType_SamplerFormat_ParameterPrecision_bool_bool_char_optional(
                    b, p, samplerType.ordinal, format.ordinal, precision.ordinal, filterable, multisample, t, s,
                )
                if (flags == null) call(NullPointer) else flags.useFilamatPinned(::call)
            }
        }
    }

    /** Names a custom interpolant ([Variable] slot) passed from the vertex to the fragment stage. */
    fun variable(v: Variable, name: String): MaterialBuilder = string(name) { b, p -> FilaFilamatMaterialBuilder_variable(b, v.ordinal, p) }

    /** Names a custom interpolant with an explicit [precision]. */
    fun variable(v: Variable, name: String, precision: ParameterPrecision): MaterialBuilder =
        string(name) { b, p -> FilaFilamatMaterialBuilder_variable_ParameterPrecision(b, v.ordinal, p, precision.ordinal) }

    /** Requires the given vertex [attribute] to be present in rendered geometry (e.g. UV1, COLOR). */
    fun require(attribute: VertexAttribute): MaterialBuilder = op { FilaFilamatMaterialBuilder_require(it, attribute.value) }

    /** Sets the material domain ([MaterialDomain.SURFACE] by default). */
    fun materialDomain(materialDomain: MaterialDomain): MaterialBuilder = op { FilaFilamatMaterialBuilder_materialDomain(it, materialDomain.ordinal) }

    /** Sets the fragment-stage code, a GLSL `void material(inout MaterialInputs)`; [line] is its first line in the source, for error messages. */
    fun material(code: String, line: Int = 0): MaterialBuilder = string(code) { b, p -> FilaFilamatMaterialBuilder_material(b, p, line) }

    /** Sets the vertex-stage code, a GLSL `void materialVertex(inout MaterialVertexInputs)`; [line] as in [material]. */
    fun materialVertex(code: String, line: Int = 0): MaterialBuilder = string(code) { b, p -> FilaFilamatMaterialBuilder_materialVertex(b, p, line) }

    /** Sets the shader quality ([ShaderQuality.DEFAULT] by default). */
    fun quality(quality: ShaderQuality): MaterialBuilder = op { FilaFilamatMaterialBuilder_quality(it, quality.ordinal - 1) }

    /** Sets the minimum feature level the material needs. */
    fun featureLevel(featureLevel: Engine.FeatureLevel): MaterialBuilder = op { FilaFilamatMaterialBuilder_featureLevel(it, featureLevel.ordinal) }

    /** Sets how the material blends with the render target ([BlendingMode.OPAQUE] by default). */
    fun blending(blending: BlendingMode): MaterialBuilder = op { FilaFilamatMaterialBuilder_blending(it, blending.ordinal) }

    /** The blend factors of [BlendingMode.CUSTOM]. */
    fun customBlendFunctions(srcRGB: BlendFunction, srcA: BlendFunction, dstRGB: BlendFunction, dstA: BlendFunction): MaterialBuilder =
        op { FilaFilamatMaterialBuilder_customBlendFunctions(it, srcRGB.ordinal, srcA.ordinal, dstRGB.ordinal, dstA.ordinal) }

    /** Sets how the post-lighting color blends with the lit result. */
    fun postLightingBlending(blending: BlendingMode): MaterialBuilder = op { FilaFilamatMaterialBuilder_postLightingBlending(it, blending.ordinal) }

    /** Sets the coordinate space of the vertex output ([VertexDomain.OBJECT] by default). */
    fun vertexDomain(domain: VertexDomain): MaterialBuilder = op { FilaFilamatMaterialBuilder_vertexDomain(it, domain.ordinal) }

    /** Sets face culling ([CullingMode.BACK] by default). */
    fun culling(culling: CullingMode): MaterialBuilder = op { FilaFilamatMaterialBuilder_culling(it, culling.ordinal) }

    /** Enables/disables writes to the color buffer (default: true). */
    fun colorWrite(enable: Boolean): MaterialBuilder = op { FilaFilamatMaterialBuilder_colorWrite(it, enable) }

    /** Enables/disables writes to the depth buffer (default: true, except for blended modes). */
    fun depthWrite(enable: Boolean): MaterialBuilder = op { FilaFilamatMaterialBuilder_depthWrite(it, enable) }

    /** Enables/disables depth testing (default: true). */
    fun depthCulling(enable: Boolean): MaterialBuilder = op { FilaFilamatMaterialBuilder_depthCulling(it, enable) }

    /** Enables instanced rendering: shaders get the instance index. */
    fun instanced(enable: Boolean): MaterialBuilder = op { FilaFilamatMaterialBuilder_instanced(it, enable) }

    /** Renders both faces and flips the normal on back faces; implies [CullingMode.NONE]. */
    fun doubleSided(doubleSided: Boolean): MaterialBuilder = op { FilaFilamatMaterialBuilder_doubleSided(it, doubleSided) }

    /** Sets the alpha cutoff for [BlendingMode.MASKED] (default: 0.4). */
    fun maskThreshold(threshold: Float): MaterialBuilder = op { FilaFilamatMaterialBuilder_maskThreshold(it, threshold) }

    /** Converts fragment alpha to MSAA coverage; smoother [BlendingMode.MASKED] edges under MSAA. */
    fun alphaToCoverage(enable: Boolean): MaterialBuilder = op { FilaFilamatMaterialBuilder_alphaToCoverage(it, enable) }

    /** UNLIT only: multiplies the final color by the shadowing factor, for shadow-receiver planes. */
    fun shadowMultiplier(shadowMultiplier: Boolean): MaterialBuilder = op { FilaFilamatMaterialBuilder_shadowMultiplier(it, shadowMultiplier) }

    /** Makes this transparent material cast (dithered) transparent shadows. */
    fun transparentShadow(transparentShadow: Boolean): MaterialBuilder = op { FilaFilamatMaterialBuilder_transparentShadow(it, transparentShadow) }

    /** Enables colored shadow penumbras for this material's transparent shadows. */
    fun coloredPenumbra(coloredPenumbra: Boolean): MaterialBuilder = op { FilaFilamatMaterialBuilder_coloredPenumbra(it, coloredPenumbra) }

    /** Reduces specular shimmering/aliasing on curved geometry (LIT models only). */
    fun specularAntiAliasing(specularAntiAliasing: Boolean): MaterialBuilder = op { FilaFilamatMaterialBuilder_specularAntiAliasing(it, specularAntiAliasing) }

    /** Screen-space variance of the specular AA filter kernel, in `[0, 1]` (default: 0.15). */
    fun specularAntiAliasingVariance(screenSpaceVariance: Float): MaterialBuilder = op { FilaFilamatMaterialBuilder_specularAntiAliasingVariance(it, screenSpaceVariance) }

    /** Clamping threshold of the specular AA roughness increase, in `[0, 1]` (default: 0.2). */
    fun specularAntiAliasingThreshold(threshold: Float): MaterialBuilder = op { FilaFilamatMaterialBuilder_specularAntiAliasingThreshold(it, threshold) }

    /** Makes the clear coat layer's IOR affect the base layer (physically correct; default: true). */
    fun clearCoatIorChange(clearCoatIorChange: Boolean): MaterialBuilder = op { FilaFilamatMaterialBuilder_clearCoatIorChange(it, clearCoatIorChange) }

    /** Flips the V texture coordinate at compile time (default: true, matching Filament's convention). */
    fun flipUV(flipUV: Boolean): MaterialBuilder = op { FilaFilamatMaterialBuilder_flipUV(it, flipUV) }

    /** Computes fog linearly rather than exponentially, a cheaper approximation. */
    fun linearFog(enabled: Boolean): MaterialBuilder = op { FilaFilamatMaterialBuilder_linearFog(it, enabled) }

    /** Fades shadows out towards the shadow far plane. */
    fun shadowFarAttenuation(enabled: Boolean): MaterialBuilder = op { FilaFilamatMaterialBuilder_shadowFarAttenuation(it, enabled) }

    /** Simulates extra light bounces in occluded areas to reduce over-darkening from AO. */
    fun multiBounceAmbientOcclusion(multiBounceAO: Boolean): MaterialBuilder = op { FilaFilamatMaterialBuilder_multiBounceAmbientOcclusion(it, multiBounceAO) }

    /** Sets how AO is applied to specular lighting ([SpecularAmbientOcclusion.NONE] by default). */
    fun specularAmbientOcclusion(specularAO: SpecularAmbientOcclusion): MaterialBuilder = op { FilaFilamatMaterialBuilder_specularAmbientOcclusion(it, specularAO.ordinal) }

    /** Sets where refracted light is sampled from ([RefractionMode.NONE] by default). */
    fun refractionMode(refraction: RefractionMode): MaterialBuilder = op { FilaFilamatMaterialBuilder_refractionMode(it, refraction.ordinal) }

    /** Sets the refraction geometry model ([RefractionType.SOLID] by default). */
    fun refractionType(refractionType: RefractionType): MaterialBuilder = op { FilaFilamatMaterialBuilder_refractionType(it, refractionType.ordinal) }

    /** Sets where reflections are sampled from ([ReflectionMode.DEFAULT] by default). */
    fun reflectionMode(mode: ReflectionMode): MaterialBuilder = op { FilaFilamatMaterialBuilder_reflectionMode(it, mode.ordinal) }

    /** Sets the transparency rendering strategy ([TransparencyMode.DEFAULT] by default). */
    fun transparencyMode(mode: TransparencyMode): MaterialBuilder = op { FilaFilamatMaterialBuilder_transparencyMode(it, mode.ordinal) }

    /** Sets the stereoscopic technique the shaders support. */
    fun stereoscopicType(stereoscopicType: Engine.StereoscopicType): MaterialBuilder = op { FilaFilamatMaterialBuilder_stereoscopicType(it, stereoscopicType.ordinal) }

    /** Sets how many eyes stereoscopic rendering draws. */
    fun stereoscopicEyeCount(eyeCount: Int): MaterialBuilder = op { FilaFilamatMaterialBuilder_stereoscopicEyeCount(it, eyeCount) }

    /** Enables custom surface shading: the material provides its own `surfaceShading()` function. */
    fun customSurfaceShading(customSurfaceShading: Boolean): MaterialBuilder = op { FilaFilamatMaterialBuilder_customSurfaceShading(it, customSurfaceShading) }

    /** Selects the platform class to generate shaders for ([Platform.ALL] to cover everything). */
    fun platform(platform: Platform): MaterialBuilder = op { FilaFilamatMaterialBuilder_platform(it, platform.ordinal) }

    /** Selects the graphics API(s) to generate shaders for; fewer APIs → smaller package. */
    fun targetApi(targetApi: TargetApi): MaterialBuilder = op { FilaFilamatMaterialBuilder_targetApi(it, targetApi.value) }

    /** Sets the shader optimization level ([Optimization.PERFORMANCE] by default). */
    fun optimization(optimization: Optimization): MaterialBuilder = op { FilaFilamatMaterialBuilder_optimization(it, optimization.ordinal) }

    /** Sets the driver workarounds baked into the shaders. */
    fun workarounds(workarounds: Workarounds): MaterialBuilder = op { FilaFilamatMaterialBuilder_workarounds(it, workarounds.value) }

    /** Prints the generated shaders to the log. */
    fun printShaders(printShaders: Boolean): MaterialBuilder = op { FilaFilamatMaterialBuilder_printShaders(it, printShaders) }

    /** Saves each variant's raw shader text, for debugging. */
    fun saveRawVariants(saveRawVariants: Boolean): MaterialBuilder = op { FilaFilamatMaterialBuilder_saveRawVariants(it, saveRawVariants) }

    /** Includes debug info in the generated SPIR-V. */
    fun generateDebugInfo(generateDebugInfo: Boolean): MaterialBuilder = op { FilaFilamatMaterialBuilder_generateDebugInfo(it, generateDebugInfo) }

    /** Bitmask of shader variants to exclude from compilation, shrinking the package. */
    fun variantFilter(variantFilter: Int): MaterialBuilder = op { FilaFilamatMaterialBuilder_variantFilter(it, variantFilter) }

    /** Adds a preprocessor define to the material's shader code. */
    fun shaderDefine(name: String, value: String): MaterialBuilder =
        op { b -> name.useFilamatCString { n -> value.useFilamatCString { v -> FilaFilamatMaterialBuilder_shaderDefine(b, n, v) } } }

    /** Adds a fragment shader output; post-process materials only. [location] -1 lets filamat pick. */
    fun output(qualifier: VariableQualifier, target: OutputTarget, precision: ParameterPrecision, type: OutputType, name: String, location: Int = -1): MaterialBuilder =
        string(name) { b, p -> FilaFilamatMaterialBuilder_output(b, qualifier.ordinal, target.ordinal, precision.ordinal, type.ordinal, p, location) }

    /** Lets post-process materials read the framebuffer they write. */
    fun enableFramebufferFetch(): MaterialBuilder = op { FilaFilamatMaterialBuilder_enableFramebufferFetch(it) }

    /** Applies the view's TAA jitter to [VertexDomain.DEVICE] positions. */
    fun vertexDomainDeviceJittered(enabled: Boolean): MaterialBuilder = op { FilaFilamatMaterialBuilder_vertexDomainDeviceJittered(it, enabled) }

    /** Uses the legacy (non-CPU-skinning-aware) morph target implementation. */
    fun useLegacyMorphing(): MaterialBuilder = op { FilaFilamatMaterialBuilder_useLegacyMorphing(it) }

    /** Sets a compute material's work group size. */
    fun groupSize(x: Int, y: Int, z: Int): MaterialBuilder = op { b -> intArrayOf(x, y, z).useFilamatPinned { FilaFilamatMaterialBuilder_groupSize(b, it) } }

    /** Uses Filament's default depth variant, skipping a custom vertex shader in depth-only passes. */
    fun useDefaultDepthVariant(): MaterialBuilder = op { FilaFilamatMaterialBuilder_useDefaultDepthVariant(it) }

    /** Records the material's `.mat` source in the package. */
    fun materialSource(source: String): MaterialBuilder = apply { materialSource = source }

    /** Sets the API level the material is compiled against (default: 1). */
    fun setApiLevel(apiLevel: Int): MaterialBuilder = op { FilaFilamatMaterialBuilder_setApiLevel(it, apiLevel) }
}
