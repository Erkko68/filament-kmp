package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.RenderingTestFixture
import io.github.erkko68.filament.testutils.TestMaterials
import kotlin.test.Test
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

        val mat = Material.Builder().payload(bytes).build(engine)
        val inst = mat.createInstance()
        assertNotNull(inst)
        assertEquals(mat.getName(), inst.material.getName())
        assertNotNull(inst.name)

        if (mat.hasParameter("emissiveFactor")) {
            inst.setParameter("emissiveFactor", 1f, 1f, 1f)
            inst.setParameter("emissiveFactor", Colors.RgbType.SRGB, 1f, 1f, 1f)
        }

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
        inst.depthFunc = TextureSampler.CompareFunction.GREATER
        assertEquals(TextureSampler.CompareFunction.GREATER, inst.depthFunc)

        inst.setStencilCompareFunction(TextureSampler.CompareFunction.ALWAYS, MaterialInstance.StencilFace.FRONT)
        inst.setStencilCompareFunction(TextureSampler.CompareFunction.ALWAYS)
        inst.setStencilOpStencilFail(MaterialInstance.StencilOperation.DECR_CLAMP, MaterialInstance.StencilFace.FRONT)
        inst.setStencilOpStencilFail(MaterialInstance.StencilOperation.DECR_CLAMP)
        inst.setStencilOpDepthFail(MaterialInstance.StencilOperation.INCR_CLAMP, MaterialInstance.StencilFace.FRONT)
        inst.setStencilOpDepthFail(MaterialInstance.StencilOperation.INCR_CLAMP)
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

        engine.destroyMaterialInstance(inst)
        engine.destroyMaterialInstance(dup)
        engine.destroyMaterial(mat)
    }
}
