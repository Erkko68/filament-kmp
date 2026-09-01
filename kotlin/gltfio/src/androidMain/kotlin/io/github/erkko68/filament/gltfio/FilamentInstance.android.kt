package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Box
import io.github.erkko68.filament.FilamentPlatform
import io.github.erkko68.filament.PlatformGap
import io.github.erkko68.filament.nativeObject
import io.github.erkko68.filament.Entity

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

    actual val root: Entity get() = nativeObject!!.root

    actual val entities: IntArray get() = nativeObject!!.entities

    actual val entityCount: Int get() = nativeObject!!.entities.size

    // No pre-load guard here (unlike the other targets): the Java binding wraps the native
    // animator pointer itself, so a call before ResourceLoader has run still hands back a
    // non-null com.google.android.filament Animator that only crashes once used.
    actual val animator: Animator get() = Animator(nativeObject!!.animator)

    actual val boundingBox: Box get() = asset.boundingBox

    actual val asset: FilamentAsset get() = ownerAsset ?: FilamentAsset(nativeObject!!.asset)
    
    actual val skinCount: Int get() = nativeObject!!.skinCount
    
    actual val skinNames: List<String> get() = nativeObject!!.skinNames.toList()
    
    actual fun attachSkin(skinIndex: Int, target: Entity) {
        nativeObject!!.attachSkin(skinIndex, target)
    }
    
    actual fun detachSkin(skinIndex: Int, target: Entity) {
        nativeObject!!.detachSkin(skinIndex, target)
    }
    
    actual fun getJointCountAt(skinIndex: Int): Int = nativeObject!!.getJointCountAt(skinIndex)
    
    actual fun getJointsAt(skinIndex: Int): IntArray = nativeObject!!.getJointsAt(skinIndex)
    
    actual fun applyMaterialVariant(variantIndex: Int) {
        nativeObject!!.applyMaterialVariant(variantIndex)
    }
    
    @PlatformGap(platforms = [FilamentPlatform.WEB], behavior = "throws at runtime with embind 'unbound types' — the vector return type is unregistered in the web prebuilt.")
    actual val materialInstances: List<io.github.erkko68.filament.MaterialInstance> get() {
        val natives = nativeObject!!.materialInstances
        return List(natives.size) { i -> io.github.erkko68.filament.MaterialInstance(natives[i]) }
    }
    
    actual val materialVariantNames: List<String> get() = nativeObject!!.materialVariantNames.toList()
}
