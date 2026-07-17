package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Box

actual class FilamentInstance {
    internal var nativeObject: com.google.android.filament.gltfio.FilamentInstance? = null
    private var ownerAsset: FilamentAsset? = null

    actual constructor()

    internal constructor(
        nativeObject: com.google.android.filament.gltfio.FilamentInstance,
        ownerAsset: FilamentAsset? = null
    ) : this() {
        this.nativeObject = nativeObject
        this.ownerAsset = ownerAsset
    }

    actual fun getRoot(): Int = nativeObject!!.root

    actual fun getEntities(): IntArray = nativeObject!!.entities

    actual fun getEntityCount(): Int = nativeObject!!.entities.size

    actual fun getAnimator(): Animator = Animator(nativeObject!!.animator)

    actual fun getBoundingBox(): Box = getAsset().getBoundingBox()

    actual fun getAsset(): FilamentAsset = ownerAsset ?: FilamentAsset(nativeObject!!.asset)
    
    actual fun getSkinCount(): Int = nativeObject!!.skinCount
    
    actual fun getSkinNames(): Array<String> = nativeObject!!.skinNames
    
    actual fun attachSkin(skinIndex: Int, target: Int) {
        nativeObject!!.attachSkin(skinIndex, target)
    }
    
    actual fun detachSkin(skinIndex: Int, target: Int) {
        nativeObject!!.detachSkin(skinIndex, target)
    }
    
    actual fun getJointCountAt(skinIndex: Int): Int = nativeObject!!.getJointCountAt(skinIndex)
    
    actual fun getJointsAt(skinIndex: Int): IntArray = nativeObject!!.getJointsAt(skinIndex)
    
    actual fun applyMaterialVariant(variantIndex: Int) {
        nativeObject!!.applyMaterialVariant(variantIndex)
    }
    
    actual fun getMaterialInstances(): Array<io.github.erkko68.filament.MaterialInstance> {
        val natives = nativeObject!!.materialInstances
        return Array(natives.size) { i -> io.github.erkko68.filament.MaterialInstance(natives[i]) }
    }
    
    actual fun getMaterialVariantNames(): Array<String> = nativeObject!!.materialVariantNames
}
