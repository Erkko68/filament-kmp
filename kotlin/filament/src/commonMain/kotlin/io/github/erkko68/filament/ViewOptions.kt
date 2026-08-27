package io.github.erkko68.filament

// View option structs. They carry no platform state — every actual declared the same
// fields with the same defaults — so they live here as data classes instead of as
// fourteen expect/actual pairs repeated four times over.

/**
 * Dynamic resolution options control rendering resolution scaling to meet target frame rates.
 *
 * Dynamic resolution can be used to either reach a desired target frame rate by lowering the
 * resolution of a View, or to increase the quality when rendering is faster than the target
 * frame rate. The scale factors can be controlled on each X and Y axis independently.
 * By default, all scale factors are set to 1.0.
 *
 * Dynamic resolution is only supported on platforms where the time to render a frame can be
 * measured accurately. On platforms where this is not supported, Dynamic Resolution can't be
 * enabled unless minScale == maxScale.
 */
data class DynamicResolutionOptions(
    /**
     * Enable or disable dynamic resolution on this View. Default: false.
     */
    var enabled: Boolean = false,

    /**
     * By default the system scales the major axis first. Set this to true to force
     * homogeneous scaling. Default: false.
     */
    var homogeneousScaling: Boolean = false,

    /**
     * The minimum scale in X and Y this View should use. Default: (0.5, 0.5).
     */
    var minScale: Float = 0.5f,

    /**
     * The maximum scale in X and Y this View should use. Default: (1.0, 1.0).
     */
    var maxScale: Float = 1.0f,

    /**
     * Sharpness when Quality.MEDIUM or higher is used [0 (disabled), 1 (sharpest)].
     * Default: 0.9.
     */
    var sharpness: Float = 0.9f,

    /**
     * Upscaling quality.
     * - LOW: bilinear filtered blit. Fastest, poor quality
     * - MEDIUM: Qualcomm Snapdragon Game Super Resolution (SGSR) 1.0
     * - HIGH: AMD FidelityFX FSR1 w/ mobile optimizations
     * - ULTRA: AMD FidelityFX FSR1
     *
     * FSR1 and SGSR require a well anti-aliased (MSAA or TAA), noise free scene.
     * Avoid FXAA and dithering. Default: LOW.
     */
    var quality: View.Quality = View.Quality.LOW,
)

/**
 * Options to control color buffer precision and quality settings.
 *
 * A quality of HIGH or ULTRA means using an RGB16F or RGBA16F color buffer. Colors in the
 * LDR range (0..1) have a 10 bit precision. A quality of LOW or MEDIUM means using an
 * R11G11B10F opaque color buffer or an RGBA16F transparent color buffer. With R11G11B10F,
 * colors in the LDR range have a precision of either 6 bits (red and green) or 5 bits (blue).
 */
data class RenderQuality(
    /**
     * Sets the quality of the HDR color buffer. Default: HIGH.
     */
    var hdrColorBuffer: View.Quality = View.Quality.HIGH,
)

/**
 * Options to control the bloom post-processing effect.
 *
 * Bloom allows bright areas to glow and bleed into surrounding areas, creating a
 * luminous quality. The effect can be enhanced with lens flare, lens artifacts, and
 * customizable bloom color and spread.
 */
