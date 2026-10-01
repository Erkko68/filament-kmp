package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.RenderingTestFixture
import io.github.erkko68.filament.testutils.TestMaterials
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** Real-backend coverage for [RenderableManager] bindings (needs a material instance). */
class RenderableManagerRenderingTest : RenderingTestFixture() {
    @Test
    fun testRenderableLifecycleAndGetters() {
        val engine = engine ?: return
        val bytes = TestMaterials.getEmissiveMaterialBytes()
        if (bytes.isEmpty()) return

        val mat = Material.Builder().payload(bytes).build(engine)!!
        val matInst = mat.createInstance()

        val vb = VertexBuffer.Builder()
            .vertexCount(3)
            .bufferCount(1)
            .attribute(VertexBuffer.VertexAttribute.POSITION, 0, VertexBuffer.AttributeType.FLOAT3, 0, 12)
            .build(engine)
        vb.setBufferAt(engine, 0, ByteArray(36))

        val ib = IndexBuffer.Builder()
            .indexCount(3)
            .bufferType(IndexBuffer.IndexType.USHORT)
            .build(engine)
        ib.setBuffer(engine, byteArrayOf(0, 0, 1, 0, 2, 0))

        val em = EntityManager.get()
        val entity = em.create()

        val builder = RenderableManager.Builder(1)
            .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, vb, ib)
            .material(0, matInst)
            .boundingBox(Box(floatArrayOf(0f, 0f, 0f), floatArrayOf(1f, 1f, 1f)))
            .culling(true)
            .castShadows(true)
            .receiveShadows(true)
            .screenSpaceContactShadows(true)
        // DYNAMIC (not STATIC): setAxisAlignedBoundingBox below requires non-static geometry.
        builder.geometryType(RenderableManager.Builder.GeometryType.DYNAMIC)
        assertEquals(RenderableManager.Builder.Result.Success, builder.build(engine, entity))

        val rm = engine.renderableManager
        assertTrue(rm.hasComponent(entity))
        val inst = rm.getInstance(entity)
        assertTrue(inst != 0)

        rm.setAxisAlignedBoundingBox(inst, Box(floatArrayOf(0f, 0f, 0f), floatArrayOf(2f, 2f, 2f)))
        val b = rm.getAxisAlignedBoundingBox(inst)
        assertEquals(2f, b.halfExtent[0])
        assertEquals(entity, rm.getEntity(inst))
        assertTrue(entity in rm.entities)

        rm.setLayerMask(inst, 0xFF, 0x01)
        assertEquals(0x01, rm.getLayerMask(inst))
        rm.setPriority(inst, 5)
        assertEquals(5, rm.getPriority(inst))
        rm.setChannel(inst, 2)
        assertEquals(2, rm.getChannel(inst))
        rm.setCulling(inst, false)
        assertTrue(!rm.isCullingEnabled(inst))
        rm.setFogEnabled(inst, false)
        assertTrue(!rm.getFogEnabled(inst))
        rm.setCastShadows(inst, false)
        assertTrue(!rm.isShadowCaster(inst))
        rm.setReceiveShadows(inst, false)
        assertTrue(!rm.isShadowReceiver(inst))
        rm.setScreenSpaceContactShadows(inst, false)
        assertTrue(!rm.isScreenSpaceContactShadowsEnabled(inst))

        assertEquals(1, rm.getPrimitiveCount(inst))

        rm.setMaterialInstanceAt(inst, 0, matInst)
        assertNotNull(rm.getMaterialInstanceAt(inst, 0))
        val attrs = rm.getEnabledAttributesAt(inst, 0)
        assertTrue(VertexBuffer.VertexAttribute.POSITION in attrs)
        assertTrue(VertexBuffer.VertexAttribute.COLOR !in attrs)
        rm.setBlendOrderAt(inst, 0, 2)
        assertEquals(2, rm.getBlendOrderAt(inst, 0))
        rm.setGlobalBlendOrderEnabledAt(inst, 0, true)
        assertTrue(rm.isGlobalBlendOrderEnabledAt(inst, 0))
        rm.setGlobalBlendOrderEnabledAt(inst, 0, false)
        assertTrue(!rm.isGlobalBlendOrderEnabledAt(inst, 0))
        rm.setLightChannel(inst, 0, true)
        assertTrue(rm.getLightChannel(inst, 0))

        rm.setGeometryAt(inst, 0, RenderableManager.PrimitiveType.TRIANGLES, vb, ib, 0, 3)
        rm.setGeometryAt(inst, 0, RenderableManager.PrimitiveType.POINTS, vb, 0, 3)
        rm.clearMaterialInstanceAt(inst, 0)
        assertNull(rm.getMaterialInstanceAt(inst, 0))

        rm.destroy(entity)
        em.destroy(entity)
        engine.destroy(vb)
        engine.destroy(ib)
        engine.destroy(matInst)
        engine.destroy(mat)
    }
}
