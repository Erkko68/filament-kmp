package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external class JsonSerializer : JsAny {
constructor ()
fun writeJson(settings: viewer_Settings): String
fun readJson(json: String, settings: viewer_Settings): Boolean
}
