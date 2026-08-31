package io.github.erkko68.filament

import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertSame

/**
 * `Engine.destroy` releases the hidden canvas it allocated itself, but must leave a
 * caller-supplied one alone: that canvas outlives the engine, is still in the caller's
 * DOM, and may back another engine later. Losing its GL context is irreversible without
 * `restoreContext`, so getting this wrong breaks the next `Engine.create` on it.
 */
class EngineCanvasOwnershipTest {
    private lateinit var canvas: HTMLCanvasElement

    @BeforeTest
    fun setUp() {
        Filament.init()
        canvas = (document.createElement("canvas") as HTMLCanvasElement).also {
            document.body?.appendChild(it)
        }
    }

    @AfterTest
    fun tearDown() {
        canvas.remove()
    }

    @Test
    fun sharedCanvasSurvivesDestroy() {
        val engine = Engine.create(canvas)
        assertSame(canvas, engine.jsCanvas, "engine should adopt the supplied canvas")
        engine.destroy()
        assertNotNull(canvas.parentElement, "a caller's canvas must stay in the DOM")
    }

    @Test
    fun sharedCanvasStillBacksANewEngine() {
        Engine.create(canvas).destroy()
        // Fails if destroy() called loseContext() on a canvas it does not own.
        val second = Engine.create(canvas)
        assertNotNull(second.createScene(), "canvas should still yield a usable GL context")
        second.destroy()
    }

    @Test
    fun ownCanvasIsRemovedOnDestroy() {
        val engine = Engine.create()
        val owned = assertNotNull(engine.jsCanvas, "create() allocates its own canvas")
        assertNotNull(owned.parentElement, "the allocated canvas is parked on the document")
        engine.destroy()
        assertNull(owned.parentElement, "the engine's own canvas should be removed")
    }
}
