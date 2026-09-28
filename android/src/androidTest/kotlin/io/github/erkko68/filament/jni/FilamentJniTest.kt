package io.github.erkko68.filament.jni

import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.erkko68.filament.jni.FilamentJni as C
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import org.junit.runner.RunWith

// One test per boundary kind the generator emits: pointers, ints/size_t, enums, bools, strings, structs, callbacks.
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
    fun structViewsRoundTripThroughTheCApi() {
        val view = C.FilaEngine_createView(engine)
        nativeScoped {
            val ao = FilaViewAmbientOcclusionOptions(alloc(FilaViewAmbientOcclusionOptions.SIZE))
            C.FilaView_getAmbientOcclusionOptions(view, ao.ptr)
            ao.enabled = true
            ao.radius = 0.75f
            ao.quality = FILA_VIEW_QUALITY_LEVEL_HIGH
            ao.ssct.enabled = true
            ao.ssct.sampleCount = 7
            // Filament normalizes the direction; (1, 0, 0) survives it.
            ao.ssct.lightDirection[0] = 1f
            ao.ssct.lightDirection[1] = 0f
            C.FilaView_setAmbientOcclusionOptions(view, ao.ptr)

            val out = FilaViewAmbientOcclusionOptions(alloc(FilaViewAmbientOcclusionOptions.SIZE))
            C.FilaView_getAmbientOcclusionOptions(view, out.ptr)
            assertTrue(out.enabled)
            assertEquals(0.75f, out.radius)
            assertEquals(FILA_VIEW_QUALITY_LEVEL_HIGH, out.quality)
            assertTrue(out.ssct.enabled)
            assertEquals(7, out.ssct.sampleCount)
            assertEquals(1f, out.ssct.lightDirection[0])
        }
        C.FilaEngine_destroyView(engine, view)
    }

    @Test
    fun bufferCallbackFiresOnceUploadIsConsumed() {
        val texture = texture(2, 2)
        val pixels = FilaJni.buffer(FilaJni.alloc(16), 16)
        repeat(16) { pixels.put(it, 0x7f) }
        val released = CountDownLatch(1)
        C.FilaTexture_setImage(
            texture, engine, 0, 0, 0, 0, 2, 2, 1,
            FilaJni.address(pixels), 16, FILA_PIXEL_DATA_FORMAT_RGBA, FILA_PIXEL_DATA_TYPE_UBYTE, 1, 0, 0, 0,
            0, FilaJni.bufferCallback(), FilaJni.newCallback({ released.countDown() }, once = true),
        )
        C.FilaEngine_flushAndWait(engine, Long.MAX_VALUE)
        assertTrue(released.await(5, TimeUnit.SECONDS), "FilaBufferCallback never ran")
        FilaJni.free(FilaJni.address(pixels))
        C.FilaEngine_destroyTexture(engine, texture)
    }

    // Frame callbacks are Metal-only, so persistence is exercised through one userData shared by two uploads.
    @Test
    fun persistentCallbackSurvivesCallsAndSeesItsArgument() {
        val texture = texture(2, 2)
        val seen = java.util.Collections.synchronizedList(mutableListOf<Long>())
        val released = CountDownLatch(2)
        val userData = FilaJni.newCallback({ seen += it; released.countDown() }, once = false)
        val uploads = List(2) { FilaJni.alloc(16) }
        uploads.forEach { pixels ->
            C.FilaTexture_setImage(
                texture, engine, 0, 0, 0, 0, 2, 2, 1,
                pixels, 16, FILA_PIXEL_DATA_FORMAT_RGBA, FILA_PIXEL_DATA_TYPE_UBYTE, 1, 0, 0, 0,
                0, FilaJni.bufferCallback(), userData,
            )
        }
        C.FilaEngine_flushAndWait(engine, Long.MAX_VALUE)
        assertTrue(released.await(5, TimeUnit.SECONDS), "persistent callback ran ${2 - released.count} of 2 times")
        assertEquals(uploads.toSet(), seen.toSet())
        FilaJni.releaseCallback(userData)
        uploads.forEach(FilaJni::free)
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
