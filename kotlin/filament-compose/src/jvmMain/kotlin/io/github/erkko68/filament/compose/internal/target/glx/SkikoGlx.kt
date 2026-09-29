package io.github.erkko68.filament.compose.internal.target.glx

import io.github.erkko68.filament.compose.internal.target.field
import io.github.erkko68.filament.compose.internal.target.findSkiaLayer
import io.github.erkko68.filament.jni.GlxHelper
import java.awt.Window
import org.jetbrains.skia.DirectContext

/**
 * skiko's OpenGL state for one window (`LinuxOpenGLRedrawer`). Its GLX context is created with the
 * redrawer, and only textures in its share group can be wrapped on Compose's [DirectContext].
 */
// ponytail: reflects skiko 0.150 internals (LinuxOpenGLRedrawer.context / contextHandler,
// SkiaLayer.backedLayer). Drop once Compose exposes its context.
internal class SkikoGlx private constructor(redrawer: Any, private val canvas: Any) {
    private val contextHandler: Any = redrawer.field("contextHandler")!!

    /** skiko's `GLXContext`; Filament's engine must be created sharing it. */
    val glxContext: Long = GlxHelper.nSkikoContext(redrawer.field("context") as Long)

    /** Null until skiko's first frame creates it. */
    val context: DirectContext? get() = contextHandler.field("context") as DirectContext?

    /**
     * Runs [block] with skiko's GL context current, holding the AWT drawing-surface lock skiko also
     * draws under, and restores skiko's framebuffer bindings afterwards; null if the surface can't be
     * locked (e.g. the window is gone). UI thread only: skiko's Linux redrawer draws there, so this
     * never races it.
     */
    fun <T> withCurrent(block: () -> T): T? {
        val session = GlxHelper.nBegin(canvas, glxContext)
        if (session == 0L) return null
        try {
            return block()
        } finally {
            GlxHelper.nEnd(session)
            // nEnd rebinds skiko's framebuffers behind Skia's state cache.
            context?.resetGLAll()
        }
    }

    companion object {
        fun find(window: Window?): SkikoGlx? {
            val layer = window?.findSkiaLayer() ?: return null
            val redrawer = layer.field("redrawerManager")!!.field("redrawer") ?: return null
            if (redrawer.javaClass.simpleName != "LinuxOpenGLRedrawer") return null
            return SkikoGlx(redrawer, layer.field("backedLayer")!!)
        }
    }
}
