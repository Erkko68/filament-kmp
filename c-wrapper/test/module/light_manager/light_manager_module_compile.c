#include "filament/Engine.h"
#include "filament/EntityManager.h"
#include "filament/LightManager.h"

// Verifies LightManager API is consumable from C and composes with Engine + EntityManager.
void fila_light_manager_module_compile_only(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaLightManager* manager = FilaEngine_getLightManager(engine);
    FilaEntity entity = FilaEntityManager_create();
    FilaEntity entities[4] = {0};

    (void)FilaLightManager_hasComponent(manager, entity);
    FilaLightManagerInstance instance = FilaLightManager_getInstance(manager, entity);
    (void)FilaLightManager_getEntity(manager, instance);
    (void)FilaLightManager_getComponentCount(manager);
    (void)FilaLightManager_empty(manager);
    (void)FilaLightManager_getEntities(manager, entities, 4u);
    FilaLightManager_destroy(manager, entity);
    FilaEntityManager_destroy(entity);
}

