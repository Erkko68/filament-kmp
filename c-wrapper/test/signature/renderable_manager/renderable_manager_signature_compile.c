#include <stdbool.h>
#include <stddef.h>
#include <stdint.h>

#include "filament/RenderableManager.h"

// Function pointer assignments lock exported C signatures.
static bool (*g_renderable_has_component)(const FilaRenderableManager*, FilaEntity) = FilaRenderableManager_hasComponent;
static FilaRenderableManagerInstance (*g_renderable_get_instance)(const FilaRenderableManager*, FilaEntity) = FilaRenderableManager_getInstance;
static size_t (*g_renderable_get_component_count)(const FilaRenderableManager*) = FilaRenderableManager_getComponentCount;
static bool (*g_renderable_empty)(const FilaRenderableManager*) = FilaRenderableManager_empty;
static FilaEntity (*g_renderable_get_entity)(const FilaRenderableManager*, FilaRenderableManagerInstance) = FilaRenderableManager_getEntity;
static size_t (*g_renderable_get_entities)(const FilaRenderableManager*, FilaEntity*, size_t) = FilaRenderableManager_getEntities;
static void (*g_renderable_destroy)(FilaRenderableManager*, FilaEntity) = FilaRenderableManager_destroy;
static size_t (*g_renderable_get_primitive_count)(const FilaRenderableManager*, FilaRenderableManagerInstance) = FilaRenderableManager_getPrimitiveCount;
static void (*g_renderable_set_layer_mask)(FilaRenderableManager*, FilaRenderableManagerInstance, uint8_t, uint8_t) = FilaRenderableManager_setLayerMask;
static uint8_t (*g_renderable_get_layer_mask)(const FilaRenderableManager*, FilaRenderableManagerInstance) = FilaRenderableManager_getLayerMask;
static void (*g_renderable_set_priority)(FilaRenderableManager*, FilaRenderableManagerInstance, uint8_t) = FilaRenderableManager_setPriority;
static uint8_t (*g_renderable_get_priority)(const FilaRenderableManager*, FilaRenderableManagerInstance) = FilaRenderableManager_getPriority;
static void (*g_renderable_set_culling)(FilaRenderableManager*, FilaRenderableManagerInstance, bool) = FilaRenderableManager_setCulling;
static bool (*g_renderable_is_culling_enabled)(const FilaRenderableManager*, FilaRenderableManagerInstance) = FilaRenderableManager_isCullingEnabled;
static FilaRenderableManagerBuilder* (*g_renderable_builder_create)(size_t) = FilaRenderableManagerBuilder_create;
static void (*g_renderable_builder_destroy)(FilaRenderableManagerBuilder*) = FilaRenderableManagerBuilder_destroy;
static void (*g_renderable_builder_layer_mask)(FilaRenderableManagerBuilder*, uint8_t, uint8_t) = FilaRenderableManagerBuilder_layerMask;
static void (*g_renderable_builder_priority)(FilaRenderableManagerBuilder*, uint8_t) = FilaRenderableManagerBuilder_priority;
static void (*g_renderable_builder_culling)(FilaRenderableManagerBuilder*, bool) = FilaRenderableManagerBuilder_culling;
static void (*g_renderable_builder_cast_shadows)(FilaRenderableManagerBuilder*, bool) = FilaRenderableManagerBuilder_castShadows;
static void (*g_renderable_builder_receive_shadows)(FilaRenderableManagerBuilder*, bool) = FilaRenderableManagerBuilder_receiveShadows;
static bool (*g_renderable_builder_build)(FilaRenderableManagerBuilder*, FilaEngine*, FilaEntity) = FilaRenderableManagerBuilder_build;

void fila_renderable_manager_signature_compile_only(void) {
    (void)g_renderable_has_component;
    (void)g_renderable_get_instance;
    (void)g_renderable_get_component_count;
    (void)g_renderable_empty;
    (void)g_renderable_get_entity;
    (void)g_renderable_get_entities;
    (void)g_renderable_destroy;
    (void)g_renderable_get_primitive_count;
    (void)g_renderable_set_layer_mask;
    (void)g_renderable_get_layer_mask;
    (void)g_renderable_set_priority;
    (void)g_renderable_get_priority;
    (void)g_renderable_set_culling;
    (void)g_renderable_is_culling_enabled;
    (void)g_renderable_builder_create;
    (void)g_renderable_builder_destroy;
    (void)g_renderable_builder_layer_mask;
    (void)g_renderable_builder_priority;
    (void)g_renderable_builder_culling;
    (void)g_renderable_builder_cast_shadows;
    (void)g_renderable_builder_receive_shadows;
    (void)g_renderable_builder_build;
}

