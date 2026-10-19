package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.gltfio.testutils.GltfioTestFixture
import io.github.erkko68.filament.gltfio.testutils.TestGlb
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class FilamentAssetTest : GltfioTestFixture() {
    /** Fox's nodes are named: "fox" is the mesh, "b_" prefixes 23 of its 24 joints. */
    @Test
    fun entitiesAreFoundByName() {
        val fox = load(TestGlb.getFoxGlbBytes(), resources = false)
        val joints = IntArray(64)
        val prefixed = fox.getEntitiesByPrefix("b_", joints)

        assertEquals(fox.renderableEntities.single(), fox.getFirstEntityByName("fox"))
        assertEquals(1, fox.getEntitiesByName("fox", IntArray(4)))
        assertEquals(0, fox.getFirstEntityByName("no such node"))
        assertEquals(23, prefixed)
        assertTrue(joints.take(prefixed).all { it in fox.instance.getJointsAt(0) })
    }

    /** The bounds come from the POSITION accessors' min/max: a triangle spanning (0,0,0)..(1,1,0), a unit box. */
    @Test
    fun boundingBoxesComeFromTheGeometry() {
        val triangle = load(TestGlb.getMaterialVariantsGlbBytes(), resources = false).boundingBox
        val box = load(TestGlb.getBoxAnimatedGlbBytes(), resources = false).boundingBox
        assertContentEquals(floatArrayOf(0f, 0f, 0f), triangle.min)
        assertContentEquals(floatArrayOf(1f, 1f, 0f), triangle.max)
        assertContentEquals(floatArrayOf(-0.5f, -0.5f, -0.5f), box.min)
        assertContentEquals(floatArrayOf(0.5f, 0.5f, 0.5f), box.max)
    }

    @Test
    fun morphTargetsAreCountedPerRenderable() {
        val cube = load(TestGlb.getAnimatedMorphCubeGlbBytes())
        val renderable = cube.renderableEntities.single()
        assertEquals(2, cube.getMorphTargetCountAt(renderable))
        assertNull(cube.getMorphTargetNameAt(renderable, 0), "AnimatedMorphCube's targets are unnamed")
    }

    /** A glb carries its own buffers; a .gltf lists what it needs. */
    @Test
    fun resourceUrisListExternalFiles() {
        assertEquals(emptyList(), load(TestGlb.getDuckGlbBytes(), resources = false).resourceUris)
        assertEquals(listOf("tri.bin"), load(TestGlb.triangleGltf("tri.bin"), resources = false).resourceUris)
    }

    @Test
    fun scenesFilterWhatIsAdded() {
        val asset = load(TestGlb.getDuckGlbBytes())
        val scene = engine.createScene()
        asset.addEntitiesToScene(scene, asset.entities, sceneFilter = 0)
        val filteredOut = scene.entityCount
        asset.addEntitiesToScene(scene, asset.entities, sceneFilter = 1) // bit 0: glTF scene 0
        val added = scene.entityCount
        engine.destroy(scene)

        assertEquals(1, asset.sceneCount)
        assertNull(asset.getSceneName(0), "Duck's scene is unnamed")
        assertEquals(0, filteredOut)
        assertEquals(asset.entityCount, added)
    }

    /** Detached components belong to the app, which destroys them before the asset (that still owns their materials). */
    @Test
    fun detachedComponentsAreTheAppsToDestroy() {
        val asset = load(TestGlb.getDuckGlbBytes())
        assertTrue(asset.wireframe != 0)
        assertFalse(asset.areFilamentComponentsDetached)
        asset.detachFilamentComponents()
        assertTrue(asset.areFilamentComponentsDetached)
        (asset.entities + asset.root).forEach {
            engine.destroy(it)
            engine.entityManager.destroy(it)
        }
    }
}
