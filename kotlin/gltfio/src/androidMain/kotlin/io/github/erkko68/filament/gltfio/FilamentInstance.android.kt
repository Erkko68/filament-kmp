package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Box
import io.github.erkko68.filament.FilamentPlatform
import io.github.erkko68.filament.PlatformGap

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
    
    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "returns 0 — filament.js exposes no joint API.")
    actual fun getJointCountAt(skinIndex: Int): Int = nativeObject!!.getJointCountAt(skinIndex)
    
    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "returns an empty array — filament.js exposes no joint API.")
    actual fun getJointsAt(skinIndex: Int): IntArray = nativeObject!!.getJointsAt(skinIndex)
    
    actual fun applyMaterialVariant(variantIndex: Int) {
        nativeObject!!.applyMaterialVariant(variantIndex)
    }
    
    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "throws at runtime with embind 'unbound types' — the vector return type is unregistered in the web prebuilt.")
    actual fun getMaterialInstances(): Array<io.github.erkko68.filament.MaterialInstance> {
        val natives = nativeObject!!.materialInstances
        return Array(natives.size) { i -> io.github.erkko68.filament.MaterialInstance(natives[i]) }
    }
    
    actual fun getMaterialVariantNames(): Array<String> = nativeObject!!.materialVariantNames
}
