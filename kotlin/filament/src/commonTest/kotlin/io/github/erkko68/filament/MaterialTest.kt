package io.github.erkko68.filament

import io.github.erkko68.filament.interop.NullPointer
import io.github.erkko68.filament.testutils.FilamentTestFixture
import io.github.erkko68.filament.testutils.TestMaterials
import io.github.erkko68.filament.testsupport.pumpUntil
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MaterialTest : FilamentTestFixture() {
    @Test
    fun testUserVariantFlagsMakeUpAll() {
        val bits = with(UserVariantFilterBit) {
            listOf(DIRECTIONAL_LIGHTING, DYNAMIC_LIGHTING, SHADOW_RECEIVER, SKINNING, FOG, VSM, SSR, STE)
        }
        assertEquals(bits.size, bits.distinct().size)
        assertEquals(UserVariantFilterBit.ALL, bits.fold(0) { acc, b -> acc or b })
    }

    // Filament's parser panics on these; the builder must turn them away first.
    @Test
    fun testInvalidPayloadsBuildNothing() {
        assertNull(Material.Builder().payload("not a material package".encodeToByteArray()).build(engine))
        assertNull(Material.Builder().payload("SREV".encodeToByteArray()).build(engine))
    }

    @Test
    fun testMaterialLifecycleAndGetters() {
        val bytes = TestMaterials.getEmissiveMaterialBytes()
        val mat = Material.Builder()
            .payload(bytes)
            .build(engine)!!
        assertTrue(engine.isValid(mat))

        // emissive.mat declares only its name, unlit shading and parameters: the rest are Filament's defaults.
        assertEquals("Emissive", mat.name)
        assertEquals(Material.Shading.UNLIT, mat.shading)
        assertEquals(Material.Interpolation.SMOOTH, mat.interpolation)
        assertEquals(Material.BlendingMode.OPAQUE, mat.blendingMode)
        assertEquals(Material.TransparencyMode.DEFAULT, mat.transparencyMode)
        assertEquals(RefractionMode.NONE, mat.refractionMode)
        assertEquals(RefractionType.SOLID, mat.refractionType)
        assertEquals(ReflectionMode.DEFAULT, mat.reflectionMode)
        assertEquals(Material.VertexDomain.OBJECT, mat.vertexDomain)
        assertEquals(Material.CullingMode.BACK, mat.cullingMode)
        assertEquals(Engine.FeatureLevel.FEATURE_LEVEL_1, mat.featureLevel)
        assertEquals(0.4f, mat.maskThreshold, 1e-6f)
        assertEquals(4, mat.parameterCount)
        val params = mat.parameters
        assertEquals(mat.parameterCount, params.size)
        assertEquals(Material.ParameterType.FLOAT3, params.single { it.name == "color" }.type)
        params.forEach {
            assertTrue(it.name.isNotEmpty())
            // Exactly one of the union's members is set.
            assertEquals(1, listOfNotNull(it.type, it.samplerType, it.subpassType).size)
            assertEquals(it.isSampler, mat.isSampler(it.name))
        }
        assertTrue(VertexBuffer.VertexAttribute.POSITION in mat.requiredAttributes)

        mat.setDefaultParameter("intensity", 3f)
        assertContentEquals(floatArrayOf(3f), mat.defaultInstance.getParameter("intensity", MaterialInstance.FloatElement.FLOAT))
        mat.setDefaultParameter("color", 0.1f, 0.2f, 0.3f)
        assertContentEquals(floatArrayOf(0.1f, 0.2f, 0.3f), mat.defaultInstance.getParameter("color", MaterialInstance.FloatElement.FLOAT3))
        mat.setDefaultParameter("color", RgbType.LINEAR, 1f, 0f, 0f)
        assertContentEquals(floatArrayOf(1f, 0f, 0f), mat.defaultInstance.getParameter("color", MaterialInstance.FloatElement.FLOAT3))

        // Instances
        val inst1 = mat.createInstance()
        assertEquals(mat.nativeObject, inst1.material.nativeObject)
        val inst2 = mat.createInstance("named_instance")
        assertEquals("named_instance", inst2.name)
        assertEquals(mat.nativeObject, mat.defaultInstance.material.nativeObject)

        engine.destroy(inst1)
        engine.destroy(inst2)
        engine.destroy(mat)
    }

    @Test
    fun testCapabilitiesAndDefaultParameters() {
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
        assertTrue(mat.hasParameter("tex"))
        assertFalse(mat.hasParameter("missing"))
        val sampler = mat.parameters.single { it.name == "tex" }
        assertTrue(sampler.isSampler)
        assertNull(sampler.type)
        assertEquals(Material.SamplerType.SAMPLER_2D, sampler.samplerType)

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

    // Without a callback there's nothing to wait on: a later compile with one must still come through.
    @Test
    fun testCompileWithoutCallback() {
        val mat = Material.Builder().payload(TestMaterials.getParamsMaterialBytes()).build(engine)!!
        val inst = mat.createInstance()
        assertFalse(NullPointer in listOf(mat.nativeObject, inst.nativeObject))
        assertNotEquals(mat.nativeObject, inst.nativeObject)
        val view = engine.createView()
        mat.compile(Material.CompilerPriorityQueue.HIGH)
        inst.compile(Material.CompilerPriorityQueue.HIGH)
        engine.compile(Material.CompilerPriorityQueue.HIGH, mat, view, Engine.FeatureState.FALSE, Engine.FeatureState.FALSE)
        var compiled: Material? = null
        mat.compile(Material.CompilerPriorityQueue.LOW) { compiled = it }
        engine.pumpUntil { compiled != null }
        assertEquals(mat, compiled)
        engine.destroy(view)
        engine.destroy(inst)
        engine.destroy(mat)
    }
}
