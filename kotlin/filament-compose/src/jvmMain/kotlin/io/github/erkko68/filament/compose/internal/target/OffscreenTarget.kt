package io.github.erkko68.filament.compose.internal.target

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Renderer
import io.github.erkko68.filament.View
import io.github.erkko68.filament.compose.internal.target.metal.MetalOffscreenTarget
import java.awt.Window
import org.jetbrains.skia.Image

/**
 * GPU-to-GPU bridge: Filament renders into a texture shared with Compose's own Skia context,
 * so frames reach the screen without a CPU readback.
 */
internal interface OffscreenTarget : AutoCloseable {
    /**
     * Renders [view] and returns the newest frame the GPU has finished, or null to keep the
     * current one. The caller owns the returned [Image]; it is a GPU copy that outlives this target.
     */
    fun renderFrame(renderer: Renderer, view: View, frameTimeNanos: Long): Image?
}

internal fun OffscreenTarget(engine: Engine, window: Window?, width: Int, height: Int): OffscreenTarget {
    val os = System.getProperty("os.name").orEmpty().lowercase()
    return when {
        "mac" in os -> MetalOffscreenTarget.create(engine, window, width, height)
        "win" in os -> windowsOffscreenTarget(engine, window, width, height)
        else -> linuxOffscreenTarget(engine, window, width, height)
    }
}
