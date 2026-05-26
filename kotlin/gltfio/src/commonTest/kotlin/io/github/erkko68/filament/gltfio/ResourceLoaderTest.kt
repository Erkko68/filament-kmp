package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.gltfio.testutils.GltfioTestFixture
import io.github.erkko68.filament.gltfio.testutils.TestGlb
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ResourceLoaderTest : GltfioTestFixture() {
    @Test
    fun testResourceLoaderLifecycle() {
        val loader = ResourceLoader(engine)
        loader.addResourceData("http://example.com/texture.png", byteArrayOf(1, 2, 3))
        assertTrue(loader.hasResourceData("http://example.com/texture.png"))
        loader.evictResourceData()
        loader.destroy()
    }

    @Test
    fun testNormalizeSkinningWeightsConstructor() {
        val loader = ResourceLoader(engine, normalizeSkinningWeights = true)
        loader.destroy()
    }

    @Test
    fun testAsyncMethods() {
        val loader = ResourceLoader(engine)
        loader.asyncGetLoadProgress()
        loader.asyncUpdateLoad()
        loader.asyncCancelLoad()
        loader.destroy()
    }

    @Test
    fun testLoadResourcesWithGlb() {
        val bytes = TestGlb.getDuckGlbBytes()
        if (bytes.isEmpty()) return

        val provider = UbershaderProvider(engine)
        val assetLoader = AssetLoader.create(engine, provider, engine.getEntityManager())
        val asset = assetLoader.createAsset(bytes)
        assertNotNull(asset)

        val resourceLoader = ResourceLoader(engine)
        assertTrue(resourceLoader.loadResources(asset))

        resourceLoader.destroy()
        assetLoader.destroyAsset(asset)
        AssetLoader.destroy(assetLoader)
        provider.destroy()
    }

    @Test
    fun testAsyncLoadWithGlb() {
        val bytes = TestGlb.getDuckGlbBytes()
        if (bytes.isEmpty()) return

        val provider = UbershaderProvider(engine)
        val assetLoader = AssetLoader.create(engine, provider, engine.getEntityManager())
        val asset = assetLoader.createAsset(bytes)
        assertNotNull(asset)

        val resourceLoader = ResourceLoader(engine)
        assertTrue(resourceLoader.asyncBeginLoad(asset))

        resourceLoader.asyncGetLoadProgress()
        resourceLoader.asyncUpdateLoad()
        resourceLoader.asyncCancelLoad()

        resourceLoader.destroy()
        assetLoader.destroyAsset(asset)
        AssetLoader.destroy(assetLoader)
        provider.destroy()
    }
}
