package io.github.erkko68.filament

import io.github.erkko68.filament.testutils.FilamentTestFixture
import io.github.erkko68.filament.testutils.TestMaterials
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class RenderableManagerTest : FilamentTestFixture() {
    @Test
    fun testComputeAABB() {
        // float4 positions; vertex 1 isn't indexed, so it stays out of the box.
        val vertices = floatArrayOf(0f, 0f, 0f, 9f, 100f, 100f, 100f, 9f, 2f, -1f, 4f, 9f)
        val expected = Box().set(floatArrayOf(0f, -1f, 0f), floatArrayOf(2f, 0f, 4f))
        assertEquals(expected, RenderableManager.computeAABB(vertices, intArrayOf(0, 2), stride = 16))
        assertEquals(expected, RenderableManager.computeAABB(vertices, shortArrayOf(0, 2), stride = 16))
        // Half floats: 1.0 = 0x3C00, 2.0 = 0x4000, -1.0 = 0xBC00.
        val halves = shortArrayOf(0x3C00, 0x4000, 0xBC00.toShort(), 0xBC00.toShort(), 0x3C00, 0x4000)
        val halfBox = Box().set(floatArrayOf(-1f, 1f, -1f), floatArrayOf(1f, 2f, 2f))
        assertEquals(halfBox, RenderableManager.computeAABB(halves, intArrayOf(0, 1)))
        assertEquals(halfBox, RenderableManager.computeAABB(halves, shortArrayOf(0, 1)))
    }

    // No material: renderables fall back to the engine's default material, which NOOP can build.
    private fun triangle(advancedSkinning: Boolean = false) = VertexBuffer.Builder()
        .vertexCount(3)
        .bufferCount(1)
        .attribute(VertexBuffer.VertexAttribute.POSITION, 0, VertexBuffer.AttributeType.FLOAT3, 0, 12)
        .advancedSkinning(advancedSkinning)
        .build(engine)

    // Skinning needs indexed primitives.
    private fun indices() = IndexBuffer.Builder().indexCount(3).bufferType(IndexBuffer.IndexType.USHORT).build(engine)

    private val unitBox = Box(floatArrayOf(0f, 0f, 0f), floatArrayOf(1f, 1f, 1f))

    @Test
    fun testBonesAndMorphTargets() {
        val vb = triangle(advancedSkinning = true)
        val ib = indices()
        val mtb = MorphTargetBuffer.Builder().vertexCount(3).count(2).build(engine)
        val entity = EntityManager.get().create()
        // (bone index, weight) pairs: 3 vertices × 2 bones.
        val pairs = floatArrayOf(0f, 0.5f, 1f, 0.5f, 0f, 1f, 1f, 0f, 0f, 0.25f, 1f, 0.75f)
        RenderableManager.Builder(1)
            .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, vb, ib)
            .boundingBox(unitBox)
            .skinning(2)
            .boneIndicesAndWeights(0, pairs, count = 6, bonesPerVertex = 2)
            .morphing(mtb)
            .morphing(0, 0, 0)
            .build(engine, entity)

        val rm = engine.renderableManager
        assertFalse(rm.empty())
        val inst = rm.getInstance(entity)
        rm.setBones(inst, Array(2) { RenderableManager.Bone() })
        val identity = floatArrayOf(1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f, 0f, 0f, 0f, 0f, 1f)
        rm.setBones(inst, identity, boneCount = 1, offset = 1)
        // The default count is the whole array: 2 bones fit, a miscounted 32 would panic.
        rm.setBones(inst, identity + identity)

        assertEquals(2, rm.getMorphTargetCount(inst))
        assertEquals(mtb.count, rm.getMorphTargetBuffer(inst)?.count)
        rm.setMorphWeights(inst, floatArrayOf(0.25f, 0.75f))
        rm.setMorphTargetBufferOffsetAt(inst, 0, 0, 0)

        engine.destroy(entity)
        EntityManager.get().destroy(entity)
        engine.destroy(mtb)
        engine.destroy(vb)
        engine.destroy(ib)
    }

    @Test
    fun testSkinningBuffer() {
        val vb = triangle()
        val ib = indices()
        val sb = SkinningBuffer.Builder().boneCount(256).initialize(true).build(engine) // setSkinningBuffer binds 256 bones
        val entity = EntityManager.get().create()
        RenderableManager.Builder(1)
            .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, vb, ib)
            .boundingBox(unitBox)
            .enableSkinningBuffers()
            .skinning(sb, 2, 0)
            .build(engine, entity)
        val rm = engine.renderableManager
        rm.setSkinningBuffer(rm.getInstance(entity), sb, 2, 0)

        engine.destroy(entity)
        EntityManager.get().destroy(entity)
        engine.destroy(sb)
        engine.destroy(vb)
        engine.destroy(ib)
    }

    @Test
    fun testRenderableLifecycleAndGetters() {
        val bytes = TestMaterials.getEmissiveMaterialBytes()
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
        assertTrue(entity in rm.allEntities)

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
        rm.setGeometryAt(inst, 0, RenderableManager.PrimitiveType.TRIANGLES, vb, ib)
        rm.setGeometryAt(inst, 0, RenderableManager.PrimitiveType.POINTS, vb)
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
