package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.wasm.*
import io.github.erkko68.filament.InternalFilamentApi

actual class Animator @InternalFilamentApi constructor(internal var nativeHandle: Int) {
    actual fun applyAnimation(index: Int, time: Float) {
        FilaAnimator_applyAnimation(nativeHandle, index, time)
    }

    actual fun applyCrossFade(previousIndex: Int, previousTime: Float, alpha: Float) {
        FilaAnimator_applyCrossFade(nativeHandle, previousIndex, previousTime, alpha)
    }

    actual fun updateBoneMatrices() {
        FilaAnimator_updateBoneMatrices(nativeHandle)
    }

    actual fun resetBoneMatrices() {
        FilaAnimator_resetBoneMatrices(nativeHandle)
    }

    actual val animationCount: Int get() = FilaAnimator_getAnimationCount(nativeHandle).toInt()

    actual fun getAnimationDuration(index: Int): Float = FilaAnimator_getAnimationDuration(nativeHandle, index)

    actual fun getAnimationName(index: Int): String? = FilaAnimator_getAnimationName(nativeHandle, index)
}
