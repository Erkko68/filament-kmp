@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package io.github.erkko68.filament.compose.internal

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.webgl.rememberWebGLRenderTarget
import androidx.compose.ui.unit.IntSize
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.NativeSurface
import io.github.erkko68.filament.Renderer
import io.github.erkko68.filament.View
import io.github.erkko68.filament.Viewport
import io.github.erkko68.filament.canvas
import org.khronos.webgl.WebGLRenderingContext
import org.khronos.webgl.WebGLTexture
import org.w3c.dom.HTMLCanvasElement

/**
 * On web an engine is bound to one WebGL context, on its own offscreen canvas, so a view cannot own a
 * surface. Each view renders into the top-left corner of that canvas, and the browser copies it, on the
 * GPU, into a texture of Compose's context that an [Image] shows: the view is an ordinary Compose layer.
 */
@Composable
internal actual fun FilamentSurface(
    modifier: Modifier,
    engine: Engine,
    renderer: Renderer,
    view: View,
    transparent: Boolean,
    renderingEnabled: Boolean,
    onResize: (aspect: Double) -> Unit,
) {
    var size by remember { mutableStateOf(IntSize(1, 1)) }
    var onScreen by remember { mutableStateOf(true) }
    val layout = modifier.onGloballyPositioned {
        onScreen = !it.boundsInWindow().isEmpty
        val now = it.size
        if (now.width > 0 && now.height > 0 && now != size) {
            size = now
            onResize(now.width.toDouble() / now.height.toDouble())
        }
    }
    // Null without a Compose window on a WebGL2 canvas (a headless test host): the view stays empty.
    val target = rememberWebGLRenderTarget(size)
    val canvas = engine.canvas
    if (target == null || canvas == null) {
        Box(layout)
        return
    }

    // The canvas is shared by every view of the engine and the browser never clears it: always clear.
    SideEffect {
        renderer.clearOptions = Renderer.ClearOptions().apply {
            clearColor = doubleArrayOf(0.0, 0.0, 0.0, 0.0)
            clear = true
        }
    }
    val swapChain = rememberOwned(engine, canvas, create = { engine.createSwapChain(NativeSurface(canvas)) }) { engine.destroy(it) }

    val gate = rememberPausedFrameGate(renderingEnabled, 1, target, size)
    // A view scrolled or clipped out of sight isn't drawn: don't render it either.
    FilamentRenderLoop(onScreen && gate.loopEnabled(renderingEnabled)) {
        var rendered = false
        target.render {
            val (w, h) = target.size
            if (canvas.width < w || canvas.height < h) {
                canvas.width = maxOf(canvas.width, w)
                canvas.height = maxOf(canvas.height, h)
            }
            // Filament's origin is bottom-left; the copy below takes the canvas's top-left corner.
            view.viewport = Viewport(0, canvas.height - h, w, h)
            if (renderer.beginFrame(swapChain, Engine.steadyClockTimeNano)) {
                renderer.render(view)
                renderer.endFrame()
                copyCanvas(target.webGLContext, target.webGlTexture, canvas, w, h)
                rendered = true
            }
        }
        if (rendered) gate.delivered(paused = !renderingEnabled)
    }

    Box(layout) {
        // The copy keeps the canvas's rows top-down and Compose reads its texture bottom-up: flip it.
        Image(target.painter, contentDescription = null, contentScale = ContentScale.FillBounds, modifier = Modifier.fillMaxSize().scale(1f, -1f))
    }
}

// Copies the top-left [w]×[h] of [source], a canvas of another context, into [texture]; alpha stays premultiplied.
private fun copyCanvas(gl: WebGLRenderingContext, texture: WebGLTexture, source: HTMLCanvasElement, w: Int, h: Int): Unit = js("""{
    gl.bindTexture(gl.TEXTURE_2D, texture);
    gl.pixelStorei(gl.UNPACK_PREMULTIPLY_ALPHA_WEBGL, true);
    gl.texSubImage2D(gl.TEXTURE_2D, 0, 0, 0, w, h, gl.RGBA, gl.UNSIGNED_BYTE, source);
    gl.pixelStorei(gl.UNPACK_PREMULTIPLY_ALPHA_WEBGL, false);
    gl.bindTexture(gl.TEXTURE_2D, null);
}""")
