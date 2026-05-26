package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Box
import io.github.erkko68.filament.MaterialInstance
import io.github.erkko68.filament.gltfio.jni.FilamentInstance as JniFilamentInstance

actual class FilamentInstance internal constructor(internal var jni: JniFilamentInstance?) {
    actual constructor() : this(null)

    private val requireJni: JniFilamentInstance
        get() = jni ?: throw IllegalStateException("FilamentInstance is uninitialized")

    actual fun getRoot(): Int = requireJni.getRoot()
    actual fun getEntities(): IntArray = requireJni.getEntities()
    actual fun getEntityCount(): Int = requireJni.getEntities().size

    actual fun getAnimator(): Animator = Animator(requireJni.getAnimator())
    actual fun getBoundingBox(): Box {
        return getAsset().getBoundingBox()
    }

    actual fun getAsset(): FilamentAsset = FilamentAsset(requireJni.getAsset())

    actual fun getSkinCount(): Int = requireJni.getSkinCount()
    actual fun getSkinNames(): Array<String> = requireJni.getSkinNames()
    actual fun attachSkin(skinIndex: Int, target: Int) = requireJni.attachSkin(skinIndex, target)
    actual fun detachSkin(skinIndex: Int, target: Int) = requireJni.detachSkin(skinIndex, target)
    actual fun getJointCountAt(skinIndex: Int): Int = requireJni.getJointCountAt(skinIndex)
    actual fun getJointsAt(skinIndex: Int): IntArray = requireJni.getJointsAt(skinIndex)

    actual fun applyMaterialVariant(variantIndex: Int) = requireJni.applyMaterialVariant(variantIndex)
    actual fun getMaterialInstances(): Array<MaterialInstance> {
        return requireJni.getMaterialInstances().map { MaterialInstance(it) }.toTypedArray()
    }
    actual fun getMaterialVariantNames(): Array<String> = requireJni.getMaterialVariantNames()
}
