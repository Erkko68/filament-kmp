#include <stdio.h>

#include "filament/Engine.h"
#include "filament/EntityManager.h"
#include "filament/RenderableManager.h"

int main(void) {
    printf("Running engine+renderable_manager smoke program...\n");

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

    FilaEntity listed[8] = {0};
    (void)FilaRenderableManager_getEntities(manager, listed, 8u);
    FilaRenderableManager_destroy(manager, entity);

    FilaEntityManager_destroy(entity);
    FilaEngine_destroy(&engine);

    printf("Engine+renderable_manager smoke program completed\n");
    return 0;
}