data class BloomOptions(
    /**
     * Enable or disable the bloom post-processing effect. Default: false.
     */
    var enabled: Boolean = false,

    /**
     * Number of successive blurs to achieve the blur effect. Minimum is 3 and maximum is 12.
     * This value together with resolution influences the spread of the blur effect.
     * This value can be silently reduced to accommodate the original image size. Default: 6.
     */
    var levels: Int = 6,

    /**
     * Resolution of bloom's minor axis. Minimum value is 2^levels and maximum is lower of
     * the original resolution and 4096. This parameter is silently clamped to the minimum
     * and maximum. Default: 384.
     */
    var resolution: Int = 384,

    /**
     * How much of the bloom is added to the original image, between 0 and 1. Default: 0.10.
     */
    var strength: Float = 0.10f,

    /**
     * When enabled, a threshold at 1.0 is applied on the source image, useful for artistic
     * reasons and usually needed when a dirt texture is used. Default: true.
     */
    var threshold: Boolean = true,

    /**
     * A dirt/scratch/smudges texture (RGB) which gets added to the bloom effect.
     * Smudges are visible where bloom occurs. Threshold must be enabled for the dirt
     * effect to work properly. Default: null.
     */
    var dirt: Texture? = null,

    /**
     * Strength of the dirt texture. Default: 0.2.
     */
    var dirtStrength: Float = 0.2f,

    /**
     * Bloom quality level.
     * - LOW (default): use a more optimized down-sampling filter, however there can be
     *   artifacts with dynamic resolution
     * - MEDIUM: Good balance between quality and performance
     * - HIGH: Bloom resolution is automatically increased to avoid artifacts. Can be
     *   significantly slower on mobile.
     *
     * Default: LOW.
     */
    var quality: View.Quality = View.Quality.LOW,

    /**
     * Enable screen-space lens flare effect. Default: false.
     */
    var lensFlare: Boolean = false,

    /**
     * Enable starburst effect on lens flare. Default: true.
     */
    var starburst: Boolean = true,

    /**
     * Amount of chromatic aberration in the lens flare effect. Default: 0.005.
     */
    var chromaticAberration: Float = 0.005f,

    /**
     * Number of flare "ghosts" (lens artifacts). Default: 4.
     */
    var ghostCount: Int = 4,

    /**
     * Spacing of the ghost in screen units [0, 1). Default: 0.6.
     */
    var ghostSpacing: Float = 0.6f,

    /**
     * HDR threshold for the ghosts. Default: 10.0.
     */
    var ghostThreshold: Float = 10.0f,

    /**
     * Radius of halo in vertical screen units [0, 0.5]. Default: 0.4.
     */
    var haloRadius: Float = 0.4f,

    /**
     * Thickness of halo in vertical screen units, 0 to disable. Default: 0.1.
     */
    var haloThickness: Float = 0.1f,

    /**
     * HDR threshold for the halo. Default: 10.0.
     */
    var haloThreshold: Float = 10.0f,

    /**
     * Limit highlights to this value before bloom, range [10, +inf]. Default: 1000.0.
     */
    var highlight: Float = 1000.0f,

    /**
     * How the bloom effect is applied.
     *
     * - ADD: Bloom is modulated by the strength parameter and added to the scene
     * - INTERPOLATE: Bloom is interpolated with the scene using the strength parameter
     *
     * Default: ADD.
     */
    var blendMode: BlendMode = BlendMode.ADD,
) {

        /**
         * Bloom blending mode.
         *
         * - ADD: Bloom is modulated by strength and added to the scene
         * - INTERPOLATE: Bloom is interpolated with the scene using strength
         */
        enum class BlendMode { ADD, INTERPOLATE }
}

/**
 * Options to control large-scale fog in the scene.
 *
 * Materials can enable the linearFog property, which uses a simplified, linear equation for
 * fog calculation; in this mode, the heightFalloff is ignored as well as the mipmap selection
 * in IBL or skyColor mode.
 */
