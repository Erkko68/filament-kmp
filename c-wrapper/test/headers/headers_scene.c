#include "filament/Engine.h"
#include "filament/IndirectLight.h"
#include "filament/Scene.h"
#include "filament/Skybox.h"

static void scene_entity_callback(FilaEntity entity, void* userData) {
    (void)entity;
    (void)userData;
}

// Verifies Scene API is consumable and composes with Engine-owned lifecycle.
void test_headers_scene(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaScene* scene = (FilaScene*)0;
    FilaEntity entity = 42;

    scene = FilaEngine_createScene(engine);
    FilaScene_addEntity(scene, entity);
    FilaScene_removeEntity(scene, entity);
    FilaScene_removeAllEntities(scene);
    (void)FilaScene_getEntityCount(scene);
    (void)FilaScene_hasEntity(scene, entity);
    FilaScene_setSkybox(scene, (FilaSkybox*)0);
    (void)FilaScene_getSkybox(scene);
    FilaScene_setIndirectLight(scene, (FilaIndirectLight*)0);
    (void)FilaScene_getIndirectLight(scene);
    FilaEntity entities[2] = { 43, 44 };
    FilaScene_addEntities(scene, entities, 2u);
    FilaScene_removeEntities(scene, entities, 2u);
    (void)FilaScene_getRenderableCount(scene);
    (void)FilaScene_getLightCount(scene);
    FilaScene_forEach(scene, scene_entity_callback, (void*)0);
    FilaEngine_destroyScene(engine, scene);
}
