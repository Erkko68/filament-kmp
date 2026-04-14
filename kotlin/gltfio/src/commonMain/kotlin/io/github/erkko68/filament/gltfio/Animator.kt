package io.github.erkko68.filament.gltfio

expect class Animator {
    fun applyAnimation(index: Int, time: Float)
    fun applyCrossFade(previousIndex: Int, previousTime: Float, alpha: Float)
    fun updateBoneMatrices()
    fun resetBoneMatrices()
    
    fun getAnimationCount(): Int
    fun getAnimationDuration(index: Int): Float
    fun getAnimationName(index: Int): String?
}
