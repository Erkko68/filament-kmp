package io.github.erkko68.filament.gltfio

actual class Animator(val nativeObject: com.google.android.filament.gltfio.Animator) {
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

    actual fun getAnimationCount(): Int = nativeObject.animationCount

    actual fun getAnimationDuration(index: Int): Float = nativeObject.getAnimationDuration(index)

    actual fun getAnimationName(index: Int): String? = nativeObject.getAnimationName(index)
}
