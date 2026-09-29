package io.github.erkko68.filament.compose.internal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.awt.LocalAwtWindow
import io.github.erkko68.filament.Engine
import io.github.erkko68.filament.Filament
import io.github.erkko68.filament.compose.internal.target.DesktopOs
import io.github.erkko68.filament.compose.internal.target.GpuFrameSharing
import io.github.erkko68.filament.compose.internal.target.d3d.D3DEngines
import io.github.erkko68.filament.compose.internal.target.glx.GlxEngines
import io.github.erkko68.filament.compose.internal.target.unavailable

/**
 * With GPU-to-GPU frame sharing on, the engine is built to reach the window's GPU context: on Linux
 * it shares skiko's GL context, on Windows it runs on skiko's D3D12 GPU. Otherwise (or if that
 * fails) it's a plain engine, and frames go through CPU readback.
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal actual fun rememberPlatformEngine(backend: Engine.Backend): Engine {
    val window = LocalAwtWindow.current
    return remember(backend) {
        Filament.init()
        val shared = if (!GpuFrameSharing.enabled) null else {
            GpuFrameSharing.guard("creating the Filament engine", window, null, {
                when (DesktopOs.current) {
                    // skiko's MTLTextures are sampleable by any engine on the same (default) GPU.
                    DesktopOs.MACOS -> Engine.create(backend)
                    DesktopOs.WINDOWS -> D3DEngines.create(backend, window)
                    DesktopOs.LINUX -> GlxEngines.create(backend, window)
                    DesktopOs.OTHER -> unavailable("no GPU-to-GPU path on ${System.getProperty("os.name")}")
                }
            }, { null })
        }
        // D3DEngines.destroy also frees the Windows engine's platform, which Filament doesn't own.
        Owned(shared?.also(GpuFrameSharing::optIn) ?: Engine.create(backend), emptyList(), D3DEngines::destroy)
    }.value
}
