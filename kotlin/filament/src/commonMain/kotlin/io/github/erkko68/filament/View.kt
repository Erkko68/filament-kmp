package io.github.erkko68.filament

import io.github.erkko68.filament.interop.*

/**
 * A View encompasses all the state needed for rendering a Scene.
 *
 * Renderer.render() operates on View objects. These View objects specify important parameters
 * such as the Scene, Camera, Viewport, and various rendering parameters.
 *
 * View instances are heavy objects that internally cache a lot of data needed for rendering.
 * It is not advised for an application to use many View objects. For example, in a game, a View
 * could be used for the main scene and another one for the game's user interface. More View
 * instances could be used for creating special effects (a View is akin to a rendering pass).
 *
 * @see Scene, Camera, RenderTarget
 */
class View @InternalFilamentApi constructor(internal var nativeHandle: NativePointer) {
    /** The native object, for interop with code calling the Fila* C API directly. Read-only: this wrapper owns it. */
    @InternalFilamentApi
    val nativeObject: NativePointer get() = nativeHandle

    /**
     * Dithering mode for temporal coherence in rendering.
     *
     * - NONE: No dithering applied
     * - TEMPORAL: Temporal dithering for reduced color banding
     */
    enum class Dithering { NONE, TEMPORAL }
    /**
     * Blending mode for the view.
     *
     * - OPAQUE: View renders opaque content
     * - TRANSLUCENT: View renders translucent content
     */
    enum class BlendMode { OPAQUE, TRANSLUCENT }
    /**
     * Generic quality level for various rendering options.
     *
     * - LOW: Lowest quality, best performance
     * - MEDIUM: Medium quality and performance balance
     * - HIGH: High quality, moderate performance impact
     * - ULTRA: Highest quality, greatest performance impact
     */
    enum class Quality { LOW, MEDIUM, HIGH, ULTRA }
    /**
     * Shadow rendering technique.
     *
     * - PCF: Percentage Closer Filtering (standard soft shadows)
     * - VSM: Variance Shadow Maps
     * - DPCF: deprecated upstream since 1.76.0 — falls back to PCSS
     * - PCSS: Percentage Closer Soft Shadows (physically-based)
     * - PCFd: Directional PCF variant
     */
    enum class ShadowType { PCF, VSM, DPCF, PCSS, PCFd }
    /**
     * Anti-aliasing technique.
     *
     * - NONE: No anti-aliasing
     * - FXAA: Fast Approximate Anti-Aliasing (post-process)
     */
    enum class AntiAliasing { NONE, FXAA }

    /**
     * Result of a picking (color-picking) query.
     *
     * @param renderable Entity ID of the picked renderable
     * @param depth Depth of the picked fragment
     * @param fragCoords Fragment coordinates (x, y) of the pick location
     */
    class PickingQueryResult(
        val renderable: Int,
        val depth: Float,
        val fragCoords: FloatArray
    )

    private var mScene: Scene? = null
    private var mCamera: Camera? = null
    private var mRenderTarget: RenderTarget? = null
    private var mShadowType: ShadowType = ShadowType.PCF
    private var mColorGrading: ColorGrading? = null

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
    class DynamicResolutionOptions constructor() {
        /**
         * Enable or disable dynamic resolution on this View. Default: false.
         */
        var enabled: Boolean = false
        /**
         * By default the system scales the major axis first. Set this to true to force
         * homogeneous scaling. Default: false.
         */
        var homogeneousScaling: Boolean = false
        /**
         * The minimum scale in X and Y this View should use. Default: (0.5, 0.5).
         */
        var minScale: Float = 0.5f
        /**
         * The maximum scale in X and Y this View should use. Default: (1.0, 1.0).
         */
        var maxScale: Float = 1.0f
        /**
         * Sharpness when Quality.MEDIUM or higher is used [0 (disabled), 1 (sharpest)].
         * Default: 0.9.
         */
        var sharpness: Float = 0.9f
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
        var quality: Quality = Quality.LOW
    }

    /**
     * Options to control color buffer precision and quality settings.
     *
     * A quality of HIGH or ULTRA means using an RGB16F or RGBA16F color buffer. Colors in the
     * LDR range (0..1) have a 10 bit precision. A quality of LOW or MEDIUM means using an
     * R11G11B10F opaque color buffer or an RGBA16F transparent color buffer. With R11G11B10F,
     * colors in the LDR range have a precision of either 6 bits (red and green) or 5 bits (blue).
     */
    class RenderQuality constructor() {
        /**
         * Sets the quality of the HDR color buffer. Default: HIGH.
         */
        var hdrColorBuffer: Quality = Quality.HIGH
    }

    /**
     * Options to control the bloom post-processing effect.
     *
     * Bloom allows bright areas to glow and bleed into surrounding areas, creating a
     * luminous quality. The effect can be enhanced with lens flare, lens artifacts, and
     * customizable bloom color and spread.
     */
    class BloomOptions constructor() {
        /**
         * Enable or disable the bloom post-processing effect. Default: false.
         */
        var enabled: Boolean = false
        /**
         * Number of successive blurs to achieve the blur effect. Minimum is 3 and maximum is 12.
         * This value together with resolution influences the spread of the blur effect.
         * This value can be silently reduced to accommodate the original image size. Default: 6.
         */
        var levels: Int = 6
        /**
         * Resolution of bloom's minor axis. Minimum value is 2^levels and maximum is lower of
         * the original resolution and 4096. This parameter is silently clamped to the minimum
         * and maximum. Default: 384.
         */
        var resolution: Int = 384
        /**
         * How much of the bloom is added to the original image, between 0 and 1. Default: 0.10.
         */
        var strength: Float = 0.10f
        /**
         * When enabled, a threshold at 1.0 is applied on the source image, useful for artistic
         * reasons and usually needed when a dirt texture is used. Default: true.
         */
        var threshold: Boolean = true
        /**
         * A dirt/scratch/smudges texture (RGB) which gets added to the bloom effect.
         * Smudges are visible where bloom occurs. Threshold must be enabled for the dirt
         * effect to work properly. Default: null.
         */
        var dirt: Texture? = null
        /**
         * Strength of the dirt texture. Default: 0.2.
         */
        var dirtStrength: Float = 0.2f
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
        var quality: Quality = Quality.LOW
        /**
         * Enable screen-space lens flare effect. Default: false.
         */
        var lensFlare: Boolean = false
        /**
         * Enable starburst effect on lens flare. Default: true.
         */
        var starburst: Boolean = true
        /**
         * Amount of chromatic aberration in the lens flare effect. Default: 0.005.
         */
        var chromaticAberration: Float = 0.005f
        /**
         * Number of flare "ghosts" (lens artifacts). Default: 4.
         */
        var ghostCount: Int = 4
        /**
         * Spacing of the ghost in screen units [0, 1). Default: 0.6.
         */
        var ghostSpacing: Float = 0.6f
        /**
         * HDR threshold for the ghosts. Default: 10.0.
         */
        var ghostThreshold: Float = 10.0f
        /**
         * Radius of halo in vertical screen units [0, 0.5]. Default: 0.4.
         */
        var haloRadius: Float = 0.4f
        /**
         * Thickness of halo in vertical screen units, 0 to disable. Default: 0.1.
         */
        var haloThickness: Float = 0.1f
        /**
         * HDR threshold for the halo. Default: 10.0.
         */
        var haloThreshold: Float = 10.0f
        /**
         * Limit highlights to this value before bloom, range [10, +inf]. Default: 1000.0.
         */
        var highlight: Float = 1000.0f
        /**
         * How the bloom effect is applied.
         *
         * - ADD: Bloom is modulated by the strength parameter and added to the scene
         * - INTERPOLATE: Bloom is interpolated with the scene using the strength parameter
         *
         * Default: ADD.
         */
        var blendMode: BlendMode = BlendMode.ADD
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
    class FogOptions constructor() {
        /**
         * Enable or disable large-scale fog. Default: false.
         */
        var enabled: Boolean = false
        /**
         * Distance in world units [m] from the camera to where the fog starts (>= 0.0).
         * Default: 0.0.
         */
        var distance: Float = 0.0f
        /**
         * Extinction factor in [1/m] at the fog height. Controls how much light is absorbed and
         * out-scattered per unit of distance. Each unit of extinction reduces incoming light to
         * 37% of its original value. In linearFog mode, this is the slope of the linear equation
         * if heightFalloff is 0. Default: 0.1.
         */
        var density: Float = 0.1f
        /**
         * Fog's floor in world units [m]. This sets the "sea level". Default: 0.0.
         */
        var height: Float = 0.0f
        /**
         * How fast the fog dissipates with altitude. heightFalloff has a unit of [1/m].
         * It can be expressed as 1/H, where H is the altitude change in world units [m] that
         * causes a factor 2.78 (e) change in fog density. A falloff of 0 means the fog density
         * is constant everywhere. Ignored in linearFog mode if set to 0. Default: 1.0.
         */
        var heightFalloff: Float = 1.0f
        /**
         * Fog's color used for ambient light in-scattering. A good value is the average of the
         * ambient light, possibly tinted towards blue for outdoor environments. Color components
         * should be between 0 and 1; values above 1 are allowed but could create a non
         * energy-conservative fog. Used as a tint when fogColorFromIbl is enabled. Default: white.
         */
        var color: FloatArray = floatArrayOf(1.0f, 1.0f, 1.0f)
        /**
         * Distance in world units [m] after which the fog calculation is disabled. This can be
         * used to exclude the skybox. The SkyBox is typically at a distance of 1e19 in world
         * space. Default: infinity.
         */
        var cutOffDistance: Float = Float.POSITIVE_INFINITY
        /**
         * Fog's maximum opacity between 0 and 1. Ignored in linearFog mode. Default: 1.0.
         */
        var maximumOpacity: Float = 1.0f
        /**
         * Distance in world units [m] from the camera where the Sun in-scattering starts.
         * Ignored in linearFog mode. Default: 0.0.
         */
        var inScatteringStart: Float = 0.0f
        /**
         * Very inaccurately simulates the Sun's in-scattering. Size of the Sun in-scattering
         * (>0 to activate). Good values are >> 1 (e.g., ~10 - 100). Smaller values result in a
         * larger scattering size. Ignored in linearFog mode. Default: -1.0.
         */
        var inScatteringSize: Float = -1.0f
        /**
         * The fog color will be sampled from the IBL in the view direction and tinted by the
         * color parameter. This simulates a more anisotropic phase-function. Ignored when
         * skyColor is specified. Default: false.
         */
        var fogColorFromIbl: Boolean = false
        /**
         * Optional sky texture (mipmapped cubemap) for fog color sampling. When provided, the
         * fog color will be sampled from this texture, with higher resolution mip levels used
         * for objects at the far clip plane and lower resolution mip levels for closer objects.
         * fogColorFromIbl is ignored when this is specified. In linearFog mode, mipmap level 0
         * is always used. Default: null.
         */
        var skyColor: Texture? = null
    }