data class FogOptions(
    /**
     * Enable or disable large-scale fog. Default: false.
     */
    var enabled: Boolean = false,

    /**
     * Distance in world units [m] from the camera to where the fog starts (>= 0.0).
     * Default: 0.0.
     */
    var distance: Float = 0.0f,

    /**
     * Extinction factor in [1/m] at the fog height. Controls how much light is absorbed and
     * out-scattered per unit of distance. Each unit of extinction reduces incoming light to
     * 37% of its original value. In linearFog mode, this is the slope of the linear equation
     * if heightFalloff is 0. Default: 0.1.
     */
    var density: Float = 0.1f,

    /**
     * Fog's floor in world units [m]. This sets the "sea level". Default: 0.0.
     */
    var height: Float = 0.0f,

    /**
     * How fast the fog dissipates with altitude. heightFalloff has a unit of [1/m].
     * It can be expressed as 1/H, where H is the altitude change in world units [m] that
     * causes a factor 2.78 (e) change in fog density. A falloff of 0 means the fog density
     * is constant everywhere. Ignored in linearFog mode if set to 0. Default: 1.0.
     */
    var heightFalloff: Float = 1.0f,

    /**
     * Fog's color used for ambient light in-scattering. A good value is the average of the
     * ambient light, possibly tinted towards blue for outdoor environments. Color components
     * should be between 0 and 1; values above 1 are allowed but could create a non
     * energy-conservative fog. Used as a tint when fogColorFromIbl is enabled. Default: white.
     */
    var color: FloatArray = floatArrayOf(1.0f, 1.0f, 1.0f),

    /**
     * Distance in world units [m] after which the fog calculation is disabled. This can be
     * used to exclude the skybox. The SkyBox is typically at a distance of 1e19 in world
     * space. Default: infinity.
     */
    var cutOffDistance: Float = Float.POSITIVE_INFINITY,

    /**
     * Fog's maximum opacity between 0 and 1. Ignored in linearFog mode. Default: 1.0.
     */
    var maximumOpacity: Float = 1.0f,

    /**
     * Distance in world units [m] from the camera where the Sun in-scattering starts.
     * Ignored in linearFog mode. Default: 0.0.
     */
    var inScatteringStart: Float = 0.0f,

    /**
     * Very inaccurately simulates the Sun's in-scattering. Size of the Sun in-scattering
     * (>0 to activate). Good values are >> 1 (e.g., ~10 - 100). Smaller values result in a
     * larger scattering size. Ignored in linearFog mode. Default: -1.0.
     */
    var inScatteringSize: Float = -1.0f,

    /**
     * The fog color will be sampled from the IBL in the view direction and tinted by the
     * color parameter. This simulates a more anisotropic phase-function. Ignored when
     * skyColor is specified. Default: false.
     */
    var fogColorFromIbl: Boolean = false,

    /**
     * Optional sky texture (mipmapped cubemap) for fog color sampling. When provided, the
     * fog color will be sampled from this texture, with higher resolution mip levels used
     * for objects at the far clip plane and lower resolution mip levels for closer objects.
     * fogColorFromIbl is ignored when this is specified. In linearFog mode, mipmap level 0
     * is always used. Default: null.
     */
    var skyColor: Texture? = null,
) {
    // `color` is an array, whose generated equality is by reference; compare contents.
    private fun key() = listOf(enabled, distance, density, height, heightFalloff, color.toList(), cutOffDistance, maximumOpacity, inScatteringStart, inScatteringSize, fogColorFromIbl, skyColor)
    override fun equals(other: Any?) = this === other || (other is FogOptions && key() == other.key())
    override fun hashCode() = key().hashCode()
}

/**
 * Options to control Depth of Field (DoF) effect in the scene.
 *
 * cocScale can be used to set the depth of field blur independently of the camera aperture,
 * e.g., for artistic reasons. This can be achieved by setting:
 * cocScale = cameraAperture / desiredDoFAperture.
 */
data class DepthOfFieldOptions(
    /**
     * Enable or disable depth of field effect. Default: false.
     */
    var enabled: Boolean = false,

    /**
     * Circle of confusion scale factor (amount of blur). Default: 1.0.
     */
    var cocScale: Float = 1.0f,
    var cocAspectRatio: Float = 1.0f,

    /**
     * Maximum aperture diameter in meters (zero to disable rotation). Default: 0.01.
     */
    var maxApertureDiameter: Float = 0.01f,

    /**
     * Filter to use for filling gaps in the kernel. Default: MEDIAN.
     */
    var filter: Filter = Filter.MEDIAN,

    /**
     * Perform DoF processing at native resolution. Default: false.
     */
    var nativeResolution: Boolean = false,

    /**
     * Number of rings used by the gather kernels for foreground. The number of rings affects
     * quality and performance. The actual number of samples per pixel is (ringCount * 2 - 1)².
     * Examples: 3 rings = 25 (5x5), 4 rings = 49 (7x7), 5 rings = 81 (9x9), 17 rings = 1089 (33x33).
     * A value of 0 means default (5 on desktop, 3 on mobile). Default: 0.
     */
    var foregroundRingCount: Int = 0,

    /**
     * Number of rings used by the gather kernels for background. Default: 0.
     */
    var backgroundRingCount: Int = 0,

    /**
     * Number of rings used by the gather kernels for fast tiles (regions with similar CoC).
     * Default: 0.
     */
    var fastGatherRingCount: Int = 0,

    /**
     * Maximum circle-of-confusion in pixels for the foreground, must be in [0, 32] range.
     * A value of 0 means default (32 on desktop, 24 on mobile). Default: 0.
     */
    var maxForegroundCOC: Int = 0,

    /**
     * Maximum circle-of-confusion in pixels for the background, must be in [0, 32] range.
     * A value of 0 means default (32 on desktop, 24 on mobile). Default: 0.
     */
    var maxBackgroundCOC: Int = 0,
) {

        /**
         * Depth of Field filter types.
         *
         * - NONE: No filtering
         * - UNUSED: Unused filter type
         * - MEDIAN: Median filtering for gap filling
         */
        enum class Filter { NONE, UNUSED, MEDIAN }
}

