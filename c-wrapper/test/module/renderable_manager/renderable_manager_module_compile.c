#include "filament/Engine.h"
#include "filament/EntityManager.h"
#include "filament/RenderableManager.h"

// Verifies RenderableManager API is consumable from C and composes with Engine + EntityManager.
void fila_renderable_manager_module_compile_only(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaRenderableManager* manager = FilaEngine_getRenderableManager(engine);
    FilaEntity entity = FilaEntityManager_create();
    FilaEntity entities[4] = {0};

    (void)FilaRenderableManager_hasComponent(manager, entity);
    FilaRenderableManagerInstance instance = FilaRenderableManager_getInstance(manager, entity);
    (void)FilaRenderableManager_getEntity(manager, instance);
    (void)FilaRenderableManager_getComponentCount(manager);
    (void)FilaRenderableManager_empty(manager);
    (void)FilaRenderableManager_getEntities(manager, entities, 4u);
    FilaRenderableManager_destroy(manager, entity);
    FilaEntityManager_destroy(entity);
}

