package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.js.`gltfio_Animator` as JSAnimator

actual class Animator(internal val jsAnimator: JSAnimator) {
    actual fun applyAnimation(index: Int, time: Float) {
        // JS binding only supports applyAnimation(index), time parameter is not available
        jsAnimator.applyAnimation(index)
    }

    actual fun applyCrossFade(previousIndex: Int, previousTime: Float, alpha: Float) {
        jsAnimator.applyCrossFade(previousIndex, previousTime, alpha)
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
        return jsAnimator.getAnimationDuration(index).toFloat()
    }

    actual fun getAnimationName(index: Int): String? {
        return jsAnimator.getAnimationName(index).let { if (it.isEmpty()) null else it }
    }
}