/**
 * Options to control the vignetting effect (darkening at screen edges).
 */
data class VignetteOptions(
    /**
     * Enable or disable the vignette effect. Default: false.
     */
    var enabled: Boolean = false,

    /**
     * High values restrict the vignette closer to the corners, between 0 and 1.
     * Default: 0.5.
     */
    var midPoint: Float = 0.5f,

    /**
     * Controls the shape of th {
    // `color` is an array, whose generated equality is by reference; compare contents.
    private fun key() = listOf(enabled, midPoint, roundness, feather, color.toList())
    override fun equals(other: Any?) = this === other || (other is VignetteOptions && key() == other.key())
    override fun hashCode() = key().hashCode()
}e vignette, from a rounded rectangle (0.0), to an oval (0.5),
     * to a circle (1.0). Default: 0.5.
     */
    var roundness: Float = 0.5f,

    /**
     * Softening amount of the vignette effect, between 0 and 1. Default: 0.5.
     */
    var feather: Float = 0.5f,

    /**
     * Color of the vignette effect (alpha is currently ignored). Default: black.
     */
    var color: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f),
)

/**
 * Options for screen space Ambient Occlusion (SSAO) and Screen Space Cone Tracing (SSCT).
 *
 * Ambient occlusion darkens crevices and contact points, adding realism and depth to scenes.
 */
