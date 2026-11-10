package io.github.erkko68.filament.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.canvas
import io.github.erkko68.filament.compose.scene.DirectionalLight
import io.github.erkko68.filament.compose.scene.GltfInstance
import io.github.erkko68.filament.compose.scene.LinearColor
import io.github.erkko68.filament.compose.scene.Skybox
import io.github.erkko68.filament.compose.scene.Position
import io.github.erkko68.filament.compose.scene.primitives.Plane
import io.github.erkko68.filament.compose.scene.rememberUnlitColorMaterialInstance
import io.github.erkko68.filament.compose.scene.rememberGltfAsset
import io.github.erkko68.filament.compose.testutils.TestGlb
import io.github.erkko68.filament.compose.testutils.onPage
import io.github.erkko68.filament.compose.testutils.pageContainer
import io.github.erkko68.filament.testsupport.TestEnv
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.test.TestResult
import kotlinx.coroutines.test.runTest
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.Event
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.fail
import kotlin.time.Duration.Companion.seconds

/**
 * What the page does to a view on its own: its container resized or hidden, the tab losing focus, the zoom
 * changing. None of it goes through the composition; the view must still be showing its scene afterwards.
 * Taking the container out of the document isn't here: Compose disposes the viewport when that happens.
 */
class PageLifecycleTest {
    private var seen = 0
    private var waiter: Pair<Int, CompletableDeferred<Unit>>? = null
    private var engine: Engine? = null

    // runTest skips delays: real time passes only in frames.
    private suspend fun frames(n: Int) {
        val done = CompletableDeferred<Unit>()
        waiter = seen + n to done
        done.await()
    }

    @Composable
    private fun World(transparent: Boolean, renderingEnabled: Boolean) {
        LaunchedEffect(Unit) {
            while (true) withFrameNanos { seen++; waiter?.let { (at, done) -> if (seen >= at) done.complete(Unit) } }
        }
        val engine = rememberFilamentEngine().also { engine = it }
        FilamentSceneView(
            Modifier.fillMaxSize(),
            engine = engine,
            transparent = transparent,
            renderingEnabled = renderingEnabled,
        ) {
            Skybox(LinearColor(0f, 1f, 0f))
            DirectionalLight()
            GltfInstance(rememberGltfAsset { TestGlb.getDuckGlbBytes() })
        }
    }

    private suspend fun assertShowing(what: String) {
        frames(20)
        val pixel = viewPixel(pageContainer, 1)
        val (r, g) = pixel.split(',').mapNotNull { it.toIntOrNull() }.takeIf { it.size == 4 } ?: fail("$what: no view canvas ($pixel)")
        assertTrue(g > 100 && g > r + 40, "$what: the view should show its green skybox, shows $pixel")
    }

    private fun size(width: String, height: String) {
        pageContainer.style.width = width
        pageContainer.style.height = height
        window.dispatchEvent(Event("resize"))
    }

    private fun bothSurfaces(renderingEnabled: Boolean = true, body: suspend (String) -> Unit): TestResult = runTest(timeout = 60.seconds) {
        if (!TestEnv.gpuBackendAvailable) return@runTest
        for (transparent in listOf(false, true)) {
            val surface = (if (transparent) "transparent" else "opaque") + if (renderingEnabled) "" else " paused"
            try {
                onPage({ World(transparent, renderingEnabled) }) {
                    assertShowing("$surface at first")
                    body(surface)
                }
            } finally {
                pageContainer.style.display = ""
                size("400px", "600px")
            }
        }
    }

    private suspend fun resizeStorm() {
        repeat(40) { i ->
            when (i % 4) {
                0 -> size("600px", "300px")
                1 -> size("0px", "0px")
                2 -> size("${150 + i * 9}px", "${500 - i * 7}px")
                else -> size("400px", "600px")
            }
            frames(if (i % 5 == 0) 6 else 1)
        }
        size("320px", "480px")
    }

