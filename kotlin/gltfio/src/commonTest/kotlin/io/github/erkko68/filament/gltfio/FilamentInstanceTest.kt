package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.gltfio.testutils.GltfioTestFixture
import io.github.erkko68.filament.gltfio.testutils.TestGlb
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class FilamentInstanceTest : GltfioTestFixture() {
    @Test
    fun testInstanceEntityQueries() {
        val bytes = TestGlb.getDuckGlbBytes()
        if (bytes.isEmpty()) return

        val provider = UbershaderProvider(engine)
        val loader = AssetLoader.create(engine, provider, engine.getEntityManager())
        val asset = loader.createAsset(bytes)
        assertNotNull(asset)

        val instance = asset.getInstance()
        assertNotNull(instance)

        assertTrue(instance.getRoot() != 0)
        val entityCount = instance.getEntityCount()
        assertTrue(entityCount > 0)
        assertEquals(entityCount, instance.getEntities().size)

        loader.destroyAsset(asset)
        AssetLoader.destroy(loader)
        provider.destroy()
    }

    @Test
    fun testInstanceBoundingBox() {
        val bytes = TestGlb.getDuckGlbBytes()
        if (bytes.isEmpty()) return

        val provider = UbershaderProvider(engine)
        val loader = AssetLoader.create(engine, provider, engine.getEntityManager())
        val asset = loader.createAsset(bytes)
        assertNotNull(asset)

        val bbox = asset.getInstance().getBoundingBox()
        assertNotNull(bbox)
        assertNotNull(bbox.center)
        assertNotNull(bbox.halfExtent)

        loader.destroyAsset(asset)
        AssetLoader.destroy(loader)
        provider.destroy()
    }

    @Test
    fun testInstanceSkinning() {
        val bytes = TestGlb.getDuckGlbBytes()
        if (bytes.isEmpty()) return

        val provider = UbershaderProvider(engine)
        val loader = AssetLoader.create(engine, provider, engine.getEntityManager())
        val asset = loader.createAsset(bytes)
        assertNotNull(asset)

        val instance = asset.getInstance()
        val skinCount = instance.getSkinCount()
        assertTrue(skinCount >= 0)
        assertNotNull(instance.getSkinNames())

        if (skinCount > 0) {
            instance.getJointCountAt(0)
            instance.getJointsAt(0)
        }

        loader.destroyAsset(asset)
        AssetLoader.destroy(loader)
        provider.destroy()
    }

    @Test
    fun testInstanceMaterials() {
        val bytes = TestGlb.getDuckGlbBytes()
        if (bytes.isEmpty()) return

        val provider = UbershaderProvider(engine)
        val loader = AssetLoader.create(engine, provider, engine.getEntityManager())
        val asset = loader.createAsset(bytes)
        assertNotNull(asset)

        val instance = asset.getInstance()
        val variantNames = instance.getMaterialVariantNames()
        assertNotNull(variantNames)

        if (variantNames.isNotEmpty()) {
            instance.applyMaterialVariant(0)
        }

        assertNotNull(instance.getMaterialInstances())

        loader.destroyAsset(asset)
        AssetLoader.destroy(loader)
        provider.destroy()
    }

    @Test
    fun testInstanceGetAssetAndAnimator() {
        val bytes = TestGlb.getDuckGlbBytes()
        if (bytes.isEmpty()) return

        val provider = UbershaderProvider(engine)
        val loader = AssetLoader.create(engine, provider, engine.getEntityManager())
        val asset = loader.createAsset(bytes)
        assertNotNull(asset)

        val instance = asset.getInstance()
        assertNotNull(instance.getAsset())
        assertNotNull(instance.getAnimator())

        loader.destroyAsset(asset)
        AssetLoader.destroy(loader)
        provider.destroy()
    }
}