data class AmbientOcclusionOptions(
    /**
     * Enable or disable screen-space ambient occlusion. Default: false.
     */
    var enabled: Boolean = false,

    /**
     * Type of ambient occlusion algorithm. Default: [AmbientOcclusionType.SAO].
     */
    var aoType: AmbientOcclusionType = AmbientOcclusionType.SAO,

    /**
     * Ambient Occlusion radius in meters, between 0 and ~10. Default: 0.3.
     */
    var radius: Float = 0.3f,

    /**
     * Self-occlusion bias in meters. Use to avoid self-occlusion. Between 0 and a few mm.
     * No effect when aoType is set to GTAO. Default: 0.0005.
     */
    var bias: Float = 0.0005f,

    /**
     * Controls ambient occlusion's contrast. Must be positive. Default: 1.0.
     */
    var power: Float = 1.0f,

    /**
     * Strength of the Ambient Occlusion effect. Default: 1.0.
     */
    var intensity: Float = 0.8f,

    /**
     * How each dimension of the AO buffer is scaled. Must be either 0.5 or 1.0. Default: 0.5.
     */
    var resolution: Float = 0.5f,

    /**
     * Depth distance that constitutes an edge for filtering. Default: 0.05.
     */
    var bilateralThreshold: Float = 0.05f,

    /**
     * Minimum angle in radians to consider. No effect when aoType is set to GTAO. Default: 0.0.
     */
    var minHorizonAngleRad: Float = 0.0f,

    /**
     * Affects number of samples used for AO and parameters for filtering. Default: LOW.
     */
    var quality: View.Quality = View.Quality.LOW,

    /**
     * Affects AO smoothness. Recommended setting to HIGH when aoType is set to GTAO.
     * Default: MEDIUM.
     */
    var lowPassFilter: View.Quality = View.Quality.MEDIUM,

    /**
     * Affects AO buffer upsampling quality. Default: LOW.
     */
    var upsampling: View.Quality = View.Quality.LOW,

    /**
     * Enable bent normals computation from AO, and specular AO. Default: false.
     */
    var bentNormals: Boolean = false,

    /**
     * Screen Space Cone Tracing (SSCT) options for ambient shadows from dominant light.
     */
    var ssct: Ssct = Ssct(),
    var gtao: Gtao = Gtao(),
) {
        /**
         * The occlusion algorithm to use.
         */
        enum class AmbientOcclusionType {
            /** Scalable Ambient Occlusion. */
            SAO,
            /** Ground Truth-based Ambient Occlusion. */
            GTAO
        }

    /**
     * Screen Space Cone Tracing options for ambient shadows.
     */
    /**
     * Screen Space Cone Tracing options for ambient shadows.
     */
    data class Ssct(
        /**
         * Enable or disable SSCT. Default: false.
         */
        var enabled: Boolean = false,

        /**
         * Full cone angle in radians, between 0 and pi/2. Default: 1.0.
         */
        var lightConeRad: Float = 1.0f,

        /**
         * How far shadows can be cast. Default: 0.3.
         */
        var shadowDistance: Float = 0.3f,

        /**
         * Maximum distance for contact. Default: 1.0.
         */
        var contactDistanceMax: Float = 1.0f,

        /**
         * In {
        // `lightDirection` is an array, whose generated equality is by reference; compare contents.
        private fun key() = listOf(enabled, lightConeRad, shadowDistance, contactDistanceMax, intensity, lightDirection.toList(), depthBias, depthSlopeBias, sampleCount, rayCount)
        override fun equals(other: Any?) = this === other || (other is Ssct && key() == other.key())
        override fun hashCode() = key().hashCode()
    }tensity of SSCT effect. Default: 0.8.
         */
        var intensity: Float = 0.8f,

        /**
         * Light direction vector. Default: (0, -1, 0).
         */
        var lightDirection: FloatArray = floatArrayOf(0f, -1f, 0f),

        /**
         * Depth bias in world units to mitigate self shadowing. Default: 0.01.
         */
        var depthBias: Float = 0.01f,

        /**
         * Depth slope bias to mitigate self shadowing. Default: 0.01.
         */
        var depthSlopeBias: Float = 0.01f,

        /**
         * Tracing sample count, between 1 and 255. Default: 4.
         */
        var sampleCount: Int = 4,

        /**
         * Number of rays to trace, between 1 and 255. Default: 1.
         */
        var rayCount: Int = 1,
    )

    /**
     * Ground-Truth-based Ambient Occlusion options.
     */
    /**
     * Ground-Truth-based Ambient Occlusion options.
     */
    data class Gtao(
        /**
         * Number of slices. Higher values make less noise. Default: 4.
         */
        var sampleSliceCount: Int = 4,

        /**
         * Number of steps the radius is divided into for integration. Higher values make less
         * bias. Default: 3.
         */
        var sampleStepsPerSlice: Int = 3,

        /**
         * Thickness heuristic, should be close to 0. No effect when [useVisibilityBitmasks] is
         * true. Default: 0.004.
         */
        var thicknessHeuristic: Float = 0.004f,

        /**
         * Enables visibility-bitmask mode. Bent normals do not work under this mode.
         *
         * Changing this at runtime is very expensive — it may trigger a shader recompilation.
         * Default: false.
         */
        var useVisibilityBitmasks: Boolean = false,

        /**
         * Constant world-space thickness assumed for on-screen objects. Only takes effect when
         * [useVisibilityBitmasks] is true. Default: 0.5.
         */
        var constThickness: Float = 0.5f,

        /**
         * Increases thickness with distance to keep detail on distant surfaces.
         *
         * Changing this at runtime is very expensive — it may trigger a shader recompilation.
         * Default: false.
         */
        var linearThickness: Boolean = false,
    )
}

/**
 * Options for Temporal Anti-aliasing (TAA).
 *
 * Most TAA parameters are extremely costly to change, as they will trigger the TAA post-process
 * shaders to be recompiled. These options should be changed or set during initialization.
 * `feedback` and `jitterPattern`, however, can be changed at any time. A feedback of 0.1
 * effectively accumulates a maximum of 19 samples in steady state.
 */
