package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external interface LightSettings : JsAny {
var enableShadows: Boolean
var enableSunlight: Boolean
var softShadowOptions: View_SoftShadowOptions
var iblIntensity: Double
var iblRotation: Double
var sunlight: LightDefinition
var lights: js.array.ReadonlyArray<LightDefinition>
}
