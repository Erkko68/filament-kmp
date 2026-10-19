package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.gltfio.testutils.GltfioTestFixture
import io.github.erkko68.filament.gltfio.testutils.TestGlb
import io.github.erkko68.filament.testsupport.TestEnv
import io.github.erkko68.filament.testsupport.TestTarget
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FilamentInstanceTest : GltfioTestFixture() {
    @Test
    fun aPlainAssetsInstanceIsTheAssetItself() {
        val asset = load(TestGlb.getDuckGlbBytes(), resources = false)
        val instance = asset.instance
        val tm = engine.transformManager
        assertEquals(asset.root, tm.getParent(tm.getInstance(instance.root)), "the instance hangs off the asset's root")
        assertEquals(asset.entities.toSet(), instance.entities.toSet())
        assertEquals(asset.nativeObject, instance.asset.nativeObject)
        assertEquals(1, asset.assetInstanceCount)
    }

    /** Fox has one skin of 24 joints, all nodes of the asset, each with an affine inverse bind matrix. */
    @Test
    fun foxIsSkinnedByItsJoints() {
        val fox = load(TestGlb.getFoxGlbBytes()).instance
        val joints = fox.getJointsAt(0)
        val inverseBinds = fox.getInverseBindMatricesAt(0)

        assertEquals(1, fox.skinCount)
        assertNull(fox.getSkinNameAt(0), "Fox's skin is unnamed")
        assertEquals(24, fox.getJointCountAt(0))
        assertEquals(24, joints.size)
        assertTrue(joints.all { it in fox.entities })
        assertEquals(16 * 24, inverseBinds.size)
        assertTrue((0 until 24).all { inverseBinds[16 * it + 15] == 1f })
        assertEquals(0, load(TestGlb.getDuckGlbBytes()).instance.skinCount)
    }

    /** A variant swaps the material instance the renderable draws with, and back. */
    @Test
    fun variantsSwapTheRenderablesMaterial() {
        val instance = load(TestGlb.getMaterialVariantsGlbBytes()).instance
        val rm = engine.renderableManager
        val renderable = rm.getInstance(instance.asset.renderableEntities.single())
        fun drawnWith(variant: Int) = run {
            instance.applyMaterialVariant(variant)
            rm.getMaterialInstanceAt(renderable, 0)?.nativeObject
        }
        val red = drawnWith(0)
        val blue = drawnWith(1)

        assertEquals(listOf("red", "blue"), List(instance.materialVariantCount) { instance.getMaterialVariantName(it) })
        assertEquals(2, instance.materialInstanceCount)
        assertNotEquals(red, blue)
        assertEquals(instance.materialInstances.map { it.nativeObject }.toSet(), setOf(red, blue))
        assertEquals(red, drawnWith(0))
    }

    /** gltfio creates the animator while loading resources. */
    @Test
    fun theAnimatorArrivesWithTheResources() {
        val asset = load(TestGlb.getFoxGlbBytes(), resources = false)
        // Android can't tell a missing animator from a real one.
        if (TestEnv.target != TestTarget.ANDROID) assertFailsWith<IllegalStateException> { asset.instance.animator }
        ResourceLoader(ResourceConfiguration(engine)).use { assertTrue(it.loadResources(asset)) }
        assertEquals(3, asset.instance.animator.animationCount)
    }

    /** Recomputed from the skinned vertices: Fox's renderable keeps finite, non-empty bounds. */
    @Test
    fun recomputedBoundsAreFinite() {
        val fox = load(TestGlb.getFoxGlbBytes())
        fox.instance.recomputeBoundingBoxes()
        val rm = engine.renderableManager
        val box = rm.getAxisAlignedBoundingBox(rm.getInstance(fox.renderableEntities.single()))
        assertTrue(box.halfExtent.all { it.isFinite() && it > 0f }, "half extent ${box.halfExtent.toList()}")
    }

    /** Detached material instances belong to the app: they outlive the asset. */
    @Test
    fun detachedMaterialInstancesOutliveTheAsset() {
        val asset = load(TestGlb.getDuckGlbBytes())
        val instances = asset.instance.materialInstances
        val materials = instances.map { it.material }
        asset.instance.detachMaterialInstances()
        destroy(asset)

        val alive = materials.zip(instances).all { (m, mi) -> engine.isValid(m, mi) }
        instances.forEach { engine.destroy(it) }
        assertTrue(alive)
    }
}
