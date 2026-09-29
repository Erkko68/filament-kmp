package io.github.erkko68.filament.compose.internal.target.glx

import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.compose.internal.target.unavailable
import io.github.erkko68.filament.jni.GlxHelper
import java.awt.Window
import java.util.Collections
import java.util.WeakHashMap

/**
 * OpenGL resources belong to a context's share group, so on Linux the Engine itself must be created
 * sharing skiko's GLX context — one window's, since each window has its own context.
 */
internal object GlxEngines {
    private val shared = Collections.synchronizedMap(WeakHashMap<Engine, Long>())

    /** An OpenGL engine sharing [window]'s skiko context. */
    fun create(backend: Engine.Backend, window: Window?): Engine {
        if (backend != Engine.Backend.DEFAULT && backend != Engine.Backend.OPENGL) {
            unavailable("on Linux it needs Engine.Backend.DEFAULT or OPENGL, not $backend")
        }
        val skiko = SkikoGlx.find(window) ?: unavailable("Compose isn't rendering this window with OpenGL")
        val bridge = skiko.withCurrent { GlxHelper.nCreateBridgeContext() }?.takeIf { it != 0L }
            ?: error("couldn't create a GLX context in skiko's share group")
        try {
            return Engine.Builder()
                .backend(Engine.Backend.OPENGL)
                .sharedContext(bridge)
                .build()
                .also { shared[it] = skiko.glxContext }
        } finally {
            // Filament only reads the shared context while its driver starts, which build() waits for.
            skiko.withCurrent { GlxHelper.nDestroyBridgeContext(bridge) }
        }
    }

    fun sharesContext(engine: Engine, skiko: SkikoGlx): Boolean = shared[engine] == skiko.glxContext
}
