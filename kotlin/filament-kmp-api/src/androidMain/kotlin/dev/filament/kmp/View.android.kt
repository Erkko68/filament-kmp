package dev.filament.kmp

import com.google.android.filament.View as AndroidFilamentView

actual class View internal constructor(
    internal var androidView: AndroidFilamentView?,
) {
    private var sceneRef: Scene? = null
    private var cameraRef: Camera? = null
    private var viewportRef: Viewport = Viewport(0, 0, 0, 0)

    private fun view(): AndroidFilamentView = requireNotNull(androidView) { "View is invalid." }

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

    actual fun isValid(): Boolean = androidView != null

    actual fun setScene(scene: Scene?) {
        view().scene = scene?.androidScene
        sceneRef = scene
    }

    actual fun getScene(): Scene? = sceneRef

    actual fun setName(name: String) {
        view().setName(name)
    }

    actual fun getName(): String? = view().name

    actual fun setCamera(camera: Camera?) {
        view().camera = camera?.androidCamera
        cameraRef = camera
    }

    actual fun hasCamera(): Boolean = view().hasCamera()

    actual fun getCamera(): Camera? = cameraRef

    actual fun setViewport(viewport: Viewport) {
        view().viewport = viewport.androidViewport
        viewportRef = viewport
    }

    actual fun getViewport(): Viewport = viewportRef

    actual fun setBlendMode(blendMode: BlendMode) {
        TODO("Not yet implemented")
    }

    actual fun getBlendMode(): BlendMode = TODO("Not yet implemented")

    actual fun setVisibleLayers(select: Int, values: Int) {
        view().setVisibleLayers(select, values)
    }

    actual fun getVisibleLayers(): Int = view().visibleLayers

    actual fun setLayerEnabled(layer: Int, enabled: Boolean) {
        view().setLayerEnabled(layer, enabled)
    }

    actual fun setShadowingEnabled(enabled: Boolean) {
        view().setShadowingEnabled(enabled)
    }

    actual fun setFrustumCullingEnabled(enabled: Boolean) {
        view().setFrustumCullingEnabled(enabled)
    }

    actual fun isFrustumCullingEnabled(): Boolean = view().isFrustumCullingEnabled

    actual fun setScreenSpaceRefractionEnabled(enabled: Boolean) {
        view().setScreenSpaceRefractionEnabled(enabled)
    }

    actual fun setRenderTarget(target: RenderTarget?) {
        view().renderTarget = target?.androidRenderTarget
    }

    actual fun getRenderTarget(): RenderTarget? = TODO("Not yet implemented")

    actual fun setSampleCount(count: Int) {
        view().setSampleCount(count)
    }

    actual fun getSampleCount(): Int = view().sampleCount

    actual fun setAntiAliasing(type: AntiAliasing) {
        TODO("Not yet implemented")
    }

    actual fun getAntiAliasing(): AntiAliasing = TODO("Not yet implemented")

    actual fun setMultiSampleAntiAliasingOptions(options: MultiSampleAntiAliasingOptions) {
        TODO("Not yet implemented")
    }

    actual fun getMultiSampleAntiAliasingOptions(): MultiSampleAntiAliasingOptions = TODO("Not yet implemented")

    actual fun setTemporalAntiAliasingOptions(options: TemporalAntiAliasingOptions) {
        TODO("Not yet implemented")
    }

    actual fun getTemporalAntiAliasingOptions(): TemporalAntiAliasingOptions = TODO("Not yet implemented")

    actual fun setScreenSpaceReflectionsOptions(options: ScreenSpaceReflectionsOptions) {
        TODO("Not yet implemented")
    }

    actual fun getScreenSpaceReflectionsOptions(): ScreenSpaceReflectionsOptions = TODO("Not yet implemented")

    actual fun setGuardBandOptions(options: GuardBandOptions) {
        TODO("Not yet implemented")
    }

    actual fun getGuardBandOptions(): GuardBandOptions = TODO("Not yet implemented")

    actual fun setToneMapping(type: ToneMapping) {
        TODO("Not yet implemented")
    }

    actual fun getToneMapping(): ToneMapping = TODO("Not yet implemented")

    actual fun setColorGrading(colorGrading: ColorGrading?) {
        view().setColorGrading(colorGrading?.androidColorGrading)
    }

    actual fun getColorGrading(): ColorGrading? = TODO("Not yet implemented")

    actual fun setDithering(dithering: Dithering) {
        TODO("Not yet implemented")
    }

    actual fun getDithering(): Dithering = TODO("Not yet implemented")

    actual fun setDynamicResolutionOptions(options: DynamicResolutionOptions) {
        TODO("Not yet implemented")
    }

    actual fun getDynamicResolutionOptions(): DynamicResolutionOptions = TODO("Not yet implemented")

    actual fun getLastDynamicResolutionScale(out: FloatArray?): FloatArray = TODO("Not yet implemented")

    actual fun setRenderQuality(renderQuality: RenderQuality) {
        TODO("Not yet implemented")
    }

    actual fun getRenderQuality(): RenderQuality = TODO("Not yet implemented")

    actual fun isPostProcessingEnabled(): Boolean = view().isPostProcessingEnabled

    actual fun setPostProcessingEnabled(enabled: Boolean) {
        view().setPostProcessingEnabled(enabled)
    }

    actual fun isFrontFaceWindingInverted(): Boolean = view().isFrontFaceWindingInverted

    actual fun setFrontFaceWindingInverted(inverted: Boolean) {
        view().setFrontFaceWindingInverted(inverted)
    }

    actual fun isTransparentPickingEnabled(): Boolean = view().isTransparentPickingEnabled

    actual fun setTransparentPickingEnabled(enabled: Boolean) {
        view().setTransparentPickingEnabled(enabled)
    }

    actual fun setDynamicLightingOptions(zLightNear: Float, zLightFar: Float) {
        view().setDynamicLightingOptions(zLightNear, zLightFar)
    }

    actual fun setShadowType(type: ShadowType) {
        TODO("Not yet implemented")
    }

    actual fun setVsmShadowOptions(options: VsmShadowOptions) {
        TODO("Not yet implemented")
    }

    actual fun getVsmShadowOptions(): VsmShadowOptions = TODO("Not yet implemented")

    actual fun setSoftShadowOptions(options: SoftShadowOptions) {
        TODO("Not yet implemented")
    }

    actual fun getSoftShadowOptions(): SoftShadowOptions = TODO("Not yet implemented")

    actual fun setAmbientOcclusion(ao: AmbientOcclusion) {
        TODO("Not yet implemented")
    }

    actual fun getAmbientOcclusion(): AmbientOcclusion = TODO("Not yet implemented")

    actual fun setAmbientOcclusionOptions(options: AmbientOcclusionOptions) {
        TODO("Not yet implemented")
    }

    actual fun getAmbientOcclusionOptions(): AmbientOcclusionOptions = TODO("Not yet implemented")

    actual fun setBloomOptions(options: BloomOptions) {
        TODO("Not yet implemented")
    }

    actual fun getBloomOptions(): BloomOptions = TODO("Not yet implemented")

    actual fun setVignetteOptions(options: VignetteOptions) {
        TODO("Not yet implemented")
    }

    actual fun getVignetteOptions(): VignetteOptions = TODO("Not yet implemented")

    actual fun setFogOptions(options: FogOptions) {
        TODO("Not yet implemented")
    }

    actual fun getFogOptions(): FogOptions = TODO("Not yet implemented")

    actual fun setDepthOfFieldOptions(options: DepthOfFieldOptions) {
        TODO("Not yet implemented")
    }

    actual fun getDepthOfFieldOptions(): DepthOfFieldOptions = TODO("Not yet implemented")

    actual fun setStencilBufferEnabled(enabled: Boolean) {
        view().setStencilBufferEnabled(enabled)
    }

    actual fun isStencilBufferEnabled(): Boolean = view().isStencilBufferEnabled

    actual fun setStereoscopicOptions(options: StereoscopicOptions) {
        TODO("Not yet implemented")
    }

    actual fun getStereoscopicOptions(): StereoscopicOptions = TODO("Not yet implemented")

    actual fun pick(x: Int, y: Int, handler: Any?, callback: OnPickCallback?) {
        TODO("Not yet implemented")
    }

    actual fun setMaterialGlobal(index: Int, value: FloatArray) {
        view().setMaterialGlobal(index, value)
    }

    actual fun getMaterialGlobal(index: Int, out: FloatArray?): FloatArray = view().getMaterialGlobal(index, out)

    @Entity
    actual fun getFogEntity(): Int = view().fogEntity

    actual fun clearFrameHistory(engine: Engine) {
        val androidEngine = requireNotNull(engine.androidEngine) { "Engine is closed." }
        view().clearFrameHistory(androidEngine)
    }

    actual fun getNativeObject(): Long = view().nativeObject

    actual internal fun invalidate() {
        androidView = null
        sceneRef = null
        cameraRef = null
    }
}

