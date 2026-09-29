package io.github.erkko68.filament.compose.internal.target.metal

import io.github.erkko68.filament.compose.internal.target.field
import io.github.erkko68.filament.compose.internal.target.skikoRedrawer
import java.awt.Window
import org.jetbrains.skia.DirectContext

/**
 * skiko's Metal state for one window. Skia GPU images only draw on the context that made them,
 * so Filament frames must be wrapped on the context Compose renders with.
 */
// ponytail: reflects skiko 0.150 internals (SkiaLayer.redrawerManager.redrawer → MetalRedrawer);
// 0.152 moves the context to MetalRedrawer.context. Drop once Compose exposes its context.
internal class SkikoMetal private constructor(private val redrawer: Any) {
    private val contextHandler: Any = redrawer.field("contextHandler")!!

    /** skiko's `id<MTLDevice>`; textures shared with Skia must live on it. */
    val devicePtr: Long = redrawer.field("adapter")!!.field("ptr") as Long

    /** Null until skiko's first frame creates it. */
    val context: DirectContext? get() = contextHandler.field("context") as DirectContext?

    /** skiko renders off the UI thread and [DirectContext] isn't thread-safe. */
    val lock: Any get() = redrawer.field("drawLock")!!

    companion object {
        fun find(window: Window?): SkikoMetal? {
            val redrawer = skikoRedrawer(window) ?: return null
            if (redrawer.javaClass.simpleName != "MetalRedrawer") return null
            return SkikoMetal(redrawer)
        }
    }
}
