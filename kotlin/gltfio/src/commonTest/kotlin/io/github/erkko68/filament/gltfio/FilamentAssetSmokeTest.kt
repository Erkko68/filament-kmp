package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Box
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Entity
import kotlin.test.Test
import kotlin.test.assertTrue

class FilamentAssetSmokeTest {
    @Test
    fun verifyFilamentAssetApi() {
        val assetVerify: (FilamentAsset) -> Unit = { asset ->
            val root: Entity = asset.getRoot()
            val pop: Entity = asset.popRenderable()
            val poppedCount: Int = asset.popRenderables(intArrayOf(0))
            val entities: IntArray = asset.getEntities()
            val lights: IntArray = asset.getLightEntities()
            val renderables: IntArray = asset.getRenderableEntities()
            val cameras: IntArray = asset.getCameraEntities()
            val byName: IntArray = asset.getEntitiesByName("name")
            val byPrefix: IntArray = asset.getEntitiesByPrefix("prefix")
            val first: Entity = asset.getFirstEntityByName("name")
            val count: Int = asset.getEntityCount()
            val instanceCount: Int = asset.getAssetInstanceCount()
            val instances: Array<FilamentInstance> = asset.getAssetInstances()
            val bounds: Box = asset.getBoundingBox()
            val name: String? = asset.getName(0)
            val extras: String? = asset.getExtras(0)
            val morphs: Array<String> = asset.getMorphTargetNames(0)
            val uris: Array<String> = asset.getResourceUris()
            
            asset.releaseSourceData()
            val engine: Engine = asset.getEngine()
            val instance: FilamentInstance = asset.getInstance()
        }
        assertTrue(true, "FilamentAsset API signatures resolved successfully.")
    }
}
