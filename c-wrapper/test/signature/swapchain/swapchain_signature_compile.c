#include <stdbool.h>
#include <stdint.h>

#include "filament/SwapChain.h"

// Function pointer assignments lock exported C signatures.
static bool (*g_swapchain_is_protected_supported)(FilaEngine*) = FilaSwapChain_isProtectedContentSupported;
static bool (*g_swapchain_is_srgb_supported)(FilaEngine*) = FilaSwapChain_isSRGBSwapChainSupported;
static bool (*g_swapchain_is_msaa_supported)(FilaEngine*, uint32_t) = FilaSwapChain_isMSAASwapChainSupported;
static void* (*g_swapchain_get_native_window)(const FilaSwapChain*) = FilaSwapChain_getNativeWindow;

void fila_swapchain_signature_compile_only(void) {
    (void)g_swapchain_is_protected_supported;
    (void)g_swapchain_is_srgb_supported;
    (void)g_swapchain_is_msaa_supported;
    (void)g_swapchain_get_native_window;
}

