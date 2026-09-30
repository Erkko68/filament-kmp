package io.github.erkko68.filament

import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement
import kotlin.test.Test
import kotlin.test.assertEquals

/** A frame cleared to red reaches the canvas: the one pixel-level check of the web runtime. */
class ClearCanvasTest {
    @Test
    fun clearColorReachesTheCanvas() {
        Filament.init()
        val canvas = (document.createElement("canvas") as HTMLCanvasElement).apply { width = 4; height = 4 }
        val engine = Engine.create(sharedContext = canvas)!!
        val swapChain = engine.createSwapChain(NativeSurface(canvas))
        val renderer = engine.createRenderer()
        val scene = engine.createScene()
        val view = engine.createView()
        val cameraEntity = EntityManager.get().create()
        view.camera = engine.createCamera(cameraEntity)
        view.scene = scene
        view.viewport = Viewport(0, 0, 4, 4)
        view.isPostProcessingEnabled = false
        renderer.clearOptions = Renderer.ClearOptions().apply {
            clearColor = doubleArrayOf(1.0, 0.0, 0.0, 1.0)
            clear = true
        }

        // Single-threaded wasm: endFrame executes the GL commands, so the drawing buffer is readable now.
        if (renderer.beginFrame(swapChain, 0L)) {
            renderer.render(view)
            renderer.endFrame()
        }
        assertEquals("255,0,0,255", readPixel(canvas))

        engine.destroyCameraComponent(cameraEntity)
        EntityManager.get().destroy(cameraEntity)
        engine.destroy(view)
        engine.destroy(scene)
        engine.destroy(renderer)
        engine.destroy(swapChain)
        Engine.destroy(engine)
    }
}

private fun readPixel(canvas: HTMLCanvasElement): String = js("""{
    const gl = canvas.getContext('webgl2');
    const p = new Uint8Array(4);
    gl.readPixels(0, 0, 1, 1, gl.RGBA, gl.UNSIGNED_BYTE, p);
    return p.join(',');
}""")
