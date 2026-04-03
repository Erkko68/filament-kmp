package dev.filament.kmp

actual class View {
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

    actual fun isValid(): Boolean = TODO("Not yet implemented")

    actual fun setScene(scene: Scene?) { TODO("Not yet implemented") }

    actual fun getScene(): Scene? = TODO("Not yet implemented")

    actual fun setName(name: String) { TODO("Not yet implemented") }
    actual fun getName(): String? = TODO("Not yet implemented")
    actual fun setCamera(camera: Camera?) { TODO("Not yet implemented") }
    actual fun hasCamera(): Boolean = TODO("Not yet implemented")
    actual fun getCamera(): Camera? = TODO("Not yet implemented")
    actual fun setViewport(viewport: Viewport) { TODO("Not yet implemented") }
    actual fun getViewport(): Viewport = TODO("Not yet implemented")
    actual fun setBlendMode(blendMode: BlendMode) { TODO("Not yet implemented") }
    actual fun getBlendMode(): BlendMode = TODO("Not yet implemented")
    actual fun setVisibleLayers(select: Int, values: Int) { TODO("Not yet implemented") }
    actual fun getVisibleLayers(): Int = TODO("Not yet implemented")
    actual fun setLayerEnabled(layer: Int, enabled: Boolean) { TODO("Not yet implemented") }
    actual fun setShadowingEnabled(enabled: Boolean) { TODO("Not yet implemented") }
    actual fun setFrustumCullingEnabled(enabled: Boolean) { TODO("Not yet implemented") }
    actual fun isFrustumCullingEnabled(): Boolean = TODO("Not yet implemented")
    actual fun setScreenSpaceRefractionEnabled(enabled: Boolean) { TODO("Not yet implemented") }
    actual fun setRenderTarget(target: RenderTarget?) { TODO("Not yet implemented") }
    actual fun getRenderTarget(): RenderTarget? = TODO("Not yet implemented")
    actual fun setSampleCount(count: Int) { TODO("Not yet implemented") }
    actual fun getSampleCount(): Int = TODO("Not yet implemented")
    actual fun setAntiAliasing(type: AntiAliasing) { TODO("Not yet implemented") }
    actual fun getAntiAliasing(): AntiAliasing = TODO("Not yet implemented")
    actual fun setMultiSampleAntiAliasingOptions(options: MultiSampleAntiAliasingOptions) { TODO("Not yet implemented") }
    actual fun getMultiSampleAntiAliasingOptions(): MultiSampleAntiAliasingOptions = TODO("Not yet implemented")
    actual fun setTemporalAntiAliasingOptions(options: TemporalAntiAliasingOptions) { TODO("Not yet implemented") }
    actual fun getTemporalAntiAliasingOptions(): TemporalAntiAliasingOptions = TODO("Not yet implemented")
    actual fun setScreenSpaceReflectionsOptions(options: ScreenSpaceReflectionsOptions) { TODO("Not yet implemented") }
    actual fun getScreenSpaceReflectionsOptions(): ScreenSpaceReflectionsOptions = TODO("Not yet implemented")
    actual fun setGuardBandOptions(options: GuardBandOptions) { TODO("Not yet implemented") }
    actual fun getGuardBandOptions(): GuardBandOptions = TODO("Not yet implemented")
    actual fun setToneMapping(type: ToneMapping) { TODO("Not yet implemented") }
    actual fun getToneMapping(): ToneMapping = TODO("Not yet implemented")
    actual fun setColorGrading(colorGrading: ColorGrading?) { TODO("Not yet implemented") }
    actual fun getColorGrading(): ColorGrading? = TODO("Not yet implemented")
    actual fun setDithering(dithering: Dithering) { TODO("Not yet implemented") }
    actual fun getDithering(): Dithering = TODO("Not yet implemented")
    actual fun setDynamicResolutionOptions(options: DynamicResolutionOptions) { TODO("Not yet implemented") }
    actual fun getDynamicResolutionOptions(): DynamicResolutionOptions = TODO("Not yet implemented")
    actual fun getLastDynamicResolutionScale(out: FloatArray?): FloatArray = TODO("Not yet implemented")
    actual fun setRenderQuality(renderQuality: RenderQuality) { TODO("Not yet implemented") }
    actual fun getRenderQuality(): RenderQuality = TODO("Not yet implemented")
    actual fun isPostProcessingEnabled(): Boolean = TODO("Not yet implemented")
    actual fun setPostProcessingEnabled(enabled: Boolean) { TODO("Not yet implemented") }
    actual fun isFrontFaceWindingInverted(): Boolean = TODO("Not yet implemented")
    actual fun setFrontFaceWindingInverted(inverted: Boolean) { TODO("Not yet implemented") }
    actual fun isTransparentPickingEnabled(): Boolean = TODO("Not yet implemented")
    actual fun setTransparentPickingEnabled(enabled: Boolean) { TODO("Not yet implemented") }
    actual fun setDynamicLightingOptions(zLightNear: Float, zLightFar: Float) { TODO("Not yet implemented") }
    actual fun setShadowType(type: ShadowType) { TODO("Not yet implemented") }
    actual fun setVsmShadowOptions(options: VsmShadowOptions) { TODO("Not yet implemented") }
    actual fun getVsmShadowOptions(): VsmShadowOptions = TODO("Not yet implemented")
    actual fun setSoftShadowOptions(options: SoftShadowOptions) { TODO("Not yet implemented") }
    actual fun getSoftShadowOptions(): SoftShadowOptions = TODO("Not yet implemented")
    actual fun setAmbientOcclusion(ao: AmbientOcclusion) { TODO("Not yet implemented") }
    actual fun getAmbientOcclusion(): AmbientOcclusion = TODO("Not yet implemented")
    actual fun setAmbientOcclusionOptions(options: AmbientOcclusionOptions) { TODO("Not yet implemented") }
    actual fun getAmbientOcclusionOptions(): AmbientOcclusionOptions = TODO("Not yet implemented")
    actual fun setBloomOptions(options: BloomOptions) { TODO("Not yet implemented") }
    actual fun getBloomOptions(): BloomOptions = TODO("Not yet implemented")
    actual fun setVignetteOptions(options: VignetteOptions) { TODO("Not yet implemented") }
    actual fun getVignetteOptions(): VignetteOptions = TODO("Not yet implemented")
    actual fun setFogOptions(options: FogOptions) { TODO("Not yet implemented") }
    actual fun getFogOptions(): FogOptions = TODO("Not yet implemented")
    actual fun setDepthOfFieldOptions(options: DepthOfFieldOptions) { TODO("Not yet implemented") }
    actual fun getDepthOfFieldOptions(): DepthOfFieldOptions = TODO("Not yet implemented")
    actual fun setStencilBufferEnabled(enabled: Boolean) { TODO("Not yet implemented") }
    actual fun isStencilBufferEnabled(): Boolean = TODO("Not yet implemented")
    actual fun setStereoscopicOptions(options: StereoscopicOptions) { TODO("Not yet implemented") }
    actual fun getStereoscopicOptions(): StereoscopicOptions = TODO("Not yet implemented")
    actual fun pick(x: Int, y: Int, handler: Any?, callback: OnPickCallback?) { TODO("Not yet implemented") }
    actual fun setMaterialGlobal(index: Int, value: FloatArray) { TODO("Not yet implemented") }
    actual fun getMaterialGlobal(index: Int, out: FloatArray?): FloatArray = TODO("Not yet implemented")
    @Entity
    actual fun getFogEntity(): Int = TODO("Not yet implemented")
    actual fun clearFrameHistory(engine: Engine) { TODO("Not yet implemented") }
    actual fun getNativeObject(): Long = TODO("Not yet implemented")

    actual internal fun invalidate() {
    }
}

