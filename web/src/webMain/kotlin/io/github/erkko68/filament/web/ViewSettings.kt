package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external interface ViewSettings : JsAny {
var antiAliasing: View_AntiAliasing
var dithering: View_Dithering
var shadowType: View_ShadowType
var postProcessingEnabled: Boolean
var ssao: View_AmbientOcclusionOptions
var screenSpaceReflections: View_ScreenSpaceReflectionsOptions
var bloom: View_BloomOptions
var dof: View_DepthOfFieldOptions
var dsr: View_DynamicResolutionOptions
var fog: View_FogOptions
var msaa: View_MultiSampleAntiAliasingOptions
var renderQuality: View_RenderQuality
var taa: View_TemporalAntiAliasingOptions
var vignette: View_VignetteOptions
var vsmShadowOptions: View_VsmShadowOptions
var guardBand: View_GuardBandOptions
var stereoscopicOptions: View_StereoscopicOptions
var colorGrading: ColorGradingSettings
var dynamicLighting: DynamicLightingSettings
var fogSettings: FogSettings
var blendMode: View_BlendMode
var stencilBufferEnabled: Boolean
var visibleLayers: Double
}
