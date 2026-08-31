package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external class IcoSphere : JsAny {
constructor (nsubdivs: Double)
fun subdivide(): Unit
var vertices: org.khronos.webgl.Float32Array
var tangents: org.khronos.webgl.Int16Array
var triangles: org.khronos.webgl.Uint16Array
}
