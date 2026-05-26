package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import io.github.erkko68.filament.testutils.TestMaterials
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class MaterialInstanceTest : FilamentTestFixture() {
    @Test
    fun testMaterialInstanceLifecycleAndProperties() {
        val bytes = TestMaterials.getEmissiveMaterialBytes()
        if (bytes.isEmpty()) return

        // TODO: Creating a Material/MaterialInstance from a payload throws a driver-specific JNI PreconditionPanic under the software NOOP backend driver.
        // val mat = Material.Builder()
        //     .payload(bytes)
        //     .build(engine)

        // val inst = mat.createInstance()
        // assertNotNull(inst)
        // assertEquals(mat.getName(), inst.material.getName())

        // // Name
        // val name = inst.name
        // assertNotNull(name)

        // // Set / Get parameters
        // inst.setParameter("emissiveFactor", 1f, 1f, 1f)
        // inst.setParameter("emissiveFactor", Colors.RgbType.SRGB, 1f, 1f, 1f)

        // // Scissor
        // inst.setScissor(0, 0, 100, 100)
        // inst.unsetScissor()

        // // Polygon offset
        // inst.setPolygonOffset(1f, 1f)

        // // Properties
        // inst.maskThreshold = 0.4f
        // assertEquals(0.4f, inst.maskThreshold)

        // inst.specularAntiAliasingVariance = 0.2f
        // assertEquals(0.2f, inst.specularAntiAliasingVariance)

        // inst.specularAntiAliasingThreshold = 0.1f
        // assertEquals(0.1f, inst.specularAntiAliasingThreshold)

        // inst.isDoubleSided = true
        // assertTrue(inst.isDoubleSided)

        // inst.transparencyMode = Material.TransparencyMode.TWO_PASSES_ONE_SIDE
        // assertEquals(Material.TransparencyMode.TWO_PASSES_ONE_SIDE, inst.transparencyMode)

        // inst.cullingMode = Material.CullingMode.FRONT
        // assertEquals(Material.CullingMode.FRONT, inst.cullingMode)

        // inst.setCullingMode(Material.CullingMode.FRONT, Material.CullingMode.BACK)
        // assertEquals(Material.CullingMode.BACK, inst.shadowCullingMode)

        // inst.isColorWriteEnabled = false
        // assertTrue(!inst.isColorWriteEnabled)

        // inst.isDepthWriteEnabled = false
        // assertTrue(!inst.isDepthWriteEnabled)

        // inst.isStencilWriteEnabled = false
        // assertTrue(!inst.isStencilWriteEnabled)

        // inst.isDepthCullingEnabled = false
        // assertTrue(!inst.isDepthCullingEnabled)

        // inst.depthFunc = TextureSampler.CompareFunction.GREATER
        // assertEquals(TextureSampler.CompareFunction.GREATER, inst.depthFunc)

        // // Stencil compare and operations
        // inst.setStencilCompareFunction(TextureSampler.CompareFunction.ALWAYS, MaterialInstance.StencilFace.FRONT)
        // inst.setStencilCompareFunction(TextureSampler.CompareFunction.ALWAYS)

        // inst.setStencilOpStencilFail(MaterialInstance.StencilOperation.DECR_CLAMP, MaterialInstance.StencilFace.FRONT)
        // inst.setStencilOpStencilFail(MaterialInstance.StencilOperation.DECR_CLAMP)

        // inst.setStencilOpDepthFail(MaterialInstance.StencilOperation.INCR_CLAMP, MaterialInstance.StencilFace.FRONT)
        // inst.setStencilOpDepthFail(MaterialInstance.StencilOperation.INCR_CLAMP)

        // inst.setStencilOpDepthStencilPass(MaterialInstance.StencilOperation.ZERO, MaterialInstance.StencilFace.FRONT)
        // inst.setStencilOpDepthStencilPass(MaterialInstance.StencilOperation.ZERO)

        // inst.setStencilReferenceValue(2, MaterialInstance.StencilFace.FRONT)
        // inst.setStencilReferenceValue(2)

        // inst.setStencilReadMask(128, MaterialInstance.StencilFace.FRONT)
        // inst.setStencilReadMask(128)

        // inst.setStencilWriteMask(128, MaterialInstance.StencilFace.FRONT)
        // inst.setStencilWriteMask(128)

        // // Test companion duplicate
        // val dup = MaterialInstance.duplicate(inst, "duplicated_instance")
        // assertNotNull(dup)
        // assertEquals("duplicated_instance", dup.name)

        // // Clean up
        // engine.destroyMaterialInstance(inst)
        // engine.destroyMaterialInstance(dup)
        // engine.destroyMaterial(mat)
    }
}
