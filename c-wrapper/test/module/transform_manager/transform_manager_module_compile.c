#include "filament/Engine.h"
#include "filament/EntityManager.h"
#include "filament/TransformManager.h"

// Verifies TransformManager API is consumable from C and composes with Engine + EntityManager.
void fila_transform_manager_module_compile_only(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaTransformManager* manager = FilaEngine_getTransformManager(engine);
    FilaEntity entity = FilaEntityManager_create();

    FilaTransformManager_create(manager, entity, 0u);
    (void)FilaTransformManager_hasComponent(manager, entity);
    FilaTransformManagerInstance instance = FilaTransformManager_getInstance(manager, entity);
    (void)FilaTransformManager_getEntity(manager, instance);
    (void)FilaTransformManager_getComponentCount(manager);
    (void)FilaTransformManager_empty(manager);
    FilaTransformManager_destroy(manager, entity);
    FilaEntityManager_destroy(entity);
}

