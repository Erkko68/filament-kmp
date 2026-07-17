package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external class AutomationSpec : JsAny {
fun size(): Double
fun get(index: Double, out: viewer_Settings): Boolean
fun getName(index: Double): String
fun delete(): Unit
companion object {
fun generate(json: String): AutomationSpec?
fun generateDefaultTestCases(): AutomationSpec
}
}
