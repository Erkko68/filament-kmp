#include <stdio.h>

#include "filament/Engine.h"
#include "filament/EntityManager.h"
#include "filament/LightManager.h"

int main(void) {
    printf("Running engine+light_manager smoke program...\n");

    FilaEngine* engine = FilaEngine_create();
    if (!engine) {
        printf("Engine creation failed\n");
        return 1;
    }

    FilaLightManager* manager = FilaEngine_getLightManager(engine);
    if (!manager) {
        printf("LightManager retrieval failed\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaEntity entity = FilaEntityManager_create();
    if (entity == 0) {
        printf("Entity creation failed\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    if (FilaLightManager_hasComponent(manager, entity)) {
        printf("Entity unexpectedly has light component\n");
        FilaEntityManager_destroy(entity);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaEntity listed[8] = {0};
    (void)FilaLightManager_getEntities(manager, listed, 8u);
    FilaLightManager_destroy(manager, entity);

    FilaEntityManager_destroy(entity);
    FilaEngine_destroy(&engine);

    printf("Engine+light_manager smoke program completed\n");
    return 0;
}

