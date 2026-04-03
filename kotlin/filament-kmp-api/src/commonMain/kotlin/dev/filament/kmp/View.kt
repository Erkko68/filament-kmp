package dev.filament.kmp

/**
 * A View defines a camera + scene pairing and rendering options.
 */
expect class View {
    enum class ToneMapping {
        LINEAR,
        ACES,
    }

    enum class AmbientOcclusion {
        NONE,
        SSAO,
    }

    enum class AntiAliasing {
        NONE,
        FXAA,
    }

    enum class Dithering {
        NONE,
        TEMPORAL,
    }

    enum class BlendMode {
        OPAQUE,
        TRANSLUCENT,
    }

    enum class ShadowType {
        PCF,
        VSM,
        DPCF,
        PCSS,
        PCFd,
    }

    class DynamicResolutionOptions()
    class RenderQuality()
    class AmbientOcclusionOptions()
    class BloomOptions()
    class FogOptions()
    class DepthOfFieldOptions()
    class VignetteOptions()
    class TemporalAntiAliasingOptions()
    class ScreenSpaceReflectionsOptions()
    class MultiSampleAntiAliasingOptions()
    class VsmShadowOptions()
    class SoftShadowOptions()
    class GuardBandOptions()
    class StereoscopicOptions()
    class PickingQueryResult constructor(renderable: Int, depth: Float, fragCoords: FloatArray)

    interface OnPickCallback {
        fun onPick(result: PickingQueryResult)
    }

    /**
     * Returns whether this View wrapper currently points to a valid native instance.
     */
    fun isValid(): Boolean

    /**
     * Sets the scene associated with this View.
     */
    fun setScene(scene: Scene?)

    /**
     * Gets the scene associated with this View.
     */
    fun getScene(): Scene?

    fun setName(name: String)

    fun getName(): String?

    fun setCamera(camera: Camera?)

    fun hasCamera(): Boolean

    fun getCamera(): Camera?

    fun setViewport(viewport: Viewport)

    fun getViewport(): Viewport

    fun setBlendMode(blendMode: BlendMode)

    fun getBlendMode(): BlendMode

    fun setVisibleLayers(select: Int, values: Int)

    fun getVisibleLayers(): Int

    fun setLayerEnabled(layer: Int, enabled: Boolean)

    fun setShadowingEnabled(enabled: Boolean)

    fun setFrustumCullingEnabled(enabled: Boolean)

    fun isFrustumCullingEnabled(): Boolean

    fun setScreenSpaceRefractionEnabled(enabled: Boolean)

    fun setRenderTarget(target: RenderTarget?)

    fun getRenderTarget(): RenderTarget?

    fun setSampleCount(count: Int)

    fun getSampleCount(): Int

    fun setAntiAliasing(type: AntiAliasing)

    fun getAntiAliasing(): AntiAliasing

    fun setMultiSampleAntiAliasingOptions(options: MultiSampleAntiAliasingOptions)

    fun getMultiSampleAntiAliasingOptions(): MultiSampleAntiAliasingOptions

    fun setTemporalAntiAliasingOptions(options: TemporalAntiAliasingOptions)

    fun getTemporalAntiAliasingOptions(): TemporalAntiAliasingOptions

    fun setScreenSpaceReflectionsOptions(options: ScreenSpaceReflectionsOptions)

    fun getScreenSpaceReflectionsOptions(): ScreenSpaceReflectionsOptions

    fun setGuardBandOptions(options: GuardBandOptions)

    fun getGuardBandOptions(): GuardBandOptions

    fun setToneMapping(type: ToneMapping)

    fun getToneMapping(): ToneMapping

    fun setColorGrading(colorGrading: ColorGrading?)

    fun getColorGrading(): ColorGrading?

    fun setDithering(dithering: Dithering)

    fun getDithering(): Dithering

    fun setDynamicResolutionOptions(options: DynamicResolutionOptions)

    fun getDynamicResolutionOptions(): DynamicResolutionOptions

    fun getLastDynamicResolutionScale(out: FloatArray? = null): FloatArray

    fun setRenderQuality(renderQuality: RenderQuality)

    fun getRenderQuality(): RenderQuality

    fun isPostProcessingEnabled(): Boolean

    fun setPostProcessingEnabled(enabled: Boolean)

    fun isFrontFaceWindingInverted(): Boolean

    fun setFrontFaceWindingInverted(inverted: Boolean)

    fun isTransparentPickingEnabled(): Boolean

    fun setTransparentPickingEnabled(enabled: Boolean)

    fun setDynamicLightingOptions(zLightNear: Float, zLightFar: Float)

    fun setShadowType(type: ShadowType)

    fun setVsmShadowOptions(options: VsmShadowOptions)

    fun getVsmShadowOptions(): VsmShadowOptions

    fun setSoftShadowOptions(options: SoftShadowOptions)

    fun getSoftShadowOptions(): SoftShadowOptions

    fun setAmbientOcclusion(ao: AmbientOcclusion)

    fun getAmbientOcclusion(): AmbientOcclusion

    fun setAmbientOcclusionOptions(options: AmbientOcclusionOptions)

    fun getAmbientOcclusionOptions(): AmbientOcclusionOptions

    fun setBloomOptions(options: BloomOptions)

    fun getBloomOptions(): BloomOptions

    fun setVignetteOptions(options: VignetteOptions)

    fun getVignetteOptions(): VignetteOptions

    fun setFogOptions(options: FogOptions)

    fun getFogOptions(): FogOptions

    fun setDepthOfFieldOptions(options: DepthOfFieldOptions)

    fun getDepthOfFieldOptions(): DepthOfFieldOptions

    fun setStencilBufferEnabled(enabled: Boolean)

    fun isStencilBufferEnabled(): Boolean

    fun setStereoscopicOptions(options: StereoscopicOptions)

    fun getStereoscopicOptions(): StereoscopicOptions

    fun pick(x: Int, y: Int, handler: Any?, callback: OnPickCallback?)

    fun setMaterialGlobal(index: Int, value: FloatArray)

    fun getMaterialGlobal(index: Int, out: FloatArray? = null): FloatArray

    @Entity
    fun getFogEntity(): Int

    fun clearFrameHistory(engine: Engine)

    fun getNativeObject(): Long

    internal fun invalidate()
}

