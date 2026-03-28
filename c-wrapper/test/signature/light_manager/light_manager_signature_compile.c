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
static FilaLightManagerBuilder* (*g_light_builder_create)(FilaLightType) = FilaLightManagerBuilder_create;
static void (*g_light_builder_destroy)(FilaLightManagerBuilder*) = FilaLightManagerBuilder_destroy;
static void (*g_light_builder_direction)(FilaLightManagerBuilder*, float, float, float) = FilaLightManagerBuilder_direction;
static void (*g_light_builder_color)(FilaLightManagerBuilder*, float, float, float) = FilaLightManagerBuilder_color;
static void (*g_light_builder_intensity)(FilaLightManagerBuilder*, float) = FilaLightManagerBuilder_intensity;
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
    (void)g_light_builder_create;
    (void)g_light_builder_destroy;
    (void)g_light_builder_direction;
    (void)g_light_builder_color;
    (void)g_light_builder_intensity;
    (void)g_light_builder_cast_shadows;
    (void)g_light_builder_build;
}

