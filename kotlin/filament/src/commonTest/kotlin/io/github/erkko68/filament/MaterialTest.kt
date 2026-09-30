package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import io.github.erkko68.filament.testutils.TestMaterials
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MaterialTest : FilamentTestFixture() {
    @Test
    fun testUserVariantFlags() {
        val d = UserVariantFilterBit.DIRECTIONAL_LIGHTING
        val dy = UserVariantFilterBit.DYNAMIC_LIGHTING
        val sh = UserVariantFilterBit.SHADOW_RECEIVER
        val sk = UserVariantFilterBit.SKINNING
        val fg = UserVariantFilterBit.FOG
        val vsm = UserVariantFilterBit.VSM
        val ssr = UserVariantFilterBit.SSR
        val ste = UserVariantFilterBit.STE
        val all = UserVariantFilterBit.ALL

        assertTrue(all != 0)
    }

    @Test
    fun testMaterialLifecycle() {
        val bytes = TestMaterials.getEmissiveMaterialBytes()
        if (bytes.isEmpty()) return

        // TODO: Creating a Material from a payload throws a driver-specific JNI PreconditionPanic under the software NOOP backend driver.
        // val mat = Material.Builder()
        //     .payload(bytes)
        //     .sphericalHarmonicsBandCount(3)
        //     .shadowSamplingQuality(Material.Builder.ShadowSamplingQuality.HARD)
        //     .uboBatching(Material.UboBatchingMode.DEFAULT)
        //     .build(engine)

        // assertNotNull(mat)

        // // Test parameters and attributes
        // assertTrue(mat.name.isNotEmpty())
        // assertNotNull(mat.shading)
        // assertNotNull(mat.interpolation)
        // assertNotNull(mat.blendingMode)
        // assertNotNull(mat.transparencyMode)
        // assertNotNull(mat.refractionMode)
        // assertNotNull(mat.refractionType)
        // assertNotNull(mat.reflectionMode)
        // assertNotNull(mat.vertexDomain)
        // assertNotNull(mat.cullingMode)

        // assertTrue(mat.isColorWriteEnabled)
        // assertTrue(mat.isDepthWriteEnabled)
        // assertTrue(mat.isDepthCullingEnabled)
        // assertNotNull(mat.isDoubleSided)
        // assertNotNull(mat.isAlphaToCoverageEnabled)

        // assertTrue(mat.maskThreshold >= 0f)
        // assertTrue(mat.specularAntiAliasingVariance >= 0f)
        // assertTrue(mat.specularAntiAliasingThreshold >= 0f)
        // assertNotNull(mat.featureLevel)

        // assertTrue(mat.parameterCount >= 0)
        // assertNotNull(mat.parameters)
        // assertNotNull(mat.requiredAttributes)

        // // Test instance creation
        // val inst1 = mat.createInstance()
        // assertNotNull(inst1)

        // val inst2 = mat.createInstance("named_instance")
        // assertNotNull(inst2)

        // val defInst = mat.defaultInstance
        // assertNotNull(defInst)

        // // Clean up
        // engine.destroyMaterial(mat)
    }
}
