@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package io.github.erkko68.filament.compose.internal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.webgl.rememberWebGLRenderTarget
import androidx.compose.ui.unit.IntSize
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Filament
import io.github.erkko68.filament.onExecuted

// ponytail: a never-rendered 1×1 target only to reach Compose's WebGL context; null without WebGL2 → own canvas.
@Composable
internal actual fun rememberPlatformEngine(backend: Engine.Backend): Engine {
    val shared = rememberWebGLRenderTarget(IntSize(1, 1))
    return remember(backend, shared) {
        Filament.init()
        val engine = checkNotNull(Engine.create(backend, sharedContext = shared?.webGLContext)) { "Failed to create a $backend Engine" }
        if (shared != null) {
            // Skia and Filament each cache the context's GL state: after Filament runs, Skia's is stale, and
            // the queued reset makes Filament's next run start by dropping its own.
            // ponytail: misses a flush Filament does on its own when its command buffer fills (a huge load).
            val resync = { engine.resetBackendState(); shared.markGLStateStale() }
            engine.onExecuted = resync
            resync() // Engine.create ran GL
        }
        Owned(engine, emptyList()) { Engine.destroy(it); shared?.markGLStateStale() }
    }.value
}
