package io.github.erkko68.filament.compose.internal.target.metal

import io.github.erkko68.filament.compose.internal.target.field
import io.github.erkko68.filament.compose.internal.target.findSkiaLayer
import io.github.erkko68.filament.compose.internal.target.skikoRenderer
import java.awt.Window
import org.jetbrains.skia.DirectContext

/**
 * skiko's Metal state for one window. Skia GPU images only draw on the context that made them,
 * so Filament frames must be wrapped on the context Compose renders with.
 */
// ponytail: reflects skiko 0.156 internals (MetalRenderer.adapter / context / drawLock).
// Drop once Compose exposes its context.
internal class SkikoMetal private constructor(private val redrawer: Any) {
    /** skiko's `id<MTLDevice>`; textures shared with Skia must live on it. */
    val devicePtr: Long = redrawer.field("adapter")!!.field("ptr") as Long

    /** Null until skiko's first frame creates it. */
    val context: DirectContext? get() = redrawer.field("context") as DirectContext?

    /** skiko renders off the UI thread and [DirectContext] isn't thread-safe. */
    val lock: Any get() = redrawer.field("drawLock")!!

    companion object {
        fun find(window: Window?): SkikoMetal? {
            val redrawer = window?.findSkiaLayer()?.let(::skikoRenderer) ?: return null
            if (redrawer.javaClass.simpleName != "MetalRenderer") return null
            return SkikoMetal(redrawer)
        }
    }
}
