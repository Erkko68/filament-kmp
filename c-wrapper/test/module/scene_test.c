#include "filament/Engine.h"
#include "filament/IndirectLight.h"
#include "filament/Scene.h"
#include "filament/Skybox.h"

// Verifies Scene API is consumable and composes with Engine-owned lifecycle.
void fila_scene_module_compile_only(void) {
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
    FilaEngine_destroyScene(engine, scene);
}

