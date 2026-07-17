package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external class View : JsAny {
fun pick(x: Double, y: Double, cb: PickCallback): Unit
fun setCamera(camera: Camera): Unit
fun setColorGrading(colorGrading: ColorGrading): Unit
fun getColorGrading(): ColorGrading?
fun setDithering(dithering: View_Dithering): Unit
fun setDynamicResolutionOptions(options: View_DynamicResolutionOptions): Unit
fun setRenderQuality(quality: View_RenderQuality): Unit
fun setScene(scene: Scene): Unit
fun setViewport(viewport: float4): Unit
fun setVisibleLayers(select: Double, values: Double): Unit
fun setGridSize(size: Double): Unit
fun getGridSize(): Double
fun getEffectiveGridSize(): Double
fun setRenderTarget(renderTarget: RenderTarget): Unit
fun setAmbientOcclusionOptions(options: View_AmbientOcclusionOptions): Unit
fun setDepthOfFieldOptions(options: View_DepthOfFieldOptions): Unit
fun setMultiSampleAntiAliasingOptions(options: View_MultiSampleAntiAliasingOptions): Unit
fun setTemporalAntiAliasingOptions(options: View_TemporalAntiAliasingOptions): Unit
fun setScreenSpaceReflectionsOptions(options: View_ScreenSpaceReflectionsOptions): Unit
fun setBloomOptions(options: View_BloomOptions): Unit
fun setFogOptions(options: View_FogOptions): Unit
fun setVignetteOptions(options: View_VignetteOptions): Unit
fun setGuardBandOptions(options: View_GuardBandOptions): Unit
fun setStereoscopicOptions(options: View_StereoscopicOptions): Unit
fun setAmbientOcclusion(ambientOcclusion: View_AmbientOcclusion): Unit
fun getAmbientOcclusion(): View_AmbientOcclusion
fun setBlendMode(mode: View_BlendMode): Unit
fun getBlendMode(): View_BlendMode
fun setPostProcessingEnabled(enabled: Boolean): Unit
fun setAntiAliasing(antialiasing: View_AntiAliasing): Unit
fun setStencilBufferEnabled(enabled: Boolean): Unit
fun isStencilBufferEnabled(): Boolean
fun setTransparentPickingEnabled(enabled: Boolean): Unit
fun isTransparentPickingEnabled(): Boolean
fun setShadowingEnabled(enabled: Boolean): Unit
fun isShadowingEnabled(): Boolean
fun setShadowType(type: View_ShadowType): Unit
fun getShadowType(): View_ShadowType
fun setVsmShadowOptions(options: View_VsmShadowOptions): Unit
fun getVsmShadowOptions(): View_VsmShadowOptions
fun setSoftShadowOptions(options: View_SoftShadowOptions): Unit
fun getSoftShadowOptions(): View_SoftShadowOptions
fun setFrustumCullingEnabled(enabled: Boolean): Unit
fun isFrustumCullingEnabled(): Boolean
fun setFrontFaceWindingInverted(inverted: Boolean): Unit
fun isFrontFaceWindingInverted(): Boolean
fun setMaterialGlobal(index: Double, value: float4): Unit
fun getMaterialGlobal(index: Double): js.array.ReadonlyArray<JsNumber>
fun getFogEntity(): Entity
fun getVisibleRenderableCount(): Double
fun clearFrameHistory(engine: Engine): Unit
fun setDynamicLightingOptions(zLightNear: Double, zLightFar: Double): Unit
}

// ── View ──────────────────────────────────────────────────────────────────────
