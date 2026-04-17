package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Box
import io.github.erkko68.filament.MaterialInstance

actual class FilamentInstance {
    actual fun getRoot(): Int {
        TODO("Not yet implemented")
    }

    actual fun getEntities(): IntArray {
        TODO("Not yet implemented")
    }

    actual fun getEntityCount(): Int {
        TODO("Not yet implemented")
    }

    actual fun getAnimator(): Animator {
        TODO("Not yet implemented")
    }

    actual fun getBoundingBox(): Box {
        TODO("Not yet implemented")
    }

    actual fun getAsset(): FilamentAsset {
        TODO("Not yet implemented")
    }

    actual fun getSkinCount(): Int {
        TODO("Not yet implemented")
    }

    actual fun getSkinNames(): Array<String> {
        TODO("Not yet implemented")
    }

    actual fun attachSkin(skinIndex: Int, target: Int) {
    }

    actual fun detachSkin(skinIndex: Int, target: Int) {
    }

    actual fun getJointCountAt(skinIndex: Int): Int {
        TODO("Not yet implemented")
    }

    actual fun getJointsAt(skinIndex: Int): IntArray {
        TODO("Not yet implemented")
    }

    actual fun applyMaterialVariant(variantIndex: Int) {
    }

    actual fun getMaterialInstances(): Array<MaterialInstance> {
        TODO("Not yet implemented")
    }

    actual fun getMaterialVariantNames(): Array<String> {
        TODO("Not yet implemented")
    }

    actual constructor() {
        TODO("Not yet implemented")
    }
}