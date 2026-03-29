#include <stdio.h>

#include "filament/Engine.h"
#include "filament/SwapChain.h"

int main(void) {
    printf("Running engine+swapchain smoke program...\n");

    FilaEngine* engine = FilaEngine_create();
    if (!engine) {
        printf("Engine creation failed\n");
        return 1;
    }

    (void)FilaSwapChain_isProtectedContentSupported(engine);
    (void)FilaSwapChain_isSRGBSwapChainSupported(engine);
    (void)FilaSwapChain_isMSAASwapChainSupported(engine, 4u);

    FilaSwapChain* swapChain = FilaEngine_createSwapChainHeadless(engine, 320u, 240u, 0u);
    if (!swapChain) {
        printf("Headless swap chain creation failed\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    (void)FilaSwapChain_getNativeWindow(swapChain);

    FilaEngine_destroySwapChain(engine, swapChain);
    FilaEngine_destroy(&engine);

    printf("Engine+swapchain smoke program completed\n");
    return 0;
}

