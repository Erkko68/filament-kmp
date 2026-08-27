package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.web.`gltfio_Animator` as JSAnimator
import io.github.erkko68.filament.InternalFilamentApi

actual class Animator @InternalFilamentApi constructor(internal val jsAnimator: JSAnimator) {
    actual fun applyAnimation(index: Int, time: Float) {
        jsAnimator.applyAnimation(index.toDouble(), time.toDouble())
    }

    actual fun applyCrossFade(previousIndex: Int, previousTime: Float, alpha: Float) {
        jsAnimator.applyCrossFade(previousIndex.toDouble(), previousTime.toDouble(), alpha.toDouble())
    }

    actual fun updateBoneMatrices() {
        jsAnimator.updateBoneMatrices()
    }

    actual fun resetBoneMatrices() {
        jsAnimator.resetBoneMatrices()
    }

    actual fun getAnimationCount(): Int {
        return jsAnimator.getAnimationCount().toInt()
    }

    actual fun getAnimationDuration(index: Int): Float {
        return jsAnimator.getAnimationDuration(index.toDouble()).toFloat()
    }

    actual fun getAnimationName(index: Int): String? {
        return jsAnimator.getAnimationName(index.toDouble()).let { if (it.isEmpty()) null else it }
    }
}