package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external interface HeapInterface : JsAny {
fun set(buffer: JsAny?, pointer: Double): JsAny?
fun subarray(buffer: JsAny?, offset: Double): JsAny?
}
