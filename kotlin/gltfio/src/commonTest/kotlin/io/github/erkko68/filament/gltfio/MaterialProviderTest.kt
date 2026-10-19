package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.VertexBuffer
import io.github.erkko68.filament.gltfio.testutils.GltfioTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MaterialProviderTest : GltfioTestFixture() {
    private fun material(key: MaterialKey) = provider.getMaterial(key, Array(UV_MAP_SIZE) { UvSet.UNUSED }.also { constrainMaterial(key, it) })

    /** The key reaches C: each shading model and alpha mode picks its own ubershader, compiled once. */
    @Test
    fun keysPickTheirUbershaderAndTheCacheKeepsIt() {
        assertEquals(emptyList(), provider.materials, "nothing is compiled before it's asked for")
        val unlit = material(MaterialKey(unlit = true))
        val names = listOf(AlphaMode.OPAQUE, AlphaMode.MASK, AlphaMode.BLEND).map { material(MaterialKey(alphaMode = it))?.name }

        assertEquals("base_unlit_opaque", unlit?.name)
        assertEquals(listOf("base_lit_opaque", "base_lit_masked", "base_lit_fade"), names)
        assertEquals(unlit?.nativeObject, material(MaterialKey(unlit = true))?.nativeObject, "a second request reuses the material")
        assertEquals(4, provider.materials.size, "materials lists what's compiled, not the cache's empty slots")
        assertTrue(provider.materialsCount >= provider.materials.size)

        provider.destroyMaterials()
        assertEquals(0, provider.materialsCount)
    }

    @Test
    fun constrainMaterialMapsTheTexcoordSetsInUse() {
        val uvmap: UvMap = Array(UV_MAP_SIZE) { UvSet.UNUSED }
        constrainMaterial(MaterialKey(hasBaseColorTexture = true, baseColorUV = 0), uvmap)
        assertEquals(UvSet.UV0, uvmap[0])
        assertEquals(1, getNumUvSets(uvmap))
    }

    /** The ubershaders read UV0 unconditionally, so gltfio pads meshes without it. */
    @Test
    fun ubershadersNeedDummyTexcoordsOnly() {
        assertTrue(provider.needsDummyData(VertexBuffer.VertexAttribute.UV0))
        assertFalse(provider.needsDummyData(VertexBuffer.VertexAttribute.POSITION))
    }
}
