package io.github.erkko68.filament.wasm

import kotlinx.browser.document
import org.khronos.webgl.set
import org.w3c.dom.HTMLCanvasElement
import kotlin.js.Promise
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

/** Phase 0 exit check: a frame cleared to red, driven entirely through the Fila* C API. */
class ClearCanvasTest {
    @Test
    fun clearsCanvasThroughCApi(): Promise<JsAny?> = loadFilament().then { m ->
        val canvas = document.createElement("canvas") as HTMLCanvasElement
        canvas.width = 4
        canvas.height = 4
        m.createGlContext(canvas)

        val builder = m._FilaEngineBuilder_create()
        m._FilaEngineBuilder_backend(builder, FILA_ENGINE_BACKEND_OPENGL)
        val engine = m._FilaEngineBuilder_build(builder)
        m._FilaEngineBuilder_destroy(builder)
        assertNotEquals(0, engine, "engine")

        // PlatformWebGL renders to the current context's default framebuffer; no native window.
        val swapChain = m._FilaEngine_createSwapChain(engine, 0, 0L.toI64())
        assertNotEquals(0, swapChain, "swapChain")

        val renderer = m._FilaEngine_createRenderer(engine)
        val scene = m._FilaEngine_createScene(engine)
        val view = m._FilaEngine_createView(engine)
        val em = m._FilaEntityManager_get()
        val cameraEntity = m._FilaEntityManager_create(em)
        m._FilaView_setCamera(view, m._FilaEngine_createCamera(engine, cameraEntity))
        m._FilaView_setScene(view, scene)
        m._FilaView_setViewport(view, 0, 0, 4, 4)
        m._FilaView_setPostProcessingEnabled(view, 0)

        val options = m._malloc(FilaRendererClearOptions.SIZE)
        val f64 = m.HEAPF64
        val color = (options + FilaRendererClearOptions.clearColor) / 8
        f64[color] = 1.0; f64[color + 1] = 0.0; f64[color + 2] = 0.0; f64[color + 3] = 1.0
        val u8 = m.HEAPU8
        u8[options + FilaRendererClearOptions.clear] = 1
        u8[options + FilaRendererClearOptions.discard] = 1
        m._FilaRenderer_setClearOptions(renderer, options)
        m._free(options)

        // Single-threaded wasm: endFrame executes the GL commands, so the drawing buffer is readable now.
        assertNotEquals(0, m._FilaRenderer_beginFrame(renderer, swapChain, 0L.toI64()), "beginFrame")
        m._FilaRenderer_render(renderer, view)
        m._FilaRenderer_endFrame(renderer)
        assertEquals("255,0,0,255", readPixel(canvas))

        m._FilaEngine_destroyCameraComponent(engine, cameraEntity)
        m._FilaEntityManager_destroy(em, cameraEntity)
        m._FilaEngine_destroyView(engine, view)
        m._FilaEngine_destroyScene(engine, scene)
        m._FilaEngine_destroyRenderer(engine, renderer)
        m._FilaEngine_destroySwapChain(engine, swapChain)
        m._FilaEngine_destroy(engine)
        null
    }
}

private fun readPixel(canvas: HTMLCanvasElement): String = js("""{
    const gl = canvas.getContext('webgl2');
    const p = new Uint8Array(4);
    gl.readPixels(0, 0, 1, 1, gl.RGBA, gl.UNSIGNED_BYTE, p);
    return p.join(',');
}""")
