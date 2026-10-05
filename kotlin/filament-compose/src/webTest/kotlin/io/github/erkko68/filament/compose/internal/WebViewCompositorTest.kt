package io.github.erkko68.filament.compose.internal

import androidx.compose.ui.unit.IntRect
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Filament
import io.github.erkko68.filament.canvas
import io.github.erkko68.filament.testsupport.TestEnv
import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

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
}
