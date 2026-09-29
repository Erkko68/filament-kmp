package io.github.erkko68.filament.compose.internal.target

import io.github.erkko68.filament.Engine
import java.awt.Window

/**
 * skiko draws with Direct3D 12 here and Filament has no D3D backend. Plan: allocate a shared D3D12
 * texture on skiko's adapter, open it in Filament's Vulkan backend (VK_KHR_external_memory_win32),
 * wrap it with `BackendRenderTarget.makeDirect3D`, and sync through a shared D3D12 fence / VkSemaphore.
 */
internal fun windowsOffscreenTarget(engine: Engine, window: Window?, width: Int, height: Int): OffscreenTarget =
    TODO("GPU-to-GPU rendering is not implemented on Windows yet")
