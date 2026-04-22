package eric.bitria.samples.skiko

import org.jetbrains.skia.BackendRenderTarget
import org.jetbrains.skia.DirectContext
import java.lang.reflect.Method

/**
 * Skiko Vulkan (Windows) interop.
 *
 * Each method represents what the ideal Skiko public API *should* look like.
 * The implementations use reflection until Skiko exposes these natively.
 *
 * Ideal future API reference:
 *   BackendRenderTarget.makeVulkan(width, height, imageInfo)
 *   SkiaLayer.directContext
 *   VulkanDevice.device / VulkanDevice.physicalDevice
 *
 * Note: Windows uses the same Vulkan backend as Linux. This file is kept
 * separate so that any Windows-specific divergence (e.g. DXGI interop,
 * different field names in a future Skiko version) can be handled here
 * without touching the Linux implementation.
 */
internal object SkikoWindows {

    // --- BackendRenderTarget.makeVulkan(width, height, imageInfo: Long) ---

    private val nMakeVulkanMethod: Method? by lazy {
        runCatching {
            Class.forName("org.jetbrains.skia.BackendRenderTargetKt")
                .methods.first { it.name == "access\$_nMakeVulkan" }
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
     * Ideal: BackendRenderTarget.makeVulkan(width, height, imageInfo)
     */
    fun makeBackendRenderTarget(width: Int, height: Int, imageInfoPtr: Long): BackendRenderTarget? =
        runCatching {
            val handle = nMakeVulkanMethod!!.invoke(null, width, height, imageInfoPtr) as Long
            backendRTConstructor!!.newInstance(handle) as BackendRenderTarget
        }.getOrNull()

    // --- DirectContext (SkiaLayer.directContext) ---

    /**
     * Ideal: SkiaLayer.directContext (or ComposeScene.directContext)
     *
     * Walks the live AWT window tree to find the Skiko contextHandler that holds
     * the active Vulkan DirectContext.
     */
    fun getDirectContext(): DirectContext? {
        fun search(c: java.awt.Component): DirectContext? {
            runCatching {
                val redrawerMgr = reflectField(c, "redrawerManager") ?: return@runCatching
                val redrawer = reflectField(redrawerMgr, "redrawer") ?: return@runCatching
                val ctxHandler = reflectField(redrawer, "contextHandler") ?: return@runCatching
                val ctx = reflectField(ctxHandler, "context")
                if (ctx is DirectContext) return ctx
            }
            return if (c is java.awt.Container) c.components.firstNotNullOfOrNull { search(it) } else null
        }
        return java.awt.Window.getWindows().firstNotNullOfOrNull { search(it) }
    }

    // --- VulkanDevice.device / VulkanDevice.physicalDevice ---

    /**
     * Ideal: VulkanDevice.device and VulkanDevice.physicalDevice
     *
     * Returns (VkDevice*, VkPhysicalDevice*) from Skiko's Vulkan renderer.
     */
    fun getDevicePtrs(): Pair<Long, Long> {
        fun search(c: java.awt.Component): Pair<Long, Long>? {
            runCatching {
                val redrawerMgr = reflectField(c, "redrawerManager") ?: return@runCatching
                val redrawer    = reflectField(redrawerMgr, "redrawer") ?: return@runCatching
                val vulkanDev   = reflectField(redrawer, "device") ?: return@runCatching
                val dev  = reflectField(vulkanDev, "device")         as? Long ?: return@runCatching
                val phys = reflectField(vulkanDev, "physicalDevice") as? Long ?: return@runCatching
                return Pair(dev, phys)
            }
            return if (c is java.awt.Container) c.components.firstNotNullOfOrNull { search(it) } else null
        }
        return java.awt.Window.getWindows().firstNotNullOfOrNull { search(it) } ?: Pair(0L, 0L)
    }
}
