package io.github.erkko68.filament.jni

/**
 * Windows-only helpers for rendering Filament's Vulkan backend into D3D12 textures skiko can wrap:
 * a Filament platform on skiko's GPU whose swap chains are shared D3D12 textures.
 */
object D3DHelper {
    /**
     * A Filament platform on the GPU of skiko's `Direct3DRedrawer.device`, whose layer has [hwnd] as
     * its content handle; 0 if skiko's device doesn't look as expected.
     */
    @JvmStatic external fun nCreatePlatform(skikoDevice: Long, hwnd: Long): Long
    /** A Vulkan `Engine*` on [platform], or 0; the engine doesn't own the platform. */
    @JvmStatic external fun nCreateEngine(platform: Long): Long
    /** Deletes a platform from [nCreatePlatform], once its engine is destroyed. */
    @JvmStatic external fun nDestroyPlatform(platform: Long)
    /** Whether the engine runs on skiko's GPU with the extensions to import D3D12 textures and fences. */
    @JvmStatic external fun nIsInteropReady(platform: Long): Boolean
    /**
     * The native window to create a Filament SwapChain with: two shared `width`×`height` RGBA8
     * textures, rendered alternately. Filament's destroySwapChain frees it; 0 on failure.
     */
    @JvmStatic external fun nCreateSwapChain(platform: Long, width: Int, height: Int): Long
    /** The `ID3D12Resource*` behind image [index] of [swapChain]. */
    @JvmStatic external fun nResource(swapChain: Long, index: Int): Long
    /** How many frames presented to [swapChain] the GPU has finished rendering. */
    @JvmStatic external fun nCompletedFrames(swapChain: Long): Long
}
