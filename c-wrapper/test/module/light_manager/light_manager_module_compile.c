#include "filament/Engine.h"
#include "filament/EntityManager.h"
#include "filament/LightManager.h"

// Verifies LightManager API is consumable from C and composes with Engine + EntityManager.
void fila_light_manager_module_compile_only(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaLightManager* manager = FilaEngine_getLightManager(engine);
    FilaEntity entity = FilaEntityManager_create();
    FilaEntity entities[4] = {0};
    float outPosition[3] = {0};
    float outDirection[3] = {0};
    float outColor[3] = {0};
    FilaLightManagerBuilder* builder = FilaLightManagerBuilder_create(FILA_LIGHT_TYPE_DIRECTIONAL);

    (void)FilaLightManager_hasComponent(manager, entity);
    FilaLightManagerInstance instance = FilaLightManager_getInstance(manager, entity);
    (void)FilaLightManager_getEntity(manager, instance);
    (void)FilaLightManager_getComponentCount(manager);
    (void)FilaLightManager_empty(manager);
    (void)FilaLightManager_getEntities(manager, entities, 4u);
    (void)FilaLightManager_getType(manager, instance);
    FilaLightManager_setPosition(manager, instance, 0.0f, 1.0f, 2.0f);
    (void)FilaLightManager_getPosition(manager, instance, outPosition);
    FilaLightManager_setDirection(manager, instance, 0.0f, -1.0f, 0.0f);
    (void)FilaLightManager_getDirection(manager, instance, outDirection);
    FilaLightManager_setColor(manager, instance, 1.0f, 0.9f, 0.8f);
    (void)FilaLightManager_getColor(manager, instance, outColor);
    FilaLightManager_setIntensity(manager, instance, 5000.0f);
    (void)FilaLightManager_getIntensity(manager, instance);
    FilaLightManager_setFalloff(manager, instance, 15.0f);
    (void)FilaLightManager_getFalloff(manager, instance);
    FilaLightManagerBuilder_direction(builder, 0.0f, -1.0f, 0.0f);
    FilaLightManagerBuilder_position(builder, 0.0f, 2.0f, 0.0f);
    FilaLightManagerBuilder_color(builder, 1.0f, 1.0f, 1.0f);
    FilaLightManagerBuilder_intensity(builder, 100000.0f);
    FilaLightManagerBuilder_falloff(builder, 10.0f);
    FilaLightManagerBuilder_spotLightCone(builder, 0.3f, 0.8f);
    FilaLightManagerBuilder_castShadows(builder, true);
    (void)FilaLightManagerBuilder_build(builder, engine, entity);
    FilaLightManagerBuilder_destroy(builder);
    FilaLightManager_destroy(manager, entity);
    FilaEntityManager_destroy(entity);
}

