package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external interface viewer_Settings : JsAny {
var view: ViewSettings
var lighting: LightSettings
var viewer: ViewerOptions
var camera: CameraSettings
var animation: AnimationSettings
var render: RenderSettings
var debug: DebugOptions
}
