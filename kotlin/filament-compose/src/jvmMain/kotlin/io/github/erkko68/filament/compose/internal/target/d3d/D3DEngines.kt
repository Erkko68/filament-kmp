package io.github.erkko68.filament.compose.internal.target.d3d

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.InternalFilamentApi
import io.github.erkko68.filament.compose.internal.target.unavailable
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

    /** A Vulkan engine on [window]'s skiko GPU. */
    @OptIn(InternalFilamentApi::class)
    fun create(backend: Engine.Backend, window: Window?): Engine {
        if (backend != Engine.Backend.DEFAULT && backend != Engine.Backend.VULKAN) {
            unavailable("on Windows it needs Engine.Backend.DEFAULT or VULKAN, not $backend")
        }
        val skiko = SkikoD3D.find(window) ?: unavailable("Compose isn't rendering this window with Direct3D")
        val platform = D3DHelper.nCreatePlatform(skiko.devicePtr, skiko.hwnd)
        check(platform != 0L) { "skiko's Direct3DRedrawer.device doesn't have the expected native layout" }
        val handle = D3DHelper.nCreateEngine(platform)
        if (handle == 0L) {
            D3DHelper.nDestroyPlatform(platform)
            error("Filament couldn't create a Vulkan engine on skiko's GPU")
        }
        return Engine(handle).also { platforms[it] = platform }
    }

    /** [engine]'s platform, if [create] made it. */
    fun platformOf(engine: Engine): Long? = platforms[engine]

    /** Destroys [engine], then its platform if [create] made it (Filament doesn't own it). */
    fun destroy(engine: Engine) {
        engine.destroy()
        platforms.remove(engine)?.let(D3DHelper::nDestroyPlatform)
    }
}
