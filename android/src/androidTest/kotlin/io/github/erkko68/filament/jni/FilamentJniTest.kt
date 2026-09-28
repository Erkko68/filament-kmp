package io.github.erkko68.filament.jni

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.erkko68.filament.jni.FilamentJni as C
import java.nio.ByteOrder
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import org.junit.runner.RunWith

// One test per boundary kind the generator emits: pointers, ints/size_t, enums, bools, strings, callbacks.
@RunWith(AndroidJUnit4::class)
class FilamentJniTest {
    private var engine = 0L

    @BeforeTest
    fun setUp() {
        val builder = C.FilaEngineBuilder_create()
        C.FilaEngineBuilder_backend(builder, FILA_ENGINE_BACKEND_OPENGL)
        engine = C.FilaEngineBuilder_build(builder)
        C.FilaEngineBuilder_destroy(builder)
        assertNotEquals(0L, engine)
    }

    @AfterTest
    fun tearDown() = C.FilaEngine_destroy(engine)

    @Test
    fun engineReportsOpenGlBackend() {
        assertEquals(FILA_ENGINE_BACKEND_OPENGL, C.FilaEngine_getBackend(engine))
    }

    @Test
    fun textureRoundTripsSizeAndFormat() {
        val texture = texture(64, 32)
        assertEquals(64L, C.FilaTexture_getWidth(texture, 0))
        assertEquals(FILA_TEXTURE_INTERNAL_FORMAT_RGBA8, C.FilaTexture_getFormat(texture))
        assertTrue(C.FilaEngine_destroyTexture(engine, texture))
    }

    @Test
    fun stringsCrossBothWays() {
        val view = C.FilaEngine_createView(engine)
        C.FilaView_setName(view, "jni-view")
        assertEquals("jni-view", C.FilaView_getName(view))
        C.FilaEngine_destroyView(engine, view)
    }

    @Test
    fun bufferCallbackFiresOnceUploadIsConsumed() {
        val texture = texture(2, 2)
        val pixels = FilaJni.view(FilaJni.malloc(16), 16).order(ByteOrder.nativeOrder())
        repeat(16) { pixels.put(it, 0x7f) }
        val released = CountDownLatch(1)
        C.FilaTexture_setImage(
            texture, engine, 0, 0, 0, 0, 2, 2, 1,
            FilaJni.address(pixels), 16, FILA_PIXEL_DATA_FORMAT_RGBA, FILA_PIXEL_DATA_TYPE_UBYTE, 1, 0, 0, 0,
            0, FilaJni.bufferCallback(), FilaJni.newCallback { released.countDown() },
        )
        C.FilaEngine_flushAndWait(engine, Long.MAX_VALUE)
        assertTrue(released.await(5, TimeUnit.SECONDS), "FilaBufferCallback never ran")
        FilaJni.free(FilaJni.address(pixels))
        C.FilaEngine_destroyTexture(engine, texture)
    }

    private fun texture(width: Int, height: Int): Long {
        val builder = C.FilaTextureBuilder_create()
        C.FilaTextureBuilder_width(builder, width)
        C.FilaTextureBuilder_height(builder, height)
        C.FilaTextureBuilder_format(builder, FILA_TEXTURE_INTERNAL_FORMAT_RGBA8)
        C.FilaTextureBuilder_usage(builder, FILA_TEXTURE_USAGE_DEFAULT)
        return C.FilaTextureBuilder_build(builder, engine).also { C.FilaTextureBuilder_destroy(builder) }
    }
}