    /**
     * Options to control Depth of Field (DoF) effect in the scene.
     *
     * cocScale can be used to set the depth of field blur independently of the camera aperture,
     * e.g., for artistic reasons. This can be achieved by setting:
     * cocScale = cameraAperture / desiredDoFAperture.
     */
    class DepthOfFieldOptions constructor() {
        /**
         * Enable or disable depth of field effect. Default: false.
         */
        var enabled: Boolean = false
        /**
         * Circle of confusion scale factor (amount of blur). Default: 1.0.
         */
        var cocScale: Float = 1.0f
        /**
         * Circle-of-confusion aspect ratio, scaling the bokeh horizontally against vertically.
         * 1.0 gives circular bokeh; other values give anamorphic ovals. Default: 1.0.
         */
        var cocAspectRatio: Float = 1.0f
        /**
         * Maximum aperture diameter in meters (zero to disable rotation). Default: 0.01.
         */
        var maxApertureDiameter: Float = 0.01f
        /**
         * Filter to use for filling gaps in the kernel. Default: MEDIAN.
         */
        var filter: Filter = Filter.MEDIAN
        /**
         * Perform DoF processing at native resolution. Default: false.
         */
        var nativeResolution: Boolean = false
        /**
         * Number of rings used by the gather kernels for foreground. The number of rings affects
         * quality and performance. The number of samples per pixel is (ringCount * 2 - 1)².
         * Examples: 3 rings = 25 (5x5), 4 rings = 49 (7x7), 5 rings = 81 (9x9), 17 rings = 1089 (33x33).
         * A value of 0 means default (5 on desktop, 3 on mobile). Default: 0.
         */
        var foregroundRingCount: Int = 0
        /**
         * Number of rings used by the gather kernels for background. Default: 0.
         */
        var backgroundRingCount: Int = 0
        /**
         * Number of rings used by the gather kernels for fast tiles (regions with similar CoC).
         * Default: 0.
         */
        var fastGatherRingCount: Int = 0
        /**
         * Maximum circle-of-confusion in pixels for the foreground, must be in [0, 32] range.
         * A value of 0 means default (32 on desktop, 24 on mobile). Default: 0.
         */
        var maxForegroundCOC: Int = 0
        /**
         * Maximum circle-of-confusion in pixels for the background, must be in [0, 32] range.
         * A value of 0 means default (32 on desktop, 24 on mobile). Default: 0.
         */
        var maxBackgroundCOC: Int = 0
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
    class VignetteOptions constructor() {
        /**
         * Enable or disable the vignette effect. Default: false.
         */
        var enabled: Boolean = false
        /**
         * High values restrict the vignette closer to the corners, between 0 and 1.
         * Default: 0.5.
         */
        var midPoint: Float = 0.5f
        /**
         * Controls the shape of the vignette, from a rounded rectangle (0.0), to an oval (0.5),
         * to a circle (1.0). Default: 0.5.
         */
        var roundness: Float = 0.5f
        /**
         * Softening amount of the vignette effect, between 0 and 1. Default: 0.5.
         */
        var feather: Float = 0.5f
        /**
         * Color of the vignette effect (alpha is currently ignored). Default: black.
         */
        var color: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
    }

    /**
     * Options for screen space Ambient Occlusion (SSAO) and Screen Space Cone Tracing (SSCT).
     *
     * Ambient occlusion darkens crevices and contact points, adding realism and depth to scenes.
     */
    class AmbientOcclusionOptions constructor() {
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
         * Type of ambient occlusion algorithm. Default: [AmbientOcclusionType.SAO].
         */
        var aoType: AmbientOcclusionType = AmbientOcclusionType.SAO
        /**
         * Ambient Occlusion radius in meters, between 0 and ~10. Default: 0.3.
         */
        var radius: Float = 0.3f
        /**
         * Self-occlusion bias in meters. Use to avoid self-occlusion. Between 0 and a few mm.
         * No effect when aoType is set to GTAO. Default: 0.0005.
         */
        var bias: Float = 0.0005f
        /**
         * Strength of the Ambient Occlusion effect. Default: 1.0.
         */
        var intensity: Float = 1.0f
        /**
         * Controls ambient occlusion's contrast. Must be positive. Default: 1.0.
         */
        var power: Float = 1.0f
        /**
         * Minimum angle in radians to consider. No effect when aoType is set to GTAO. Default: 0.0.
         */
        var minHorizonAngleRad: Float = 0.0f
        /**
         * Affects number of samples used for AO and parameters for filtering. Default: LOW.
         */
        var quality: Quality = Quality.LOW
        /**
         * Affects AO smoothness. Recommended setting to HIGH when aoType is set to GTAO.
         * Default: MEDIUM.
         */
        var lowPassFilter: Quality = Quality.MEDIUM
        /**
         * Affects AO buffer upsampling quality. Default: LOW.
         */
        var upsampling: Quality = Quality.LOW
        /**
         * Enable or disable screen-space ambient occlusion. Default: false.
         */
        var enabled: Boolean = false
        /**
         * Enable bent normals computation from AO, and specular AO. Default: false.
         */
        var bentNormals: Boolean = false
        /**
         * Depth distance that constitutes an edge for filtering. Default: 0.05.
         */
        var bilateralThreshold: Float = 0.05f
        /**
         * How each dimension of the AO buffer is scaled. Must be either 0.5 or 1.0. Default: 0.5.
         */
        var resolution: Float = 0.5f
        /**
         * Screen Space Cone Tracing (SSCT) options for ambient shadows from dominant light.
         */
        var ssct: Ssct = Ssct()
        /**
         * Ground-Truth-based Ambient Occlusion tuning. Only takes effect when [aoType] is
         * [AmbientOcclusionType.GTAO].
         */
        var gtao: Gtao = Gtao()
        /**
         * Screen Space Cone Tracing options for ambient shadows.
         */
        class Ssct constructor() {
            /**
             * Enable or disable SSCT. Default: false.
             */
            var enabled: Boolean = false
            /**
             * Full cone angle in radians, between 0 and pi/2. Default: 1.0.
             */
            var lightConeRad: Float = 1.0f
            /**
             * How far shadows can be cast. Default: 0.3.
             */
            var shadowDistance: Float = 0.3f
            /**
             * Maximum distance for contact. Default: 1.0.
             */
            var contactDistanceMax: Float = 1.0f
            /**
             * Intensity of SSCT effect. Default: 0.8.
             */
            var intensity: Float = 0.8f
            /**
             * Light direction vector. Default: (0, -1, 0).
             */
            var lightDirection: FloatArray = floatArrayOf(0f, -1f, 0f)
            /**
             * Depth bias in world units to mitigate self shadowing. Default: 0.01.
             */
            var depthBias: Float = 0.01f
            /**
             * Depth slope bias to mitigate self shadowing. Default: 0.01.
             */
            var depthSlopeBias: Float = 0.01f
            /**
             * Tracing sample count, between 1 and 255. Default: 4.
             */
            var sampleCount: Int = 4
            /**
             * Number of rays to trace, between 1 and 255. Default: 1.
             */
            var rayCount: Int = 1
        }
        /**
         * Ground-Truth-based Ambient Occlusion options.
         */
        class Gtao constructor() {
            /**
             * Number of slices. Higher values make less noise. Default: 4.
             */
            var sampleSliceCount: Int = 4
            /**
             * Number of steps the radius is divided into for integration. Higher values make less
             * bias. Default: 3.
             */
            var sampleStepsPerSlice: Int = 3
            /**
             * Thickness heuristic, should be close to 0. No effect when [useVisibilityBitmasks] is
             * true. Default: 0.004.
             */
            var thicknessHeuristic: Float = 0.004f
            /**
             * Enables visibility-bitmask mode. Bent normals do not work under this mode.
             *
             * Changing this at runtime is very expensive — it may trigger a shader recompilation.
             * Default: false.
             */
            var useVisibilityBitmasks: Boolean = false
            /**
             * Constant world-space thickness assumed for on-screen objects. Only takes effect when
             * [useVisibilityBitmasks] is true. Default: 0.5.
             */
            var constThickness: Float = 0.5f
            /**
             * Increases thickness with distance to keep detail on distant surfaces.
             *
             * Changing this at runtime is very expensive — it may trigger a shader recompilation.
             * Default: false.
             */
            var linearThickness: Boolean = false
        }
    }

