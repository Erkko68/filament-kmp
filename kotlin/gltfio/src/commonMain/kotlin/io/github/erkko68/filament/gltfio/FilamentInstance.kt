package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Box

expect class FilamentInstance {
    constructor()

    fun getRoot(): Int
    fun getEntities(): IntArray
    fun getEntityCount(): Int
    
    fun getAnimator(): Animator
    fun getBoundingBox(): Box
    
    fun getAsset(): FilamentAsset
    
    fun getSkinCount(): Int
    fun getSkinNames(): Array<String>
    fun attachSkin(skinIndex: Int, target: Int)
    fun detachSkin(skinIndex: Int, target: Int)
    fun getJointCountAt(skinIndex: Int): Int
    fun getJointsAt(skinIndex: Int): IntArray
    
    fun applyMaterialVariant(variantIndex: Int)
    fun getMaterialInstances(): Array<io.github.erkko68.filament.MaterialInstance>
    fun getMaterialVariantNames(): Array<String>
}