data class TemporalAntiAliasingOptions(
    /**
     * Enable or disable temporal anti-aliasing. Default: false.
     */
    var enabled: Boolean = false,

    /**
     * History feedback, between 0 (maximum temporal AA) and 1 (no temporal AA). Default: 0.12.
     */
    var feedback: Float = 0.12f,

    /**
     * Texturing LOD bias (typically -1 or -2). Default: -1.0.
     */
    var lodBias: Float = -1.0f,

    /**
     * Post-TAA sharpening, especially useful when upscaling is true. Default: 0.0.
     */
    var sharpness: Float = 0.0f,

    /**
     * Upscaling factor. Disables Dynamic Resolution. Default: 1.0 (Beta).
     */
    var upscaling: Float = 1.0f,

    /**
     * Whether to filter the history buffer. Default: true.
     */
    var filterHistory: Boolean = true,

    /**
     * Whether to apply the reconstruction filter to the input. Default: true.
     */
    var filterInput: Boolean = true,

    /**
     * Whether to use the YcoCg color-space for history rejection. Default: false.
     */
    var useYCoCg: Boolean = false,

    /**
     * Set to true for HDR content. Default: true.
     */
    var hdr: Boolean = true,

    /**
     * Type of color gamut box. Default: [BoxType.AABB].
     */
    var boxType: BoxType = BoxType.AABB,

    /**
     * Clipping algorithm. Default: [BoxClipping.ACCURATE].
     */
    var boxClipping: BoxClipping = BoxClipping.ACCURATE,

    /**
     * Jitter pattern for sampling. Default: [JitterPattern.HALTON_23_X16].
     */
    var jitterPattern: JitterPattern = JitterPattern.HALTON_23_X16,

    /**
     * High values increase ghosting artifacts, lower values increase jittering, range [0.75, 1.25].
     * Default: 1.0.
     */
    var varianceGamma: Float = 1.0f,

    /**
     * Adjust the feedback dynamically to reduce flickering. Default: false.
     */
    var preventFlickering: Boolean = false,

    /**
     * Whether to apply history reprojection (debug option). Default: true.
     */
    var historyReprojection: Boolean = true,
) {
        /**
         * Type of color gamut box used for history rejection.
         */
        enum class BoxType {
            /** Use an AABB neighborhood. */
            AABB,
            /** Use both AABB and variance. */
            AABB_VARIANCE
        }

        /**
         * Clipping algorithm for history rejection.
         */
        enum class BoxClipping {
            /** Accurate box clipping. */
            ACCURATE,
            /** Clamping. */
            CLAMP,
            /** No rejections (use for debugging). */
            NONE
        }

        /**
         * Jitter pattern used for sampling.
         */
        enum class JitterPattern {
            /** 4-sample rotated grid sampling. */
            RGSS_X4,
            /** 4-sample uniform grid in helix sequence. */
            UNIFORM_HELIX_X4,
            /** 8 samples of Halton 2,3. */
            HALTON_23_X8,
            /** 16 samples of Halton 2,3. */
            HALTON_23_X16,
            /** 32 samples of Halton 2,3. */
            HALTON_23_X32
        }
}

/**
 * Options for Screen-space Reflections (SSR).
 *
 * SSR allows objects to reflect their environment in real-time using only screen-space
 * information, making it very efficient but limited to on-screen reflections.
 */
data class ScreenSpaceReflectionsOptions(
    /**
     * Enable or disable screen-space reflections. Default: false.
     */
    var enabled: Boolean = false,

    /**
     * Ray thickness in world units. Default: 0.1.
     */
    var thickness: Float = 0.1f,

    /**
     * Bias in world units to prevent self-intersections. Default: 0.01.
     */
    var bias: Float = 0.01f,

    /**
     * Maximum distance in world units to raycast. Default: 3.0.
     */
    var maxDistance: Float = 3.0f,

    /**
     * Stride in texels for samples along the ray. Default: 2.0.
     */
    var stride: Float = 2.0f,
)

