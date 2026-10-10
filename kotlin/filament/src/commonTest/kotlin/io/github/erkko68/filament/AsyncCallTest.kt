package io.github.erkko68.filament

import io.github.erkko68.filament.testsupport.TestEnv
import io.github.erkko68.filament.testutils.pumpUntil
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/** Async calls need a real backend (Noop has none) and the `backend.enable_asynchronous_operation` feature. */
class AsyncCallTest {
    private var engine: Engine? = null

    @BeforeTest
    fun setUp() {
        Filament.init()
        if (!TestEnv.gpuBackendAvailable) return
        val config = Engine.Config().apply { asynchronousMode = Engine.AsynchronousMode.THREAD_PREFERRED }
        val created = Engine.Builder().backend(Engine.Backend.DEFAULT).config(config)
            .feature("backend.enable_asynchronous_operation", true)
            .build() ?: return
        if (created.isAsynchronousModeEnabled) engine = created else Engine.destroy(created)
    }

    @AfterTest
    fun tearDown() {
        engine?.let { it.flushAndWait(); Engine.destroy(it) }
    }

    @Test
    fun testRunCommandAsync() {
        val engine = engine ?: return
        var ran = false
        var status: AsyncCallStatus? = null
        engine.runCommandAsync({ ran = true }) { status = it }
        engine.pumpUntil { status != null }
        assertTrue(ran)
        assertEquals(AsyncCallStatus.COMPLETED, status)
    }

    @Test
    fun testCancelReportsCanceled() {
        val engine = engine ?: return
        // Canceled right after queueing it usually hasn't started; either way the outcome must match the call's.
        var ran = false
        var status: AsyncCallStatus? = null
        val id = engine.runCommandAsync({ ran = true }) { status = it }
        val canceled = engine.cancelAsyncCall(id)
        engine.pumpUntil { status != null }
        assertEquals(if (canceled) AsyncCallStatus.CANCELED else AsyncCallStatus.COMPLETED, status)
        assertEquals(!canceled, ran)
        assertTrue(!engine.cancelAsyncCall(id))
    }

    @Test
    fun testAsyncResources() {
        val engine = engine ?: return
        val statuses = mutableListOf<Pair<String, AsyncCallStatus>>()
        val ib = IndexBuffer.Builder().indexCount(3).bufferType(IndexBuffer.IndexType.USHORT)
            .async { b, s -> statuses += "ib" to s; check(b.isCreationComplete == (s == AsyncCallStatus.COMPLETED)) }
            .build(engine)
        ib.setBufferAsync(engine, byteArrayOf(0, 0, 1, 0, 2, 0)) { _, s -> statuses += "ib data" to s }

        val vb = VertexBuffer.Builder().vertexCount(3).bufferCount(2)
            .attribute(VertexBuffer.VertexAttribute.POSITION, 0, VertexBuffer.AttributeType.FLOAT3, 0, 12)
            .attribute(VertexBuffer.VertexAttribute.UV0, 1, VertexBuffer.AttributeType.FLOAT2, 0, 8)
            .enableBufferObjects(true)
            .async { _, s -> statuses += "vb" to s }
            .build(engine)
        val bo = BufferObject.Builder().size(36).build(engine)
        bo.setBuffer(engine, ByteArray(36))
        vb.setBufferObjectAtAsync(engine, 0, bo) { _, s -> statuses += "vb object" to s }
        val uvs = BufferObject.Builder().size(24).build(engine)
        vb.setBufferObjectAtAsync(engine, 1, uvs)

        val plain = VertexBuffer.Builder().vertexCount(3).bufferCount(1)
            .attribute(VertexBuffer.VertexAttribute.POSITION, 0, VertexBuffer.AttributeType.FLOAT3, 0, 12)
            .build(engine)
        plain.setBufferAtAsync(engine, 0, ByteArray(36)) { _, s -> statuses += "vb data" to s }

        val texture = Texture.Builder().width(2).height(2).format(Texture.InternalFormat.RGBA8)
            .async { _, s -> statuses += "texture" to s }
            .build(engine)
        val pixels = { Texture.PixelBufferDescriptor(ByteArray(16), 16, Texture.Format.RGBA, Texture.Type.UBYTE) }
        texture.setImageAsync(engine, 0, pixels()) { _, s -> statuses += "image" to s }
        texture.setImageAsync(engine, 0, 0, 0, 2, 2, pixels()) { _, s -> statuses += "region" to s }
        texture.setImageAsync(engine, 0, 0, 0, 0, 2, 2, 1, pixels()) { _, s -> statuses += "volume" to s }

        engine.pumpUntil { statuses.size == 9 }
        assertEquals(
            listOf("ib", "ib data", "vb", "vb object", "vb data", "texture", "image", "region", "volume").map { it to AsyncCallStatus.COMPLETED }.toSet(),
            statuses.toSet(),
        )
        assertTrue(ib.isCreationComplete && vb.isCreationComplete && texture.isCreationComplete())

        listOf(ib, vb, plain, bo, uvs, texture).forEach {
            when (it) {
                is IndexBuffer -> engine.destroy(it)
                is VertexBuffer -> engine.destroy(it)
                is BufferObject -> engine.destroy(it)
                is Texture -> engine.destroy(it)
            }
        }
    }

    // With no completion callback there's nothing to wait on; the queue runs in order, so a later command marks them done.
    @Test
    fun testAsyncCallsWithoutCallbacks() {
        val engine = engine ?: return
        val ib = IndexBuffer.Builder().indexCount(3).bufferType(IndexBuffer.IndexType.USHORT).build(engine)
        // Only the first 6 bytes are indices.
        ib.setBufferAsync(engine, byteArrayOf(0, 0, 1, 0, 2, 0, 9, 9), count = 6)
        val vb = VertexBuffer.Builder().vertexCount(3).bufferCount(1)
            .attribute(VertexBuffer.VertexAttribute.POSITION, 0, VertexBuffer.AttributeType.FLOAT3, 0, 12)
            .build(engine)
        vb.setBufferAtAsync(engine, 0, ByteArray(40), count = 36)
        val texture = Texture.Builder().width(2).height(2).format(Texture.InternalFormat.RGBA8).build(engine)
        val pixels = { Texture.PixelBufferDescriptor(ByteArray(16), 16, Texture.Format.RGBA, Texture.Type.UBYTE) }
        texture.setImageAsync(engine, 0, pixels())
        texture.setImageAsync(engine, 0, 0, 0, 2, 2, pixels())
        texture.setImageAsync(engine, 0, 0, 0, 0, 2, 2, 1, pixels())

        var ran = false
        engine.runCommandAsync({ ran = true })
        var status: AsyncCallStatus? = null
        engine.runCommandAsync({}) { status = it }
        engine.pumpUntil { status != null }
        assertTrue(ran)
        assertEquals(AsyncCallStatus.COMPLETED, status)

        engine.destroy(ib)
        engine.destroy(vb)
        engine.destroy(texture)
    }
}
