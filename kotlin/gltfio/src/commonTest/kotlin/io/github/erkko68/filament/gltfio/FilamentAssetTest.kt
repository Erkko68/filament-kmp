package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.gltfio.testutils.GltfioTestFixture
import io.github.erkko68.filament.gltfio.testutils.TestGlb
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FilamentAssetTest : GltfioTestFixture() {
    @Test
    fun testAssetEntityQueries() {
        val bytes = TestGlb.getDuckGlbBytes()
        if (bytes.isEmpty()) return

        val provider = UbershaderProvider(engine)
        val loader = AssetLoader.create(engine, provider, engine.getEntityManager())
        val asset = loader.createAsset(bytes)
        assertNotNull(asset)

        val root = asset.getRoot()
        assertTrue(root != 0)

        val entityCount = asset.getEntityCount()
        assertTrue(entityCount > 0)

        val entities = asset.getEntities()
        assertEquals(entityCount, entities.size)

        assertNotNull(asset.getRenderableEntities())
        assertNotNull(asset.getLightEntities())
        assertNotNull(asset.getCameraEntities())

        loader.destroyAsset(asset)
        AssetLoader.destroy(loader)
        provider.destroy()
    }

    @Test
    fun testAssetBoundingBox() {
        val bytes = TestGlb.getDuckGlbBytes()
        if (bytes.isEmpty()) return

        val provider = UbershaderProvider(engine)
        val loader = AssetLoader.create(engine, provider, engine.getEntityManager())
        val asset = loader.createAsset(bytes)
        assertNotNull(asset)

        val bbox = asset.getBoundingBox()
        assertNotNull(bbox)
        assertNotNull(bbox.center)
        assertNotNull(bbox.halfExtent)

        loader.destroyAsset(asset)
        AssetLoader.destroy(loader)
        provider.destroy()
    }

    @Test
    fun testAssetNameAndSearchMethods() {
        val bytes = TestGlb.getDuckGlbBytes()
        if (bytes.isEmpty()) return

        val provider = UbershaderProvider(engine)
        val loader = AssetLoader.create(engine, provider, engine.getEntityManager())
        val asset = loader.createAsset(bytes)
        assertNotNull(asset)

        asset.getName(asset.getRoot())
        asset.getExtras(asset.getRoot())
        asset.getEntitiesByName("Duck")
        asset.getEntitiesByPrefix("")
        asset.getFirstEntityByName("Duck")
        asset.getMorphTargetNames(asset.getRoot())

        loader.destroyAsset(asset)
        AssetLoader.destroy(loader)
        provider.destroy()
    }

    @Test
    fun testAssetResourceUris() {
        val bytes = TestGlb.getDuckGlbBytes()
        if (bytes.isEmpty()) return

        val provider = UbershaderProvider(engine)
        val loader = AssetLoader.create(engine, provider, engine.getEntityManager())
        val asset = loader.createAsset(bytes)
        assertNotNull(asset)

        assertNotNull(asset.getResourceUris())

        loader.destroyAsset(asset)
        AssetLoader.destroy(loader)
        provider.destroy()
    }

    @Test
    fun testAssetPopRenderables() {
        val bytes = TestGlb.getDuckGlbBytes()
        if (bytes.isEmpty()) return

        val provider = UbershaderProvider(engine)
        val loader = AssetLoader.create(engine, provider, engine.getEntityManager())
        val asset = loader.createAsset(bytes)
        assertNotNull(asset)

        val buffer = IntArray(64)
        asset.popRenderables(buffer)
        asset.popRenderable()

        loader.destroyAsset(asset)
        AssetLoader.destroy(loader)
        provider.destroy()
    }

    @Test
    fun testAssetInstanceAndEngine() {
        val bytes = TestGlb.getDuckGlbBytes()
        if (bytes.isEmpty()) return

        val provider = UbershaderProvider(engine)
        val loader = AssetLoader.create(engine, provider, engine.getEntityManager())
        val asset = loader.createAsset(bytes)
        assertNotNull(asset)

        assertNotNull(asset.getInstance())
        assertNotNull(asset.getEngine())
        assertTrue(asset.getAssetInstanceCount() >= 1)
        assertNotNull(asset.getAssetInstances())

        asset.releaseSourceData()

        loader.destroyAsset(asset)
        AssetLoader.destroy(loader)
        provider.destroy()
    }
}
