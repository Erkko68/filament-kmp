package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Box
import io.github.erkko68.filament.Entity
import kotlin.test.Test
import kotlin.test.assertTrue

class FilamentInstanceSmokeTest {
    @Test
    fun verifyFilamentInstanceApi() {
        val instanceVerify: (FilamentInstance) -> Unit = { instance ->
            val root: Entity = instance.getRoot()
            val entities: IntArray = instance.getEntities()
            val entityCount: Int = instance.getEntityCount()
            val animator: Animator = instance.getAnimator()
            val bounds: Box = instance.getBoundingBox()
            val asset: FilamentAsset = instance.getAsset()
            val skinCount: Int = instance.getSkinCount()
            val skinNames: Array<String> = instance.getSkinNames()
            
            instance.attachSkin(0, 0)
            instance.detachSkin(0, 0)
            val jointCount: Int = instance.getJointCountAt(0)
            val joints: IntArray = instance.getJointsAt(0)
            instance.applyMaterialVariant(0)
            val materials = instance.getMaterialInstances()
            val variantNames: Array<String> = instance.getMaterialVariantNames()
        }
        assertTrue(true, "FilamentInstance API signatures resolved successfully.")
    }
}
