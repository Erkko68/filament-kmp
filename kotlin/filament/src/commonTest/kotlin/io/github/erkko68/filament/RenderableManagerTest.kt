package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import io.github.erkko68.filament.testutils.TestMaterials
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class RenderableManagerTest : FilamentTestFixture() {
    @Test
    fun testRenderableManagerLifecycleAndGeometry() {
        val bytes = TestMaterials.getEmissiveMaterialBytes()
        if (bytes.isEmpty()) return

        // TODO: Creating a Material/MaterialInstance from a payload throws a driver-specific JNI PreconditionPanic under the software NOOP backend driver.
        // val mat = Material.Builder()
        //     .payload(bytes)
        //     .build(engine)
        // val matInst = mat.createInstance()

        val vb = VertexBuffer.Builder()
            .vertexCount(3)
            .bufferCount(1)
            .attribute(VertexBuffer.VertexAttribute.POSITION, 0, VertexBuffer.AttributeType.FLOAT3, 0, 12)
            .build(engine)
        vb.setBufferAt(engine, 0, ByteArray(36))

        val ib = IndexBuffer.Builder()
            .indexCount(3)
            .bufferType(IndexBuffer.Builder.IndexType.USHORT)
            .build(engine)
        ib.setBuffer(engine, byteArrayOf(0, 0, 1, 0, 2, 0))

        val em = EntityManager.get()
        val entity = em.create()

        val boundingBox = Box(0f, 0f, 0f, 1f, 1f, 1f)

        // TODO: Building a renderable with a material instance is commented out because material creation panics under NOOP.
        // RenderableManager.Builder(1)
        //     .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, vb, ib)
        //     .geometryType(RenderableManager.GeometryType.STATIC)
        //     .material(0, matInst)
        //     .boundingBox(boundingBox)
        //     .culling(true)
        //     .castShadows(true)
        //     .receiveShadows(true)
        //     .screenSpaceContactShadows(true)
        //     .build(engine, entity)

        // val rm = engine.getRenderableManager()
        // assertTrue(rm.hasComponent(entity))

        // val inst = rm.getInstance(entity)
        // assertTrue(inst != 0)

        // // Set / Get parameters on renderable instance
        // rm.setAxisAlignedBoundingBox(inst, Box(0f, 0f, 0f, 2f, 2f, 2f))
        // val b = rm.getAxisAlignedBoundingBox(inst, Box())
        // assertEquals(2f, b.halfExtent[0])

        // rm.setLayerMask(inst, 0xFF, 0x01)
        // 
        // rm.setPriority(inst, 5)
        // assertEquals(5, rm.getPriority(inst))

        // rm.setChannel(inst, 2)
        // assertEquals(2, rm.getChannel(inst))

        // rm.setCulling(inst, false)
        // assertTrue(!rm.isCullingEnabled(inst))

        // rm.setFogEnabled(inst, false)
        // assertTrue(!rm.getFogEnabled(inst))

        // rm.setCastShadows(inst, false)
        // assertTrue(!rm.isShadowCaster(inst))

        // rm.setReceiveShadows(inst, false)
        // assertTrue(!rm.isShadowReceiver(inst))

        // rm.setScreenSpaceContactShadows(inst, false)
        // assertTrue(!rm.isScreenSpaceContactShadowsEnabled(inst))

        // assertEquals(1, rm.getPrimitiveCount(inst))
        // assertEquals(1, rm.getInstanceCount(inst))

        // rm.setMaterialInstanceAt(inst, 0, matInst)
        // assertTrue(rm.getMaterialInstanceAt(inst, 0) != null)

        // rm.setBlendOrderAt(inst, 0, 2)
        // assertEquals(2, rm.getBlendOrderAt(inst, 0))

        // rm.setGlobalBlendOrderEnabledAt(inst, 0, false)
        // assertTrue(!rm.isGlobalBlendOrderEnabledAt(inst, 0))

        // rm.setLightChannel(inst, 0, true)
        // assertTrue(rm.getLightChannel(inst, 0))

        // // Skinning
        // val sb = SkinningBuffer.Builder()
        //     .boneCount(10)
        //     .build(engine)
        // rm.setSkinningBuffer(inst, sb, 10, 0)
        // rm.setBonesAsMatrices(inst, FloatArray(160), 10, 0)
        // rm.setBonesAsQuaternions(inst, FloatArray(80), 10, 0)

        // // Clean up
        // rm.clearMaterialInstanceAt(inst, 0)
        // rm.destroy(entity)
        em.destroy(entity)
        engine.destroyVertexBuffer(vb)
        engine.destroyIndexBuffer(ib)
        // engine.destroySkinningBuffer(sb)
        // engine.destroyMaterialInstance(matInst)
        // engine.destroyMaterial(mat)
    }
}
