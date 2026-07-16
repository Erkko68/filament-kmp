// Automatically generated - do not modify!

package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external class View : JsAny {
fun pick(x: Double, y: Double, cb: PickCallback): Unit
fun setCamera(camera: Camera): Unit
fun setColorGrading(colorGrading: ColorGrading): Unit
fun setScene(scene: Scene): Unit
fun setViewport(viewport: float4): Unit
fun setVisibleLayers(select: Double, values: Double): Unit
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
