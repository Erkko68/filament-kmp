#include <stdbool.h>
#include <stdio.h>

#include "filament/Camera.h"
#include "filament/Engine.h"
#include "filament/Renderer.h"
#include "filament/Scene.h"
#include "filament/View.h"

int main(void) {
    printf("Running engine+scene+view first-frame functionality program...\n");

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

    FilaSwapChain* swapChain = FilaEngine_createSwapChainHeadless(engine, 640u, 480u, 0);
    if (!swapChain) {
        printf("SwapChain creation failed\n");
        FilaEngine_destroyRenderer(engine, renderer);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaScene* scene = FilaEngine_createScene(engine);
    if (!scene) {
        printf("Scene creation failed\n");
        FilaEngine_destroySwapChain(engine, swapChain);
        FilaEngine_destroyRenderer(engine, renderer);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaView* view = FilaEngine_createView(engine);
    if (!view) {
        printf("View creation failed\n");
        FilaEngine_destroyScene(engine, scene);
        FilaEngine_destroySwapChain(engine, swapChain);
        FilaEngine_destroyRenderer(engine, renderer);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaEntity cameraEntity = 101;
    FilaCamera* camera = FilaEngine_createCamera(engine, cameraEntity);
    if (!camera) {
        printf("Camera creation failed\n");
        FilaEngine_destroyView(engine, view);
        FilaEngine_destroyScene(engine, scene);
        FilaEngine_destroySwapChain(engine, swapChain);
        FilaEngine_destroyRenderer(engine, renderer);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaView_setScene(view, scene);
    FilaViewport viewport = {0, 0, 640u, 480u};
    FilaView_setViewport(view, viewport);
    FilaView_setCamera(view, camera);
    FilaCamera_setProjectionFov(camera, 45.0, 640.0 / 480.0, 0.1, 1000.0, FILA_CAMERA_FOV_VERTICAL);
    FilaCamera_lookAt(camera, 0.0, 1.5, 3.5, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);

    bool rendered = false;
    for (int i = 0; i < 3; ++i) {
        if (FilaRenderer_beginFrame(renderer, swapChain, 0u)) {
            FilaRenderer_render(renderer, view);
            FilaRenderer_endFrame(renderer);
            rendered = true;
            break;
        }
    }

    if (!rendered) {
        printf("No frame rendered after retries\n");
        FilaEngine_destroyCameraComponent(engine, cameraEntity);
        FilaEngine_destroyView(engine, view);
        FilaEngine_destroyScene(engine, scene);
        FilaEngine_destroySwapChain(engine, swapChain);
        FilaEngine_destroyRenderer(engine, renderer);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaEngine_destroyCameraComponent(engine, cameraEntity);
    FilaEngine_destroyView(engine, view);
    FilaEngine_destroyScene(engine, scene);
    FilaEngine_destroySwapChain(engine, swapChain);
    FilaEngine_destroyRenderer(engine, renderer);
    FilaEngine_destroy(&engine);

    printf("First-frame functionality program completed\n");
    return 0;
}

