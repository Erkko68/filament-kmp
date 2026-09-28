package io.github.erkko68.filament

import io.github.erkko68.filament.interop.NativePointer
import io.github.erkko68.filament.wasm.createGlContext
import io.github.erkko68.filament.wasm.fila
import io.github.erkko68.filament.wasm.releaseGlContext
import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement

/**
 * On web an engine is bound to one WebGL2 context, on [canvas]. [glContext] is its Emscripten handle
 * (0 for the NOOP backend), made current again whenever a different engine was driven last.
 */
internal class WebEnginePlatform(
    val canvas: HTMLCanvasElement?,
    private val glContext: Int,
    // Only the hidden canvas we allocated is ours to tear down; a caller's canvas outlives us.
    private val ownsCanvas: Boolean,
) : EnginePlatform {
    override fun makeCurrent() {
        if (glContext != 0 && current != glContext) {
            fila.GL.makeContextCurrent(glContext)
            current = glContext
        }
    }

    override fun release() {
        if (glContext != 0) {
            canvas?.let { fila.releaseGlContext(it, glContext) }
            if (current == glContext) current = 0
        }
        if (ownsCanvas) canvas?.remove()
    }

    companion object {
        // GL context handle last made current; the Emscripten GL layer has one global current context.
        var current = 0
    }
}

/** The shared context is the [HTMLCanvasElement] to render into; without one a hidden canvas is made. */
internal actual fun enginePlatform(backend: Engine.Backend, sharedContext: Any?): EnginePlatform {
    // WebGL is the only GPU backend in the wasm build: DEFAULT and OPENGL both mean it.
    val gpu = backend != Engine.Backend.NOOP
    val given = sharedContext as? HTMLCanvasElement
    val target = given ?: if (gpu) hiddenCanvas() else null
    val context = if (gpu && target != null) fila.createGlContext(target, alpha = true).also { WebEnginePlatform.current = it } else 0
    return WebEnginePlatform(target, context, ownsCanvas = target != null && given == null)
}

// The canvas goes to enginePlatform, not to FilaEngineBuilder_sharedContext.
internal actual fun sharedContextPointer(sharedContext: Any): NativePointer = 0

// The swapchain renders into the engine's canvas.
internal actual fun acquireWindow(surface: NativeSurface): NativePointer = 0

internal actual fun releaseWindow(window: NativePointer) {}

// Parked off-screen on body until a consumer (e.g. FilamentView) adopts it.
private fun hiddenCanvas(): HTMLCanvasElement =
    (document.createElement("canvas") as HTMLCanvasElement).apply {
        width = 1
        height = 1
        style.position = "absolute"
        style.left = "-9999px"
        style.top = "0"
        document.body?.appendChild(this)
    }
