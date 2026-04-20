package eric.bitria.samples.skiko

import org.jetbrains.skia.BackendRenderTarget
import org.jetbrains.skia.DirectContext

// Shared reflection helper used by all three platform objects.
internal fun reflectField(obj: Any, name: String): Any? = runCatching {
    var cls: Class<*>? = obj.javaClass
    while (cls != null) {
        cls.declaredFields.firstOrNull { it.name == name }
            ?.apply { isAccessible = true }
            ?.let { return@runCatching it.get(obj) }
        cls = cls.superclass
    }
    null
}.getOrNull()

private val os = System.getProperty("os.name", "").lowercase()
private val isMacOS   = os.startsWith("mac")
private val isWindows = os.startsWith("win")
// Linux is the fallback

/**
 * Platform-agnostic entry point. Routes each call to the correct
 * platform object (SkikoMacOS / SkikoWindows / SkikoLinux).
 *
 * To migrate off reflection once Skiko exposes these officially:
 * replace the body of each delegation with the real API call —
 * the signatures here stay the same.
 */
object SkikoInterop {

    /**
     * Returns the Skia GPU context active in the current window.
     *
     * Ideal: SkiaLayer.directContext
     */
    fun getDirectContext(): DirectContext? = when {
        isMacOS   -> SkikoMacOS.getDirectContext()
        isWindows -> SkikoWindows.getDirectContext()
        else      -> SkikoLinux.getDirectContext()
    }

    /**
     * Returns (devicePtr, physicalDevicePtr) for the GPU backend in use.
     *
     * macOS:          (MTLDevice*, 0)
     * Windows/Linux:  (VkDevice*, VkPhysicalDevice*)
     *
     * Ideal: MetalAdapter.choose().devicePtr  /  VulkanDevice.device + physicalDevice
     */
    fun getDevicePtrs(): Pair<Long, Long> = when {
        isMacOS   -> Pair(SkikoMacOS.getDevicePtr(), 0L)
        isWindows -> SkikoWindows.getDevicePtrs()
        else      -> SkikoLinux.getDevicePtrs()
    }

    /**
     * Creates a Skia BackendRenderTarget wrapping [textureHandle].
     *
     * On macOS [textureHandle] is an MTLTexture ID.
     * On Linux/Windows it is a pointer to a VkImageInfo struct.
     *
     * Ideal: BackendRenderTarget.makeMetal(...)  /  BackendRenderTarget.makeVulkan(...)
     */
    fun makeBackendRenderTarget(width: Int, height: Int, textureHandle: Long): BackendRenderTarget? =
        when {
            isMacOS   -> SkikoMacOS.makeBackendRenderTarget(width, height, textureHandle)
            isWindows -> SkikoWindows.makeBackendRenderTarget(width, height, textureHandle)
            else      -> SkikoLinux.makeBackendRenderTarget(width, height, textureHandle)
        }
}