    // The frame crosses from Filament's canvas into a Compose texture: its rows must not arrive upside down.
    @Test
    fun theViewIsTheRightWayUp(): TestResult = runTest(timeout = 60.seconds) {
        if (!TestEnv.gpuBackendAvailable) return@runTest
        onPage({
            LaunchedEffect(Unit) {
                while (true) withFrameNanos { seen++; waiter?.let { (at, done) -> if (seen >= at) done.complete(Unit) } }
            }
            FilamentSceneView(Modifier.fillMaxSize()) {
                Skybox(LinearColor(0f, 1f, 0f))
                // A red ceiling over the camera: it fills the view above the horizon only.
                Plane(rememberUnlitColorMaterialInstance(LinearColor(1f, 0f, 0f)), width = 200f, depth = 200f, position = Position(0f, 3f, 0f))
            }
        }) {
            frames(20)
            val (topR, topG) = viewPixel(pageContainer, 1).split(',').map { it.toInt() }
            val (bottomR, bottomG) = viewPixel(pageContainer, 3).split(',').map { it.toInt() }
            assertTrue(topR > 100 && topR > topG + 40, "the ceiling should be at the top, which shows $topR,$topG")
            assertTrue(bottomG > 100 && bottomG > bottomR + 40, "the sky should be at the bottom, which shows $bottomR,$bottomG")
        }
    }

    @Test
    fun containerResizedInAStorm() = bothSurfaces { surface ->
        resizeStorm()
        assertShowing("$surface after the resizes")
    }

    @Test
    fun pausedContainerResizedInAStorm() = bothSurfaces(renderingEnabled = false) { surface ->
        resizeStorm()
        assertShowing("$surface after the resizes")
    }

    @Test
    fun containerHiddenAndShown() = bothSurfaces { surface ->
        repeat(3) {
            pageContainer.style.display = "none"
            window.dispatchEvent(Event("resize"))
            frames(8)
            pageContainer.style.display = ""
            size("${300 + 40 * it}px", "500px") // resized while hidden
            assertShowing("$surface shown again ${it + 1}")
        }
    }

    @Test
    fun tabBlurredAndZoomed() = bothSurfaces { surface ->
        try {
            repeat(3) {
                document.dispatchEvent(Event("visibilitychange"))
                window.dispatchEvent(Event("blur"))
                frames(5)
                setDevicePixelRatio(if (it % 2 == 0) 2.0 else 0.75) // browser zoom, or a move to another screen
                window.dispatchEvent(Event("resize"))
                frames(5)
                window.dispatchEvent(Event("focus"))
                document.dispatchEvent(Event("visibilitychange"))
                assertShowing("$surface at zoom ${it + 1}")
            }
        } finally {
            setDevicePixelRatio(1.0)
            window.dispatchEvent(Event("resize"))
        }
    }

    // The browser takes a WebGL context away on its own: a GPU process restart, too many contexts, a backgrounded tab.
    // Filament doesn't rebuild its resources on a restored context, so the view stays blank: surviving is the assertion.
    @Test
    fun glContextLostAndRestored() = bothSurfaces { _ ->
        val canvas = engine!!.canvas!!
        assertTrue(loseContext(canvas, restore = false), "WEBGL_lose_context should be available")
        frames(20)
        loseContext(canvas, restore = true)
        frames(30)
    }
}

// The viewport's canvas, [quarter] quarters down its middle, copied out through a 2D one: the view is a Compose layer of it.
private fun viewPixel(container: HTMLElement, quarter: Int): String = js("""{
    const scratch = document.createElement('canvas');
    scratch.width = scratch.height = 1;
    const ctx = scratch.getContext('2d');
    let found = 'none';
    for (const c of window.webglCanvases) {
        let node = c;
        while (node && node !== container) node = node.parentNode || node.host;
        if (!node || c.width === 0 || c.height === 0) continue;
        ctx.clearRect(0, 0, 1, 1);
        ctx.drawImage(c, c.width >> 1, (c.height * quarter) >> 2, 1, 1, 0, 0, 1, 1);
        found = Array.from(ctx.getImageData(0, 0, 1, 1).data).join(',');
    }
    return found;
}""")

private fun setDevicePixelRatio(ratio: Double): Unit =
    js("Object.defineProperty(window, 'devicePixelRatio', { value: ratio, configurable: true })")

private fun loseContext(canvas: HTMLCanvasElement, restore: Boolean): Boolean = js("""{
    const gl = canvas.getContext('webgl2');
    canvas.__lose = canvas.__lose || (gl && gl.getExtension('WEBGL_lose_context'));
    if (!canvas.__lose) return false;
    if (restore) canvas.__lose.restoreContext(); else canvas.__lose.loseContext();
    return true;
}""")
