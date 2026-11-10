package io.github.erkko68.filament.compose.internal.target.d3d

import io.github.erkko68.filament.compose.internal.target.field
import io.github.erkko68.filament.compose.internal.target.findSkiaLayer
import io.github.erkko68.filament.compose.internal.target.skikoRenderer
import java.awt.Window
import org.jetbrains.skia.DirectContext

/**
 * skiko's Direct3D 12 state for one window (`Direct3DRenderer`). Skia GPU images only draw on the
 * context that made them, so Filament frames must be wrapped on the context Compose renders with.
 */
// ponytail: reflects skiko 0.156 internals (Direct3DRenderer.device / context / drawLock) and
// the native DirectXDevice layout behind `device` (checked in D3DHelper). Drop once Compose exposes its context.
internal class SkikoD3D private constructor(private val redrawer: Any, layerHandle: Long) {
    /** skiko's native `DirectXDevice*`. */
    val devicePtr: Long = redrawer.field("device") as Long

    /** The layer's HWND, the first field of [devicePtr]'s struct. */
    val hwnd: Long = layerHandle

    /** Null until skiko's first frame creates it. */
    val context: DirectContext? get() = redrawer.field("context") as DirectContext?

    /** skiko renders off the UI thread and [DirectContext] isn't thread-safe. */
    val lock: Any get() = redrawer.field("drawLock")!!

    companion object {
        fun find(window: Window?): SkikoD3D? {
            val layer = window?.findSkiaLayer() ?: return null
            val redrawer = skikoRenderer(layer) ?: return null
            if (redrawer.javaClass.simpleName != "Direct3DRenderer") return null
            return SkikoD3D(redrawer, layer.contentHandle)
        }
    }
}
