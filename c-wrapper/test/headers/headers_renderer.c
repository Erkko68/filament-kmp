#include <stdint.h>

#include "filament/Engine.h"
#include "filament/Renderer.h"

// Verifies Renderer API is consumable from C and composes with Engine lifecycle.
void test_headers_renderer(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaRenderer* renderer = FilaEngine_createRenderer(engine);
    FilaSwapChain* swapChain = (FilaSwapChain*)0;
    const FilaView* view = (const FilaView*)0;
    FilaRenderTarget* renderTarget = (FilaRenderTarget*)0;
    FilaPixelBufferDescriptor* pixelBuffer = (FilaPixelBufferDescriptor*)0;
    FilaViewport viewport = {0, 0, 1u, 1u};

    FilaRendererDisplayInfo displayInfo;
    FilaRendererFrameRateOptions frameRate;
    FilaRendererClearOptions clearOptions;
    FilaRendererFrameInfo frameInfos[2];

    FilaRendererDisplayInfo_setDefaults(&displayInfo);
    FilaRendererFrameRateOptions_setDefaults(&frameRate);
    FilaRendererClearOptions_setDefaults(&clearOptions);

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

    FilaRenderer_setDisplayInfo(renderer, &displayInfo);
    (void)FilaRenderer_getEngine(renderer);
    (void)FilaRenderer_getEngineConst(renderer);
    FilaRenderer_setFrameRateOptions(renderer, &frameRate);
    FilaRenderer_setClearOptions(renderer, &clearOptions);
    (void)FilaRenderer_getClearOptions(renderer, &clearOptions);
    FilaRenderer_setVsyncTime(renderer, 0u);
    FilaRenderer_skipFrame(renderer, 0u);
    (void)FilaRenderer_shouldRenderFrame(renderer);
    FilaRenderer_setPresentationTime(renderer, 0);
    FilaRenderer_skipNextFrames(renderer, 2u);
    (void)FilaRenderer_getFrameToSkipCount(renderer);
    (void)FilaRenderer_getUserTime(renderer);
    FilaRenderer_resetUserTime(renderer);
    (void)FilaRenderer_getMaxFrameHistorySize(renderer);
    (void)FilaRenderer_getFrameInfoHistory(renderer, 2u, frameInfos, 2u);

    FilaEngine_destroyRenderer(engine, renderer);
}
