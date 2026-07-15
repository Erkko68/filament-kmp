// Automatically generated - do not modify!

package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

@JsName("ColorGrading\$Builder")
external class ColorGrading_Builder : JsAny {
fun quality(qualityLevel: ColorGrading_QualityLevel): ColorGrading_Builder
fun format(format: ColorGrading_LutFormat): ColorGrading_Builder
fun dimensions(dim: Double): ColorGrading_Builder
fun toneMapping(toneMapping: ColorGrading_ToneMapping): ColorGrading_Builder
fun luminanceScaling(luminanceScaling: Boolean): ColorGrading_Builder
fun gamutMapping(gamutMapping: Boolean): ColorGrading_Builder
fun exposure(exposure: Double): ColorGrading_Builder
fun nightAdaptation(adaptation: Boolean): ColorGrading_Builder
fun whiteBalance(temperature: Double, tint: Double): ColorGrading_Builder
fun channelMixer(outRed: float3, outGreen: float3, outBlue: float3): ColorGrading_Builder
fun shadowsMidtonesHighlights(shadows: float4, midtones: float4, highlights: float4, ranges: float4): ColorGrading_Builder
fun slopeOffsetPower(slope: float3, offset: float3, power: float3): ColorGrading_Builder
fun contrast(contrast: Double): ColorGrading_Builder
fun vibrance(vibrance: Double): ColorGrading_Builder
fun saturation(saturation: Double): ColorGrading_Builder
fun curves(shadowGamma: float3, midPoint: float3, highlightScale: float3): ColorGrading_Builder
fun build(engine: Engine): ColorGrading
fun fastMath(fastMath: Boolean): ColorGrading_Builder
}