    /**
     * Options for Temporal Anti-aliasing (TAA).
     *
     * Most TAA parameters are extremely costly to change, as they will trigger the TAA post-process
     * shaders to be recompiled. These options should be changed or set during initialization.
     * `feedback` and `jitterPattern`, however, can be changed at any time. A feedback of 0.1
     * effectively accumulates a maximum of 19 samples in steady state.
     */
    class TemporalAntiAliasingOptions constructor() {
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
        /**
         * History feedback, between 0 (maximum temporal AA) and 1 (no temporal AA). Default: 0.12.
         */
        var feedback: Float = 0.12f
        /**
         * Enable or disable temporal anti-aliasing. Default: false.
         */
        var enabled: Boolean = false
        /**
         * Texturing LOD bias (typically -1 or -2). Default: -1.0.
         */
        var lodBias: Float = -1.0f
        /**
         * Post-TAA sharpening, especially useful when upscaling is true. Default: 0.0.
         */
        var sharpness: Float = 0.0f
        /**
         * Upscaling factor. Disables Dynamic Resolution. Default: 1.0 (Beta).
         */
        var upscaling: Float = 1.0f
        /**
         * Whether to filter the history buffer. Default: true.
         */
        var filterHistory: Boolean = true
        /**
         * Whether to apply the reconstruction filter to the input. Default: true.
         */
        var filterInput: Boolean = true
        /**
         * Whether to use the YcoCg color-space for history rejection. Default: false.
         */
        var useYCoCg: Boolean = false
        /**
         * Set to true for HDR content. Default: true.
         */
        var hdr: Boolean = true
        /**
         * Type of color gamut box. Default: [BoxType.AABB].
         */
        var boxType: BoxType = BoxType.AABB
        /**
         * Clipping algorithm. Default: [BoxClipping.ACCURATE].
         */
        var boxClipping: BoxClipping = BoxClipping.ACCURATE
        /**
         * Jitter pattern for sampling. Default: [JitterPattern.HALTON_23_X16].
         */
        var jitterPattern: JitterPattern = JitterPattern.HALTON_23_X16
        /**
         * High values increase ghosting artifacts, lower values increase jittering, range [0.75, 1.25].
         * Default: 1.0.
         */
        var varianceGamma: Float = 1.0f
        /**
         * Adjust the feedback dynamically to reduce flickering. Default: false.
         */
        var preventFlickering: Boolean = false
        /**
         * Whether to apply history reprojection (debug option). Default: true.
         */
        var historyReprojection: Boolean = true
    }

    /**
     * Options for Screen-space Reflections (SSR).
     *
     * SSR allows objects to reflect their environment in real-time using only screen-space
     * information, making it very efficient but limited to on-screen reflections.
     */
    class ScreenSpaceReflectionsOptions constructor() {
        /**
         * Enable or disable screen-space reflections. Default: false.
         */
        var enabled: Boolean = false
        /**
         * Ray thickness in world units. Default: 0.1.
         */
        var thickness: Float = 0.1f
        /**
         * Bias in world units to prevent self-intersections. Default: 0.01.
         */
        var bias: Float = 0.01f
        /**
         * Maximum distance in world units to raycast. Default: 3.0.
         */
        var maxDistance: Float = 3.0f
        /**
         * Stride in texels for samples along the ray. Default: 2.0.
         */
        var stride: Float = 2.0f
    }

    /**
     * View-level options for VSM (Variance Shadow Maps) shadowing.
     *
     * Warning: This API is still experimental and subject to change.
     */
    class VsmShadowOptions constructor() {
        /**
         * Number of anisotropic samples to use when sampling a VSM shadow map. If greater than 0,
         * mipmaps will automatically be generated each frame for all lights. The number of
         * anisotropic samples = 2 ^ anisotropy. Default: 0.
         */
        var anisotropy: Int = 0
        /**
         * Whether to generate mipmaps for all VSM shadow maps. Default: false.
         */
        var mipmapping: Boolean = false
        /**
         * The number of MSAA samples to use when rendering VSM shadow maps. Must be a power-of-two
         * and greater than or equal to 1. A value of 1 effectively turns off MSAA. Higher values
         * may not be available depending on the underlying hardware. Default: 1.
         */
        var msaaSamples: Int = 1
        /**
         * Whether to use a 32-bits or 16-bits texture format for VSM shadow maps. 32-bits precision
         * is rarely needed, but it does reduce light leaks as well as "fading" of the shadows.
         * Setting this to true for a single shadow map will double the memory usage of all shadow
         * maps. This may not be supported on all mobile devices. Default: false.
         */
        var highPrecision: Boolean = false
        /**
         * VSM light bleeding reduction amount, between 0 and 1. Default: 0.15.
         */
        var lightBleedReduction: Float = 0.15f
    }

    /**
     * View-level options for DPCF and PCSS (soft) shadowing.
     *
     * Warning: This API is still experimental and subject to change.
     */
    class SoftShadowOptions constructor() {
        /**
         * Globally scales the penumbra of all DPCF and PCSS shadows. Acceptable values are greater
         * than 0. Default: 1.0.
         */
        var penumbraScale: Float = 1.0f
        /**
         * Globally scales the computed penumbra ratio of all DPCF and PCSS shadows. This effectively
         * controls the strength of contact hardening effect and is useful for artistic purposes.
         * Higher values make the shadows become softer faster. Acceptable values are equal to or
         * greater than 1. Default: 1.0.
         */
        var penumbraRatioScale: Float = 1.0f
        /**
         * Caps the penumbra ratio used by PCSS contact hardening, applied as a smooth asymptotic
         * squash rather than a hard clamp. Limits how soft a shadow can get as the occluder moves
         * away from the receiver. Default: 10.0.
         */
        var maxPenumbraRatio: Float = 10.0f
        /**
         * Limits the physical footprint, in world units, of the PCSS blocker search. Acts as a
         * global ceiling on the per-light [LightManager.ShadowOptions.maxSearchRadius].
         * Default: 1.0.
         */
        var maxSearchRadius: Float = 1.0f
    }

