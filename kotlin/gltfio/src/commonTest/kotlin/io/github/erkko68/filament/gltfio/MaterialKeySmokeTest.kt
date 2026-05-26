package io.github.erkko68.filament.gltfio

import kotlin.test.Test
import kotlin.test.assertTrue

class MaterialKeySmokeTest {
    @Test
    fun verifyMaterialKeyApi() {
        val keyVerify: (MaterialKey) -> Unit = { key ->
            key.doubleSided = true
            key.unlit = false
            key.hasVertexColors = true
            key.hasBaseColorTexture = true
            key.hasNormalTexture = true
            key.hasOcclusionTexture = true
            key.hasEmissiveTexture = true
            key.useSpecularGlossiness = false
            key.alphaMode = 1
            key.enableDiagnostics = true
            key.hasMetallicRoughnessTexture = true
            key.metallicRoughnessUV = 0
            key.baseColorUV = 0
            key.hasClearCoatTexture = true
            key.clearCoatUV = 0
            key.hasClearCoatRoughnessTexture = true
            key.clearCoatRoughnessUV = 0
            key.hasClearCoatNormalTexture = true
            key.clearCoatNormalUV = 0
            key.hasClearCoat = true
            key.hasTransmission = true
            key.hasTextureTransforms = true
            key.emissiveUV = 0
            key.aoUV = 0
            key.normalUV = 0
            key.hasTransmissionTexture = true
            key.transmissionUV = 0
            key.hasSheenColorTexture = true
            key.sheenColorUV = 0
            key.hasSheenRoughnessTexture = true
            key.sheenRoughnessUV = 0
            key.hasVolumeThicknessTexture = true
            key.volumeThicknessUV = 0
            key.hasSheen = true
            key.hasIOR = true
            
            key.constrainMaterial(intArrayOf(0))
        }
        assertTrue(true, "MaterialKey API signatures resolved successfully.")
    }
}
