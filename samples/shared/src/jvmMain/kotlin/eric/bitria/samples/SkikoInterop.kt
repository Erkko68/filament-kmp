package eric.bitria.samples

import org.jetbrains.skia.BackendRenderTarget
import org.jetbrains.skia.DirectContext
import java.lang.reflect.Method

/**
 * Helper object to access internal Skiko/Skia handles via reflection.
 * This is necessary for zero-copy texture sharing between Filament and Skia.
 */
object SkikoInterop {
    private val isMacOS = System.getProperty("os.name", "").startsWith("Mac", ignoreCase = true)

    // Walks the superclass chain to find and read a field by name.
    private fun reflectField(obj: Any, name: String): Any? = runCatching {
        var cls: Class<*>? = obj.javaClass
        while (cls != null) {
            cls.declaredFields.firstOrNull { it.name == name }
                ?.apply { isAccessible = true }
                ?.let { return@runCatching it.get(obj) }
            cls = cls.superclass
        }
        null
    }.getOrNull()

    private val metalBackendRTMethod: Method? by lazy {
        runCatching {
            Class.forName("org.jetbrains.skia.BackendRenderTargetKt")
                .methods.first { it.name == "access\$_nMakeMetal" }
        }.getOrNull()
    }
    
    private val vulkanBackendRTMethod: Method? by lazy {
        runCatching {
            Class.forName("org.jetbrains.skia.BackendRenderTargetKt")
                .methods.first { it.name == "access\$_nMakeVulkan" }
        }.getOrNull()
    }
    
    private val backendRTConstructor by lazy {
        runCatching {
            BackendRenderTarget::class.java.getDeclaredConstructor(Long::class.java)
                .apply { isAccessible = true }
        }.getOrNull()
    }

    private fun makeMetalBackendRT(width: Int, height: Int, ptr: Long): BackendRenderTarget? = runCatching {
        val handle = metalBackendRTMethod!!.invoke(null, width, height, ptr) as Long
        backendRTConstructor!!.newInstance(handle) as BackendRenderTarget
    }.getOrNull()

    private fun makeVulkanBackendRT(width: Int, height: Int, imageInfoPtr: Long): BackendRenderTarget? = runCatching {
        val handle = vulkanBackendRTMethod!!.invoke(null, width, height, imageInfoPtr) as Long
        backendRTConstructor!!.newInstance(handle) as BackendRenderTarget
    }.getOrNull()

    fun makeBackendRT(width: Int, height: Int, handle: Long): BackendRenderTarget? =
        if (isMacOS) makeMetalBackendRT(width, height, handle)
        else         makeVulkanBackendRT(width, height, handle)

    // Returns the MTLDevice* or VkDevice* used by Skiko.
    fun skikoDevicePtrs(): Pair<Long, Long> =
        if (isMacOS) Pair(skikoMetalDevicePtr(), 0L)
        else         skikoVulkanDevicePtrs()

    private fun skikoMetalDevicePtr(): Long = runCatching {
        val gpuPriority = Class.forName("org.jetbrains.skiko.GpuPriority")
        val auto = gpuPriority.getMethod("valueOf", String::class.java).invoke(null, "Auto")
        val adapter = Class.forName("org.jetbrains.skiko.MetalApiKt")
            .getDeclaredMethod("chooseMetalAdapter", gpuPriority).invoke(null, auto)
        adapter.javaClass.getDeclaredField("ptr").apply { isAccessible = true }.get(adapter) as Long
    }.getOrElse { 0L }

    private fun skikoVulkanDevicePtrs(): Pair<Long, Long> {
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

    private var cachedSkikoCtx: DirectContext? = null
    
    fun skikoContext(): DirectContext? {
        if (cachedSkikoCtx != null) return cachedSkikoCtx
        return findSkikoContext().also { cachedSkikoCtx = it }
    }

    private fun findSkikoContext(): DirectContext? {
        fun search(c: java.awt.Component): DirectContext? {
            runCatching {
                val redrawerMgr = reflectField(c, "redrawerManager") ?: return@runCatching
                val redrawer = reflectField(redrawerMgr, "redrawer") ?: return@runCatching
                val contextHandler = reflectField(redrawer, "contextHandler") ?: return@runCatching
                val ctx = reflectField(contextHandler, "context")
                if (ctx is DirectContext) return ctx
            }
            return if (c is java.awt.Container) c.components.firstNotNullOfOrNull { search(it) } else null
        }
        return java.awt.Window.getWindows().firstNotNullOfOrNull { search(it) }
    }
}
