#include <stdint.h>

#include "filament/Engine.h"
#include "filament/Renderer.h"

// Verifies Renderer API is consumable from C and composes with Engine lifecycle.
void fila_renderer_module_compile_only(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaRenderer* renderer = FilaEngine_createRenderer(engine);
    FilaSwapChain* swapChain = (FilaSwapChain*)0;
    const FilaView* view = (const FilaView*)0;
    FilaRenderTarget* renderTarget = (FilaRenderTarget*)0;
    FilaPixelBufferDescriptor* pixelBuffer = (FilaPixelBufferDescriptor*)0;
    FilaViewport viewport = {0, 0, 1u, 1u};

    (void)FilaRenderer_beginFrame(renderer, swapChain, (uint64_t)0);
    FilaRenderer_render(renderer, view);
    FilaRenderer_endFrame(renderer);
    FilaRenderer_copyFrame(
        renderer,
        swapChain,
        viewport,
        viewport,
        FILA_RENDERER_COPY_FRAME_COMMIT | FILA_RENDERER_COPY_FRAME_CLEAR);
    FilaRenderer_readPixels(renderer, 0u, 0u, 1u, 1u, pixelBuffer);
    FilaRenderer_readPixelsRenderTarget(renderer, renderTarget, 0u, 0u, 1u, 1u, pixelBuffer);
    FilaRenderer_renderStandaloneView(renderer, view);
    FilaEngine_destroyRenderer(engine, renderer);
}