    /**
     * Options for the screen-space guard band.
     *
     * A guard band can be enabled to avoid artifacts towards the edge of the screen when using
     * screen-space effects such as SSAO. Enabling the guard band reduces performance slightly.
     * Currently the guard band can only be enabled or disabled.
     */
    class GuardBandOptions constructor() {
        /**
         * Enable or disable the guard band. Default: false.
         */
        var enabled: Boolean = false
    }

    /**
     * Options for stereoscopic (multi-eye) rendering.
     *
     * Used for VR and other multi-view rendering scenarios.
     */
    class StereoscopicOptions constructor() {
        /**
         * Enable or disable stereoscopic rendering. Default: false.
         */
        var enabled: Boolean = false
    }

    /**
     * Options for Multi-Sample Anti-aliasing (MSAA).
     *
     * MSAA is a GPU-native anti-aliasing technique that reduces jagged edges by sampling multiple
     * points per pixel.
     */
    class MultiSampleAntiAliasingOptions constructor() {
        /**
         * Enable or disable MSAA. Default: false.
         */
        var enabled: Boolean = false
        /**
         * Number of samples to use for multi-sampled anti-aliasing.
         * - 0: treated as 1
         * - 1: no anti-aliasing
         * - n: sample count. Effective sample could be different depending on the GPU capabilities.
         *
         * Default: 4.
         */
        var sampleCount: Int = 4
        /**
         * Custom resolve improves quality for HDR scenes, but may impact performance. Default: false.
         */
        var customResolve: Boolean = false
    }

    /** Debug name of this View, shown in diagnostic tools. */
    var name: String?
        get() = stringFromInterop(FilaView_getName(nativeHandle))
        set(value) { (value ?: "").useCString { FilaView_setName(nativeHandle, it) } }

    /**
     * The [Scene] associated with this View. A Scene can be associated to several Views.
     *
     * Set to `null` to dissociate the current Scene. The View does not take ownership of the Scene.
     *
     * There is no reference-counting: if a Scene is destroyed while still associated with a View, it
     * is automatically dissociated (the View's scene becomes `null`).
     */
    var scene: Scene?
        get() = mScene
        set(value) {
            mScene = value
            FilaView_setScene(nativeHandle, value?.nativeHandle ?: NullPointer)
        }

    /**
     * The [Camera] this View is rendered from. A Camera can be associated to several Views.
     *
     * Set to `null` to dissociate the current Camera; the View does not take ownership.
     */
    var camera: Camera?
        get() = mCamera
        set(value) {
            mCamera = value
            FilaView_setCamera(nativeHandle, value?.nativeHandle ?: NullPointer)
        }
    /** Whether a [Camera] is currently associated with this View. */
    val hasCamera: Boolean get() = FilaView_hasCamera(nativeHandle)

    /** The rectangular region of the render target this View renders into. */
    var viewport: Viewport
        get() {
            val out = Array(4) { IntArray(1) }
            interopScope {
                val p = out.map { toInterop(it) }
                FilaView_getViewport(nativeHandle, p[0], p[1], p[2], p[3])
                p.forEachIndexed { i, ptr -> ptr.fromInterop(out[i]) }
            }
            return Viewport(out[0][0], out[1][0], out[2][0], out[3][0])
        }
        set(value) { FilaView_setViewport(nativeHandle, value.left, value.bottom, value.width, value.height) }

    /** How this View's result blends over the render target's existing content. */
    var blendMode: BlendMode
        get() = BlendMode.entries[FilaView_getBlendMode(nativeHandle)]
        set(value) { FilaView_setBlendMode(nativeHandle, value.ordinal) }

    /**
     * Sets which layers are visible: for each bit set in [select], visibility is taken from the
     * corresponding bit in [values]. Renderables are assigned layers via
     * `RenderableManager.setLayerMask`. By default all layers are visible.
     */
    fun setVisibleLayers(select: Int, values: Int) { FilaView_setVisibleLayers(nativeHandle, select, values) }
    /** Convenience over [setVisibleLayers] toggling a single layer (0–7). */
    fun setLayerEnabled(layer: Int, enabled: Boolean) {
        val mask = (1 shl layer)
        FilaView_setVisibleLayers(nativeHandle, mask, if (enabled) mask else 0)
    }
    /** Returns the current visible-layer bitmask. */
    val visibleLayers: Int get() = FilaView_getVisibleLayers(nativeHandle)

    /**
     * Enables or disables the post-processing stage (tone mapping, bloom, color grading, FXAA,
     * dynamic scaling, …). Disabling it also disables features that depend on it. Default: enabled.
     */
    var isPostProcessingEnabled: Boolean
        get() = FilaView_isPostProcessingEnabled(nativeHandle)
        set(value) { FilaView_setPostProcessingEnabled(nativeHandle, value) }

    /** Dithering applied to the final render to hide banding. Default: [Dithering.TEMPORAL]. */
    var dithering: Dithering
        get() = Dithering.entries[FilaView_getDithering(nativeHandle)]
        set(value) { FilaView_setDithering(nativeHandle, value.ordinal) }

    /**
     * Dynamic-resolution (render scaling) configuration for this View.
     * The getter returns a snapshot — mutate it and assign back to apply.
     */
    var dynamicResolutionOptions: DynamicResolutionOptions
        get() = run {
            val outF = FloatArray(5)
            val outI = IntArray(3)
            outF.usePinned { pf -> outI.usePinned { pi -> FilaView_getDynamicResolutionOptions(nativeHandle, pf, pi) } }
            DynamicResolutionOptions().apply {
                enabled = (outI[0] != 0)
                homogeneousScaling = (outI[1] != 0)
                minScale = outF.readF32(0)
                maxScale = outF.readF32(2)
                sharpness = outF.readF32(4)
                quality = Quality.entries[outI[2]]
            }
        }
        set(value) {
            FilaView_setDynamicResolutionOptions(nativeHandle, value.minScale, value.minScale, value.maxScale, value.maxScale, value.sharpness, value.enabled, value.homogeneousScaling, value.quality.ordinal)
        }

    /** Returns the `[x, y]` scale factors dynamic resolution used on the last frame. */
    val lastDynamicResolutionScale: FloatArray
        get() = FloatArray(2).also { out -> out.usePinned { FilaView_getLastDynamicResolutionScale(nativeHandle, it) } }

    /**
     * Global quality/performance trade-offs (e.g. color-buffer precision) for this View.
     * The getter returns a snapshot — mutate it and assign back to apply.
     */
    var renderQuality: RenderQuality
        get() = RenderQuality().apply {
            hdrColorBuffer = Quality.entries[FilaView_getRenderQuality(nativeHandle)]
        }
        set(value) { FilaView_setRenderQuality(nativeHandle, value.hdrColorBuffer.ordinal) }

    /**
     * Bloom post-processing configuration (requires post-processing enabled).
     * The getter returns a snapshot — mutate it and assign back to apply.
     */
    var bloomOptions: BloomOptions
        get() = run {
            val outF = FloatArray(9)
            val outI = IntArray(9)
            outF.usePinned { pf -> outI.usePinned { pi -> FilaView_getBloomOptions(nativeHandle, pf, pi) } }
            BloomOptions().apply {
                enabled = (outI[4] != 0)
                levels = outI[1]
                resolution = outI[0]
                strength = outF.readF32(1)
                threshold = (outI[3] != 0)
                dirtStrength = outF.readF32(0)
                quality = Quality.entries[outI[5]]
                lensFlare = (outI[6] != 0)
                starburst = (outI[7] != 0)
                chromaticAberration = outF.readF32(3)
                ghostCount = outI[8]
                ghostSpacing = outF.readF32(4)
                ghostThreshold = outF.readF32(5)
                haloRadius = outF.readF32(7)
                haloThickness = outF.readF32(6)
                haloThreshold = outF.readF32(8)
                highlight = outF.readF32(2)
                blendMode = BloomOptions.BlendMode.entries[outI[2]]
            }
        }
        set(value) {
            FilaView_setBloomOptions(nativeHandle, value.dirt?.nativeHandle ?: NullPointer, value.dirtStrength, value.strength, value.resolution, value.levels, value.blendMode.ordinal, value.threshold, value.enabled, value.highlight, value.quality.ordinal, value.lensFlare, value.starburst, value.chromaticAberration, value.ghostCount, value.ghostSpacing, value.ghostThreshold, value.haloThickness, value.haloRadius, value.haloThreshold)
        }

