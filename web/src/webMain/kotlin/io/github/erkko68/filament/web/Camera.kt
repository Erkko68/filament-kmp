// Automatically generated - do not modify!

package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external class Camera : JsAny {
fun setProjection(proj: Camera_Projection, left: Double, right: Double, bottom: Double, top: Double, near: Double, far: Double): Unit
fun setProjectionFov(fovInDegrees: Double, aspect: Double, near: Double, far: Double, fov: Camera_Fov): Unit
fun setLensProjection(focalLength: Double, aspect: Double, near: Double, far: Double): Unit
fun setCustomProjection(projection: mat4, near: Double, far: Double): Unit
fun setScaling(scale: double2): Unit
fun getProjectionMatrix(): mat4
fun getCullingProjectionMatrix(): mat4
fun getScaling(): double4
fun getNear(): Double
fun getCullingFar(): Double
fun setModelMatrix(view: mat4): Unit
fun lookAt(eye: float3, center: float3, up: float3): Unit
fun setLookAt(manip: Camutils_Manipulator): Unit
fun getModelMatrix(): mat4
fun getViewMatrix(): mat4
fun getPosition(): float3
fun getLeftVector(): float3
fun getUpVector(): float3
fun getForwardVector(): float3
fun getFrustum(): Frustum
fun setExposure(aperture: Double, shutterSpeed: Double, sensitivity: Double): Unit
fun setExposureDirect(exposure: Double): Unit
fun getAperture(): Double
fun getShutterSpeed(): Double
fun getSensitivity(): Double
fun getFocalLength(): Double
fun getFocusDistance(): Double
fun setFocusDistance(distance: Double): Unit
fun setShift(shift: float2): Unit
fun getShift(): float2
companion object {
fun inverseProjection(p: mat4): mat4
fun computeEffectiveFocalLength(focalLength: Double, focusDistance: Double): Double
fun computeEffectiveFov(fovInDegrees: Double, focusDistance: Double): Double
}
}

// ── Camera ────────────────────────────────────────────────────────────────────
