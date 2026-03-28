#include <stdbool.h>
#include <stddef.h>

#include "filament/LightManager.h"

// Function pointer assignments lock exported C signatures.
static bool (*g_light_has_component)(const FilaLightManager*, FilaEntity) = FilaLightManager_hasComponent;
static FilaLightManagerInstance (*g_light_get_instance)(const FilaLightManager*, FilaEntity) = FilaLightManager_getInstance;
static size_t (*g_light_get_component_count)(const FilaLightManager*) = FilaLightManager_getComponentCount;
static bool (*g_light_empty)(const FilaLightManager*) = FilaLightManager_empty;
static FilaEntity (*g_light_get_entity)(const FilaLightManager*, FilaLightManagerInstance) = FilaLightManager_getEntity;
static size_t (*g_light_get_entities)(const FilaLightManager*, FilaEntity*, size_t) = FilaLightManager_getEntities;
static void (*g_light_destroy)(FilaLightManager*, FilaEntity) = FilaLightManager_destroy;
static FilaLightType (*g_light_get_type)(const FilaLightManager*, FilaLightManagerInstance) = FilaLightManager_getType;
static void (*g_light_set_position)(FilaLightManager*, FilaLightManagerInstance, float, float, float) = FilaLightManager_setPosition;
static bool (*g_light_get_position)(const FilaLightManager*, FilaLightManagerInstance, float[3]) = FilaLightManager_getPosition;
static void (*g_light_set_direction)(FilaLightManager*, FilaLightManagerInstance, float, float, float) = FilaLightManager_setDirection;
static bool (*g_light_get_direction)(const FilaLightManager*, FilaLightManagerInstance, float[3]) = FilaLightManager_getDirection;
static void (*g_light_set_color)(FilaLightManager*, FilaLightManagerInstance, float, float, float) = FilaLightManager_setColor;
static bool (*g_light_get_color)(const FilaLightManager*, FilaLightManagerInstance, float[3]) = FilaLightManager_getColor;
static void (*g_light_set_intensity)(FilaLightManager*, FilaLightManagerInstance, float) = FilaLightManager_setIntensity;
static float (*g_light_get_intensity)(const FilaLightManager*, FilaLightManagerInstance) = FilaLightManager_getIntensity;
static void (*g_light_set_falloff)(FilaLightManager*, FilaLightManagerInstance, float) = FilaLightManager_setFalloff;
static float (*g_light_get_falloff)(const FilaLightManager*, FilaLightManagerInstance) = FilaLightManager_getFalloff;
static FilaLightManagerBuilder* (*g_light_builder_create)(FilaLightType) = FilaLightManagerBuilder_create;
static void (*g_light_builder_destroy)(FilaLightManagerBuilder*) = FilaLightManagerBuilder_destroy;
static void (*g_light_builder_direction)(FilaLightManagerBuilder*, float, float, float) = FilaLightManagerBuilder_direction;
static void (*g_light_builder_position)(FilaLightManagerBuilder*, float, float, float) = FilaLightManagerBuilder_position;
static void (*g_light_builder_color)(FilaLightManagerBuilder*, float, float, float) = FilaLightManagerBuilder_color;
static void (*g_light_builder_intensity)(FilaLightManagerBuilder*, float) = FilaLightManagerBuilder_intensity;
static void (*g_light_builder_falloff)(FilaLightManagerBuilder*, float) = FilaLightManagerBuilder_falloff;
static void (*g_light_builder_spot_cone)(FilaLightManagerBuilder*, float, float) = FilaLightManagerBuilder_spotLightCone;
static void (*g_light_builder_cast_shadows)(FilaLightManagerBuilder*, bool) = FilaLightManagerBuilder_castShadows;
static bool (*g_light_builder_build)(FilaLightManagerBuilder*, FilaEngine*, FilaEntity) = FilaLightManagerBuilder_build;

void fila_light_manager_signature_compile_only(void) {
    (void)g_light_has_component;
    (void)g_light_get_instance;
    (void)g_light_get_component_count;
    (void)g_light_empty;
    (void)g_light_get_entity;
    (void)g_light_get_entities;
    (void)g_light_destroy;
    (void)g_light_get_type;
    (void)g_light_set_position;
    (void)g_light_get_position;
    (void)g_light_set_direction;
    (void)g_light_get_direction;
    (void)g_light_set_color;
    (void)g_light_get_color;
    (void)g_light_set_intensity;
    (void)g_light_get_intensity;
    (void)g_light_set_falloff;
    (void)g_light_get_falloff;
    (void)g_light_builder_create;
    (void)g_light_builder_destroy;
    (void)g_light_builder_direction;
    (void)g_light_builder_position;
    (void)g_light_builder_color;
    (void)g_light_builder_intensity;
    (void)g_light_builder_falloff;
    (void)g_light_builder_spot_cone;
    (void)g_light_builder_cast_shadows;
    (void)g_light_builder_build;
}

