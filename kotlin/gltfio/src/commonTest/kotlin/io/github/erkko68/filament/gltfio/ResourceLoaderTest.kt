package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.gltfio.testutils.GltfioTestFixture
import io.github.erkko68.filament.gltfio.testutils.TestGlb
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ResourceLoaderTest : GltfioTestFixture() {
    @Test
    fun testResourceLoaderLifecycle() {
        val loader = ResourceLoader(ResourceConfiguration(engine))
        loader.addResourceData("http://example.com/texture.png", byteArrayOf(1, 2, 3))
        assertTrue(loader.hasResourceData("http://example.com/texture.png"))
        loader.evictResourceData()
        loader.destroy()
    }

    @Test
    fun testNormalizeSkinningWeightsConstructor() {
        val loader = ResourceLoader(ResourceConfiguration(engine, normalizeSkinningWeights = true))
        loader.destroy()
    }

    @Test
    fun testAsyncMethods() {
        val loader = ResourceLoader(ResourceConfiguration(engine))
        loader.asyncGetLoadProgress()
        loader.asyncUpdateLoad()
        loader.asyncCancelLoad()
        loader.destroy()
    }

    @Test
    fun testLoadResourcesWithGlb() {
        val bytes = TestGlb.getDuckGlbBytes()
        if (bytes.isEmpty()) return

        val provider = createUbershaderProvider(engine)
        val assetLoader = AssetLoader.create(AssetConfiguration(engine, provider, engine.entityManager))
        val asset = assetLoader.createAsset(bytes)
        assertNotNull(asset)

        val resourceLoader = ResourceLoader(ResourceConfiguration(engine))
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

        val provider = createUbershaderProvider(engine)
        val assetLoader = AssetLoader.create(AssetConfiguration(engine, provider, engine.entityManager))
        val asset = assetLoader.createAsset(bytes)
        assertNotNull(asset)

        val resourceLoader = ResourceLoader(ResourceConfiguration(engine))
        assertTrue(resourceLoader.asyncBeginLoad(asset))

        resourceLoader.asyncGetLoadProgress()
        resourceLoader.asyncUpdateLoad()
        resourceLoader.asyncCancelLoad()

        resourceLoader.destroy()
        assetLoader.destroyAsset(asset)
        AssetLoader.destroy(assetLoader)
        provider.destroy()
    }

    @Test
    fun testTextureProviders() {
        val bytes = TestGlb.getDuckGlbBytes()
        if (bytes.isEmpty()) return

        val provider = createUbershaderProvider(engine)
        val assetLoader = AssetLoader.create(AssetConfiguration(engine, provider, engine.entityManager))
        val asset = assetLoader.createAsset(bytes)
        assertNotNull(asset)

        // Duck's base color is a PNG: loading decodes it through the registered provider.
        val stb = createStbProvider(engine)
        val ktx2 = createKtx2Provider(engine)
        val resourceLoader = ResourceLoader(ResourceConfiguration(engine))
        resourceLoader.addTextureProvider("image/png", stb)
        resourceLoader.addTextureProvider("image/ktx2", ktx2)
        assertTrue(resourceLoader.loadResources(asset))
        createWebpProvider(engine)?.destroy()

        resourceLoader.destroy()
        stb.destroy()
        ktx2.destroy()
        assetLoader.destroyAsset(asset)
        AssetLoader.destroy(assetLoader)
        provider.destroy()
    }
}
