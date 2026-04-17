package io.github.erkko68.filament.gltfio

actual class Animator {
    actual fun applyAnimation(index: Int, time: Float) {
    }

    actual fun applyCrossFade(previousIndex: Int, previousTime: Float, alpha: Float) {
    }

    actual fun updateBoneMatrices() {
    }

    actual fun resetBoneMatrices() {
    }

    actual fun getAnimationCount(): Int {
        TODO("Not yet implemented")
    }

    actual fun getAnimationDuration(index: Int): Float {
        TODO("Not yet implemented")
    }

    actual fun getAnimationName(index: Int): String? {
        TODO("Not yet implemented")
    }
}