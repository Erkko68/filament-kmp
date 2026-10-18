package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Every [RenderableManager.Builder] setting must reach the built renderable (NOOP renders with the default material). */
class RenderableManagerBuilderTest : FilamentTestFixture() {
    @Test
    fun builderSettingsReachTheRenderable() {
        val vb = VertexBuffer.Builder()
            .vertexCount(3)
            .bufferCount(1)
            .attribute(VertexBuffer.VertexAttribute.POSITION, 0, VertexBuffer.AttributeType.FLOAT3, 0, 12)
            .build(engine)
        vb.setBufferAt(engine, 0, ByteArray(36))
        val ib = IndexBuffer.Builder().indexCount(3).bufferType(IndexBuffer.IndexType.USHORT).build(engine)
        ib.setBuffer(engine, byteArrayOf(0, 0, 1, 0, 2, 0))
        val mtb = MorphTargetBuffer.Builder().vertexCount(3).count(2).build(engine)

        val entity = EntityManager.get().create()
        // Each later geometry() overload replaces the earlier ones for primitive 0.
        val result = RenderableManager.Builder(1)
            .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, vb, ib)
            .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, vb, ib, 0, 3)
            .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, vb, ib, 0, 0, 2, 3)
            .boundingBox(Box(floatArrayOf(0f, 0f, 0f), floatArrayOf(1f, 1f, 1f)))
            .layerMask(0xFF, 0x04)
            .priority(3)
            .channel(2)
            .culling(false)
            .castShadows(true)
            .receiveShadows(false)
            .fog(false)
            .lightChannel(3, true)
            .blendOrder(0, 2)
            .globalBlendOrderEnabled(0, true)
            .instances(2)
            .morphing(mtb)
            .build(engine, entity)
        assertEquals(RenderableManager.Builder.Result.Success, result)

        val rm = engine.renderableManager
        val inst = rm.getInstance(entity)
        assertEquals(1, rm.getPrimitiveCount(inst))
        assertEquals(0x04, rm.getLayerMask(inst))
        assertEquals(3, rm.getPriority(inst))
        assertEquals(2, rm.getChannel(inst))
        assertFalse(rm.isCullingEnabled(inst))
        assertTrue(rm.isShadowCaster(inst))
        assertFalse(rm.isShadowReceiver(inst))
        assertFalse(rm.getFogEnabled(inst))
        assertTrue(rm.getLightChannel(inst, 3))
        assertEquals(2, rm.getBlendOrderAt(inst, 0))
        assertTrue(rm.isGlobalBlendOrderEnabledAt(inst, 0))
        assertEquals(2, rm.getInstanceCount(inst))
        assertEquals(2, rm.getMorphTargetCount(inst))

        engine.destroy(entity)
        EntityManager.get().destroy(entity)
        engine.destroy(mtb)
        engine.destroy(vb)
        engine.destroy(ib)
    }
}