    /**
     * Large-scale atmospheric fog configuration.
     * The getter returns a snapshot — mutate it and assign back to apply.
     */
    var fogOptions: FogOptions
        get() = run {
            val outF = FloatArray(11)
            val outI = IntArray(2)
            outF.usePinned { pf -> outI.usePinned { pi -> FilaView_getFogOptions(nativeHandle, pf, pi) } }
            FogOptions().apply {
                enabled = (outI[1] != 0)
                distance = outF.readF32(0)
                density = outF.readF32(8)
                height = outF.readF32(3)
                heightFalloff = outF.readF32(4)
                color = floatArrayOf(outF.readF32(5), outF.readF32(6), outF.readF32(7))
                cutOffDistance = outF.readF32(1)
                maximumOpacity = outF.readF32(2)
                inScatteringStart = outF.readF32(9)
                inScatteringSize = outF.readF32(10)
                fogColorFromIbl = (outI[0] != 0)
            }
        }
        set(value) {
            FilaView_setFogOptions(nativeHandle, value.distance, value.cutOffDistance, value.maximumOpacity, value.height, value.heightFalloff, value.color[0], value.color[1], value.color[2], value.density, value.inScatteringStart, value.inScatteringSize, value.fogColorFromIbl, value.skyColor?.nativeHandle ?: NullPointer, value.enabled)
        }

    /**
     * Depth-of-field post-processing configuration (needs a focused [camera]).
     * The getter returns a snapshot — mutate it and assign back to apply.
     */
    var depthOfFieldOptions: DepthOfFieldOptions
        get() = run {
            val outF = FloatArray(3)
            val outI = IntArray(8)
            outF.usePinned { pf -> outI.usePinned { pi -> FilaView_getDepthOfFieldOptions(nativeHandle, pf, pi) } }
            DepthOfFieldOptions().apply {
                enabled = (outI[0] != 0)
                cocScale = outF.readF32(0)
                cocAspectRatio = outF.readF32(1)
                maxApertureDiameter = outF.readF32(2)
                filter = DepthOfFieldOptions.Filter.entries[outI[1]]
                nativeResolution = (outI[2] != 0)
                foregroundRingCount = outI[3]
                backgroundRingCount = outI[4]
                fastGatherRingCount = outI[5]
                maxForegroundCOC = outI[6]
                maxBackgroundCOC = outI[7]
            }
        }
        set(value) {
            FilaView_setDepthOfFieldOptions(nativeHandle, value.cocScale, value.cocAspectRatio, value.maxApertureDiameter, value.enabled, value.filter.ordinal, value.nativeResolution, value.foregroundRingCount, value.backgroundRingCount, value.fastGatherRingCount, value.maxForegroundCOC, value.maxBackgroundCOC)
        }

    /**
     * Vignette post-processing configuration.
     * The getter returns a snapshot — mutate it and assign back to apply.
     */
    var vignetteOptions: VignetteOptions
        get() = run {
            val outF = FloatArray(7)
            val outI = IntArray(1)
            outF.usePinned { pf -> outI.usePinned { pi -> FilaView_getVignetteOptions(nativeHandle, pf, pi) } }
            VignetteOptions().apply {
                enabled = (outI[0] != 0)
                midPoint = outF.readF32(0)
                roundness = outF.readF32(1)
                feather = outF.readF32(2)
                color = floatArrayOf(outF.readF32(3), outF.readF32(4), outF.readF32(5), outF.readF32(6))
            }
        }
        set(value) {
            FilaView_setVignetteOptions(nativeHandle, value.midPoint, value.roundness, value.feather, value.color[0], value.color[1], value.color[2], value.color[3], value.enabled)
        }

    /**
     * Screen-space ambient occlusion (SSAO) configuration.
     * The getter returns a snapshot — mutate it and assign back to apply.
     */
    var ambientOcclusionOptions: AmbientOcclusionOptions
        get() = run {
            val outF = FloatArray(18)
            val outI = IntArray(13)
            outF.usePinned { pf -> outI.usePinned { pi -> FilaView_getAmbientOcclusionOptions(nativeHandle, pf, pi) } }
            AmbientOcclusionOptions().apply {
                enabled = (outI[3] != 0)
                aoType = AmbientOcclusionOptions.AmbientOcclusionType.entries[outI[12]]
                radius = outF.readF32(0)
                bias = outF.readF32(1)
                intensity = outF.readF32(4)
                resolution = outF.readF32(3)
                power = outF.readF32(2)
                minHorizonAngleRad = outF.readF32(6)
                quality = Quality.entries[outI[0]]
                lowPassFilter = Quality.entries[outI[1]]
                upsampling = Quality.entries[outI[2]]
                bentNormals = (outI[4] != 0)
                bilateralThreshold = outF.readF32(5)
                ssct = AmbientOcclusionOptions.Ssct().apply {
                    enabled = (outI[7] != 0)
                    lightConeRad = outF.readF32(7)
                    shadowDistance = outF.readF32(8)
                    contactDistanceMax = outF.readF32(9)
                    intensity = outF.readF32(10)
                    lightDirection = floatArrayOf(outF.readF32(11), outF.readF32(12), outF.readF32(13))
                    depthBias = outF.readF32(14)
                    depthSlopeBias = outF.readF32(15)
                    sampleCount = outI[5]
                    rayCount = outI[6]
                }
                gtao = AmbientOcclusionOptions.Gtao().apply {
                    sampleSliceCount = outI[8]
                    sampleStepsPerSlice = outI[9]
                    thicknessHeuristic = outF.readF32(16)
                    useVisibilityBitmasks = (outI[10] != 0)
                    constThickness = outF.readF32(17)
                    linearThickness = (outI[11] != 0)
                }
            }
        }
        set(value) {
            FilaView_setAmbientOcclusionOptions(nativeHandle, value.radius, value.bias, value.power, value.resolution, value.intensity, value.bilateralThreshold, value.quality.ordinal, value.lowPassFilter.ordinal, value.upsampling.ordinal, value.enabled, value.bentNormals, value.minHorizonAngleRad, value.ssct.lightConeRad, value.ssct.shadowDistance, value.ssct.contactDistanceMax, value.ssct.intensity, value.ssct.lightDirection[0], value.ssct.lightDirection[1], value.ssct.lightDirection[2], value.ssct.depthBias, value.ssct.depthSlopeBias, value.ssct.sampleCount, value.ssct.rayCount, value.ssct.enabled, value.gtao.sampleSliceCount, value.gtao.sampleStepsPerSlice, value.gtao.thicknessHeuristic, value.gtao.useVisibilityBitmasks, value.gtao.constThickness, value.gtao.linearThickness, value.aoType.ordinal)
        }

    /**
     * Temporal anti-aliasing (TAA) configuration; effective when [antiAliasing] permits it.
     * The getter returns a snapshot — mutate it and assign back to apply.
     */
    var temporalAntiAliasingOptions: TemporalAntiAliasingOptions
        get() = run {
            val outF = FloatArray(5)
            val outI = IntArray(10)
            outF.usePinned { pf -> outI.usePinned { pi -> FilaView_getTemporalAntiAliasingOptions(nativeHandle, pf, pi) } }
            TemporalAntiAliasingOptions().apply {
                enabled = (outI[0] != 0)
                feedback = outF.readF32(0)
                lodBias = outF.readF32(1)
                sharpness = outF.readF32(2)
                upscaling = outF.readF32(3)
                filterHistory = (outI[1] != 0)
                filterInput = (outI[2] != 0)
                useYCoCg = (outI[3] != 0)
                hdr = (outI[4] != 0)
                boxType = TemporalAntiAliasingOptions.BoxType.entries[outI[5]]
                boxClipping = TemporalAntiAliasingOptions.BoxClipping.entries[outI[6]]
                jitterPattern = TemporalAntiAliasingOptions.JitterPattern.entries[outI[7]]
                varianceGamma = outF.readF32(4)
                preventFlickering = (outI[8] != 0)
                historyReprojection = (outI[9] != 0)
            }
        }
        set(value) {
            FilaView_setTemporalAntiAliasingOptions(nativeHandle, value.feedback, value.lodBias, value.sharpness, value.enabled, value.upscaling, value.filterHistory, value.filterInput, value.useYCoCg, value.hdr, value.boxType.ordinal, value.boxClipping.ordinal, value.jitterPattern.ordinal, value.varianceGamma, value.preventFlickering, value.historyReprojection)
        }

