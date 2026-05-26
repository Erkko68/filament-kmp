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
        val d = Material.UserVariantFilterBit.DIRECTIONAL_LIGHTING
        val dy = Material.UserVariantFilterBit.DYNAMIC_LIGHTING
        val sh = Material.UserVariantFilterBit.SHADOW_RECEIVER
        val sk = Material.UserVariantFilterBit.SKINNING
        val fg = Material.UserVariantFilterBit.FOG
        val vsm = Material.UserVariantFilterBit.VSM
        val ssr = Material.UserVariantFilterBit.SSR
        val ste = Material.UserVariantFilterBit.STE
        val all = Material.UserVariantFilterBit.ALL

        assertTrue(all != 0)
    }

    @Test
    fun testParameterClass() {
        val param = Material.Parameter("test", Material.Parameter.Type.FLOAT, Material.Parameter.Precision.HIGH, 1)
        assertEquals("test", param.name)
        assertEquals(Material.Parameter.Type.FLOAT, param.type)
        assertEquals(Material.Parameter.Precision.HIGH, param.precision)
        assertEquals(1, param.count)
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
        // assertTrue(mat.getName().isNotEmpty())
        // assertNotNull(mat.getShading())
        // assertNotNull(mat.getInterpolation())
        // assertNotNull(mat.getBlendingMode())
        // assertNotNull(mat.getTransparencyMode())
        // assertNotNull(mat.getRefractionMode())
        // assertNotNull(mat.getRefractionType())
        // assertNotNull(mat.getReflectionMode())
        // assertNotNull(mat.getVertexDomain())
        // assertNotNull(mat.getCullingMode())

        // assertTrue(mat.isColorWriteEnabled())
        // assertTrue(mat.isDepthWriteEnabled())
        // assertTrue(mat.isDepthCullingEnabled())
        // assertNotNull(mat.isDoubleSided())
        // assertNotNull(mat.isAlphaToCoverageEnabled())

        // assertTrue(mat.getMaskThreshold() >= 0f)
        // assertTrue(mat.getSpecularAntiAliasingVariance() >= 0f)
        // assertTrue(mat.getSpecularAntiAliasingThreshold() >= 0f)
        // assertNotNull(mat.getFeatureLevel())

        // assertTrue(mat.getParameterCount() >= 0)
        // assertNotNull(mat.getParameters())
        // assertNotNull(mat.getRequiredAttributes())

        // // Test instance creation
        // val inst1 = mat.createInstance()
        // assertNotNull(inst1)

        // val inst2 = mat.createInstance("named_instance")
        // assertNotNull(inst2)

        // val defInst = mat.getDefaultInstance()
        // assertNotNull(defInst)

        // // Clean up
        // engine.destroyMaterial(mat)
    }
}
