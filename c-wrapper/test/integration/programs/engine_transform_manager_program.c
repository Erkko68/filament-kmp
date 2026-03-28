#include <stdio.h>

#include "filament/Engine.h"
#include "filament/EntityManager.h"
#include "filament/TransformManager.h"

int main(void) {
    printf("Running engine+transform_manager smoke program...\n");

    FilaEngine* engine = FilaEngine_create();
    if (!engine) {
        printf("Engine creation failed\n");
        return 1;
    }

    FilaTransformManager* manager = FilaEngine_getTransformManager(engine);
    if (!manager) {
        printf("TransformManager retrieval failed\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaEntity entity = FilaEntityManager_create();
    if (entity == 0) {
        printf("Entity creation failed\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    if (FilaTransformManager_hasComponent(manager, entity)) {
        printf("Entity unexpectedly has transform component\n");
        FilaEntityManager_destroy(entity);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaTransformManager_create(manager, entity, 0u);
    if (!FilaTransformManager_hasComponent(manager, entity)) {
        printf("Transform component creation failed\n");
        FilaEntityManager_destroy(entity);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaTransformManagerInstance instance = FilaTransformManager_getInstance(manager, entity);
    if (instance == 0u) {
        printf("Transform instance retrieval failed\n");
        FilaTransformManager_destroy(manager, entity);
        FilaEntityManager_destroy(entity);
        FilaEngine_destroy(&engine);
        return 1;
    }

    if (FilaTransformManager_getEntity(manager, instance) != entity) {
        printf("Transform instance entity mismatch\n");
        FilaTransformManager_destroy(manager, entity);
        FilaEntityManager_destroy(entity);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaTransformManager_destroy(manager, entity);
    if (FilaTransformManager_hasComponent(manager, entity)) {
        printf("Transform component still exists after destroy\n");
        FilaEntityManager_destroy(entity);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaEntityManager_destroy(entity);
    FilaEngine_destroy(&engine);

    printf("Engine+transform_manager smoke program completed\n");
    return 0;
}

