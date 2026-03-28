#include <stdbool.h>
#include <stddef.h>

#include "filament/TransformManager.h"

// Function pointer assignments lock exported C signatures.
static bool (*g_transform_has_component)(const FilaTransformManager*, FilaEntity) = FilaTransformManager_hasComponent;
static FilaTransformManagerInstance (*g_transform_get_instance)(const FilaTransformManager*, FilaEntity) = FilaTransformManager_getInstance;
static void (*g_transform_create)(FilaTransformManager*, FilaEntity, FilaTransformManagerInstance) = FilaTransformManager_create;
static void (*g_transform_destroy)(FilaTransformManager*, FilaEntity) = FilaTransformManager_destroy;
static size_t (*g_transform_get_component_count)(const FilaTransformManager*) = FilaTransformManager_getComponentCount;
static bool (*g_transform_empty)(const FilaTransformManager*) = FilaTransformManager_empty;
static FilaEntity (*g_transform_get_entity)(const FilaTransformManager*, FilaTransformManagerInstance) = FilaTransformManager_getEntity;

void fila_transform_manager_signature_compile_only(void) {
    (void)g_transform_has_component;
    (void)g_transform_get_instance;
    (void)g_transform_create;
    (void)g_transform_destroy;
    (void)g_transform_get_component_count;
    (void)g_transform_empty;
    (void)g_transform_get_entity;
}