    /**
     * Screen-space reflections configuration.
     * The getter returns a snapshot — mutate it and assign back to apply.
     */
    var screenSpaceReflectionsOptions: ScreenSpaceReflectionsOptions
        get() = run {
            val outF = FloatArray(4)
            val outI = IntArray(1)
            outF.usePinned { pf -> outI.usePinned { pi -> FilaView_getScreenSpaceReflectionsOptions(nativeHandle, pf, pi) } }
            ScreenSpaceReflectionsOptions().apply {
                enabled = (outI[0] != 0)
                thickness = outF.readF32(0)
                bias = outF.readF32(1)
                maxDistance = outF.readF32(2)
                stride = outF.readF32(3)
            }
        }
        set(value) {
            FilaView_setScreenSpaceReflectionsOptions(nativeHandle, value.thickness, value.bias, value.maxDistance, value.stride, value.enabled)
        }

    /**
     * Off-screen [RenderTarget] to render into, or `null` to render into the SwapChain.
     * The render target is not owned by the View.
     */
    var renderTarget: RenderTarget?
        get() = mRenderTarget
        set(value) {
            mRenderTarget = value
            FilaView_setRenderTarget(nativeHandle, value?.nativeHandle ?: NullPointer)
        }

    /** Shadow mapping technique for the whole View ([ShadowType.PCF], VSM, DPCF, PCSS). */
    var shadowType: ShadowType
        get() = mShadowType
        set(value) {
            mShadowType = value
            FilaView_setShadowType(nativeHandle, value.ordinal)
        }

    /**
     * Variance shadow mapping options; only applies when [shadowType] is [ShadowType.VSM].
     * The getter returns a snapshot — mutate it and assign back to apply.
     */
    var vsmShadowOptions: VsmShadowOptions
        get() = run {
            val outF = FloatArray(1)
            val outI = IntArray(4)
            outF.usePinned { pf -> outI.usePinned { pi -> FilaView_getVsmShadowOptions(nativeHandle, pf, pi) } }
            VsmShadowOptions().apply {
                anisotropy = outI[0]
                mipmapping = (outI[1] != 0)
                msaaSamples = outI[2]
                highPrecision = (outI[3] != 0)
                lightBleedReduction = outF.readF32(0)
            }
        }
        set(value) {
            FilaView_setVsmShadowOptions(nativeHandle, value.anisotropy, value.mipmapping, value.msaaSamples, value.highPrecision, value.lightBleedReduction)
        }

    /**
     * Soft shadow options; only applies when [shadowType] is DPCF or PCSS.
     * The getter returns a snapshot — mutate it and assign back to apply.
     */
    var softShadowOptions: SoftShadowOptions
        get() = run {
            val outF = FloatArray(4)
            outF.usePinned { pf -> FilaView_getSoftShadowOptions(nativeHandle, pf) }
            SoftShadowOptions().apply {
                penumbraScale = outF.readF32(0)
                penumbraRatioScale = outF.readF32(1)
                maxPenumbraRatio = outF.readF32(2)
                maxSearchRadius = outF.readF32(3)
            }
        }
        set(value) {
            FilaView_setSoftShadowOptions(nativeHandle, value.penumbraScale, value.penumbraRatioScale, value.maxPenumbraRatio, value.maxSearchRadius)
        }

    /**
     * Guard-band configuration, letting some effects sample outside the viewport.
     * The getter returns a snapshot — mutate it and assign back to apply.
     */
    var guardBandOptions: GuardBandOptions
        get() = run {
            val outI = IntArray(1)
            outI.usePinned { pi -> FilaView_getGuardBandOptions(nativeHandle, pi) }
            GuardBandOptions().apply { enabled = (outI[0] != 0) }
        }
        set(value) {
            FilaView_setGuardBandOptions(nativeHandle, value.enabled)
        }

    /**
     * Stereoscopic (VR) rendering configuration; must be set before the first frame.
     * The getter returns a snapshot — mutate it and assign back to apply.
     */
    var stereoscopicOptions: StereoscopicOptions
        get() = run {
            val outI = IntArray(1)
            outI.usePinned { pi -> FilaView_getStereoscopicOptions(nativeHandle, pi) }
            StereoscopicOptions().apply { enabled = (outI[0] != 0) }
        }
        set(value) {
            FilaView_setStereoscopicOptions(nativeHandle, value.enabled)
        }

    /**
     * Hardware MSAA configuration (independent of [antiAliasing]/TAA).
     * The getter returns a snapshot — mutate it and assign back to apply.
     */
    var multiSampleAntiAliasingOptions: MultiSampleAntiAliasingOptions
        get() = run {
            val outI = IntArray(3)
            outI.usePinned { pi -> FilaView_getMultiSampleAntiAliasingOptions(nativeHandle, pi) }
            MultiSampleAntiAliasingOptions().apply {
                enabled = (outI[0] != 0)
                sampleCount = outI[1]
                customResolve = (outI[2] != 0)
            }
        }
        set(value) {
            FilaView_setMultiSampleAntiAliasingOptions(nativeHandle, value.enabled, value.sampleCount, value.customResolve)
        }

    /** Culls renderables outside the camera frustum. Default: true (disable only for debugging). */
    var isFrustumCullingEnabled: Boolean
        get() = FilaView_isFrustumCullingEnabled(nativeHandle)
        set(value) { FilaView_setFrustumCullingEnabled(nativeHandle, value) }
    /** Master switch for shadow mapping in this View. Default: true. */
    var isShadowingEnabled: Boolean
        get() = FilaView_isShadowingEnabled(nativeHandle)
        set(value) { FilaView_setShadowingEnabled(nativeHandle, value) }
    /** Enables screen-space refraction for refractive materials. Default: true. */
    var isScreenSpaceRefractionEnabled: Boolean
        get() = FilaView_isScreenSpaceRefractionEnabled(nativeHandle)
        set(value) { FilaView_setScreenSpaceRefractionEnabled(nativeHandle, value) }
    /** Allocates a stencil buffer for this View (required for stencil-based effects). Default: false. */
    var isStencilBufferEnabled: Boolean
        get() = FilaView_isStencilBufferEnabled(nativeHandle)
        set(value) { FilaView_setStencilBufferEnabled(nativeHandle, value) }
    /**
     * Inverts the winding order considered front-facing (counter-clockwise by default).
     * Useful for mirror-like reflections rendered with a flipped camera.
     */
    var isFrontFaceWindingInverted: Boolean
        get() = FilaView_isFrontFaceWindingInverted(nativeHandle)
        set(value) { FilaView_setFrontFaceWindingInverted(nativeHandle, value) }
    /** Includes transparent renderables in [pick] results. Default: true. */
    var isTransparentPickingEnabled: Boolean
        get() = FilaView_isTransparentPickingEnabled(nativeHandle)
        set(value) { FilaView_setTransparentPickingEnabled(nativeHandle, value) }

    /**
     * Grid size in world units used for grid-based world-origin snapping. 0 or negative means the
     * size is calculated automatically from the camera frustum. Default: 0 (automatic).
     */
    var gridSize: Double
        get() = FilaView_getGridSize(nativeHandle)
        set(value) { FilaView_setGridSize(nativeHandle, value) }
    /**
     * The effective grid size used for world-origin snapping: [gridSize] when positive, otherwise
     * the automatically calculated size.
     */
    val effectiveGridSize: Double
        get() = FilaView_getEffectiveGridSize(nativeHandle)

    /** Sets the float4 material-global value at [index] (0–3), readable from all materials. */
    fun setMaterialGlobal(index: Int, value: FloatArray) {
        FilaView_setMaterialGlobal(nativeHandle, index, value[0], value[1], value[2], value[3])
    }
    /** Returns the float4 material-global value at [index] (0–3). */
    fun getMaterialGlobal(index: Int): FloatArray {
        return FloatArray(4).also { out -> out.usePinned { FilaView_getMaterialGlobal(nativeHandle, index, it) } }
    }
    /** Entity representing the large-scale fog object; can be transformed via TransformManager. */
    val fogEntity: Entity get() = FilaView_getFogEntity(nativeHandle)
    /**
     * Returns the most recent number of visible renderables for the current Scene, as calculated
     * the last time Renderer.render() was called with this View and Scene.
     *
     * @return the number of visible renderables, or -1 if no value is available (e.g. before the
     *         first render call, or if the scene was detached).
     */
    val visibleRenderableCount: Int get() = FilaView_getVisibleRenderableCount(nativeHandle)
    /** Discards accumulated frame history (TAA, SSR). Call after a camera cut to avoid ghosting. */
    fun clearFrameHistory(engine: Engine) { FilaView_clearFrameHistory(nativeHandle, engine.nativeHandle) }

