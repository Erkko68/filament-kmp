package eric.bitria.samples.skiko

import org.jetbrains.skia.BackendRenderTarget
import org.jetbrains.skia.DirectContext
import java.lang.reflect.Method

/**
 * Skiko Metal (macOS) interop.
 *
 * Each method represents what the ideal Skiko public API *should* look like.
 * The implementations use reflection until Skiko exposes these natively.
 *
 * Ideal future API reference:
 *   BackendRenderTarget.makeMetal(width, height, mtlTexture)
 *   DirectContext.makeMetal() or SkiaLayer.directContext
 *   MetalAdapter.choose(GpuPriority.Auto).devicePtr
 */
internal object SkikoMacOS {

    // --- BackendRenderTarget.makeMetal(width, height, mtlTexture: Long) ---

    private val nMakeMetalMethod: Method? by lazy {
        runCatching {
            Class.forName("org.jetbrains.skia.BackendRenderTargetKt")
                .methods.first { it.name == "access\$_nMakeMetal" }
        }.getOrNull()
    }

    private val backendRTConstructor by lazy {
        runCatching {
            BackendRenderTarget::class.java
                .getDeclaredConstructor(Long::class.java)
                .apply { isAccessible = true }
        }.getOrNull()
    }

    /**
     * Ideal: BackendRenderTarget.makeMetal(width, height, mtlTexture)
     */
    fun makeBackendRenderTarget(width: Int, height: Int, mtlTexture: Long): BackendRenderTarget? =
        runCatching {
            val handle = nMakeMetalMethod!!.invoke(null, width, height, mtlTexture) as Long
            backendRTConstructor!!.newInstance(handle) as BackendRenderTarget
        }.getOrNull()

    // --- DirectContext (SkiaLayer.directContext or DirectContext.makeMetal()) ---

    /**
     * Ideal: SkiaLayer.directContext (or ComposeScene.directContext)
     *
     * Walks the live AWT window tree to find the Skiko contextHandler that holds
     * the active Metal DirectContext.
     */
    /**
     * Ideal: SkiaLayer.directContext (or ComposeScene.directContext)
     *
     * Direct path (no recursive AWT traversal):
     *   ComposeWindow.composePanel → direct children → find redrawerManager
     *   → redrawer → contextHandler → context
     *
     * Discovered path:
     *   ComposeWindow
     *     .composePanel (ComposeWindowPanel : JLayeredPane)
     *       [child] WindowSkiaLayerComponent$contentComponent$1
     *         .redrawerManager (RedrawerManager)
     *           .redrawer (MetalRedrawer)
     *             .contextHandler (MetalContextHandler)
     *               .context (DirectContext)
     */
    fun getDirectContext(): DirectContext? = runCatching {
        val window = java.awt.Window.getWindows().firstOrNull() ?: return null
        val composePanel = reflectField(window, "composePanel") as? java.awt.Container ?: return null
        for (child in composePanel.components) {
            val mgr = reflectField(child, "redrawerManager") ?: continue
            val redrawer = reflectField(mgr, "redrawer") ?: continue
            val ctxHandler = reflectField(redrawer, "contextHandler") ?: continue
            val ctx = reflectField(ctxHandler, "context")
            if (ctx is DirectContext) return ctx
        }
        null
    }.getOrNull()

    // Fallback: full recursive AWT component tree search.
    // Uncomment if the direct path above breaks after a Skiko/Compose update.
    //
    // fun getDirectContext(): DirectContext? {
    //     fun search(c: java.awt.Component): DirectContext? {
    //         runCatching {
    //             val redrawerMgr = reflectField(c, "redrawerManager") ?: return@runCatching
    //             val redrawer = reflectField(redrawerMgr, "redrawer") ?: return@runCatching
    //             val ctxHandler = reflectField(redrawer, "contextHandler") ?: return@runCatching
    //             val ctx = reflectField(ctxHandler, "context")
    //             if (ctx is DirectContext) return ctx
    //         }
    //         return if (c is java.awt.Container) c.components.firstNotNullOfOrNull { search(it) } else null
    //     }
    //     return java.awt.Window.getWindows().firstNotNullOfOrNull { search(it) }
    // }

    // --- MetalAdapter.choose(GpuPriority.Auto).devicePtr ---

    /**
     * Ideal: MetalAdapter.choose(GpuPriority.Auto).devicePtr
     *
     * Returns the MTLDevice* pointer used by Skiko's Metal renderer.
     */
    fun getDevicePtr(): Long = runCatching {
        val gpuPriority = Class.forName("org.jetbrains.skiko.GpuPriority")
        val auto = gpuPriority.getMethod("valueOf", String::class.java).invoke(null, "Auto")
        val adapter = Class.forName("org.jetbrains.skiko.MetalApiKt")
            .getDeclaredMethod("chooseMetalAdapter", gpuPriority)
            .invoke(null, auto)
        adapter.javaClass.getDeclaredField("ptr").apply { isAccessible = true }.get(adapter) as Long
    }.getOrElse { 0L }
}
