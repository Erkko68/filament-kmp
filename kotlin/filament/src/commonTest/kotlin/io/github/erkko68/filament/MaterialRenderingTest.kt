package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.RenderingTestFixture
import io.github.erkko68.filament.testutils.TestMaterials
import io.github.erkko68.filament.testutils.pumpUntil
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Real-backend coverage for [Material] bindings that NOOP can't build.
 * See [RenderingTestFixture] — skips when no backend is available.
 */
class MaterialRenderingTest : RenderingTestFixture() {
    @Test
    fun testMaterialLifecycleAndGetters() {
        val engine = engine ?: return
        val bytes = TestMaterials.getEmissiveMaterialBytes()
        if (bytes.isEmpty()) return

        val mat = Material.Builder()
            .payload(bytes)
            .build(engine)
        assertNotNull(mat)
        assertTrue(engine.isValid(mat))

        // Getters: just exercise the binding path and round-trip what we can.
        assertTrue(mat.name.isNotEmpty())
        assertNotNull(mat.shading)
        assertNotNull(mat.interpolation)
        assertNotNull(mat.blendingMode)
        assertNotNull(mat.transparencyMode)
        assertNotNull(mat.refractionMode)
        assertNotNull(mat.refractionType)
        assertNotNull(mat.reflectionMode)
        assertNotNull(mat.vertexDomain)
        assertNotNull(mat.cullingMode)
        assertNotNull(mat.featureLevel)
        assertTrue(mat.maskThreshold >= 0f)
        assertTrue(mat.specularAntiAliasingVariance >= 0f)
        assertTrue(mat.specularAntiAliasingThreshold >= 0f)
        assertTrue(mat.parameterCount >= 0)
        val params = mat.parameters
        assertEquals(mat.parameterCount, params.size)
        assertEquals(Material.ParameterType.FLOAT3, params.single { it.name == "color" }.type)
        params.forEach {
            assertTrue(it.name.isNotEmpty())
            // Exactly one of the union's members is set.
            assertEquals(1, listOfNotNull(it.type, it.samplerType, it.subpassType).size)
            assertEquals(it.isSampler, mat.isSampler(it.name))
        }
        assertNotNull(mat.requiredAttributes)

        mat.setDefaultParameter("intensity", 3f)
        assertContentEquals(floatArrayOf(3f), mat.defaultInstance.getParameter("intensity", MaterialInstance.FloatElement.FLOAT))
        mat.setDefaultParameter("color", 0.1f, 0.2f, 0.3f)
        assertContentEquals(floatArrayOf(0.1f, 0.2f, 0.3f), mat.defaultInstance.getParameter("color", MaterialInstance.FloatElement.FLOAT3))
        mat.setDefaultParameter("color", RgbType.LINEAR, 1f, 0f, 0f)
        assertContentEquals(floatArrayOf(1f, 0f, 0f), mat.defaultInstance.getParameter("color", MaterialInstance.FloatElement.FLOAT3))

        // Instances
        val inst1 = mat.createInstance()
        assertNotNull(inst1)
        val inst2 = mat.createInstance("named_instance")
        assertNotNull(inst2)
        val defInst = mat.defaultInstance
        assertNotNull(defInst)

        engine.destroy(inst1)
        engine.destroy(inst2)
        engine.destroy(mat)
    }

