package io.github.erkko68.filament.compose.internal.target.d3d

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.InternalFilamentApi
import io.github.erkko68.filament.jni.D3DHelper
import java.awt.Window
import java.util.Collections
import java.util.IdentityHashMap

/**
 * Filament has no Direct3D backend, so on Windows the Engine runs Vulkan on skiko's GPU through a
 * platform whose swap chains are D3D12 textures skiko can wrap ([D3DOffscreenTarget]).
 */
internal object D3DEngines {
    private val platforms = Collections.synchronizedMap(IdentityHashMap<Engine, Long>())

    /** A Vulkan engine on [window]'s skiko GPU, or null when skiko isn't drawing it with Direct3D. */
    @OptIn(InternalFilamentApi::class)
    fun create(backend: Engine.Backend, window: Window?): Engine? {
        if (backend != Engine.Backend.DEFAULT && backend != Engine.Backend.VULKAN) return null
        val skiko = SkikoD3D.find(window) ?: return null
        val platform = D3DHelper.nCreatePlatform(skiko.devicePtr, skiko.hwnd).takeIf { it != 0L } ?: return null
        val handle = D3DHelper.nCreateEngine(platform)
        if (handle == 0L) {
            D3DHelper.nDestroyPlatform(platform)
            return null
        }
        return Engine(handle).also { platforms[it] = platform }
    }

    /** [engine]'s platform, if [create] made it. */
    fun platformOf(engine: Engine): Long? = platforms[engine]

    /** Destroys [engine] (if still alive), then the platform Filament doesn't own. */
    fun destroy(engine: Engine) {
        val platform = platforms.remove(engine) ?: return
        engine.destroy()
        D3DHelper.nDestroyPlatform(platform)
    }
}
