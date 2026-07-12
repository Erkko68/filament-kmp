// Automatically generated - do not modify!

package io.github.erkko68.filament.web

// unhandled import: * as glm from "gl-matrix"

external class gltfio_Animator : JsAny {
fun applyAnimation(index: Double): Unit
fun applyAnimation(index: Double, time: Double): Unit
fun applyCrossFade(previousAnimIndex: Double, previousAnimTime: Double, alpha: Double): Unit
fun updateBoneMatrices(): Unit
fun resetBoneMatrices(): Unit
fun getAnimationCount(): Double
fun getAnimationDuration(index: Double): Double
fun getAnimationName(index: Double): String
}