    @Test
    fun testCapabilitiesAndDefaultParameters() {
        val engine = engine ?: return
        val materialsBefore = engine.materialCount
        val mat = Material.Builder()
            .payload(TestMaterials.getParamsMaterialBytes())
            .sphericalHarmonicsBandCount(2)
            .shadowSamplingQuality(Material.Builder.ShadowSamplingQuality.LOW)
            .uboBatching(Material.UboBatchingMode.DISABLED)
            .build(engine)!!
        assertEquals(materialsBefore + 1, engine.materialCount)

        assertEquals(MaterialDomain.SURFACE, mat.materialDomain)
        assertEquals(Material.BlendingMode.MASKED, mat.blendingMode)
        assertTrue(mat.isDoubleSided)
        assertTrue(mat.hasSpecularAntiAliasing)
        assertFalse(mat.hasShadowMultiplier)
        assertTrue(mat.isColorWriteEnabled)
        assertTrue(mat.isDepthWriteEnabled)
        assertTrue(mat.isDepthCullingEnabled)
        assertTrue(mat.isAlphaToCoverageEnabled) // defaults on for MASKED
        assertEquals(0, mat.supportedVariants and UserVariantFilterBit.SKINNING) // filtered out in params.mat
        assertTrue(mat.source.isEmpty() || "Params" in mat.source)
        assertEquals("extTransform", mat.getParameterTransformName("ext"))
        assertNull(mat.getParameterTransformName("tex"))

        mat.setDefaultParameter("b1", true)
        mat.setDefaultParameter("b2", true, false)
        mat.setDefaultParameter("b3", true, false, true)
        mat.setDefaultParameter("b4", true, false, true, false)
        mat.setDefaultParameter("i1", 1)
        mat.setDefaultParameter("i2", 1, 2)
        mat.setDefaultParameter("i3", 1, 2, 3)
        mat.setDefaultParameter("i4", 1, 2, 3, 4)
        mat.setDefaultParameter("f2", 0.5f, 0.25f)
        mat.setDefaultParameter("f4", 0.1f, 0.2f, 0.3f, 0.4f)
        val defaults = mat.defaultInstance
        assertContentEquals(intArrayOf(1), defaults.getParameter("i1", MaterialInstance.IntElement.INT))
        assertContentEquals(intArrayOf(1, 2, 3), defaults.getParameter("i3", MaterialInstance.IntElement.INT3))
        assertContentEquals(intArrayOf(1, 2, 3, 4), defaults.getParameter("i4", MaterialInstance.IntElement.INT4))
        assertContentEquals(floatArrayOf(0.5f, 0.25f), defaults.getParameter("f2", MaterialInstance.FloatElement.FLOAT2))
        mat.setDefaultParameter("f4", RgbaType.LINEAR, 1f, 0f, 0f, 1f)
        assertContentEquals(floatArrayOf(1f, 0f, 0f, 1f), defaults.getParameter("f4", MaterialInstance.FloatElement.FLOAT4))

        val texture = Texture.Builder().width(1).height(1).format(Texture.InternalFormat.RGBA8).build(engine)
        mat.setDefaultParameter("tex", texture, TextureSampler())

        var compiled: Material? = null
        mat.compile(Material.CompilerPriorityQueue.HIGH, UserVariantFilterBit.ALL) { compiled = it }
        engine.pumpUntil { compiled != null }
        assertEquals(mat, compiled)

        val view = engine.createView()
        var viewCompiled: Material? = null
        engine.compile(Material.CompilerPriorityQueue.LOW, mat, view, Engine.FeatureState.FALSE, Engine.FeatureState.INDETERMINATE) {
            viewCompiled = it
        }
        engine.pumpUntil { viewCompiled != null }
        assertEquals(mat, viewCompiled)

        engine.destroy(view)
        engine.destroy(mat)
        engine.destroy(texture)
        assertEquals(materialsBefore, engine.materialCount)
    }

    @Test
    fun testBuilderSpecializationConstants() {
        val engine = engine ?: return
        val mat = Material.Builder()
            .payload(TestMaterials.getConstantsMaterialBytes())
            .constant("testBool", false)
            .constant("testInt", 9)
            .constant("testFloat", 0.75f)
            .build(engine)!!
        val inst = mat.defaultInstance
        assertEquals(false, inst.getConstantBoolean("testBool"))
        assertEquals(9, inst.getConstantInt("testInt"))
        assertEquals(0.75f, inst.getConstantFloat("testFloat"))
        engine.destroy(mat)
    }
}
