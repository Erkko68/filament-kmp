package io.github.erkko68.filament.gltfio

import io.github.erkko68.filament.gltfio.testutils.GltfioTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MaterialKeyTest : GltfioTestFixture() {
    @Test
    fun testBooleanPropertiesRoundTrip() {
        val key = MaterialKey()

        key.doubleSided = true;  assertTrue(key.doubleSided)
        key.doubleSided = false; assertFalse(key.doubleSided)

        key.unlit = true;  assertTrue(key.unlit)
        key.unlit = false; assertFalse(key.unlit)

        key.hasVertexColors = true;  assertTrue(key.hasVertexColors)
        key.hasVertexColors = false; assertFalse(key.hasVertexColors)

        key.hasBaseColorTexture = true;  assertTrue(key.hasBaseColorTexture)
        key.hasBaseColorTexture = false; assertFalse(key.hasBaseColorTexture)

        key.hasNormalTexture = true;  assertTrue(key.hasNormalTexture)
        key.hasNormalTexture = false; assertFalse(key.hasNormalTexture)

        key.hasOcclusionTexture = true;  assertTrue(key.hasOcclusionTexture)
        key.hasOcclusionTexture = false; assertFalse(key.hasOcclusionTexture)

        key.hasEmissiveTexture = true;  assertTrue(key.hasEmissiveTexture)
        key.hasEmissiveTexture = false; assertFalse(key.hasEmissiveTexture)

        key.useSpecularGlossiness = true;  assertTrue(key.useSpecularGlossiness)
        key.useSpecularGlossiness = false; assertFalse(key.useSpecularGlossiness)

        key.enableDiagnostics = true;  assertTrue(key.enableDiagnostics)
        key.enableDiagnostics = false; assertFalse(key.enableDiagnostics)

        key.hasMetallicRoughnessTexture = true;  assertTrue(key.hasMetallicRoughnessTexture)
        key.hasMetallicRoughnessTexture = false; assertFalse(key.hasMetallicRoughnessTexture)

        key.hasClearCoatTexture = true;  assertTrue(key.hasClearCoatTexture)
        key.hasClearCoatTexture = false; assertFalse(key.hasClearCoatTexture)

        key.hasClearCoatRoughnessTexture = true;  assertTrue(key.hasClearCoatRoughnessTexture)
        key.hasClearCoatRoughnessTexture = false; assertFalse(key.hasClearCoatRoughnessTexture)

        key.hasClearCoatNormalTexture = true;  assertTrue(key.hasClearCoatNormalTexture)
        key.hasClearCoatNormalTexture = false; assertFalse(key.hasClearCoatNormalTexture)

        key.hasClearCoat = true;  assertTrue(key.hasClearCoat)
        key.hasClearCoat = false; assertFalse(key.hasClearCoat)

        key.hasTransmission = true;  assertTrue(key.hasTransmission)
        key.hasTransmission = false; assertFalse(key.hasTransmission)

        key.hasTextureTransforms = true;  assertTrue(key.hasTextureTransforms)
        key.hasTextureTransforms = false; assertFalse(key.hasTextureTransforms)

        key.hasTransmissionTexture = true;  assertTrue(key.hasTransmissionTexture)
        key.hasTransmissionTexture = false; assertFalse(key.hasTransmissionTexture)

        key.hasSheenColorTexture = true;  assertTrue(key.hasSheenColorTexture)
        key.hasSheenColorTexture = false; assertFalse(key.hasSheenColorTexture)

        key.hasSheenRoughnessTexture = true;  assertTrue(key.hasSheenRoughnessTexture)
        key.hasSheenRoughnessTexture = false; assertFalse(key.hasSheenRoughnessTexture)

        key.hasVolumeThicknessTexture = true;  assertTrue(key.hasVolumeThicknessTexture)
        key.hasVolumeThicknessTexture = false; assertFalse(key.hasVolumeThicknessTexture)

        key.hasSheen = true;  assertTrue(key.hasSheen)
        key.hasSheen = false; assertFalse(key.hasSheen)

        key.hasIOR = true;  assertTrue(key.hasIOR)
        key.hasIOR = false; assertFalse(key.hasIOR)
    }

    @Test
    fun testIntPropertiesRoundTrip() {
        val key = MaterialKey()

        key.alphaMode = 2; assertEquals(2, key.alphaMode)
        key.alphaMode = 0; assertEquals(0, key.alphaMode)

        key.metallicRoughnessUV = 1; assertEquals(1, key.metallicRoughnessUV)
        key.baseColorUV = 1;         assertEquals(1, key.baseColorUV)
        key.clearCoatUV = 1;         assertEquals(1, key.clearCoatUV)
        key.clearCoatRoughnessUV = 1; assertEquals(1, key.clearCoatRoughnessUV)
        key.clearCoatNormalUV = 1;   assertEquals(1, key.clearCoatNormalUV)
        key.emissiveUV = 1;          assertEquals(1, key.emissiveUV)
        key.aoUV = 1;                assertEquals(1, key.aoUV)
        key.normalUV = 1;            assertEquals(1, key.normalUV)
        key.transmissionUV = 1;      assertEquals(1, key.transmissionUV)
        key.sheenColorUV = 1;        assertEquals(1, key.sheenColorUV)
        key.sheenRoughnessUV = 1;    assertEquals(1, key.sheenRoughnessUV)
        key.volumeThicknessUV = 1;   assertEquals(1, key.volumeThicknessUV)
    }

    @Test
    fun testConstrainMaterial() {
        val key = MaterialKey()
        key.hasBaseColorTexture = true
        key.baseColorUV = 0
        val uvmap = IntArray(8)
        key.constrainMaterial(uvmap)
    }
}
