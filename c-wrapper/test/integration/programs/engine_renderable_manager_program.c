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

    FilaEntity listed[8] = {0};
    (void)FilaRenderableManager_getEntities(manager, listed, 8u);
    FilaRenderableManager_destroy(manager, entity);

    FilaEntityManager_destroy(entity);
    FilaEngine_destroy(&engine);

    printf("Engine+renderable_manager smoke program completed\n");
    return 0;
}

