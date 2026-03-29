#include "filament/Engine.h"
#include "filament/SwapChain.h"

// Verifies SwapChain API is consumable from C and composes with Engine lifecycle.
void fila_swapchain_module_compile_only(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaSwapChain* swapChain = FilaEngine_createSwapChainHeadless(engine, 640u, 480u, 0u);

    (void)FilaSwapChain_isProtectedContentSupported(engine);
    (void)FilaSwapChain_isSRGBSwapChainSupported(engine);
    (void)FilaSwapChain_isMSAASwapChainSupported(engine, 4u);
    (void)FilaSwapChain_getNativeWindow(swapChain);
    FilaEngine_destroySwapChain(engine, swapChain);
}

