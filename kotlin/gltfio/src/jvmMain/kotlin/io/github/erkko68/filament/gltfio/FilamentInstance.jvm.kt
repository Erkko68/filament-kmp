package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Box
import io.github.erkko68.filament.MaterialInstance
import io.github.erkko68.filament.gltfio.jni.FilamentInstance as JniFilamentInstance

actual class FilamentInstance(internal val jni: JniFilamentInstance) {
    actual constructor() : this(throw IllegalStateException("Empty constructor not supported in JVM"))

    actual fun getRoot(): Int = jni.getRoot()
    actual fun getEntities(): IntArray = jni.getEntities()
    actual fun getEntityCount(): Int = jni.getEntities().size
    
    actual fun getAnimator(): Animator = Animator(jni.getAnimator())
    actual fun getBoundingBox(): Box {
        return getAsset().getBoundingBox()
    }
    
    actual fun getAsset(): FilamentAsset = FilamentAsset(jni.getAsset())
    
    actual fun getSkinCount(): Int = jni.getSkinCount()
    actual fun getSkinNames(): Array<String> = jni.getSkinNames()
    actual fun attachSkin(skinIndex: Int, target: Int) = jni.attachSkin(skinIndex, target)
    actual fun detachSkin(skinIndex: Int, target: Int) = jni.detachSkin(skinIndex, target)
    actual fun getJointCountAt(skinIndex: Int): Int = jni.getJointCountAt(skinIndex)
    actual fun getJointsAt(skinIndex: Int): IntArray = jni.getJointsAt(skinIndex)
    
    actual fun applyMaterialVariant(variantIndex: Int) = jni.applyMaterialVariant(variantIndex)
    actual fun getMaterialInstances(): Array<MaterialInstance> {
        return jni.getMaterialInstances().map { MaterialInstance(it) }.toTypedArray()
    }
    actual fun getMaterialVariantNames(): Array<String> = jni.getMaterialVariantNames()
}
