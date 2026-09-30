package io.github.erkko68.filament

import io.github.erkko68.filament.capi.*
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
     * Result of a picking (color-picking) query.
     *
     * @param renderable Entity ID of the picked renderable
     * @param depth Depth of the picked fragment
     * @param fragCoords Screen-space coordinates (x, y, z) of the hit, in GL convention
     */
    class PickingQueryResult(
        val renderable: Int,
        val depth: Float,
        val fragCoords: FloatArray
    )

    private var mScene: Scene? = null
    private var mCamera: Camera? = null
    private var mRenderTarget: RenderTarget? = null
    private var mColorGrading: ColorGrading? = null
    // C hands textures back as handles only: the getters return these while the handle still matches.
    private var mBloomDirt: Texture? = null
    private var mFogSkyColor: Texture? = null

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
        get() = viewportOf { FilaView_getViewport(nativeHandle, it) }
        set(value) { value.useNative { FilaView_setViewport(nativeHandle, it) } }

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
    /** Enables or disables a single visibility [layer] (0–7). */
    fun setLayerEnabled(layer: Int, enabled: Boolean) { FilaView_setLayerEnabled(nativeHandle, layer, enabled) }
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
        get() = withHandle({ FilaDynamicResolutionOptions_create() }, { FilaDynamicResolutionOptions_destroy(it) }) { o ->
            FilaView_getDynamicResolutionOptions(nativeHandle, o)
            DynamicResolutionOptions().apply {
                minScale.usePinned { FilaDynamicResolutionOptions_getMinScale(o, it) }
                maxScale.usePinned { FilaDynamicResolutionOptions_getMaxScale(o, it) }
                sharpness = FilaDynamicResolutionOptions_getSharpness(o)
                enabled = FilaDynamicResolutionOptions_getEnabled(o)
                homogeneousScaling = FilaDynamicResolutionOptions_getHomogeneousScaling(o)
                quality = QualityLevel.entries[FilaDynamicResolutionOptions_getQuality(o)]
            }
        }
        set(value) = withHandle({ FilaDynamicResolutionOptions_create() }, { FilaDynamicResolutionOptions_destroy(it) }) { o ->
            value.minScale.usePinned { FilaDynamicResolutionOptions_setMinScale(o, it) }
            value.maxScale.usePinned { FilaDynamicResolutionOptions_setMaxScale(o, it) }
            FilaDynamicResolutionOptions_setSharpness(o, value.sharpness)
            FilaDynamicResolutionOptions_setEnabled(o, value.enabled)
            FilaDynamicResolutionOptions_setHomogeneousScaling(o, value.homogeneousScaling)
            FilaDynamicResolutionOptions_setQuality(o, value.quality.ordinal)
            FilaView_setDynamicResolutionOptions(nativeHandle, o)
        }

    /** Returns the `[x, y]` scale factors dynamic resolution used on the last frame. */
    val lastDynamicResolutionScale: FloatArray
        get() = FloatArray(2).also { out -> out.usePinned { FilaView_getLastDynamicResolutionScale(nativeHandle, it) } }

    /**
     * Global quality/performance trade-offs (e.g. color-buffer precision) for this View.
     * The getter returns a snapshot — mutate it and assign back to apply.
     */
    var renderQuality: RenderQuality
        get() = withHandle({ FilaRenderQuality_create() }, { FilaRenderQuality_destroy(it) }) { o ->
            FilaView_getRenderQuality(nativeHandle, o)
            RenderQuality().apply { hdrColorBuffer = QualityLevel.entries[FilaRenderQuality_getHdrColorBuffer(o)] }
        }
        set(value) = withHandle({ FilaRenderQuality_create() }, { FilaRenderQuality_destroy(it) }) { o ->
            FilaRenderQuality_setHdrColorBuffer(o, value.hdrColorBuffer.ordinal)
            FilaView_setRenderQuality(nativeHandle, o)
        }

    /**
     * Bloom post-processing configuration (requires post-processing enabled).
     * The getter returns a snapshot — mutate it and assign back to apply.
     */
    var bloomOptions: BloomOptions
        get() = withHandle({ FilaBloomOptions_create() }, { FilaBloomOptions_destroy(it) }) { o ->
            FilaView_getBloomOptions(nativeHandle, o)
            BloomOptions().apply {
                dirtStrength = FilaBloomOptions_getDirtStrength(o)
                strength = FilaBloomOptions_getStrength(o)
                resolution = FilaBloomOptions_getResolution(o)
                levels = FilaBloomOptions_getLevels(o)
                blendMode = BloomOptions.BlendMode.entries[FilaBloomOptions_getBlendMode(o)]
                threshold = FilaBloomOptions_getThreshold(o)
                enabled = FilaBloomOptions_getEnabled(o)
                highlight = FilaBloomOptions_getHighlight(o)
                quality = QualityLevel.entries[FilaBloomOptions_getQuality(o)]
                lensFlare = FilaBloomOptions_getLensFlare(o)
                starburst = FilaBloomOptions_getStarburst(o)
                chromaticAberration = FilaBloomOptions_getChromaticAberration(o)
                ghostCount = FilaBloomOptions_getGhostCount(o)
                ghostSpacing = FilaBloomOptions_getGhostSpacing(o)
                ghostThreshold = FilaBloomOptions_getGhostThreshold(o)
                haloThickness = FilaBloomOptions_getHaloThickness(o)
                haloRadius = FilaBloomOptions_getHaloRadius(o)
                haloThreshold = FilaBloomOptions_getHaloThreshold(o)
                dirt = mBloomDirt?.takeIf { it.nativeHandle == FilaBloomOptions_getDirt(o) }
            }
        }
        set(value) = withHandle({ FilaBloomOptions_create() }, { FilaBloomOptions_destroy(it) }) { o ->
            FilaBloomOptions_setDirtStrength(o, value.dirtStrength)
            FilaBloomOptions_setStrength(o, value.strength)
            FilaBloomOptions_setResolution(o, value.resolution)
            FilaBloomOptions_setLevels(o, value.levels)
            FilaBloomOptions_setBlendMode(o, value.blendMode.ordinal)
            FilaBloomOptions_setThreshold(o, value.threshold)
            FilaBloomOptions_setEnabled(o, value.enabled)
            FilaBloomOptions_setHighlight(o, value.highlight)
            FilaBloomOptions_setQuality(o, value.quality.ordinal)
            FilaBloomOptions_setLensFlare(o, value.lensFlare)
            FilaBloomOptions_setStarburst(o, value.starburst)
            FilaBloomOptions_setChromaticAberration(o, value.chromaticAberration)
            FilaBloomOptions_setGhostCount(o, value.ghostCount)
            FilaBloomOptions_setGhostSpacing(o, value.ghostSpacing)
            FilaBloomOptions_setGhostThreshold(o, value.ghostThreshold)
            FilaBloomOptions_setHaloThickness(o, value.haloThickness)
            FilaBloomOptions_setHaloRadius(o, value.haloRadius)
            FilaBloomOptions_setHaloThreshold(o, value.haloThreshold)
            FilaBloomOptions_setDirt(o, value.dirt?.nativeHandle ?: NullPointer)
            mBloomDirt = value.dirt
            FilaView_setBloomOptions(nativeHandle, o)
        }

    /**
     * Large-scale atmospheric fog configuration.
     * The getter returns a snapshot — mutate it and assign back to apply.
     */
    var fogOptions: FogOptions
        get() = withHandle({ FilaFogOptions_create() }, { FilaFogOptions_destroy(it) }) { o ->
            FilaView_getFogOptions(nativeHandle, o)
            FogOptions().apply {
                distance = FilaFogOptions_getDistance(o)
                cutOffDistance = FilaFogOptions_getCutOffDistance(o)
                maximumOpacity = FilaFogOptions_getMaximumOpacity(o)
                height = FilaFogOptions_getHeight(o)
                heightFalloff = FilaFogOptions_getHeightFalloff(o)
                color.usePinned { FilaFogOptions_getColor(o, it) }
                density = FilaFogOptions_getDensity(o)
                inScatteringStart = FilaFogOptions_getInScatteringStart(o)
                inScatteringSize = FilaFogOptions_getInScatteringSize(o)
                fogColorFromIbl = FilaFogOptions_getFogColorFromIbl(o)
                enabled = FilaFogOptions_getEnabled(o)
                skyColor = mFogSkyColor?.takeIf { it.nativeHandle == FilaFogOptions_getSkyColor(o) }
            }
        }
        set(value) = withHandle({ FilaFogOptions_create() }, { FilaFogOptions_destroy(it) }) { o ->
            FilaFogOptions_setDistance(o, value.distance)
            FilaFogOptions_setCutOffDistance(o, value.cutOffDistance)
            FilaFogOptions_setMaximumOpacity(o, value.maximumOpacity)
            FilaFogOptions_setHeight(o, value.height)
            FilaFogOptions_setHeightFalloff(o, value.heightFalloff)
            value.color.usePinned { FilaFogOptions_setColor(o, it) }
            FilaFogOptions_setDensity(o, value.density)
            FilaFogOptions_setInScatteringStart(o, value.inScatteringStart)
            FilaFogOptions_setInScatteringSize(o, value.inScatteringSize)
            FilaFogOptions_setFogColorFromIbl(o, value.fogColorFromIbl)
            FilaFogOptions_setEnabled(o, value.enabled)
            FilaFogOptions_setSkyColor(o, value.skyColor?.nativeHandle ?: NullPointer)
            mFogSkyColor = value.skyColor
            FilaView_setFogOptions(nativeHandle, o)
        }

    /**
     * Depth-of-field post-processing configuration (needs a focused [camera]).
     * The getter returns a snapshot — mutate it and assign back to apply.
     */
    var depthOfFieldOptions: DepthOfFieldOptions
        get() = withHandle({ FilaDepthOfFieldOptions_create() }, { FilaDepthOfFieldOptions_destroy(it) }) { o ->
            FilaView_getDepthOfFieldOptions(nativeHandle, o)
            DepthOfFieldOptions().apply {
                cocScale = FilaDepthOfFieldOptions_getCocScale(o)
                cocAspectRatio = FilaDepthOfFieldOptions_getCocAspectRatio(o)
                maxApertureDiameter = FilaDepthOfFieldOptions_getMaxApertureDiameter(o)
                enabled = FilaDepthOfFieldOptions_getEnabled(o)
                filter = DepthOfFieldOptions.Filter.entries[FilaDepthOfFieldOptions_getFilter(o)]
                nativeResolution = FilaDepthOfFieldOptions_getNativeResolution(o)
                foregroundRingCount = FilaDepthOfFieldOptions_getForegroundRingCount(o)
                backgroundRingCount = FilaDepthOfFieldOptions_getBackgroundRingCount(o)
                fastGatherRingCount = FilaDepthOfFieldOptions_getFastGatherRingCount(o)
                maxForegroundCOC = FilaDepthOfFieldOptions_getMaxForegroundCOC(o)
                maxBackgroundCOC = FilaDepthOfFieldOptions_getMaxBackgroundCOC(o)
            }
        }
        set(value) = withHandle({ FilaDepthOfFieldOptions_create() }, { FilaDepthOfFieldOptions_destroy(it) }) { o ->
            FilaDepthOfFieldOptions_setCocScale(o, value.cocScale)
            FilaDepthOfFieldOptions_setCocAspectRatio(o, value.cocAspectRatio)
            FilaDepthOfFieldOptions_setMaxApertureDiameter(o, value.maxApertureDiameter)
            FilaDepthOfFieldOptions_setEnabled(o, value.enabled)
            FilaDepthOfFieldOptions_setFilter(o, value.filter.ordinal)
            FilaDepthOfFieldOptions_setNativeResolution(o, value.nativeResolution)
            FilaDepthOfFieldOptions_setForegroundRingCount(o, value.foregroundRingCount)
            FilaDepthOfFieldOptions_setBackgroundRingCount(o, value.backgroundRingCount)
            FilaDepthOfFieldOptions_setFastGatherRingCount(o, value.fastGatherRingCount)
            FilaDepthOfFieldOptions_setMaxForegroundCOC(o, value.maxForegroundCOC)
            FilaDepthOfFieldOptions_setMaxBackgroundCOC(o, value.maxBackgroundCOC)
            FilaView_setDepthOfFieldOptions(nativeHandle, o)
        }

    /**
     * Vignette post-processing configuration.
     * The getter returns a snapshot — mutate it and assign back to apply.
     */
    var vignetteOptions: VignetteOptions
        get() = withHandle({ FilaVignetteOptions_create() }, { FilaVignetteOptions_destroy(it) }) { o ->
            FilaView_getVignetteOptions(nativeHandle, o)
            VignetteOptions().apply {
                midPoint = FilaVignetteOptions_getMidPoint(o)
                roundness = FilaVignetteOptions_getRoundness(o)
                feather = FilaVignetteOptions_getFeather(o)
                color.usePinned { FilaVignetteOptions_getColor(o, it) }
                enabled = FilaVignetteOptions_getEnabled(o)
            }
        }
        set(value) = withHandle({ FilaVignetteOptions_create() }, { FilaVignetteOptions_destroy(it) }) { o ->
            FilaVignetteOptions_setMidPoint(o, value.midPoint)
            FilaVignetteOptions_setRoundness(o, value.roundness)
            FilaVignetteOptions_setFeather(o, value.feather)
            value.color.usePinned { FilaVignetteOptions_setColor(o, it) }
            FilaVignetteOptions_setEnabled(o, value.enabled)
            FilaView_setVignetteOptions(nativeHandle, o)
        }

    /**
     * Screen-space ambient occlusion (SSAO) configuration.
     * The getter returns a snapshot — mutate it and assign back to apply.
     */
    var ambientOcclusionOptions: AmbientOcclusionOptions
        get() = withHandle({ FilaAmbientOcclusionOptions_create() }, { FilaAmbientOcclusionOptions_destroy(it) }) { o ->
            FilaView_getAmbientOcclusionOptions(nativeHandle, o)
            AmbientOcclusionOptions().apply {
                aoType = AmbientOcclusionOptions.AmbientOcclusionType.entries[FilaAmbientOcclusionOptions_getAoType(o)]
                radius = FilaAmbientOcclusionOptions_getRadius(o)
                power = FilaAmbientOcclusionOptions_getPower(o)
                bias = FilaAmbientOcclusionOptions_getBias(o)
                resolution = FilaAmbientOcclusionOptions_getResolution(o)
                intensity = FilaAmbientOcclusionOptions_getIntensity(o)
                bilateralThreshold = FilaAmbientOcclusionOptions_getBilateralThreshold(o)
                quality = QualityLevel.entries[FilaAmbientOcclusionOptions_getQuality(o)]
                lowPassFilter = QualityLevel.entries[FilaAmbientOcclusionOptions_getLowPassFilter(o)]
                upsampling = QualityLevel.entries[FilaAmbientOcclusionOptions_getUpsampling(o)]
                enabled = FilaAmbientOcclusionOptions_getEnabled(o)
                bentNormals = FilaAmbientOcclusionOptions_getBentNormals(o)
                minHorizonAngleRad = FilaAmbientOcclusionOptions_getMinHorizonAngleRad(o)
                withHandle({ FilaAmbientOcclusionOptionsSsct_create() }, { FilaAmbientOcclusionOptionsSsct_destroy(it) }) { n ->
                    FilaAmbientOcclusionOptions_getSsct(o, n)
                    ssct.apply {
                        lightConeRad = FilaAmbientOcclusionOptionsSsct_getLightConeRad(n)
                        shadowDistance = FilaAmbientOcclusionOptionsSsct_getShadowDistance(n)
                        contactDistanceMax = FilaAmbientOcclusionOptionsSsct_getContactDistanceMax(n)
                        intensity = FilaAmbientOcclusionOptionsSsct_getIntensity(n)
                        lightDirection.usePinned { FilaAmbientOcclusionOptionsSsct_getLightDirection(n, it) }
                        depthBias = FilaAmbientOcclusionOptionsSsct_getDepthBias(n)
                        depthSlopeBias = FilaAmbientOcclusionOptionsSsct_getDepthSlopeBias(n)
                        sampleCount = FilaAmbientOcclusionOptionsSsct_getSampleCount(n)
                        rayCount = FilaAmbientOcclusionOptionsSsct_getRayCount(n)
                        enabled = FilaAmbientOcclusionOptionsSsct_getEnabled(n)
                    }
                }
                withHandle({ FilaAmbientOcclusionOptionsGtao_create() }, { FilaAmbientOcclusionOptionsGtao_destroy(it) }) { n ->
                    FilaAmbientOcclusionOptions_getGtao(o, n)
                    gtao.apply {
                        sampleSliceCount = FilaAmbientOcclusionOptionsGtao_getSampleSliceCount(n)
                        sampleStepsPerSlice = FilaAmbientOcclusionOptionsGtao_getSampleStepsPerSlice(n)
                        thicknessHeuristic = FilaAmbientOcclusionOptionsGtao_getThicknessHeuristic(n)
                        useVisibilityBitmasks = FilaAmbientOcclusionOptionsGtao_getUseVisibilityBitmasks(n)
                        constThickness = FilaAmbientOcclusionOptionsGtao_getConstThickness(n)
                        linearThickness = FilaAmbientOcclusionOptionsGtao_getLinearThickness(n)
                    }
                }
            }
        }
        set(value) = withHandle({ FilaAmbientOcclusionOptions_create() }, { FilaAmbientOcclusionOptions_destroy(it) }) { o ->
            FilaAmbientOcclusionOptions_setAoType(o, value.aoType.ordinal)
            FilaAmbientOcclusionOptions_setRadius(o, value.radius)
            FilaAmbientOcclusionOptions_setPower(o, value.power)
            FilaAmbientOcclusionOptions_setBias(o, value.bias)
            FilaAmbientOcclusionOptions_setResolution(o, value.resolution)
            FilaAmbientOcclusionOptions_setIntensity(o, value.intensity)
            FilaAmbientOcclusionOptions_setBilateralThreshold(o, value.bilateralThreshold)
            FilaAmbientOcclusionOptions_setQuality(o, value.quality.ordinal)
            FilaAmbientOcclusionOptions_setLowPassFilter(o, value.lowPassFilter.ordinal)
            FilaAmbientOcclusionOptions_setUpsampling(o, value.upsampling.ordinal)
            FilaAmbientOcclusionOptions_setEnabled(o, value.enabled)
            FilaAmbientOcclusionOptions_setBentNormals(o, value.bentNormals)
            FilaAmbientOcclusionOptions_setMinHorizonAngleRad(o, value.minHorizonAngleRad)
            withHandle({ FilaAmbientOcclusionOptionsSsct_create() }, { FilaAmbientOcclusionOptionsSsct_destroy(it) }) { n ->
                FilaAmbientOcclusionOptionsSsct_setLightConeRad(n, value.ssct.lightConeRad)
                FilaAmbientOcclusionOptionsSsct_setShadowDistance(n, value.ssct.shadowDistance)
                FilaAmbientOcclusionOptionsSsct_setContactDistanceMax(n, value.ssct.contactDistanceMax)
                FilaAmbientOcclusionOptionsSsct_setIntensity(n, value.ssct.intensity)
                value.ssct.lightDirection.usePinned { FilaAmbientOcclusionOptionsSsct_setLightDirection(n, it) }
                FilaAmbientOcclusionOptionsSsct_setDepthBias(n, value.ssct.depthBias)
                FilaAmbientOcclusionOptionsSsct_setDepthSlopeBias(n, value.ssct.depthSlopeBias)
                FilaAmbientOcclusionOptionsSsct_setSampleCount(n, value.ssct.sampleCount)
                FilaAmbientOcclusionOptionsSsct_setRayCount(n, value.ssct.rayCount)
                FilaAmbientOcclusionOptionsSsct_setEnabled(n, value.ssct.enabled)
                FilaAmbientOcclusionOptions_setSsct(o, n)
            }
            withHandle({ FilaAmbientOcclusionOptionsGtao_create() }, { FilaAmbientOcclusionOptionsGtao_destroy(it) }) { n ->
                FilaAmbientOcclusionOptionsGtao_setSampleSliceCount(n, value.gtao.sampleSliceCount)
                FilaAmbientOcclusionOptionsGtao_setSampleStepsPerSlice(n, value.gtao.sampleStepsPerSlice)
                FilaAmbientOcclusionOptionsGtao_setThicknessHeuristic(n, value.gtao.thicknessHeuristic)
                FilaAmbientOcclusionOptionsGtao_setUseVisibilityBitmasks(n, value.gtao.useVisibilityBitmasks)
                FilaAmbientOcclusionOptionsGtao_setConstThickness(n, value.gtao.constThickness)
                FilaAmbientOcclusionOptionsGtao_setLinearThickness(n, value.gtao.linearThickness)
                FilaAmbientOcclusionOptions_setGtao(o, n)
            }
            FilaView_setAmbientOcclusionOptions(nativeHandle, o)
        }

    /**
     * Temporal anti-aliasing (TAA) configuration; effective when [antiAliasing] permits it.
     * The getter returns a snapshot — mutate it and assign back to apply.
     */
    var temporalAntiAliasingOptions: TemporalAntiAliasingOptions
        get() = withHandle({ FilaTemporalAntiAliasingOptions_create() }, { FilaTemporalAntiAliasingOptions_destroy(it) }) { o ->
            FilaView_getTemporalAntiAliasingOptions(nativeHandle, o)
            TemporalAntiAliasingOptions().apply {
                feedback = FilaTemporalAntiAliasingOptions_getFeedback(o)
                lodBias = FilaTemporalAntiAliasingOptions_getLodBias(o)
                sharpness = FilaTemporalAntiAliasingOptions_getSharpness(o)
                enabled = FilaTemporalAntiAliasingOptions_getEnabled(o)
                upscaling = FilaTemporalAntiAliasingOptions_getUpscaling(o)
                filterHistory = FilaTemporalAntiAliasingOptions_getFilterHistory(o)
                filterInput = FilaTemporalAntiAliasingOptions_getFilterInput(o)
                useYCoCg = FilaTemporalAntiAliasingOptions_getUseYCoCg(o)
                hdr = FilaTemporalAntiAliasingOptions_getHdr(o)
                boxType = TemporalAntiAliasingOptions.BoxType.entries[FilaTemporalAntiAliasingOptions_getBoxType(o)]
                boxClipping = TemporalAntiAliasingOptions.BoxClipping.entries[FilaTemporalAntiAliasingOptions_getBoxClipping(o)]
                jitterPattern = TemporalAntiAliasingOptions.JitterPattern.entries[FilaTemporalAntiAliasingOptions_getJitterPattern(o)]
                varianceGamma = FilaTemporalAntiAliasingOptions_getVarianceGamma(o)
                preventFlickering = FilaTemporalAntiAliasingOptions_getPreventFlickering(o)
                historyReprojection = FilaTemporalAntiAliasingOptions_getHistoryReprojection(o)
            }
        }
        set(value) = withHandle({ FilaTemporalAntiAliasingOptions_create() }, { FilaTemporalAntiAliasingOptions_destroy(it) }) { o ->
            FilaTemporalAntiAliasingOptions_setFeedback(o, value.feedback)
            FilaTemporalAntiAliasingOptions_setLodBias(o, value.lodBias)
            FilaTemporalAntiAliasingOptions_setSharpness(o, value.sharpness)
            FilaTemporalAntiAliasingOptions_setEnabled(o, value.enabled)
            FilaTemporalAntiAliasingOptions_setUpscaling(o, value.upscaling)
            FilaTemporalAntiAliasingOptions_setFilterHistory(o, value.filterHistory)
            FilaTemporalAntiAliasingOptions_setFilterInput(o, value.filterInput)
            FilaTemporalAntiAliasingOptions_setUseYCoCg(o, value.useYCoCg)
            FilaTemporalAntiAliasingOptions_setHdr(o, value.hdr)
            FilaTemporalAntiAliasingOptions_setBoxType(o, value.boxType.ordinal)
            FilaTemporalAntiAliasingOptions_setBoxClipping(o, value.boxClipping.ordinal)
            FilaTemporalAntiAliasingOptions_setJitterPattern(o, value.jitterPattern.ordinal)
            FilaTemporalAntiAliasingOptions_setVarianceGamma(o, value.varianceGamma)
            FilaTemporalAntiAliasingOptions_setPreventFlickering(o, value.preventFlickering)
            FilaTemporalAntiAliasingOptions_setHistoryReprojection(o, value.historyReprojection)
            FilaView_setTemporalAntiAliasingOptions(nativeHandle, o)
        }

    /**
     * Screen-space reflections configuration.
     * The getter returns a snapshot — mutate it and assign back to apply.
     */
    var screenSpaceReflectionsOptions: ScreenSpaceReflectionsOptions
        get() = withHandle({ FilaScreenSpaceReflectionsOptions_create() }, { FilaScreenSpaceReflectionsOptions_destroy(it) }) { o ->
            FilaView_getScreenSpaceReflectionsOptions(nativeHandle, o)
            ScreenSpaceReflectionsOptions().apply {
                thickness = FilaScreenSpaceReflectionsOptions_getThickness(o)
                bias = FilaScreenSpaceReflectionsOptions_getBias(o)
                maxDistance = FilaScreenSpaceReflectionsOptions_getMaxDistance(o)
                stride = FilaScreenSpaceReflectionsOptions_getStride(o)
                enabled = FilaScreenSpaceReflectionsOptions_getEnabled(o)
            }
        }
        set(value) = withHandle({ FilaScreenSpaceReflectionsOptions_create() }, { FilaScreenSpaceReflectionsOptions_destroy(it) }) { o ->
            FilaScreenSpaceReflectionsOptions_setThickness(o, value.thickness)
            FilaScreenSpaceReflectionsOptions_setBias(o, value.bias)
            FilaScreenSpaceReflectionsOptions_setMaxDistance(o, value.maxDistance)
            FilaScreenSpaceReflectionsOptions_setStride(o, value.stride)
            FilaScreenSpaceReflectionsOptions_setEnabled(o, value.enabled)
            FilaView_setScreenSpaceReflectionsOptions(nativeHandle, o)
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
        get() = ShadowType.entries[FilaView_getShadowType(nativeHandle)]
        set(value) { FilaView_setShadowType(nativeHandle, value.ordinal) }

    /**
     * Variance shadow mapping options; only applies when [shadowType] is [ShadowType.VSM].
     * The getter returns a snapshot — mutate it and assign back to apply.
     */
    var vsmShadowOptions: VsmShadowOptions
        get() = withHandle({ FilaVsmShadowOptions_create() }, { FilaVsmShadowOptions_destroy(it) }) { o ->
            FilaView_getVsmShadowOptions(nativeHandle, o)
            VsmShadowOptions().apply {
                anisotropy = FilaVsmShadowOptions_getAnisotropy(o)
                mipmapping = FilaVsmShadowOptions_getMipmapping(o)
                msaaSamples = FilaVsmShadowOptions_getMsaaSamples(o)
                highPrecision = FilaVsmShadowOptions_getHighPrecision(o)
                lightBleedReduction = FilaVsmShadowOptions_getLightBleedReduction(o)
            }
        }
        set(value) = withHandle({ FilaVsmShadowOptions_create() }, { FilaVsmShadowOptions_destroy(it) }) { o ->
            FilaVsmShadowOptions_setAnisotropy(o, value.anisotropy)
            FilaVsmShadowOptions_setMipmapping(o, value.mipmapping)
            FilaVsmShadowOptions_setMsaaSamples(o, value.msaaSamples)
            FilaVsmShadowOptions_setHighPrecision(o, value.highPrecision)
            FilaVsmShadowOptions_setLightBleedReduction(o, value.lightBleedReduction)
            FilaView_setVsmShadowOptions(nativeHandle, o)
        }

    /**
     * Soft shadow options; only applies when [shadowType] is DPCF or PCSS.
     * The getter returns a snapshot — mutate it and assign back to apply.
     */
    var softShadowOptions: SoftShadowOptions
        get() = withHandle({ FilaSoftShadowOptions_create() }, { FilaSoftShadowOptions_destroy(it) }) { o ->
            FilaView_getSoftShadowOptions(nativeHandle, o)
            SoftShadowOptions().apply {
                penumbraScale = FilaSoftShadowOptions_getPenumbraScale(o)
                penumbraRatioScale = FilaSoftShadowOptions_getPenumbraRatioScale(o)
                maxPenumbraRatio = FilaSoftShadowOptions_getMaxPenumbraRatio(o)
                maxSearchRadius = FilaSoftShadowOptions_getMaxSearchRadius(o)
            }
        }
        set(value) = withHandle({ FilaSoftShadowOptions_create() }, { FilaSoftShadowOptions_destroy(it) }) { o ->
            FilaSoftShadowOptions_setPenumbraScale(o, value.penumbraScale)
            FilaSoftShadowOptions_setPenumbraRatioScale(o, value.penumbraRatioScale)
            FilaSoftShadowOptions_setMaxPenumbraRatio(o, value.maxPenumbraRatio)
            FilaSoftShadowOptions_setMaxSearchRadius(o, value.maxSearchRadius)
            FilaView_setSoftShadowOptions(nativeHandle, o)
        }

    /**
     * Guard-band configuration, letting some effects sample outside the viewport.
     * The getter returns a snapshot — mutate it and assign back to apply.
     */
    var guardBandOptions: GuardBandOptions
        get() = withHandle({ FilaGuardBandOptions_create() }, { FilaGuardBandOptions_destroy(it) }) { o ->
            FilaView_getGuardBandOptions(nativeHandle, o)
            GuardBandOptions().apply {
                enabled = FilaGuardBandOptions_getEnabled(o)
            }
        }
        set(value) = withHandle({ FilaGuardBandOptions_create() }, { FilaGuardBandOptions_destroy(it) }) { o ->
            FilaGuardBandOptions_setEnabled(o, value.enabled)
            FilaView_setGuardBandOptions(nativeHandle, o)
        }

    /**
     * Stereoscopic (VR) rendering configuration; must be set before the first frame.
     * The getter returns a snapshot — mutate it and assign back to apply.
     */
    var stereoscopicOptions: StereoscopicOptions
        get() = withHandle({ FilaStereoscopicOptions_create() }, { FilaStereoscopicOptions_destroy(it) }) { o ->
            FilaView_getStereoscopicOptions(nativeHandle, o)
            StereoscopicOptions().apply {
                enabled = FilaStereoscopicOptions_getEnabled(o)
            }
        }
        set(value) = withHandle({ FilaStereoscopicOptions_create() }, { FilaStereoscopicOptions_destroy(it) }) { o ->
            FilaStereoscopicOptions_setEnabled(o, value.enabled)
            FilaView_setStereoscopicOptions(nativeHandle, o)
        }

    /**
     * Hardware MSAA configuration (independent of [antiAliasing]/TAA).
     * The getter returns a snapshot — mutate it and assign back to apply.
     */
    var multiSampleAntiAliasingOptions: MultiSampleAntiAliasingOptions
        get() = withHandle({ FilaMultiSampleAntiAliasingOptions_create() }, { FilaMultiSampleAntiAliasingOptions_destroy(it) }) { o ->
            FilaView_getMultiSampleAntiAliasingOptions(nativeHandle, o)
            MultiSampleAntiAliasingOptions().apply {
                enabled = FilaMultiSampleAntiAliasingOptions_getEnabled(o)
                sampleCount = FilaMultiSampleAntiAliasingOptions_getSampleCount(o)
                customResolve = FilaMultiSampleAntiAliasingOptions_getCustomResolve(o)
            }
        }
        set(value) = withHandle({ FilaMultiSampleAntiAliasingOptions_create() }, { FilaMultiSampleAntiAliasingOptions_destroy(it) }) { o ->
            FilaMultiSampleAntiAliasingOptions_setEnabled(o, value.enabled)
            FilaMultiSampleAntiAliasingOptions_setSampleCount(o, value.sampleCount)
            FilaMultiSampleAntiAliasingOptions_setCustomResolve(o, value.customResolve)
            FilaView_setMultiSampleAntiAliasingOptions(nativeHandle, o)
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
        value.usePinned { FilaView_setMaterialGlobal(nativeHandle, index, it) }
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
    /**
     * Enables or disables clearing the depth buffer before each light [channel] (0–7) renders.
     * Default: disabled.
     */
    fun setChannelDepthClearEnabled(channel: Int, enabled: Boolean) { FilaView_setChannelDepthClearEnabled(nativeHandle, channel, enabled) }
    /** Whether the depth buffer is cleared before light [channel] renders. */
    fun isChannelDepthClearEnabled(channel: Int): Boolean = FilaView_isChannelDepthClearEnabled(nativeHandle, channel)
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
        // The result only lives for the callback's duration: copy it out right away.
        val userData = Callbacks.register(once = true) { result ->
            val fragCoords = FloatArray(3).also { out -> out.usePinned { FilaViewPickingQueryResult_getFragCoords(result, it) } }
            callback(PickingQueryResult(FilaViewPickingQueryResult_getRenderable(result), FilaViewPickingQueryResult_getDepth(result), fragCoords))
        }
        FilaView_pick(nativeHandle, x, y, NullPointer, Callbacks.argUser, userData)
    }
}
