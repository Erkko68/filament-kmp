package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.Engine
import kotlin.test.Test
import kotlin.test.assertTrue

class ResourceLoaderSmokeTest {
    @Test
    fun verifyResourceLoaderApi() {
        val loaderVerify: (Engine, FilamentAsset) -> Unit = { engine, asset ->
            val loader = ResourceLoader(engine, true)
            loader.addResourceData("url", byteArrayOf(0))
            val hasData: Boolean = loader.hasResourceData("url")
            val loaded: Boolean = loader.loadResources(asset)
            val begin: Boolean = loader.asyncBeginLoad(asset)
            val progress: Float = loader.asyncGetLoadProgress()
            loader.asyncUpdateLoad()
            loader.asyncCancelLoad()
            loader.evictResourceData()
            loader.destroy()
        }
        assertTrue(true, "ResourceLoader API signatures resolved successfully.")
    }
}
