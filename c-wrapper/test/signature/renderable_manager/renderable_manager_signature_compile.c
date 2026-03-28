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
}

