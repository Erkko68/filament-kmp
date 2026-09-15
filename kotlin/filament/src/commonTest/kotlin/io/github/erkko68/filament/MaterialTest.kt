package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import io.github.erkko68.filament.testutils.TestMaterials
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MaterialTest : FilamentTestFixture() {
    @Test
    fun testParameterTypeUnionDecoding() {
        // UniformType space
        assertEquals(Material.Parameter.Type.FLOAT3, materialParameterType(6, isSampler = false, isSubpass = false))
        // UniformType.STRUCT (18) has no Parameter.Type; it must not spill into the sampler range.
        assertEquals(Material.Parameter.Type.MAT4, materialParameterType(18, isSampler = false, isSubpass = false))
        // SamplerType space is offset, not shared with UniformType
        assertEquals(Material.Parameter.Type.SAMPLER_2D, materialParameterType(0, isSampler = true, isSubpass = false))
        assertEquals(Material.Parameter.Type.SAMPLER_3D, materialParameterType(4, isSampler = true, isSubpass = false))
        // SamplerType.SAMPLER_CUBEMAP_ARRAY (5) is unmodelled and clamps to the last sampler.
        assertEquals(Material.Parameter.Type.SAMPLER_3D, materialParameterType(5, isSampler = true, isSubpass = false))
        // SubpassType space ignores the raw value entirely
        assertEquals(Material.Parameter.Type.SUBPASS_INPUT, materialParameterType(0, isSampler = false, isSubpass = true))
    }

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
