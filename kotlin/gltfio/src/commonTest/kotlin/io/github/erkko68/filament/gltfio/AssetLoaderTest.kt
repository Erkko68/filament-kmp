package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.gltfio.testutils.GltfioTestFixture
import io.github.erkko68.filament.gltfio.testutils.TestGlb
import io.github.erkko68.filament.testsupport.IgnoreJs
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AssetLoaderTest : GltfioTestFixture() {
    @Test
    fun testAssetLoaderLifecycle() {
        val provider = UbershaderProvider(engine)
        val loader = AssetLoader.create(engine, provider, engine.getEntityManager())
        assertNotNull(loader)

        loader.enableDiagnostics(true)
        loader.enableDiagnostics(false)

        AssetLoader.destroy(loader)
        provider.destroy()
    }

    @Test
    fun testCreateWithDefaultEntityManager() {
        // Omitting the EntityManager exercises the null/default branch.
        val provider = UbershaderProvider(engine)
        val loader = AssetLoader.create(engine, provider)
        assertNotNull(loader)

        AssetLoader.destroy(loader)
        provider.destroy()
    }

    @Test
    fun testCreateAssetWithValidGlb() {
        val bytes = TestGlb.getDuckGlbBytes()
        if (bytes.isEmpty()) return

        val provider = UbershaderProvider(engine)
        val loader = AssetLoader.create(engine, provider, engine.getEntityManager())

        val asset = loader.createAsset(bytes)
        assertNotNull(asset)
        assertTrue(asset.getEntityCount() > 0)
        assertTrue(asset.getRoot() != 0)

        loader.destroyAsset(asset)
        AssetLoader.destroy(loader)
        provider.destroy()
    }

    @Test
    @IgnoreJs // getAssetInstances exists in filament.js but its vector return type is unregistered (embind "unbound types") in the web prebuilt.
    fun testCreateInstancedAsset() {
        val bytes = TestGlb.getDuckGlbBytes()
        if (bytes.isEmpty()) return

        val provider = UbershaderProvider(engine)
        val loader = AssetLoader.create(engine, provider, engine.getEntityManager())

        val instances = arrayOf(FilamentInstance(), FilamentInstance())
        val asset = loader.createInstancedAsset(bytes, instances)
        assertNotNull(asset)
        assertTrue(asset.getAssetInstanceCount() >= 1)

        loader.destroyAsset(asset)
        AssetLoader.destroy(loader)
        provider.destroy()
    }

    @Test
    @IgnoreJs // getAssetInstances exists in filament.js but its vector return type is unregistered (embind "unbound types") in the web prebuilt.
    fun testCreateInstance() {
        val bytes = TestGlb.getDuckGlbBytes()
        if (bytes.isEmpty()) return

        val provider = UbershaderProvider(engine)
        val loader = AssetLoader.create(engine, provider, engine.getEntityManager())

        val instances = arrayOf(FilamentInstance())
        val asset = loader.createInstancedAsset(bytes, instances)
        assertNotNull(asset)

        val newInstance = loader.createInstance(asset)
        assertNotNull(newInstance)
        assertTrue(newInstance.getEntityCount() > 0)

        loader.destroyAsset(asset)
        AssetLoader.destroy(loader)
        provider.destroy()
    }
}
