#include <stdio.h>

#include "filament/Engine.h"
#include "filament/Scene.h"

int main(void) {
    printf("Running engine+scene smoke program...\n");

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
    FilaScene_addEntity(scene, sampleEntity);
    printf("Entity count: %zu\n", FilaScene_getEntityCount(scene));
    FilaScene_removeAllEntities(scene);

    FilaEngine_destroyScene(engine, scene);
    FilaEngine_destroy(&engine);
    printf("Engine+scene smoke program completed\n");
    return 0;
}

