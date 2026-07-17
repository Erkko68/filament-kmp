package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external interface AutomationEngine_Options : JsAny {
var sleepDuration: Double
var minFrameCount: Double
var verbose: Boolean
var exportScreenshots: Boolean
var exportSettings: Boolean
var exportFormat: AutomationEngine_ExportFormat
}
