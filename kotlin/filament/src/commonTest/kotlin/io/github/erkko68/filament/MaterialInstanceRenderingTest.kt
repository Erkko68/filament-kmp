package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.RenderingTestFixture
import io.github.erkko68.filament.testutils.TestMaterials
import io.github.erkko68.filament.testutils.pumpUntil
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/** Real-backend coverage for [MaterialInstance] bindings. See [RenderingTestFixture]. */
class MaterialInstanceRenderingTest : RenderingTestFixture() {
    @Test
    fun testMaterialInstanceLifecycleAndProperties() {
        val engine = engine ?: return
        val bytes = TestMaterials.getEmissiveMaterialBytes()
        if (bytes.isEmpty()) return

        val mat = Material.Builder().payload(bytes).build(engine)!!
        val inst = mat.createInstance()
        assertNotNull(inst)
        assertEquals(mat.name, inst.material.name)
        assertNotNull(inst.name)

        inst.setParameter("color", RgbType.LINEAR, 0.5f, 0.25f, 1f)
        assertContentEquals(floatArrayOf(0.5f, 0.25f, 1f), inst.getParameter("color", MaterialInstance.FloatElement.FLOAT3))
        inst.setParameter("color", 0.25f, 0.5f, 0.75f)
        assertContentEquals(floatArrayOf(0.25f, 0.5f, 0.75f), inst.getParameter("color", MaterialInstance.FloatElement.FLOAT3))
        inst.setParameter("intensity", 2f)
        assertContentEquals(floatArrayOf(2f), inst.getParameter("intensity", MaterialInstance.FloatElement.FLOAT))
        inst.setParameter("flags", 0xFFFFFFFFu)
        assertContentEquals(intArrayOf(-1), inst.getParameter("flags", MaterialInstance.UIntElement.UINT))
        inst.setParameter("ids", 1u, 2u, 0x80000000u)
        assertContentEquals(intArrayOf(1, 2, Int.MIN_VALUE), inst.getParameter("ids", MaterialInstance.UIntElement.UINT3))

        inst.setScissor(0, 0, 100, 100)
        inst.unsetScissor()
        inst.setPolygonOffset(1f, 1f)

        // maskThreshold, specularAntiAliasing* and isDoubleSided panic unless the material has the
        // capability; testCapabilityPropertiesAndParameterOverloads covers them on params.mat.
        inst.transparencyMode = Material.TransparencyMode.TWO_PASSES_ONE_SIDE
        assertEquals(Material.TransparencyMode.TWO_PASSES_ONE_SIDE, inst.transparencyMode)

        inst.cullingMode = Material.CullingMode.FRONT
        assertEquals(Material.CullingMode.FRONT, inst.cullingMode)
        inst.setCullingMode(Material.CullingMode.FRONT, Material.CullingMode.BACK)
        assertEquals(Material.CullingMode.BACK, inst.shadowCullingMode)

        inst.isColorWriteEnabled = false
        assertTrue(!inst.isColorWriteEnabled)
        inst.isDepthWriteEnabled = false
        assertTrue(!inst.isDepthWriteEnabled)
        inst.isStencilWriteEnabled = false
        assertTrue(!inst.isStencilWriteEnabled)
        inst.isDepthCullingEnabled = false
        assertTrue(!inst.isDepthCullingEnabled)
        inst.depthFunc = TextureSampler.CompareFunc.G
        assertEquals(TextureSampler.CompareFunc.G, inst.depthFunc)

        inst.setStencilCompareFunction(TextureSampler.CompareFunc.A, MaterialInstance.StencilFace.FRONT)
        inst.setStencilCompareFunction(TextureSampler.CompareFunc.A)
        inst.setStencilOpStencilFail(MaterialInstance.StencilOperation.DECR, MaterialInstance.StencilFace.FRONT)
        inst.setStencilOpStencilFail(MaterialInstance.StencilOperation.DECR)
        inst.setStencilOpDepthFail(MaterialInstance.StencilOperation.INCR, MaterialInstance.StencilFace.FRONT)
        inst.setStencilOpDepthFail(MaterialInstance.StencilOperation.INCR)
        inst.setStencilOpDepthStencilPass(MaterialInstance.StencilOperation.ZERO, MaterialInstance.StencilFace.FRONT)
        inst.setStencilOpDepthStencilPass(MaterialInstance.StencilOperation.ZERO)
        inst.setStencilReferenceValue(2, MaterialInstance.StencilFace.FRONT)
        inst.setStencilReferenceValue(2)
        inst.setStencilReadMask(128, MaterialInstance.StencilFace.FRONT)
        inst.setStencilReadMask(128)
        inst.setStencilWriteMask(128, MaterialInstance.StencilFace.FRONT)
        inst.setStencilWriteMask(128)

        val dup = MaterialInstance.duplicate(inst, "duplicated_instance")
        assertNotNull(dup)
        assertEquals("duplicated_instance", dup.name)
        engine.destroy(dup)

        engine.destroy(inst)
        engine.destroy(mat)
    }

