package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external class Frustum : JsAny {
constructor (pv: mat4)
fun setProjection(pv: mat4): Unit
fun getNormalizedPlane(plane: Frustum_Plane): float4
fun intersectsBox(box: Box): Boolean
fun intersectsSphere(sphere: float4): Boolean
}
