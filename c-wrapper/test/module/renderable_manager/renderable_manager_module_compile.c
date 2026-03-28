#include "filament/Engine.h"
#include "filament/EntityManager.h"
#include "filament/RenderableManager.h"

// Verifies RenderableManager API is consumable from C and composes with Engine + EntityManager.
void fila_renderable_manager_module_compile_only(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaRenderableManager* manager = FilaEngine_getRenderableManager(engine);
    FilaEntity entity = FilaEntityManager_create();
    FilaEntity entities[4] = {0};
    FilaRenderableManagerBuilder* builder = FilaRenderableManagerBuilder_create(0u);

    (void)FilaRenderableManager_hasComponent(manager, entity);
    FilaRenderableManagerInstance instance = FilaRenderableManager_getInstance(manager, entity);
    (void)FilaRenderableManager_getEntity(manager, instance);
    (void)FilaRenderableManager_getComponentCount(manager);
    (void)FilaRenderableManager_empty(manager);
    (void)FilaRenderableManager_getEntities(manager, entities, 4u);
    (void)FilaRenderableManager_getPrimitiveCount(manager, instance);
    FilaRenderableManager_setLayerMask(manager, instance, 0xFFu, 0x02u);
    (void)FilaRenderableManager_getLayerMask(manager, instance);
    FilaRenderableManager_setPriority(manager, instance, 4u);
    (void)FilaRenderableManager_getPriority(manager, instance);
    FilaRenderableManager_setCulling(manager, instance, true);
    (void)FilaRenderableManager_isCullingEnabled(manager, instance);
    FilaRenderableManagerBuilder_layerMask(builder, 0xFFu, 0x02u);
    FilaRenderableManagerBuilder_priority(builder, 4u);
    FilaRenderableManagerBuilder_culling(builder, true);
    FilaRenderableManagerBuilder_castShadows(builder, false);
    FilaRenderableManagerBuilder_receiveShadows(builder, true);
    (void)FilaRenderableManagerBuilder_build(builder, engine, entity);
    FilaRenderableManagerBuilder_destroy(builder);
    FilaRenderableManager_destroy(manager, entity);
    FilaEntityManager_destroy(entity);
}

