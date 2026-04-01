#include <stdio.h>

#include "filament/Engine.h"
#include "filament/Scene.h"

static void scene_count_callback(FilaEntity entity, void* userData) {
    (void)entity;
    size_t* counter = (size_t*) userData;
    if (counter) {
        (*counter)++;
    }
}

int main(void) {
    printf("Running engine+scene functionality program...\n");

    FilaEngine* engine = FilaEngine_create();
    if (!engine) {
        printf("Engine creation failed\n");
        return 1;
    }

    FilaScene* scene = FilaEngine_createScene(engine);
    if (!scene) {
        printf("Scene creation failed\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaEntity sampleEntity = 123;
    FilaEntity moreEntities[2] = { 124, 125 };
    FilaScene_addEntity(scene, sampleEntity);
    FilaScene_addEntities(scene, moreEntities, 2u);
    printf("Entity count: %zu\n", FilaScene_getEntityCount(scene));
    printf("Renderable count: %zu, light count: %zu\n",
        FilaScene_getRenderableCount(scene),
        FilaScene_getLightCount(scene));
    {
        size_t enumeratedCount = 0;
        FilaScene_forEach(scene, scene_count_callback, &enumeratedCount);
        printf("Enumerated entities: %zu\n", enumeratedCount);
    }
    FilaScene_removeEntities(scene, moreEntities, 2u);
    FilaScene_removeAllEntities(scene);

    FilaEngine_destroyScene(engine, scene);
    FilaEngine_destroy(&engine);
    printf("Engine+scene functionality program completed\n");
    return 0;
}
