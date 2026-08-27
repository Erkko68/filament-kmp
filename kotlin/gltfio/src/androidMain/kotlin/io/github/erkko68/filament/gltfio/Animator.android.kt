package io.github.erkko68.filament.gltfio
import io.github.erkko68.filament.InternalFilamentApi


actual class Animator @InternalFilamentApi constructor(internal val nativeObject: com.google.android.filament.gltfio.Animator) {
    actual fun applyAnimation(index: Int, time: Float) {
        nativeObject.applyAnimation(index, time)
    }

    actual fun applyCrossFade(previousIndex: Int, previousTime: Float, alpha: Float) {
        nativeObject.applyCrossFade(previousIndex, previousTime, alpha)
    }

    actual fun updateBoneMatrices() {
        nativeObject.updateBoneMatrices()
    }

    actual fun resetBoneMatrices() {
        nativeObject.resetBoneMatrices()
    }

    actual val animationCount: Int get() = nativeObject.animationCount

    actual fun getAnimationDuration(index: Int): Float = nativeObject.getAnimationDuration(index)

    actual fun getAnimationName(index: Int): String? = nativeObject.getAnimationName(index)
}
