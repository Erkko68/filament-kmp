#include <stdio.h>

#include "filament/Engine.h"
#include "utils/EntityManager.h"
#include "filament/RenderableManager.h"

int main(void) {
    printf("Running engine+renderable_manager functionality program...\n");

    FilaEngine* engine = FilaEngine_create();
    if (!engine) {
        printf("Engine creation failed\n");
        return 1;
    }

    FilaRenderableManager* manager = FilaEngine_getRenderableManager(engine);
    if (!manager) {
        printf("RenderableManager retrieval failed\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaEntity entity = FilaEntityManager_create();
    if (entity == 0) {
        printf("Entity creation failed\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    if (FilaRenderableManager_hasComponent(manager, entity)) {
        printf("Entity unexpectedly has renderable component\n");
        FilaEntityManager_destroy(entity);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaRenderableManagerInstance instance = FilaRenderableManager_getInstance(manager, entity);
    if (instance != 0u) {
        printf("Renderable instance unexpectedly valid\n");
        FilaEntityManager_destroy(entity);
        FilaEngine_destroy(&engine);
        return 1;
    }

    if (FilaRenderableManager_getPrimitiveCount(manager, instance) != 0u) {
        printf("Renderable primitive count unexpectedly non-zero\n");
        FilaEntityManager_destroy(entity);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaRenderableManager_setLayerMask(manager, instance, 0xFFu, 0x01u);
    if (FilaRenderableManager_getLayerMask(manager, instance) != 0u) {
        printf("Renderable layer mask unexpectedly non-zero\n");
        FilaEntityManager_destroy(entity);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaRenderableManager_setPriority(manager, instance, 7u);
    if (FilaRenderableManager_getPriority(manager, instance) != 0u) {
        printf("Renderable priority unexpectedly non-zero\n");
        FilaEntityManager_destroy(entity);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaRenderableManager_setChannel(manager, instance, 2u);
    if (FilaRenderableManager_getChannel(manager, instance) != 0u) {
        printf("Renderable channel unexpectedly non-zero\n");
        FilaEntityManager_destroy(entity);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaRenderableManager_setCulling(manager, instance, false);
    if (FilaRenderableManager_isCullingEnabled(manager, instance)) {
        printf("Renderable culling unexpectedly enabled\n");
        FilaEntityManager_destroy(entity);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaRenderableManager_setFogEnabled(manager, instance, true);
    if (FilaRenderableManager_getFogEnabled(manager, instance)) {
        printf("Renderable fog unexpectedly enabled\n");
        FilaEntityManager_destroy(entity);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaRenderableManager_setLightChannel(manager, instance, 1u, true);
    if (FilaRenderableManager_getLightChannel(manager, instance, 1u)) {
        printf("Renderable light channel unexpectedly enabled\n");
        FilaEntityManager_destroy(entity);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaRenderableManager_setCastShadows(manager, instance, true);
    FilaRenderableManager_setReceiveShadows(manager, instance, true);
    FilaRenderableManager_setScreenSpaceContactShadows(manager, instance, true);
    if (FilaRenderableManager_isShadowCaster(manager, instance) ||
            FilaRenderableManager_isShadowReceiver(manager, instance) ||
            FilaRenderableManager_isScreenSpaceContactShadowsEnabled(manager, instance)) {
        printf("Renderable shadow/contact state unexpectedly enabled\n");
        FilaEntityManager_destroy(entity);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaRenderableManagerBuilder* builder = FilaRenderableManagerBuilder_create(0u);
    if (!builder) {
        printf("Renderable builder creation failed\n");
        FilaEntityManager_destroy(entity);
        FilaEngine_destroy(&engine);
        return 1;
    }
    FilaRenderableManagerBuilder_layerMask(builder, 0xFFu, 0x03u);
    FilaRenderableManagerBuilder_priority(builder, 4u);
    FilaRenderableManagerBuilder_channel(builder, 1u);
    FilaRenderableManagerBuilder_lightChannel(builder, 1u, true);
    FilaRenderableManagerBuilder_culling(builder, true);
    FilaRenderableManagerBuilder_fog(builder, true);
    FilaRenderableManagerBuilder_screenSpaceContactShadows(builder, true);
    FilaRenderableManagerBuilder_castShadows(builder, false);
    FilaRenderableManagerBuilder_receiveShadows(builder, true);
    FilaRenderableManagerBuilder_blendOrder(builder, 0u, 1u);
    FilaRenderableManagerBuilder_globalBlendOrderEnabled(builder, 0u, true);
    (void)FilaRenderableManagerBuilder_build(builder, engine, entity);
    FilaRenderableManagerBuilder_destroy(builder);

    FilaRenderableManager_clearMaterialInstanceAt(manager, instance, 0u);
    FilaRenderableManager_setBlendOrderAt(manager, instance, 0u, 1u);
    if (FilaRenderableManager_getBlendOrderAt(manager, instance, 0u) != 0u) {
        printf("Renderable blend order unexpectedly non-zero\n");
        FilaEntityManager_destroy(entity);
        FilaEngine_destroy(&engine);
        return 1;
    }
    FilaRenderableManager_setGlobalBlendOrderEnabledAt(manager, instance, 0u, true);
    if (FilaRenderableManager_isGlobalBlendOrderEnabledAt(manager, instance, 0u)) {
        printf("Renderable global blend order unexpectedly enabled\n");
        FilaEntityManager_destroy(entity);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaEntity listed[8] = {0};
    (void)FilaRenderableManager_getEntities(manager, listed, 8u);
    FilaRenderableManager_destroy(manager, entity);

    FilaEntityManager_destroy(entity);
    FilaEngine_destroy(&engine);

    printf("Engine+renderable_manager functionality program completed\n");
    return 0;
}