/**
 * View-level options for VSM (Variance Shadow Maps) shadowing.
 *
 * Warning: This API is still experimental and subject to change.
 */
data class VsmShadowOptions(
    /**
     * Number of anisotropic samples to use when sampling a VSM shadow map. If greater than 0,
     * mipmaps will automatically be generated each frame for all lights. The number of
     * anisotropic samples = 2 ^ anisotropy. Default: 0.
     */
    var anisotropy: Int = 0,

    /**
     * Whether to generate mipmaps for all VSM shadow maps. Default: false.
     */
    var mipmapping: Boolean = false,

    /**
     * The number of MSAA samples to use when rendering VSM shadow maps. Must be a power-of-two
     * and greater than or equal to 1. A value of 1 effectively turns off MSAA. Higher values
     * may not be available depending on the underlying hardware. Default: 1.
     */
    var msaaSamples: Int = 1,

    /**
     * Whether to use a 32-bits or 16-bits texture format for VSM shadow maps. 32-bits precision
     * is rarely needed, but it does reduce light leaks as well as "fading" of the shadows.
     * Setting this to true for a single shadow map will double the memory usage of all shadow
     * maps. This may not be supported on all mobile devices. Default: false.
     */
    var highPrecision: Boolean = false,

    /**
     * VSM light bleeding reduction amount, between 0 and 1. Default: 0.15.
     */
    var lightBleedReduction: Float = 0.15f,
)

/**
 * View-level options for DPCF and PCSS (soft) shadowing.
 *
 * Warning: This API is still experimental and subject to change.
 */
data class SoftShadowOptions(
    /**
     * Globally scales the penumbra of all DPCF and PCSS shadows. Acceptable values are greater
     * than 0. Default: 1.0.
     */
    var penumbraScale: Float = 1.0f,

    /**
     * Globally scales the computed penumbra ratio of all DPCF and PCSS shadows. This effectively
     * controls the strength of contact hardening effect and is useful for artistic purposes.
     * Higher values make the shadows become softer faster. Acceptable values are equal to or
     * greater than 1. Default: 1.0.
     */
    var penumbraRatioScale: Float = 1.0f,

    /**
     * Caps the penumbra ratio used by PCSS contact hardening, applied as a smooth asymptotic
     * squash rather than a hard clamp. Limits how soft a shadow can get as the occluder moves
     * away from the receiver. Default: 10.0.
     */
    var maxPenumbraRatio: Float = 10.0f,

    /**
     * Limits the physical footprint, in world units, of the PCSS blocker search. Acts as a
     * global ceiling on the per-light [LightManager.ShadowOptions.maxSearchRadius].
     * Default: 1.0.
     */
    var maxSearchRadius: Float = 1.0f,
)

/**
 * Options for the screen-space guard band.
 *
 * A guard band can be enabled to avoid artifacts towards the edge of the screen when using
 * screen-space effects such as SSAO. Enabling the guard band reduces performance slightly.
 * Currently the guard band can only be enabled or disabled.
 */
data class GuardBandOptions(
    /**
     * Enable or disable the guard band. Default: false.
     */
    var enabled: Boolean = false,
)

/**
 * Options for stereoscopic (multi-eye) rendering.
 *
 * Used for VR and other multi-view rendering scenarios.
 */
data class StereoscopicOptions(
    /**
     * Enable or disable stereoscopic rendering. Default: false.
     */
    var enabled: Boolean = false,
)

/**
 * Options for Multi-Sample Anti-aliasing (MSAA).
 *
 * MSAA is a GPU-native anti-aliasing technique that reduces jagged edges by sampling multiple
 * points per pixel.
 */
data class MultiSampleAntiAliasingOptions(
    /**
     * Enable or disable MSAA. Default: false.
     */
    var enabled: Boolean = false,

    /**
     * Number of samples to use for multi-sampled anti-aliasing.
     * - 0: treated as 1
     * - 1: no anti-aliasing
     * - n: sample count. Effective sample could be different depending on the GPU capabilities.
     *
     * Default: 4.
     */
    var sampleCount: Int = 4,

    /**
     * Custom resolve improves quality for HDR scenes, but may impact performance. Default: false.
     */
    var customResolve: Boolean = false,
)

