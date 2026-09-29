package io.github.erkko68.filament.compose.internal.target.d3d

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.NativeSurface
import io.github.erkko68.filament.Renderer
import io.github.erkko68.filament.SwapChain
import io.github.erkko68.filament.View
import io.github.erkko68.filament.compose.internal.target.OffscreenTarget
import io.github.erkko68.filament.compose.internal.target.unavailable
import io.github.erkko68.filament.jni.D3DHelper
import java.awt.Window
import org.jetbrains.skia.BackendRenderTarget
import org.jetbrains.skia.ColorSpace
import org.jetbrains.skia.Image
import org.jetbrains.skia.Surface
import org.jetbrains.skia.SurfaceColorFormat
import org.jetbrains.skia.SurfaceOrigin

private const val DXGI_FORMAT_R8G8B8A8_UNORM = 28
private const val IMAGE_COUNT = 2

/**
 * Filament's Vulkan engine ([D3DEngines]) renders into a swap chain of shared D3D12 textures on
 * skiko's GPU, alternating between them, and signals a shared fence as each frame finishes; Skia then
 * wraps that texture on skiko's [org.jetbrains.skia.DirectContext] and snapshots it (a GPU-side copy).
 */
internal class D3DOffscreenTarget private constructor(
    private val engine: Engine,
    private val skiko: SkikoD3D,
    platform: Long,
    private val width: Int,
    private val height: Int,
) : OffscreenTarget {

    private val chain = D3DHelper.nCreateSwapChain(platform, width, height).also {
        check(it != 0L) { "Failed to create ${width}x$height shared D3D12 textures" }
    }
    private val swapChain: SwapChain = engine.createSwapChain(NativeSurface(chain), 0L)
    private val resources = LongArray(IMAGE_COUNT) { D3DHelper.nResource(chain, it) }

    // Frame n (from 1) renders into image (n - 1) % IMAGE_COUNT, the order the platform acquires them in.
    private var rendered = 0L
    private var snapshotted = 0L

    override fun renderFrame(renderer: Renderer, view: View, frameTimeNanos: Long): Image? {
        engine.flush()
        val completed = D3DHelper.nCompletedFrames(chain).coerceAtMost(rendered)
        var image: Image? = null
        if (completed > snapshotted) {
            image = snapshot(((completed - 1) % IMAGE_COUNT).toInt())
            snapshotted = completed
        }

        // Every image holds a frame not yet snapshotted: rendering now would overwrite one.
        if (rendered - snapshotted >= IMAGE_COUNT) return image
        view.renderTarget = null
        if (renderer.beginFrame(swapChain, frameTimeNanos)) {
            renderer.render(view)
            renderer.endFrame()
            rendered++
        }
        return image
    }

    private fun snapshot(index: Int): Image? {
        val context = skiko.context ?: return null
        return synchronized(skiko.lock) {
            // Fresh wrappers each frame: Skia caches snapshots of surfaces it didn't draw to.
            val brt = BackendRenderTarget.makeDirect3D(width, height, resources[index], DXGI_FORMAT_R8G8B8A8_UNORM, 1, 1)
            val surface = Surface.makeFromBackendRenderTarget(
                context, brt, SurfaceOrigin.TOP_LEFT, SurfaceColorFormat.RGBA_8888, ColorSpace.sRGB,
            )
            val image = surface?.makeImageSnapshot()
            surface?.close()
            brt.close()
            // ponytail: CPU-waits for the copy so Filament can't overwrite the texture mid-copy; a GPU
            // wait on the shared fence would avoid it, but skiko's Skia binding exposes no semaphores.
            context.flush()
            context.submit(true)
            image
        }
    }

    override fun close() {
        // Destroying the swap chain frees the shared textures, once the GPU is done with them.
        engine.destroySwapChain(swapChain)
        engine.flushAndWait()
    }

    companion object {
        fun create(engine: Engine, window: Window?, width: Int, height: Int): D3DOffscreenTarget {
            val skiko = SkikoD3D.find(window) ?: unavailable("Compose isn't rendering this window with Direct3D")
            val platform = D3DEngines.platformOf(engine)
                ?: unavailable("the engine must come from rememberFilamentEngine() inside the window that shows it")
            if (!D3DHelper.nIsInteropReady(platform)) {
                unavailable(
                    "the Vulkan driver can't import Compose's D3D12 textures (needs VK_KHR_external_memory_win32, " +
                        "VK_KHR_external_semaphore_win32 and timeline semaphores on Compose's GPU)",
                )
            }
            return D3DOffscreenTarget(engine, skiko, platform, width, height)
        }
    }
}
