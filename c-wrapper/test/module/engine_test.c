#include "filament/Engine.h"

// Verifies Engine header can be consumed from plain C.
void fila_engine_module_compile_only(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaRenderer* renderer = (FilaRenderer*)0;
    FilaSwapChain* swapChain = (FilaSwapChain*)0;
    FilaVertexBuffer* vertexBuffer = (FilaVertexBuffer*)0;
    FilaIndexBuffer* indexBuffer = (FilaIndexBuffer*)0;
    FilaMaterial* material = (FilaMaterial*)0;
    FilaMaterialInstance* materialInstance = (FilaMaterialInstance*)0;
    FilaScene* scene = (FilaScene*)0;
    FilaView* view = (FilaView*)0;
    FilaTransformManager* transformManager = (FilaTransformManager*)0;
    FilaLightManager* lightManager = (FilaLightManager*)0;
    FilaRenderableManager* renderableManager = (FilaRenderableManager*)0;
    FilaEntity entity = 7;

    swapChain = FilaEngine_createSwapChain(engine, (void*)0, 0);
    swapChain = FilaEngine_createSwapChainHeadless(engine, 640u, 480u, 0);

    FilaEngine_destroy(&engine);
    FilaEngine_destroyRenderer(engine, renderer);
    FilaEngine_destroySwapChain(engine, swapChain);
    FilaEngine_destroyVertexBuffer(engine, vertexBuffer);
    FilaEngine_destroyIndexBuffer(engine, indexBuffer);
    FilaEngine_destroyMaterial(engine, material);
    FilaEngine_destroyMaterialInstance(engine, materialInstance);
    FilaEngine_destroyScene(engine, scene);
    FilaEngine_destroyView(engine, view);
    FilaEngine_destroyCameraComponent(engine, entity);
    transformManager = FilaEngine_getTransformManager(engine);
    lightManager = FilaEngine_getLightManager(engine);
    renderableManager = FilaEngine_getRenderableManager(engine);
    (void)transformManager;
    (void)lightManager;
    (void)renderableManager;
}

