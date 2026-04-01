#include <stdio.h>

#include "filament/Engine.h"
#include "filament/Renderer.h"
#include "filament/Scene.h"

int main(void) {
    printf("Running engine+scene+renderer functionality program...\n");

    FilaEngine* engine = FilaEngine_create();
    if (!engine) {
        printf("Engine creation failed\n");
        return 1;
    }

    FilaRenderer* renderer = FilaEngine_createRenderer(engine);
    if (!renderer) {
        printf("Renderer creation failed\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaScene* scene = FilaEngine_createScene(engine);
    if (!scene) {
        printf("Scene creation failed\n");
        FilaEngine_destroyRenderer(engine, renderer);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaEntity sampleEntity = 123;
    FilaScene_addEntity(scene, sampleEntity);
    printf("Scene entity count: %zu\n", FilaScene_getEntityCount(scene));
    FilaScene_removeAllEntities(scene);

    {
        FilaRendererDisplayInfo displayInfo;
        FilaRendererFrameRateOptions frameRate;
        FilaRendererClearOptions clearOptions;

        FilaRendererDisplayInfo_setDefaults(&displayInfo);
        FilaRendererFrameRateOptions_setDefaults(&frameRate);
        FilaRendererClearOptions_setDefaults(&clearOptions);

        displayInfo.refreshRate = 60.0f;
        frameRate.interval = 1u;
        clearOptions.clear = true;
        clearOptions.discard = true;

        FilaRenderer_setDisplayInfo(renderer, &displayInfo);
        FilaRenderer_setFrameRateOptions(renderer, &frameRate);
        FilaRenderer_setClearOptions(renderer, &clearOptions);

        if (!FilaRenderer_getClearOptions(renderer, &clearOptions)) {
            printf("Renderer clear options readback failed\n");
            FilaEngine_destroyScene(engine, scene);
            FilaEngine_destroyRenderer(engine, renderer);
            FilaEngine_destroy(&engine);
            return 1;
        }

        FilaRenderer_setVsyncTime(renderer, FilaEngine_getSteadyClockTimeNano());
        FilaRenderer_skipFrame(renderer, 0u);
        (void)FilaRenderer_shouldRenderFrame(renderer);

        const double userTimeBefore = FilaRenderer_getUserTime(renderer);
        FilaRenderer_resetUserTime(renderer);
        const double userTimeAfterReset = FilaRenderer_getUserTime(renderer);
        if (userTimeAfterReset > userTimeBefore + 1.0) {
            printf("Renderer user time reset appears inconsistent\n");
            FilaEngine_destroyScene(engine, scene);
            FilaEngine_destroyRenderer(engine, renderer);
            FilaEngine_destroy(&engine);
            return 1;
        }
    }

    FilaViewport viewport = {0, 0, 16u, 16u};
    FilaRenderer_copyFrame(
        renderer,
        (FilaSwapChain*)0,
        viewport,
        viewport,
        FILA_RENDERER_COPY_FRAME_COMMIT);
    FilaRenderer_readPixels(renderer, 0u, 0u, 1u, 1u, (FilaPixelBufferDescriptor*)0);
    FilaRenderer_readPixelsRenderTarget(
        renderer,
        (FilaRenderTarget*)0,
        0u,
        0u,
        1u,
        1u,
        (FilaPixelBufferDescriptor*)0);
    FilaRenderer_renderStandaloneView(renderer, (const FilaView*)0);

    FilaEngine_destroyScene(engine, scene);
    FilaEngine_destroyRenderer(engine, renderer);
    FilaEngine_destroy(&engine);

    printf("Engine+scene+renderer functionality program completed\n");
    return 0;
}
