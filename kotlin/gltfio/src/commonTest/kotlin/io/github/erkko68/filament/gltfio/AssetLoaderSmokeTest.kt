package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.EntityManager
import kotlin.test.Test
import kotlin.test.assertTrue

class AssetLoaderSmokeTest {
    @Test
    fun verifyAssetLoaderApi() {
        val loaderVerify: (Engine, MaterialProvider, EntityManager?) -> Unit = { engine, materials, entities ->
            val loader = AssetLoader.create(engine, materials, entities)
            val asset: FilamentAsset? = loader.createAsset(byteArrayOf(0))
            val instance: FilamentInstance? = asset?.let { loader.createInstance(it) }
            
            val instances = if (instance != null) arrayOf(instance) else emptyArray<FilamentInstance>()
            val instancedAsset: FilamentAsset? = loader.createInstancedAsset(byteArrayOf(0), instances)
            
            loader.enableDiagnostics(true)
            if (asset != null) {
                loader.destroyAsset(asset)
            }
            AssetLoader.destroy(loader)
        }
        assertTrue(true, "AssetLoader API signatures resolved successfully.")
    }
}
