package io.github.erkko68.filament

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
expect class View {
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
     * - DPCF: Directional Percentage Closer Filtering
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
     * Structure used to set the quality of the HDR color buffer.
     *
     * Kept nested: one field, and `RenderQuality` at package level would collide with
     * filament-compose's own `RenderQuality`.
     */
    class RenderQuality() {
        /** Sets the quality of the HDR color buffer. Default: HIGH. */
        var hdrColorBuffer: Quality
    }

    /**
     * Result of a picking (color-picking) query.
     *
     * @param renderable Entity ID of the picked renderable
     * @param depth Depth of the picked fragment
     * @param fragCoords Fragment coordinates (x, y) of the pick location
     */
    class PickingQueryResult(
        renderable: Int,
        depth: Float,
        fragCoords: FloatArray
    ) {
        val renderable: Int
        val depth: Float
        val fragCoords: FloatArray
    }

    /** Debug name of this View, shown in diagnostic tools. */
    var name: String?

    /**
     * The [Scene] associated with this View. A Scene can be associated to several Views.
     *
     * Set to `null` to dissociate the current Scene. The View does not take ownership of the Scene.
     *
     * There is no reference-counting: if a Scene is destroyed while still associated with a View, it
     * is automatically dissociated (the View's scene becomes `null`).
     */
    var scene: Scene?

    /**
     * The [Camera] this View is rendered from. A Camera can be associated to several Views.
     *
     * Set to `null` to dissociate the current Camera; the View does not take ownership.
     */
    var camera: Camera?

    /** Whether a [Camera] is currently associated with this View. */
    val hasCamera: Boolean

    /** The rectangular region of the render target this View renders into. */
    var viewport: Viewport

    /** How this View's result blends over the render target's existing content. */
    var blendMode: BlendMode

    /**
     * Sets which layers are visible: for each bit set in [select], visibility is taken from the
     * corresponding bit in [values]. Renderables are assigned layers via
     * `RenderableManager.setLayerMask`. By default all layers are visible.
     */
    fun setVisibleLayers(select: Int, values: Int)

    /** Convenience over [setVisibleLayers] toggling a single layer (0–7). */
    fun setLayerEnabled(layer: Int, enabled: Boolean)

    /** Returns the current visible-layer bitmask. */
    val visibleLayers: Int

    /**
     * Enables or disables the post-processing stage (tone mapping, bloom, color grading, FXAA,
     * dynamic scaling, …). Disabling it also disables features that depend on it. Default: enabled.
     */
    var isPostProcessingEnabled: Boolean

    /** Dithering applied to the final render to hide banding. Default: [Dithering.TEMPORAL]. */
    var dithering: Dithering

    /** Dynamic-resolution (render scaling) configuration for this View. */
    var dynamicResolutionOptions: DynamicResolutionOptions

    /** Returns the `[x, y]` scale factors dynamic resolution used on the last frame. */
    val lastDynamicResolutionScale: FloatArray

    /** Global quality/performance trade-offs (e.g. color-buffer precision) for this View. */
    var renderQuality: RenderQuality

    /** Bloom post-processing configuration (requires post-processing enabled). */
    var bloomOptions: BloomOptions

    /** Large-scale atmospheric fog configuration. */
    var fogOptions: FogOptions

    /** Depth-of-field post-processing configuration (needs a focused [camera]). */
    var depthOfFieldOptions: DepthOfFieldOptions

    /** Vignette post-processing configuration. */
    var vignetteOptions: VignetteOptions

    /** Screen-space ambient occlusion (SSAO) configuration. */
    var ambientOcclusionOptions: AmbientOcclusionOptions

    /** Temporal anti-aliasing (TAA) configuration; effective when [antiAliasing] permits it. */
    var temporalAntiAliasingOptions: TemporalAntiAliasingOptions

    /** Screen-space reflections configuration. */
    var screenSpaceReflectionsOptions: ScreenSpaceReflectionsOptions

    /**
     * Off-screen [RenderTarget] to render into, or `null` to render into the SwapChain.
     * The render target is not owned by the View.
     */
    var renderTarget: RenderTarget?

    /** Shadow mapping technique for the whole View ([ShadowType.PCF], VSM, DPCF, PCSS). */
    var shadowType: ShadowType

    /** Variance shadow mapping options; only applies when [shadowType] is [ShadowType.VSM]. */
    var vsmShadowOptions: VsmShadowOptions

    /** Soft shadow options; only applies when [shadowType] is DPCF or PCSS. */
    var softShadowOptions: SoftShadowOptions

    /** Guard-band configuration, letting some effects sample outside the viewport. */
    var guardBandOptions: GuardBandOptions

    /** Stereoscopic (VR) rendering configuration; must be set before the first frame. */
    var stereoscopicOptions: StereoscopicOptions

    /** Hardware MSAA configuration (independent of [antiAliasing]/TAA). */
    var multiSampleAntiAliasingOptions: MultiSampleAntiAliasingOptions

    /** Culls renderables outside the camera frustum. Default: true (disable only for debugging). */
    var isFrustumCullingEnabled: Boolean

    /** Master switch for shadow mapping in this View. Default: true. */
    var isShadowingEnabled: Boolean

    /** Enables screen-space refraction for refractive materials. Default: true. */
    var isScreenSpaceRefractionEnabled: Boolean

    /** Allocates a stencil buffer for this View (required for stencil-based effects). Default: false. */
    var isStencilBufferEnabled: Boolean

    /**
     * Inverts the winding order considered front-facing (counter-clockwise by default).
     * Useful for mirror-like reflections rendered with a flipped camera.
     */
    var isFrontFaceWindingInverted: Boolean

    /** Includes transparent renderables in [pick] results. Default: true. */
    var isTransparentPickingEnabled: Boolean

    /**
     * Grid size in world units used for grid-based world-origin snapping. 0 or negative means the
     * size is calculated automatically from the camera frustum. Default: 0 (automatic).
     */
    var gridSize: Double

    /**
     * The effective grid size used for world-origin snapping: [gridSize] when positive, otherwise
     * the automatically calculated size.
     */
    val effectiveGridSize: Double

    /** Sets the float4 material-global value at [index] (0–3), readable from all materials. */
    fun setMaterialGlobal(index: Int, value: FloatArray)

    /** Returns the float4 material-global value at [index] (0–3). */
    fun getMaterialGlobal(index: Int): FloatArray

    /** Discards accumulated frame history (TAA, SSR). Call after a camera cut to avoid ghosting. */
    fun clearFrameHistory(engine: Engine)

    /**
     * Sets the near/far planes (in world units, > 0) used to compute the froxel grid for dynamic
     * lighting. Only lights within this range are lit. Defaults: 5 / 100.
     */
    fun setDynamicLightingOptions(zNear: Float, zFar: Float)

    /** Entity representing the large-scale fog object; can be transformed via TransformManager. */
    val fogEntity: Entity

    /** Post-process anti-aliasing operator ([AntiAliasing.FXAA] by default). */
    var antiAliasing: AntiAliasing

    /** Color grading to apply, or `null` for the default. The View does not own it. */
    var colorGrading: ColorGrading?

    /**
     * Returns the most recent number of visible renderables for the current Scene, as calculated
     * the last time Renderer.render() was called with this View and Scene.
     *
     * @return the number of visible renderables, or -1 if no value is available (e.g. before the
     *         first render call, or if the scene was detached).
     */
    val visibleRenderableCount: Int

    /**
     * Asynchronously picks the renderable at viewport coordinates ([x], [y]) — origin bottom-left —
     * and invokes [callback] with the result a few frames later. Requires the picking feature
     * (enabled by default) and a rendered frame.
     */
    fun pick(x: Int, y: Int, callback: (PickingQueryResult) -> Unit)
}

