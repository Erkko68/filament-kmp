package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external interface ColorGradingSettings : JsAny {
var enabled: Boolean
var linkedCurves: Boolean
var luminanceScaling: Boolean
var gamutMapping: Boolean
var quality: ColorGrading_QualityLevel
var toneMapping: viewer_ToneMapping
var customLut: viewer_CustomLut
var agxToneMapper: AgxToneMapperSettings
var colorspace: JsAny?// color::ColorSpace is not fully bound

var genericToneMapper: GenericToneMapperSettings
var shadows: float4
var midtones: float4
var highlights: float4
var ranges: float4
var outRed: float3
var outGreen: float3
var outBlue: float3
var slope: float3
var offset: float3
var power: float3
var gamma: float3
var midPoint: float3
var scale: float3
var exposure: Double
var nightAdaptation: Double
var temperature: Double
var tint: Double
var contrast: Double
var vibrance: Double
var saturation: Double
}
