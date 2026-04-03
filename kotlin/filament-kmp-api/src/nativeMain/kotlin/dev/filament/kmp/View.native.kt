package dev.filament.kmp

actual class View internal constructor() {
    actual enum class ToneMapping {
        LINEAR,
        ACES,
    }

    actual enum class AmbientOcclusion {
        NONE,
        SSAO,
    }

    actual enum class AntiAliasing {
        NONE,
        FXAA,
    }

    actual enum class Dithering {
        NONE,
        TEMPORAL,
    }

    actual enum class BlendMode {
        OPAQUE,
        TRANSLUCENT,
    }

    actual enum class ShadowType {
        PCF,
        VSM,
        DPCF,
        PCSS,
        PCFd,
    }

    actual class DynamicResolutionOptions actual constructor()
    actual class RenderQuality actual constructor()
    actual class AmbientOcclusionOptions actual constructor()
    actual class BloomOptions actual constructor()
    actual class FogOptions actual constructor()
    actual class DepthOfFieldOptions actual constructor()
    actual class VignetteOptions actual constructor()
    actual class TemporalAntiAliasingOptions actual constructor()
    actual class ScreenSpaceReflectionsOptions actual constructor()
    actual class MultiSampleAntiAliasingOptions actual constructor()
    actual class VsmShadowOptions actual constructor()
    actual class SoftShadowOptions actual constructor()
    actual class GuardBandOptions actual constructor()
    actual class StereoscopicOptions actual constructor()
    actual class PickingQueryResult actual constructor(renderable: Int, depth: Float, fragCoords: FloatArray)

    actual interface OnPickCallback {
        actual fun onPick(result: PickingQueryResult)
    }

    actual fun isValid(): Boolean = false

    actual fun setScene(scene: Scene?) = Unit

    actual fun getScene(): Scene? = null

    actual fun setName(name: String) = Unit
    actual fun getName(): String? = null
    actual fun setCamera(camera: Camera?) = Unit
    actual fun hasCamera(): Boolean = false
    actual fun getCamera(): Camera? = null
    actual fun setViewport(viewport: Viewport) = Unit
    actual fun getViewport(): Viewport = TODO("Not yet implemented")
    actual fun setBlendMode(blendMode: BlendMode) = Unit
    actual fun getBlendMode(): BlendMode = BlendMode.OPAQUE
    actual fun setVisibleLayers(select: Int, values: Int) = Unit
    actual fun getVisibleLayers(): Int = 0
    actual fun setLayerEnabled(layer: Int, enabled: Boolean) = Unit
    actual fun setShadowingEnabled(enabled: Boolean) = Unit
    actual fun setFrustumCullingEnabled(enabled: Boolean) = Unit
    actual fun isFrustumCullingEnabled(): Boolean = false
    actual fun setScreenSpaceRefractionEnabled(enabled: Boolean) = Unit
    actual fun setRenderTarget(target: RenderTarget?) = Unit
    actual fun getRenderTarget(): RenderTarget? = null
    actual fun setSampleCount(count: Int) = Unit
    actual fun getSampleCount(): Int = 1
    actual fun setAntiAliasing(type: AntiAliasing) = Unit
    actual fun getAntiAliasing(): AntiAliasing = AntiAliasing.NONE
    actual fun setMultiSampleAntiAliasingOptions(options: MultiSampleAntiAliasingOptions) = Unit
    actual fun getMultiSampleAntiAliasingOptions(): MultiSampleAntiAliasingOptions = MultiSampleAntiAliasingOptions()
    actual fun setTemporalAntiAliasingOptions(options: TemporalAntiAliasingOptions) = Unit
    actual fun getTemporalAntiAliasingOptions(): TemporalAntiAliasingOptions = TemporalAntiAliasingOptions()
    actual fun setScreenSpaceReflectionsOptions(options: ScreenSpaceReflectionsOptions) = Unit
    actual fun getScreenSpaceReflectionsOptions(): ScreenSpaceReflectionsOptions = ScreenSpaceReflectionsOptions()
    actual fun setGuardBandOptions(options: GuardBandOptions) = Unit
    actual fun getGuardBandOptions(): GuardBandOptions = GuardBandOptions()
    actual fun setToneMapping(type: ToneMapping) = Unit
    actual fun getToneMapping(): ToneMapping = ToneMapping.ACES
    actual fun setColorGrading(colorGrading: ColorGrading?) = Unit
    actual fun getColorGrading(): ColorGrading? = null
    actual fun setDithering(dithering: Dithering) = Unit
    actual fun getDithering(): Dithering = Dithering.NONE
    actual fun setDynamicResolutionOptions(options: DynamicResolutionOptions) = Unit
    actual fun getDynamicResolutionOptions(): DynamicResolutionOptions = DynamicResolutionOptions()
    actual fun getLastDynamicResolutionScale(out: FloatArray?): FloatArray = out ?: floatArrayOf(1.0f, 1.0f)
    actual fun setRenderQuality(renderQuality: RenderQuality) = Unit
    actual fun getRenderQuality(): RenderQuality = RenderQuality()
    actual fun isPostProcessingEnabled(): Boolean = false
    actual fun setPostProcessingEnabled(enabled: Boolean) = Unit
    actual fun isFrontFaceWindingInverted(): Boolean = false
    actual fun setFrontFaceWindingInverted(inverted: Boolean) = Unit
    actual fun isTransparentPickingEnabled(): Boolean = false
    actual fun setTransparentPickingEnabled(enabled: Boolean) = Unit
    actual fun setDynamicLightingOptions(zLightNear: Float, zLightFar: Float) = Unit
    actual fun setShadowType(type: ShadowType) = Unit
    actual fun setVsmShadowOptions(options: VsmShadowOptions) = Unit
    actual fun getVsmShadowOptions(): VsmShadowOptions = VsmShadowOptions()
    actual fun setSoftShadowOptions(options: SoftShadowOptions) = Unit
    actual fun getSoftShadowOptions(): SoftShadowOptions = SoftShadowOptions()
    actual fun setAmbientOcclusion(ao: AmbientOcclusion) = Unit
    actual fun getAmbientOcclusion(): AmbientOcclusion = AmbientOcclusion.NONE
    actual fun setAmbientOcclusionOptions(options: AmbientOcclusionOptions) = Unit
    actual fun getAmbientOcclusionOptions(): AmbientOcclusionOptions = AmbientOcclusionOptions()
    actual fun setBloomOptions(options: BloomOptions) = Unit
    actual fun getBloomOptions(): BloomOptions = BloomOptions()
    actual fun setVignetteOptions(options: VignetteOptions) = Unit
    actual fun getVignetteOptions(): VignetteOptions = VignetteOptions()
    actual fun setFogOptions(options: FogOptions) = Unit
    actual fun getFogOptions(): FogOptions = FogOptions()
    actual fun setDepthOfFieldOptions(options: DepthOfFieldOptions) = Unit
    actual fun getDepthOfFieldOptions(): DepthOfFieldOptions = DepthOfFieldOptions()
    actual fun setStencilBufferEnabled(enabled: Boolean) = Unit
    actual fun isStencilBufferEnabled(): Boolean = false
    actual fun setStereoscopicOptions(options: StereoscopicOptions) = Unit
    actual fun getStereoscopicOptions(): StereoscopicOptions = StereoscopicOptions()
    actual fun pick(x: Int, y: Int, handler: Any?, callback: OnPickCallback?) = Unit
    actual fun setMaterialGlobal(index: Int, value: FloatArray) = Unit
    actual fun getMaterialGlobal(index: Int, out: FloatArray?): FloatArray = out ?: floatArrayOf(0.0f, 0.0f, 0.0f, 1.0f)
    @Entity
    actual fun getFogEntity(): Int = 0
    actual fun clearFrameHistory(engine: Engine) = Unit
    actual fun getNativeObject(): Long = 0L

    actual internal fun invalidate() = Unit
}

