package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.RenderingTestFixture
import io.github.erkko68.filament.testutils.TestMaterials
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
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

        if (mat.hasParameter("emissiveFactor")) {
            inst.setParameter("emissiveFactor", 1f, 1f, 1f)
            inst.setParameter("emissiveFactor", RgbType.sRGB, 1f, 1f, 1f)
        }

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

        // Omitted: maskThreshold (needs MASKED blending), specularAntiAliasing*,
        // isDoubleSided — these panic unless the material was *built* with the
        // matching capability, which the emissive test material isn't.
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
}
