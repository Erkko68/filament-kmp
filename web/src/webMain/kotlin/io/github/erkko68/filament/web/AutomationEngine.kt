package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external class AutomationEngine : JsAny {
constructor (spec: AutomationSpec, settings: viewer_Settings)
fun startRunning(): Unit
fun startBatchMode(): Unit
fun tick(engine: Engine, content: ViewerContent, deltaTime: Double): Unit
fun applySettings(engine: Engine, json: String, content: ViewerContent): Unit
fun getColorGrading(engine: Engine): ColorGrading
fun getViewerOptions(): ViewerOptions
fun getSettings(): viewer_Settings
fun signalBatchMode(): Unit
fun stopRunning(): Unit
fun terminate(): Unit
fun shouldClose(): Boolean
fun getOptions(): AutomationEngine_Options
fun setOptions(options: AutomationEngine_Options): Unit
fun isRunning(): Boolean
fun currentTest(): Double
fun testCount(): Double
fun getStatusMessage(): String
fun delete(): Unit
companion object {
fun createFromJSON(json: String): AutomationEngine?
fun createDefault(): AutomationEngine
}
}