    @Test
    fun testGetSpecializationConstants() {
        val engine = engine ?: return
        val bytes = TestMaterials.getConstantsMaterialBytes()
        if (bytes.isEmpty()) return

        val mat = Material.Builder().payload(bytes).build(engine)!!
        val inst = mat.createInstance()

        assertEquals(true, inst.getConstantBoolean("testBool"))
        assertEquals(7, inst.getConstantInt("testInt"))
        assertEquals(0.5f, inst.getConstantFloat("testFloat"))

        // No compile or draw after setConstant: destroying such an instance aborts upstream.
        inst.setConstant("testBool", false)
        inst.setConstant("testInt", 3)
        inst.setConstant("testFloat", 0.25f)
        assertEquals(false, inst.getConstantBoolean("testBool"))
        assertEquals(3, inst.getConstantInt("testInt"))
        assertEquals(0.25f, inst.getConstantFloat("testFloat"))

        engine.destroy(inst)
        engine.destroy(mat)
    }

    @Test
    fun testCapabilityPropertiesAndParameterOverloads() {
        val engine = engine ?: return
        val mat = Material.Builder().payload(TestMaterials.getParamsMaterialBytes()).build(engine)!!
        val inst = mat.createInstance()
        assertTrue(engine.isValid(mat, inst))
        assertTrue(engine.isValidExpensive(inst))

        inst.maskThreshold = 0.25f
        assertEquals(0.25f, inst.maskThreshold)
        inst.specularAntiAliasingVariance = 0.5f
        assertEquals(0.5f, inst.specularAntiAliasingVariance)
        inst.specularAntiAliasingThreshold = 0.125f
        assertEquals(0.125f, inst.specularAntiAliasingThreshold)
        assertTrue(inst.isDoubleSided)
        inst.isDoubleSided = false
        assertFalse(inst.isDoubleSided)

        inst.setParameter("b1", true)
        inst.setParameter("b2", true, false)
        inst.setParameter("b3", true, false, true)
        inst.setParameter("b4", true, false, true, false)
        inst.setParameter("i1", -1)
        assertContentEquals(intArrayOf(-1), inst.getParameter("i1", MaterialInstance.IntElement.INT))
        inst.setParameter("i2", 1, -2)
        assertContentEquals(intArrayOf(1, -2), inst.getParameter("i2", MaterialInstance.IntElement.INT2))
        inst.setParameter("i3", 1, 2, 3)
        inst.setParameter("i4", 1, 2, 3, 4)
        assertContentEquals(intArrayOf(1, 2, 3, 4), inst.getParameter("i4", MaterialInstance.IntElement.INT4))
        inst.setParameter("u2", 1u, 0x80000000u)
        assertContentEquals(intArrayOf(1, Int.MIN_VALUE), inst.getParameter("u2", MaterialInstance.UIntElement.UINT2))
        inst.setParameter("u4", 1u, 2u, 3u, 0xFFFFFFFFu)
        assertContentEquals(intArrayOf(1, 2, 3, -1), inst.getParameter("u4", MaterialInstance.UIntElement.UINT4))
        inst.setParameter("f2", 0.5f, 0.25f)
        assertContentEquals(floatArrayOf(0.5f, 0.25f), inst.getParameter("f2", MaterialInstance.FloatElement.FLOAT2))
        inst.setParameter("f4", 0.1f, 0.2f, 0.3f, 0.4f)
        assertContentEquals(floatArrayOf(0.1f, 0.2f, 0.3f, 0.4f), inst.getParameter("f4", MaterialInstance.FloatElement.FLOAT4))
        inst.setParameter("f4", RgbaType.LINEAR, 0f, 1f, 0f, 1f)
        assertContentEquals(floatArrayOf(0f, 1f, 0f, 1f), inst.getParameter("f4", MaterialInstance.FloatElement.FLOAT4))
        // Array forms, from an offset.
        inst.setParameter("i3", MaterialInstance.IntElement.INT3, intArrayOf(9, 7, 8, 9), 1, 1)
        assertContentEquals(intArrayOf(7, 8, 9), inst.getParameter("i3", MaterialInstance.IntElement.INT3))
        inst.setParameter("b2", MaterialInstance.BooleanElement.BOOL2, booleanArrayOf(false, true), 0, 1)

        val texture = Texture.Builder().width(1).height(1).format(Texture.InternalFormat.RGBA8).build(engine)
        inst.setParameter("tex", texture, TextureSampler())
        inst.commit(engine)

        var compiled: MaterialInstance? = null
        inst.compile(Material.CompilerPriorityQueue.CRITICAL) { compiled = it }
        engine.pumpUntil { compiled != null }
        assertEquals(inst, compiled)

        engine.destroy(inst)
        engine.destroy(mat)
        engine.destroy(texture)
    }
}
