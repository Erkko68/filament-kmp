package io.github.erkko68.filament.compose.internal

import androidx.compose.ui.unit.IntRect
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Filament
import io.github.erkko68.filament.Skybox
import io.github.erkko68.filament.canvas
import io.github.erkko68.filament.testsupport.TestEnv
import kotlinx.browser.document
import org.khronos.webgl.get
import org.w3c.dom.HTMLCanvasElement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

/** Driven by hand: Compose's test host has no HTML interop container to put a `FilamentSurface` in. */
class WebViewCompositorTest {

    /** The engine's one canvas spans the views on screen: one scrolled far away must not grow it to reach there. */
    @Test
    fun onlyViewsOnScreenSizeTheCanvas() {
        if (!TestEnv.gpuBackendAvailable) return
        Filament.init()
        val engine = Engine.create(Engine.Backend.DEFAULT) ?: return
        val canvas = assertNotNull(engine.canvas)
        val view = engine.createView()
        val compositor = WebViewCompositor.of(engine)
        val entry = compositor.register(view, document.createElement("canvas") as HTMLCanvasElement)
        try {
            entry.rect = IntRect(10, 20, 60, 70)
            entry.visible = entry.rect
            compositor.renderFrame()
            assertEquals(60 to 70, canvas.width to canvas.height)
            assertEquals(50 to 50, entry.target.width to entry.target.height)

            // Scrolled out of sight: not rendered, and the canvas does not follow it.
            entry.rect = IntRect(10, 4000, 60, 4050)
            entry.visible = IntRect(0, 0, 0, 0)
            compositor.renderFrame()
            assertEquals(60 to 70, canvas.width to canvas.height)

            // Half above the window: the canvas ends where the view's visible part does.
            entry.rect = IntRect(0, -20, 50, 30)
            entry.visible = IntRect(0, 0, 50, 30)
            compositor.renderFrame()
            assertEquals(50 to 30, canvas.width to canvas.height)
        } finally {
            compositor.unregister(entry)
            engine.destroy(view)
            Engine.destroy(engine)
        }
    }

    /** Views share one canvas: where two overlap, each must still get its own pixels, whichever is drawn last. */
    @Test
    fun overlappingViewsKeepTheirOwnPixels() {
        if (!TestEnv.gpuBackendAvailable) return
        Filament.init()
        val engine = Engine.create(Engine.Backend.DEFAULT) ?: return
        val compositor = WebViewCompositor.of(engine)
        val camera = engine.createCamera(engine.entityManager.create())
        val cleanup = mutableListOf<() -> Unit>()

        // A view of nothing but a flat sky.
        fun register(r: Float, g: Float, b: Float, rect: IntRect): WebViewCompositor.Entry {
            val skybox = Skybox.Builder().color(r, g, b, 1f).build(engine)
            val scene = engine.createScene().also { it.skybox = skybox }
            val view = engine.createView().also { it.scene = scene; it.camera = camera }
            val entry = compositor.register(view, document.createElement("canvas") as HTMLCanvasElement)
            entry.rect = rect
            entry.visible = rect
            cleanup += { compositor.unregister(entry); engine.destroy(view); engine.destroy(scene); engine.destroy(skybox) }
            return entry
        }
        fun WebViewCompositor.Entry.pixel(x: Int, y: Int): List<Int> {
            val data = assertNotNull(ctx).getImageData(x.toDouble(), y.toDouble(), 1.0, 1.0).data
            return List(3) { data[it].toInt() and 0xFF }
        }
        try {
            val red = register(1f, 0f, 0f, IntRect(0, 0, 40, 40))
            val blue = register(0f, 0f, 1f, IntRect(20, 20, 60, 60))
            compositor.renderFrame()

            // (30, 30) in the window is inside both.
            val inRed = red.pixel(30, 30)
            val inBlue = blue.pixel(10, 10)
            assertTrue(inRed[0] > inRed[2], "the red view shows rgb(${inRed[0]}, ${inRed[1]}, ${inRed[2]}) where the blue one overlaps it")
            assertTrue(inBlue[2] > inBlue[0], "the blue view shows rgb(${inBlue[0]}, ${inBlue[1]}, ${inBlue[2]}) where it overlaps the red one")
            // And outside the overlap.
            assertTrue(red.pixel(5, 5).let { it[0] > it[2] } && blue.pixel(35, 35).let { it[2] > it[0] })
        } finally {
            cleanup.forEach { it() }
            engine.destroyCameraComponent(camera.entity)
            Engine.destroy(engine)
        }
    }
}
