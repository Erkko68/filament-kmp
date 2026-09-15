package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.gltfio.testutils.GltfioTestFixture
import io.github.erkko68.filament.gltfio.testutils.TestGlb
import io.github.erkko68.filament.testsupport.IgnoreJs
import io.github.erkko68.filament.testsupport.TestEnv
import io.github.erkko68.filament.testsupport.TestTarget
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import io.github.erkko68.filament.Entity

class FilamentInstanceTest : GltfioTestFixture() {
    @Test
    fun testInstanceEntityQueries() {
        val bytes = TestGlb.getDuckGlbBytes()
        if (bytes.isEmpty()) return

        val provider = UbershaderProvider(engine)
        val loader = AssetLoader.create(engine, provider, engine.entityManager)
        val asset = loader.createAsset(bytes)
        assertNotNull(asset)

        val instance = asset.instance
        assertNotNull(instance)

        assertTrue(instance.root != 0)
        val entityCount = instance.entityCount
        assertTrue(entityCount > 0)
        assertEquals(entityCount, instance.entities.size)

        loader.destroyAsset(asset)
        AssetLoader.destroy(loader)
        provider.destroy()
    }

    @Test
    fun testInstanceBoundingBox() {
        val bytes = TestGlb.getDuckGlbBytes()
        if (bytes.isEmpty()) return

        val provider = UbershaderProvider(engine)
        val loader = AssetLoader.create(engine, provider, engine.entityManager)
        val asset = loader.createAsset(bytes)
        assertNotNull(asset)

        val bbox = asset.instance.boundingBox
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
        val loader = AssetLoader.create(engine, provider, engine.entityManager)
        val asset = loader.createAsset(bytes)
        assertNotNull(asset)

        val instance = asset.instance
        val skinCount = instance.skinCount
        assertTrue(skinCount >= 0)
        assertNotNull(instance.skinNames)

        if (skinCount > 0) {
            instance.getJointCountAt(0)
            instance.getJointsAt(0)
        }

        loader.destroyAsset(asset)
        AssetLoader.destroy(loader)
        provider.destroy()
    }

    @Test
    @IgnoreJs // getMaterialInstances hits an unregistered vector return type (embind "unbound types") in the web prebuilt.
    fun testInstanceMaterials() {
        val bytes = TestGlb.getDuckGlbBytes()
        if (bytes.isEmpty()) return

        val provider = UbershaderProvider(engine)
        val loader = AssetLoader.create(engine, provider, engine.entityManager)
        val asset = loader.createAsset(bytes)
        assertNotNull(asset)

        val instance = asset.instance
        val variantNames = instance.materialVariantNames
        assertNotNull(variantNames)

        if (variantNames.isNotEmpty()) {
            instance.applyMaterialVariant(0)
        }

        assertNotNull(instance.materialInstances)

        loader.destroyAsset(asset)
        AssetLoader.destroy(loader)
        provider.destroy()
    }

    @Test
    fun testSkinnedInstanceJointsAndSkins() {
        val bytes = TestGlb.getFoxGlbBytes()
        if (bytes.isEmpty()) return

        val provider = UbershaderProvider(engine)
        val loader = AssetLoader.create(engine, provider, engine.entityManager)
        val asset = loader.createAsset(bytes)
        assertNotNull(asset)

        val resourceLoader = ResourceLoader(engine)
        resourceLoader.loadResources(asset)

        val instance = asset.instance
        val skinCount = instance.skinCount
        // Fox is a rigged model with at least one skin.
        assertTrue(skinCount > 0)

        val skinNames = instance.skinNames
        assertEquals(skinCount, skinNames.size)

        val jointCount = instance.getJointCountAt(0)
        assertTrue(jointCount > 0)
        val joints = instance.getJointsAt(0)
        assertEquals(jointCount, joints.size)

        // Re-attach the first joint to its skin to exercise attach/detach bindings.
        val target = joints[0]
        instance.detachSkin(0, target)
        instance.attachSkin(0, target)

        resourceLoader.destroy()
        loader.destroyAsset(asset)
        AssetLoader.destroy(loader)
        provider.destroy()
    }

    @Test
    fun testInstanceMaterialVariants() {
        val bytes = TestGlb.getMaterialVariantsGlbBytes()
        if (bytes.isEmpty()) return

        val provider = UbershaderProvider(engine)
        val loader = AssetLoader.create(engine, provider, engine.entityManager)
        val asset = loader.createAsset(bytes)
        assertNotNull(asset)

        val instance = asset.instance
        val variantNames = instance.materialVariantNames
        // The synthetic asset declares two KHR_materials_variants.
        assertEquals(2, variantNames.size)

        instance.applyMaterialVariant(0)
        instance.applyMaterialVariant(1)

        loader.destroyAsset(asset)
        AssetLoader.destroy(loader)
        provider.destroy()
    }

    @Test
    fun testInstanceGetAssetAndAnimator() {
        val bytes = TestGlb.getDuckGlbBytes()
        if (bytes.isEmpty()) return

        val provider = UbershaderProvider(engine)
        val loader = AssetLoader.create(engine, provider, engine.entityManager)
        val asset = loader.createAsset(bytes)
        assertNotNull(asset)

        val instance = asset.instance
        assertNotNull(instance.asset)

        // gltfio creates the animator during resource load, so it does not exist yet — every
        // target but Android can see that and says so instead of handing back a broken animator.
        if (TestEnv.target != TestTarget.ANDROID) {
            assertFailsWith<IllegalStateException> { instance.animator }
        }

        val resourceLoader = ResourceLoader(engine)
        assertTrue(resourceLoader.loadResources(asset))
        assertNotNull(instance.animator)

        resourceLoader.destroy()
        loader.destroyAsset(asset)
        AssetLoader.destroy(loader)
        provider.destroy()
    }
}
