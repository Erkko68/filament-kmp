#include <stdio.h>

#include <string.h>

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

    if (FilaRenderer_getEngine(renderer) != engine ||
            FilaRenderer_getEngineConst((const FilaRenderer*)renderer) != (const FilaEngine*)engine) {
        printf("Renderer engine getter mismatch\n");
        FilaEngine_destroyRenderer(engine, renderer);
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
        FilaRenderer_setPresentationTime((FilaRenderer*)0, 0);
        (void)FilaRenderer_shouldRenderFrame(renderer);

        FilaRenderer_skipNextFrames(renderer, 2u);
        if (FilaRenderer_getFrameToSkipCount(renderer) > 2u) {
            printf("Renderer frame-to-skip count mismatch\n");
            FilaEngine_destroyScene(engine, scene);
            FilaEngine_destroyRenderer(engine, renderer);
            FilaEngine_destroy(&engine);
            return 1;
        }

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

        if (FilaRenderer_getMaxFrameHistorySize((const FilaRenderer*)0) != 0u) {
            printf("Renderer max frame history should be zero for null renderer\n");
            FilaEngine_destroyScene(engine, scene);
            FilaEngine_destroyRenderer(engine, renderer);
            FilaEngine_destroy(&engine);
            return 1;
        }

        FilaRendererFrameInfo history[4];
        memset(history, 0xAB, sizeof(history));
        if (FilaRenderer_getFrameInfoHistory((const FilaRenderer*)0, 1u, history, 4u) != 0u ||
                FilaRenderer_getFrameInfoHistory(renderer, 0u, history, 4u) != 0u ||
                FilaRenderer_getFrameInfoHistory(renderer, 1u, (FilaRendererFrameInfo*)0, 4u) != 0u ||
                FilaRenderer_getFrameInfoHistory(renderer, 1u, history, 0u) != 0u) {
            printf("Renderer frame history null-safety contract mismatch\n");
            FilaEngine_destroyScene(engine, scene);
            FilaEngine_destroyRenderer(engine, renderer);
            FilaEngine_destroy(&engine);
            return 1;
        }

        const size_t maxHistory = FilaRenderer_getMaxFrameHistorySize(renderer);
        const size_t requested = maxHistory > 0u ? (maxHistory < 2u ? maxHistory : 2u) : 1u;
        const size_t written = FilaRenderer_getFrameInfoHistory(renderer, requested, history, 4u);
        if (written > requested || written > 4u || written > maxHistory) {
            printf("Renderer frame history size contract mismatch\n");
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
