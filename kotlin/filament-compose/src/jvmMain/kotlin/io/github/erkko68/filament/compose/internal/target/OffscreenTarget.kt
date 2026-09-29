package io.github.erkko68.filament.compose.internal.target

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Renderer
import io.github.erkko68.filament.View
import io.github.erkko68.filament.compose.internal.target.d3d.D3DOffscreenTarget
import io.github.erkko68.filament.compose.internal.target.glx.GlxOffscreenTarget
import io.github.erkko68.filament.compose.internal.target.metal.MetalOffscreenTarget
import java.awt.Window
import org.jetbrains.skia.Image

/**
 * Where Filament renders the frames Compose shows: a texture shared with Compose's own Skia context
 * (GPU-to-GPU, opt-in) or CPU readback.
 */
internal interface OffscreenTarget : AutoCloseable {
    /**
     * Renders [view] and returns the newest frame the GPU has finished, or null to keep the
     * current one. The caller owns the returned [Image]; it outlives this target.
     */
    fun renderFrame(renderer: Renderer, view: View, frameTimeNanos: Long): Image?

    /** Whether the returned images' rows run bottom-up, so they must be drawn flipped. */
    val bottomUp: Boolean get() = false
}

internal fun OffscreenTarget(engine: Engine, window: Window?, width: Int, height: Int, transparent: Boolean): OffscreenTarget {
    val readback = { ReadbackOffscreenTarget(engine, width, height, transparent) }
    if (!GpuFrameSharing.enabledFor(engine)) return readback()
    val gpu = GpuFrameSharing.guard("creating the GPU-to-GPU target", window, engine, {
        when (DesktopOs.current) {
            DesktopOs.MACOS -> MetalOffscreenTarget.create(engine, window, width, height)
            DesktopOs.WINDOWS -> D3DOffscreenTarget.create(engine, window, width, height)
            DesktopOs.LINUX -> GlxOffscreenTarget.create(engine, window, width, height)
            DesktopOs.OTHER -> unavailable("no GPU-to-GPU path on ${System.getProperty("os.name")}")
        }
    }, { null }) ?: return readback()
    return FallbackOffscreenTarget(gpu, window, engine, readback)
}

/** Renders with [current] (a GPU-to-GPU target) until it throws, then reports it and reads back instead. */
private class FallbackOffscreenTarget(
    private var current: OffscreenTarget,
    private val window: Window?,
    private val engine: Engine,
    private val readback: () -> OffscreenTarget,
) : OffscreenTarget {
    private var onGpu = true

    override val bottomUp: Boolean get() = current.bottomUp

    override fun renderFrame(renderer: Renderer, view: View, frameTimeNanos: Long): Image? {
        if (!onGpu) return current.renderFrame(renderer, view, frameTimeNanos)
        return GpuFrameSharing.guard("rendering a frame", window, engine, {
            current.renderFrame(renderer, view, frameTimeNanos)
        }, {
            onGpu = false
            // A target that failed mid-frame may fail to close too; the readback replaces it either way.
            runCatching { current.close() }
            current = readback()
            current.renderFrame(renderer, view, frameTimeNanos)
        })
    }

    override fun close() = current.close()
}
