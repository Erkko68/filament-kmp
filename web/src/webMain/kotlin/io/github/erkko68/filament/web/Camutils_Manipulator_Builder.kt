// Automatically generated - do not modify!

package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

@JsName("Camutils\$Manipulator\$Builder")
external class Camutils_Manipulator_Builder : JsAny {
constructor ()
fun viewport(width: Double, height: Double): Camutils_Manipulator_Builder
fun targetPosition(x: Double, y: Double, z: Double): Camutils_Manipulator_Builder
fun upVector(x: Double, y: Double, z: Double): Camutils_Manipulator_Builder
fun zoomSpeed(`val`: Double): Camutils_Manipulator_Builder
fun orbitHomePosition(x: Double, y: Double, z: Double): Camutils_Manipulator_Builder
fun orbitSpeed(x: Double, y: Double): Camutils_Manipulator_Builder
fun fovDirection(fov: Camutils_Fov): Camutils_Manipulator_Builder
fun fovDegrees(degrees: Double): Camutils_Manipulator_Builder
fun farPlane(distance: Double): Camutils_Manipulator_Builder
fun mapExtent(worldWidth: Double, worldHeight: Double): Camutils_Manipulator_Builder
fun mapMinDistance(mindist: Double): Camutils_Manipulator_Builder
fun flightStartPosition(x: Double, y: Double, z: Double): Camutils_Manipulator_Builder
fun flightStartOrientation(pitch: Double, yaw: Double): Camutils_Manipulator_Builder
fun flightMaxMoveSpeed(maxSpeed: Double): Camutils_Manipulator_Builder
fun flightSpeedSteps(steps: Double): Camutils_Manipulator_Builder
fun flightPanSpeed(x: Double, y: Double): Camutils_Manipulator_Builder
fun flightMoveDamping(damping: Double): Camutils_Manipulator_Builder
fun groundPlane(a: Double, b: Double, c: Double, d: Double): Camutils_Manipulator_Builder
fun panning(enabled: Boolean): Camutils_Manipulator_Builder
fun build(mode: Camutils_Mode): Camutils_Manipulator
}
