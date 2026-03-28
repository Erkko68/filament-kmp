#include <stdint.h>

#include "filament/Engine.h"
#include "filament/Renderer.h"

// Verifies Renderer API is consumable from C and composes with Engine lifecycle.
void fila_renderer_module_compile_only(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaRenderer* renderer = FilaEngine_createRenderer(engine);
    FilaSwapChain* swapChain = (FilaSwapChain*)0;
    const FilaView* view = (const FilaView*)0;

    (void)FilaRenderer_beginFrame(renderer, swapChain, (uint64_t)0);
    FilaRenderer_render(renderer, view);
    FilaRenderer_endFrame(renderer);
    FilaEngine_destroyRenderer(engine, renderer);
}

