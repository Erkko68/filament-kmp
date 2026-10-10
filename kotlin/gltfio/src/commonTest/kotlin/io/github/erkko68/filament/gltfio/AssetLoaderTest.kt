package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.gltfio.testutils.GltfioTestFixture
import io.github.erkko68.filament.gltfio.testutils.TestGlb
import io.github.erkko68.filament.interop.NullPointer
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AssetLoaderTest : GltfioTestFixture() {
    @Test
    fun testAssetLoaderLifecycle() {
        val provider = createUbershaderProvider(engine)
        val loader = AssetLoader.create(AssetConfiguration(engine, provider, engine.entityManager))
        assertNotNull(loader)

        loader.enableDiagnostics(true)
        loader.enableDiagnostics(false)

        AssetLoader.destroy(loader)
        provider.destroy()
    }

    @Test
    fun testCreateWithDefaultEntityManager() {
        // Omitting the EntityManager exercises the null/default branch.
        val provider = createUbershaderProvider(engine)
        val loader = AssetLoader.create(AssetConfiguration(engine, provider))
        assertNotNull(loader)

        AssetLoader.destroy(loader)
        provider.destroy()
    }

    @Test
    fun testCreateAssetWithValidGlb() {
        val bytes = TestGlb.getDuckGlbBytes()
        if (bytes.isEmpty()) return

        val provider = createUbershaderProvider(engine)
        val loader = AssetLoader.create(AssetConfiguration(engine, provider, engine.entityManager))

        val asset = loader.createAsset(bytes)
        assertNotNull(asset)
        assertTrue(asset.entityCount > 0)
        assertTrue(asset.root != 0)

        loader.destroyAsset(asset)
        AssetLoader.destroy(loader)
        provider.destroy()
    }

    @Test
    fun testCreateInstancedAsset() {
        val bytes = TestGlb.getDuckGlbBytes()
        if (bytes.isEmpty()) return

        val provider = createUbershaderProvider(engine)
        val loader = AssetLoader.create(AssetConfiguration(engine, provider, engine.entityManager))

        val instances = arrayOf(FilamentInstance(), FilamentInstance())
        val asset = loader.createInstancedAsset(bytes, instances)
        assertNotNull(asset)
        assertTrue(asset.assetInstanceCount >= 1)

        loader.destroyAsset(asset)
        AssetLoader.destroy(loader)
        provider.destroy()
    }

    @Test
    fun testCreateInstance() {
        val bytes = TestGlb.getDuckGlbBytes()
        if (bytes.isEmpty()) return

        val provider = createUbershaderProvider(engine)
        val loader = AssetLoader.create(AssetConfiguration(engine, provider, engine.entityManager))

        val instances = arrayOf(FilamentInstance())
        val asset = loader.createInstancedAsset(bytes, instances)
        assertNotNull(asset)

        val newInstance = loader.createInstance(asset)
        assertNotNull(newInstance)
        assertTrue(newInstance.entityCount > 0)

        loader.destroyAsset(asset)
        AssetLoader.destroy(loader)
        provider.destroy()
    }

    @Test
    fun testMaterialsAndGc() {
        val bytes = TestGlb.getDuckGlbBytes()
        if (bytes.isEmpty()) return

        createUbershaderProvider(engine).use { provider ->
            val loader = AssetLoader.create(AssetConfiguration(engine, provider, engine.entityManager))
            val asset = assertNotNull(loader.createAsset(bytes))
            assertEquals(loader.materialsCount, loader.materials.size)
            assertEquals(provider.materialsCount, loader.materialProvider.materialsCount)
            assertEquals(provider.materialsCount, provider.materials.size)
            loader.destroyAsset(asset)
            loader.gc()
            AssetLoader.destroy(loader)
        }
    }

    // A parse failure is a null asset rather than a crash, and a frozen asset can't grow instances.
    @Test
    fun testFailuresBuildNothing() {
        val provider = createUbershaderProvider(engine)
        val loader = AssetLoader.create(AssetConfiguration(engine, provider, engine.entityManager))
        val garbage = "not a glTF".encodeToByteArray()
        val parsed = loader.createAsset(garbage)
        val parsedInstanced = loader.createInstancedAsset(garbage, arrayOf(FilamentInstance()))

        val asset = assertNotNull(loader.createInstancedAsset(TestGlb.getDuckGlbBytes(), arrayOf(FilamentInstance())))
        val handles = listOf(loader.nativeObject, asset.nativeObject, asset.instance.nativeObject)
        asset.releaseSourceData()
        val late = loader.createInstance(asset)
        loader.destroyAsset(asset)
        AssetLoader.destroy(loader)
        provider.destroy()
        // Checked after the teardown: a failure that skipped it would abort on the live material instances.
        assertNull(parsed)
        assertNull(parsedInstanced)
        assertFalse(NullPointer in handles)
        assertEquals(handles.size, handles.distinct().size)
        assertNull(late)
    }
}
