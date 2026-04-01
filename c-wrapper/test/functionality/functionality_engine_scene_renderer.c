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
