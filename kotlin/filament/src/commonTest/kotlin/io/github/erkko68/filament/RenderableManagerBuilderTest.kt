package io.github.erkko68.filament

import io.github.erkko68.filament.testsupport.IgnoreJs
import io.github.erkko68.filament.testutils.FilamentTestFixture
import kotlin.test.Test
import kotlin.test.assertNotNull

/**
 * Exercises every chainable [RenderableManager.Builder] setter. Each setter issues
 * its native call immediately (not on build()), so calling them covers the bindings
 * without needing a material payload, which panics under the NOOP backend.
 */
@IgnoreJs // skinning/morphing builders are not bound in the web wrapper.
class RenderableManagerBuilderTest : FilamentTestFixture() {
    @Test
    fun testBuilderSetters() {
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

        val sb = SkinningBuffer.Builder()
            .boneCount(10)
            .initialize(true)
            .build(engine)

        val mtb = MorphTargetBuffer.Builder()
            .vertexCount(3)
            .count(2)
            .build(engine)

        val builder = RenderableManager.Builder(1)
            .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, vb, ib)
            .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, vb, ib, 0, 3)
            .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, vb, ib, 0, 0, 2, 3)
            .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, vb, 0, 3)
            .geometry(0, RenderableManager.PrimitiveType.TRIANGLES, vb)
            .layerMask(0xFF, 0x01)
            .priority(3)
            .channel(2)
            .fog(true)
            .lightChannel(0, true)
            .blendOrder(0, 2)
            .globalBlendOrderEnabled(0, true)
            .instances(2)
            .enableSkinningBuffers(true)
            .skinning(10)
            .skinning(10, FloatArray(160))
            .skinning(sb, 10, 0)
            .morphing(2)
            .morphing(mtb)

        assertNotNull(builder)

        engine.destroyVertexBuffer(vb)
        engine.destroyIndexBuffer(ib)
        engine.destroySkinningBuffer(sb)
        engine.destroyMorphTargetBuffer(mtb)
    }
}
