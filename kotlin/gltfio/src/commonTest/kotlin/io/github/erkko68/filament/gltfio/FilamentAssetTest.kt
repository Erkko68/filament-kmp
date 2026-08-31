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
        val loader = AssetLoader.create(engine, provider, engine.entityManager)
        val asset = loader.createAsset(bytes)
        assertNotNull(asset)

        val root = asset.root
        assertTrue(root != 0)

        val entityCount = asset.entityCount
        assertTrue(entityCount > 0)

        val entities = asset.entities
        assertEquals(entityCount, entities.size)

        assertNotNull(asset.renderableEntities)
        assertNotNull(asset.lightEntities)
        assertNotNull(asset.cameraEntities)

        loader.destroyAsset(asset)
        AssetLoader.destroy(loader)
        provider.destroy()
    }

    @Test
    fun testAssetBoundingBox() {
        val bytes = TestGlb.getDuckGlbBytes()
        if (bytes.isEmpty()) return

        val provider = UbershaderProvider(engine)
        val loader = AssetLoader.create(engine, provider, engine.entityManager)
        val asset = loader.createAsset(bytes)
        assertNotNull(asset)

        val bbox = asset.boundingBox
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
        val loader = AssetLoader.create(engine, provider, engine.entityManager)
        val asset = loader.createAsset(bytes)
        assertNotNull(asset)

        asset.getName(asset.root)
        asset.getExtras(asset.root)
        asset.getEntitiesByName("Duck")
        asset.getEntitiesByPrefix("")
        asset.getFirstEntityByName("Duck")
        asset.getMorphTargetNames(asset.root)

        loader.destroyAsset(asset)
        AssetLoader.destroy(loader)
        provider.destroy()
    }

    @Test
    fun testMorphTargetNames() {
        val bytes = TestGlb.getAnimatedMorphCubeGlbBytes()
        if (bytes.isEmpty()) return

        val provider = UbershaderProvider(engine)
        val loader = AssetLoader.create(engine, provider, engine.entityManager)
        val asset = loader.createAsset(bytes)
        assertNotNull(asset)

        val resourceLoader = ResourceLoader(engine)
        resourceLoader.loadResources(asset)

        // The morph target entity carries the named targets; scan all entities for them.
        var foundNames = false
        for (entity in asset.entities) {
            if (asset.getMorphTargetNames(entity).isNotEmpty()) {
                foundNames = true
            }
        }
        assertTrue(foundNames)

        resourceLoader.destroy()
        loader.destroyAsset(asset)
        AssetLoader.destroy(loader)
        provider.destroy()
    }

    @Test
    fun testAssetResourceUris() {
        val bytes = TestGlb.getDuckGlbBytes()
        if (bytes.isEmpty()) return

        val provider = UbershaderProvider(engine)
        val loader = AssetLoader.create(engine, provider, engine.entityManager)
        val asset = loader.createAsset(bytes)
        assertNotNull(asset)

        assertNotNull(asset.resourceUris)

        loader.destroyAsset(asset)
        AssetLoader.destroy(loader)
        provider.destroy()
    }

    @Test
    fun testAssetPopRenderables() {
        val bytes = TestGlb.getDuckGlbBytes()
        if (bytes.isEmpty()) return

        val provider = UbershaderProvider(engine)
        val loader = AssetLoader.create(engine, provider, engine.entityManager)
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
        val loader = AssetLoader.create(engine, provider, engine.entityManager)
        val asset = loader.createAsset(bytes)
        assertNotNull(asset)

        assertNotNull(asset.instance)
        assertNotNull(asset.engine)
        assertTrue(asset.assetInstanceCount >= 1)
        assertNotNull(asset.assetInstances)

        asset.releaseSourceData()

        loader.destroyAsset(asset)
        AssetLoader.destroy(loader)
        provider.destroy()
    }
}