    /**
     * Sets the near/far planes (in world units, > 0) used to compute the froxel grid for dynamic
     * lighting. Only lights within this range are lit. Defaults: 5 / 100.
     */
    fun setDynamicLightingOptions(zNear: Float, zFar: Float) {
        FilaView_setDynamicLightingOptions(nativeHandle, zNear, zFar)
    }

    /** Post-process anti-aliasing operator ([AntiAliasing.FXAA] by default). */
    var antiAliasing: AntiAliasing
        get() = AntiAliasing.entries[FilaView_getAntiAliasing(nativeHandle)]
        set(value) { FilaView_setAntiAliasing(nativeHandle, value.ordinal) }

    /** Color grading to apply, or `null` for the default. The View does not own it. */
    var colorGrading: ColorGrading?
        get() = mColorGrading
        set(value) {
            mColorGrading = value
            FilaView_setColorGrading(nativeHandle, value?.nativeHandle ?: NullPointer)
        }

    /**
     * Asynchronously picks the renderable at viewport coordinates ([x], [y]) — origin bottom-left —
     * and invokes [callback] with the result a few frames later. Requires the picking feature
     * (enabled by default) and a rendered frame.
     */
    fun pick(x: Int, y: Int, callback: (PickingQueryResult) -> Unit) {
        // The result lives on the C stack for the callback's duration: copy it out right away.
        val userData = Callbacks.register(once = true) { result ->
            val renderable = IntArray(1)
            val values = FloatArray(4)
            renderable.usePinned { r -> values.usePinned { v -> FilaView_readPickingResult(result, r, v) } }
            callback(PickingQueryResult(renderable[0], values[0], values.copyOfRange(1, 4)))
        }
        FilaView_pick(nativeHandle, x, y, NullPointer, Callbacks.argUser, userData)
    }
}

@ExternalSymbolName("FilaView_clearFrameHistory")
private external fun FilaView_clearFrameHistory(view: NativePointer, engine: NativePointer)

@ExternalSymbolName("FilaView_getAmbientOcclusionOptions")
private external fun FilaView_getAmbientOcclusionOptions(view: NativePointer, floats: NativePointer, ints: NativePointer)

@ExternalSymbolName("FilaView_getAntiAliasing")
private external fun FilaView_getAntiAliasing(view: NativePointer): Int

@ExternalSymbolName("FilaView_getBlendMode")
private external fun FilaView_getBlendMode(view: NativePointer): Int

@ExternalSymbolName("FilaView_getBloomOptions")
private external fun FilaView_getBloomOptions(view: NativePointer, floats: NativePointer, ints: NativePointer)

@ExternalSymbolName("FilaView_getDepthOfFieldOptions")
private external fun FilaView_getDepthOfFieldOptions(view: NativePointer, floats: NativePointer, ints: NativePointer)

@ExternalSymbolName("FilaView_getDithering")
private external fun FilaView_getDithering(view: NativePointer): Int

@ExternalSymbolName("FilaView_getDynamicResolutionOptions")
private external fun FilaView_getDynamicResolutionOptions(view: NativePointer, floats: NativePointer, ints: NativePointer)

@ExternalSymbolName("FilaView_getEffectiveGridSize")
private external fun FilaView_getEffectiveGridSize(view: NativePointer): Double

@ExternalSymbolName("FilaView_getFogEntity")
private external fun FilaView_getFogEntity(view: NativePointer): Int

@ExternalSymbolName("FilaView_getFogOptions")
private external fun FilaView_getFogOptions(view: NativePointer, floats: NativePointer, ints: NativePointer)

@ExternalSymbolName("FilaView_getGridSize")
private external fun FilaView_getGridSize(view: NativePointer): Double

@ExternalSymbolName("FilaView_getGuardBandOptions")
private external fun FilaView_getGuardBandOptions(view: NativePointer, ints: NativePointer)

@ExternalSymbolName("FilaView_getLastDynamicResolutionScale")
private external fun FilaView_getLastDynamicResolutionScale(view: NativePointer, out: NativePointer)

@ExternalSymbolName("FilaView_getMaterialGlobal")
private external fun FilaView_getMaterialGlobal(view: NativePointer, index: Int, out: NativePointer)

@ExternalSymbolName("FilaView_getMultiSampleAntiAliasingOptions")
private external fun FilaView_getMultiSampleAntiAliasingOptions(view: NativePointer, ints: NativePointer)

@ExternalSymbolName("FilaView_getName")
private external fun FilaView_getName(view: NativePointer): NativePointer

@ExternalSymbolName("FilaView_getRenderQuality")
private external fun FilaView_getRenderQuality(view: NativePointer): Int

@ExternalSymbolName("FilaView_getScreenSpaceReflectionsOptions")
private external fun FilaView_getScreenSpaceReflectionsOptions(view: NativePointer, floats: NativePointer, ints: NativePointer)

@ExternalSymbolName("FilaView_getSoftShadowOptions")
private external fun FilaView_getSoftShadowOptions(view: NativePointer, floats: NativePointer)

@ExternalSymbolName("FilaView_getStereoscopicOptions")
private external fun FilaView_getStereoscopicOptions(view: NativePointer, ints: NativePointer)

@ExternalSymbolName("FilaView_getTemporalAntiAliasingOptions")
private external fun FilaView_getTemporalAntiAliasingOptions(view: NativePointer, floats: NativePointer, ints: NativePointer)

@ExternalSymbolName("FilaView_getViewport")
private external fun FilaView_getViewport(view: NativePointer, left: NativePointer, bottom: NativePointer, width: NativePointer, height: NativePointer)

@ExternalSymbolName("FilaView_getVignetteOptions")
private external fun FilaView_getVignetteOptions(view: NativePointer, floats: NativePointer, ints: NativePointer)

@ExternalSymbolName("FilaView_getVisibleLayers")
private external fun FilaView_getVisibleLayers(view: NativePointer): Int

@ExternalSymbolName("FilaView_getVisibleRenderableCount")
private external fun FilaView_getVisibleRenderableCount(view: NativePointer): Int

@ExternalSymbolName("FilaView_getVsmShadowOptions")
private external fun FilaView_getVsmShadowOptions(view: NativePointer, floats: NativePointer, ints: NativePointer)

@ExternalSymbolName("FilaView_hasCamera")
private external fun FilaView_hasCamera(view: NativePointer): Boolean

@ExternalSymbolName("FilaView_isFrontFaceWindingInverted")
private external fun FilaView_isFrontFaceWindingInverted(view: NativePointer): Boolean

@ExternalSymbolName("FilaView_isFrustumCullingEnabled")
private external fun FilaView_isFrustumCullingEnabled(view: NativePointer): Boolean

@ExternalSymbolName("FilaView_isPostProcessingEnabled")
private external fun FilaView_isPostProcessingEnabled(view: NativePointer): Boolean

@ExternalSymbolName("FilaView_isScreenSpaceRefractionEnabled")
private external fun FilaView_isScreenSpaceRefractionEnabled(view: NativePointer): Boolean

@ExternalSymbolName("FilaView_isShadowingEnabled")
private external fun FilaView_isShadowingEnabled(view: NativePointer): Boolean

@ExternalSymbolName("FilaView_isStencilBufferEnabled")
private external fun FilaView_isStencilBufferEnabled(view: NativePointer): Boolean

@ExternalSymbolName("FilaView_isTransparentPickingEnabled")
private external fun FilaView_isTransparentPickingEnabled(view: NativePointer): Boolean

@ExternalSymbolName("FilaView_pick")
private external fun FilaView_pick(view: NativePointer, x: Int, y: Int, handler: NativePointer, callback: NativePointer, userData: NativePointer)

@ExternalSymbolName("FilaView_readPickingResult")
private external fun FilaView_readPickingResult(result: NativePointer, renderable: NativePointer, depthAndFragCoords: NativePointer)

@ExternalSymbolName("FilaView_setAmbientOcclusionOptions")
private external fun FilaView_setAmbientOcclusionOptions(view: NativePointer, radius: Float, bias: Float, power: Float, resolution: Float, intensity: Float, bilateralThreshold: Float, quality: Int, lowPassFilter: Int, upsampling: Int, enabled: Boolean, bentNormals: Boolean, minHorizonAngleRad: Float, ssct_lightConeRad: Float, ssct_shadowDistance: Float, ssct_contactDistanceMax: Float, ssct_intensity: Float, ssct_lightDirection_0: Float, ssct_lightDirection_1: Float, ssct_lightDirection_2: Float, ssct_depthBias: Float, ssct_depthSlopeBias: Float, ssct_sampleCount: Int, ssct_rayCount: Int, ssct_enabled: Boolean, gtao_sampleSliceCount: Int, gtao_sampleStepsPerSlice: Int, gtao_thicknessHeuristic: Float, gtao_useVisibilityBitmasks: Boolean, gtao_constThickness: Float, gtao_linearThickness: Boolean, aoType: Int)

@ExternalSymbolName("FilaView_setAntiAliasing")
private external fun FilaView_setAntiAliasing(view: NativePointer, type: Int)

@ExternalSymbolName("FilaView_setBlendMode")
private external fun FilaView_setBlendMode(view: NativePointer, blendMode: Int)

@ExternalSymbolName("FilaView_setBloomOptions")
private external fun FilaView_setBloomOptions(view: NativePointer, dirt: NativePointer, dirtStrength: Float, strength: Float, resolution: Int, levels: Int, blendMode: Int, threshold: Boolean, enabled: Boolean, highlight: Float, quality: Int, lensFlare: Boolean, starburst: Boolean, chromaticAberration: Float, ghostCount: Int, ghostSpacing: Float, ghostThreshold: Float, haloThickness: Float, haloRadius: Float, haloThreshold: Float)

@ExternalSymbolName("FilaView_setCamera")
private external fun FilaView_setCamera(view: NativePointer, camera: NativePointer)

@ExternalSymbolName("FilaView_setColorGrading")
private external fun FilaView_setColorGrading(view: NativePointer, colorGrading: NativePointer)

@ExternalSymbolName("FilaView_setDepthOfFieldOptions")
private external fun FilaView_setDepthOfFieldOptions(view: NativePointer, cocScale: Float, cocAspectRatio: Float, maxApertureDiameter: Float, enabled: Boolean, filter: Int, nativeResolution: Boolean, foregroundRingCount: Int, backgroundRingCount: Int, fastGatherRingCount: Int, maxForegroundCOC: Int, maxBackgroundCOC: Int)

@ExternalSymbolName("FilaView_setDithering")
private external fun FilaView_setDithering(view: NativePointer, dithering: Int)

@ExternalSymbolName("FilaView_setDynamicLightingOptions")
private external fun FilaView_setDynamicLightingOptions(view: NativePointer, zLightNear: Float, zLightFar: Float)

@ExternalSymbolName("FilaView_setDynamicResolutionOptions")
private external fun FilaView_setDynamicResolutionOptions(view: NativePointer, minScale_0: Float, minScale_1: Float, maxScale_0: Float, maxScale_1: Float, sharpness: Float, enabled: Boolean, homogeneousScaling: Boolean, quality: Int)

@ExternalSymbolName("FilaView_setFogOptions")
private external fun FilaView_setFogOptions(view: NativePointer, distance: Float, cutOffDistance: Float, maximumOpacity: Float, height: Float, heightFalloff: Float, color_0: Float, color_1: Float, color_2: Float, density: Float, inScatteringStart: Float, inScatteringSize: Float, fogColorFromIbl: Boolean, skyColor: NativePointer, enabled: Boolean)

@ExternalSymbolName("FilaView_setFrontFaceWindingInverted")
private external fun FilaView_setFrontFaceWindingInverted(view: NativePointer, inverted: Boolean)

@ExternalSymbolName("FilaView_setFrustumCullingEnabled")
private external fun FilaView_setFrustumCullingEnabled(view: NativePointer, enabled: Boolean)

@ExternalSymbolName("FilaView_setGridSize")
private external fun FilaView_setGridSize(view: NativePointer, size: Double)

@ExternalSymbolName("FilaView_setGuardBandOptions")
private external fun FilaView_setGuardBandOptions(view: NativePointer, enabled: Boolean)

@ExternalSymbolName("FilaView_setMaterialGlobal")
private external fun FilaView_setMaterialGlobal(view: NativePointer, index: Int, x: Float, y: Float, z: Float, w: Float)

@ExternalSymbolName("FilaView_setMultiSampleAntiAliasingOptions")
private external fun FilaView_setMultiSampleAntiAliasingOptions(view: NativePointer, enabled: Boolean, sampleCount: Int, customResolve: Boolean)

@ExternalSymbolName("FilaView_setName")
private external fun FilaView_setName(view: NativePointer, name: NativePointer)

@ExternalSymbolName("FilaView_setPostProcessingEnabled")
private external fun FilaView_setPostProcessingEnabled(view: NativePointer, enabled: Boolean)

@ExternalSymbolName("FilaView_setRenderQuality")
private external fun FilaView_setRenderQuality(view: NativePointer, hdrColorBufferQuality: Int)

@ExternalSymbolName("FilaView_setRenderTarget")
private external fun FilaView_setRenderTarget(view: NativePointer, renderTarget: NativePointer)

@ExternalSymbolName("FilaView_setScene")
private external fun FilaView_setScene(view: NativePointer, scene: NativePointer)

@ExternalSymbolName("FilaView_setScreenSpaceReflectionsOptions")
private external fun FilaView_setScreenSpaceReflectionsOptions(view: NativePointer, thickness: Float, bias: Float, maxDistance: Float, stride: Float, enabled: Boolean)

@ExternalSymbolName("FilaView_setScreenSpaceRefractionEnabled")
private external fun FilaView_setScreenSpaceRefractionEnabled(view: NativePointer, enabled: Boolean)

@ExternalSymbolName("FilaView_setShadowType")
private external fun FilaView_setShadowType(view: NativePointer, type: Int)

@ExternalSymbolName("FilaView_setShadowingEnabled")
private external fun FilaView_setShadowingEnabled(view: NativePointer, enabled: Boolean)

@ExternalSymbolName("FilaView_setSoftShadowOptions")
private external fun FilaView_setSoftShadowOptions(view: NativePointer, penumbraScale: Float, penumbraRatioScale: Float, maxPenumbraRatio: Float, maxSearchRadius: Float)

@ExternalSymbolName("FilaView_setStencilBufferEnabled")
private external fun FilaView_setStencilBufferEnabled(view: NativePointer, enabled: Boolean)

@ExternalSymbolName("FilaView_setStereoscopicOptions")
private external fun FilaView_setStereoscopicOptions(view: NativePointer, enabled: Boolean)

@ExternalSymbolName("FilaView_setTemporalAntiAliasingOptions")
private external fun FilaView_setTemporalAntiAliasingOptions(view: NativePointer, feedback: Float, lodBias: Float, sharpness: Float, enabled: Boolean, upscaling: Float, filterHistory: Boolean, filterInput: Boolean, useYCoCg: Boolean, hdr: Boolean, boxType: Int, boxClipping: Int, jitterPattern: Int, varianceGamma: Float, preventFlickering: Boolean, historyReprojection: Boolean)

@ExternalSymbolName("FilaView_setTransparentPickingEnabled")
private external fun FilaView_setTransparentPickingEnabled(view: NativePointer, enabled: Boolean)

@ExternalSymbolName("FilaView_setViewport")
private external fun FilaView_setViewport(view: NativePointer, left: Int, bottom: Int, width: Int, height: Int)

@ExternalSymbolName("FilaView_setVignetteOptions")
private external fun FilaView_setVignetteOptions(view: NativePointer, midPoint: Float, roundness: Float, feather: Float, color_0: Float, color_1: Float, color_2: Float, color_3: Float, enabled: Boolean)

@ExternalSymbolName("FilaView_setVisibleLayers")
private external fun FilaView_setVisibleLayers(view: NativePointer, select: Int, value: Int)

@ExternalSymbolName("FilaView_setVsmShadowOptions")
private external fun FilaView_setVsmShadowOptions(view: NativePointer, anisotropy: Int, mipmapping: Boolean, msaaSamples: Int, highPrecision: Boolean, lightBleedReduction: Float)
