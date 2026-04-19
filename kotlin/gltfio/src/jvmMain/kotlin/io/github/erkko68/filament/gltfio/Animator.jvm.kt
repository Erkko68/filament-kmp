package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.gltfio.jni.Animator as JniAnimator

actual class Animator(val jni: JniAnimator) {
    actual fun applyAnimation(index: Int, time: Float) : Unit { jni.applyAnimation(index, time) }
    actual fun applyCrossFade(previousIndex: Int, previousTime: Float, alpha: Float) : Unit { jni.applyCrossFade(previousIndex, previousTime, alpha) }
    actual fun updateBoneMatrices() : Unit { jni.updateBoneMatrices() }
    actual fun resetBoneMatrices() : Unit { jni.resetBoneMatrices() }
    
    actual fun getAnimationCount(): Int = jni.getAnimationCount()
    actual fun getAnimationDuration(index: Int): Float = jni.getAnimationDuration(index)
    actual fun getAnimationName(index: Int): String? = jni.getAnimationName(index)
}
