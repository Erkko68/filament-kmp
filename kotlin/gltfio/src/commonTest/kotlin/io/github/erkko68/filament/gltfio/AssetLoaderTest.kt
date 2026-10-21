package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.gltfio.testutils.GltfioTestFixture
import io.github.erkko68.filament.gltfio.testutils.TestGlb
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AssetLoaderTest : GltfioTestFixture() {
    /** Duck.glb: three nodes, one mesh, one camera, no lights. */
    @Test
    fun anAssetHasAnEntityPerNode() {
        val asset = load(TestGlb.getDuckGlbBytes(), resources = false)
        assertEquals(3, asset.entityCount)
        assertEquals(1, asset.renderableEntityCount)
        assertEquals(1, asset.cameraEntityCount)
        assertEquals(0, asset.lightEntityCount)
        assertEquals(engine.nativeObject, asset.engine.nativeObject)
        val entities = asset.entities + asset.root
        assertTrue(entities.all(engine.entityManager::isAlive))
        assertTrue(engine.renderableManager.hasComponent(asset.renderableEntities.single()))

        destroy(asset)
        assertTrue(entities.none(engine.entityManager::isAlive), "destroyAsset destroys its entities")
    }

    @Test
    fun theDefaultEntityManagerIsTheEngines() {
        val defaultLoader = AssetLoader.create(AssetConfiguration(engine, provider))
        val asset = defaultLoader.createAsset(TestGlb.getDuckGlbBytes())!!
        val alive = engine.entityManager.isAlive(asset.root)
        defaultLoader.destroyAsset(asset)
        AssetLoader.destroy(defaultLoader)
        assertTrue(alive)
    }

    /** Duck's root and camera nodes have no name of their own, so they take the default ("<unknown>" without one, AssetLoader.cpp). */
    @Test
    fun anonymousNodesTakeTheDefaultNodeName() {
        val named = AssetLoader.create(AssetConfiguration(engine, provider, defaultNodeName = "anonymous"))
        val asset = named.createAsset(TestGlb.getDuckGlbBytes())!!
        val found = asset.getEntitiesByName("anonymous", IntArray(4)) to asset.getEntitiesByName("<unknown>", IntArray(4))
        named.destroyAsset(asset)
        AssetLoader.destroy(named)
        assertEquals(2 to 0, found)
    }

    /** Each instance is its own entity tree with the same shape; one more can join until the source data goes. */
    @Test
    fun instancesAreSeparateCopiesOfTheAsset() {
        val asset = checkNotNull(loader.createInstancedAsset(TestGlb.getDuckGlbBytes(), arrayOf(FilamentInstance(), FilamentInstance())))
        val added = loader.createInstance(asset)
        val instances = asset.assetInstances
        val counts = instances.map { it.entityCount }
        val roots = instances.map { it.root }
        val owners = instances.map { it.asset.nativeObject }
        asset.releaseSourceData()
        val late = loader.createInstance(asset)
        val entities = instances.flatMap { it.entities.toList() }
        val instanceCount = asset.assetInstanceCount
        val handle = asset.nativeObject
        loader.destroyAsset(asset)

        assertEquals(3, instanceCount)
        assertEquals(added?.nativeObject, instances.last().nativeObject)
        assertEquals(listOf(3, 3, 3), counts)
        assertEquals(3, roots.distinct().size)
        assertEquals(List(3) { handle }, owners)
        assertEquals(entities.size, entities.distinct().size, "instances share no entities")
        assertNull(late, "no instances once the source data is released")
    }

    /** Assets share the loader's materials: a second duck compiles nothing new. */
    @Test
    fun assetsShareCompiledMaterials() {
        load(TestGlb.getDuckGlbBytes())
        val compiled = loader.materials.map { it.name }
        load(TestGlb.getDuckGlbBytes())
        assertEquals(listOf("base_lit_opaque"), compiled)
        assertEquals(compiled, loader.materials.map { it.name })
        assertEquals(provider.materials.map { it.nativeObject }, loader.materials.map { it.nativeObject })
    }

    @Test
    fun garbageParsesToNothing() {
        val garbage = "not a glTF".encodeToByteArray()
        assertNull(loader.createAsset(garbage))
        assertNull(loader.createInstancedAsset(garbage, arrayOf(FilamentInstance())))
    }
}
