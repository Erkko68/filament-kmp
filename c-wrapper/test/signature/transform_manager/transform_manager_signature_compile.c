#include <stdbool.h>
#include <stddef.h>

#include "filament/TransformManager.h"

// Function pointer assignments lock exported C signatures.
static bool (*g_transform_has_component)(const FilaTransformManager*, FilaEntity) = FilaTransformManager_hasComponent;
static FilaTransformManagerInstance (*g_transform_get_instance)(const FilaTransformManager*, FilaEntity) = FilaTransformManager_getInstance;
static void (*g_transform_create)(FilaTransformManager*, FilaEntity, FilaTransformManagerInstance) = FilaTransformManager_create;
static void (*g_transform_create_with_transform_mat4f)(FilaTransformManager*, FilaEntity, FilaTransformManagerInstance, const float[16]) = FilaTransformManager_createWithTransformMat4f;
static void (*g_transform_create_with_transform_mat4)(FilaTransformManager*, FilaEntity, FilaTransformManagerInstance, const double[16]) = FilaTransformManager_createWithTransformMat4;
static void (*g_transform_destroy)(FilaTransformManager*, FilaEntity) = FilaTransformManager_destroy;
static size_t (*g_transform_get_component_count)(const FilaTransformManager*) = FilaTransformManager_getComponentCount;
static bool (*g_transform_empty)(const FilaTransformManager*) = FilaTransformManager_empty;
static void (*g_transform_set_accurate_enabled)(FilaTransformManager*, bool) = FilaTransformManager_setAccurateTranslationsEnabled;
static bool (*g_transform_is_accurate_enabled)(const FilaTransformManager*) = FilaTransformManager_isAccurateTranslationsEnabled;
static size_t (*g_transform_get_entities)(const FilaTransformManager*, FilaEntity*, size_t) = FilaTransformManager_getEntities;
static FilaEntity (*g_transform_get_entity)(const FilaTransformManager*, FilaTransformManagerInstance) = FilaTransformManager_getEntity;
static void (*g_transform_set_parent)(FilaTransformManager*, FilaTransformManagerInstance, FilaTransformManagerInstance) = FilaTransformManager_setParent;
static FilaEntity (*g_transform_get_parent)(const FilaTransformManager*, FilaTransformManagerInstance) = FilaTransformManager_getParent;
static size_t (*g_transform_get_child_count)(const FilaTransformManager*, FilaTransformManagerInstance) = FilaTransformManager_getChildCount;
static size_t (*g_transform_get_children)(const FilaTransformManager*, FilaTransformManagerInstance, FilaEntity*, size_t) = FilaTransformManager_getChildren;
static void (*g_transform_set_transform_mat4f)(FilaTransformManager*, FilaTransformManagerInstance, const float[16]) = FilaTransformManager_setTransformMat4f;
static bool (*g_transform_get_transform_mat4f)(const FilaTransformManager*, FilaTransformManagerInstance, float[16]) = FilaTransformManager_getTransformMat4f;
static bool (*g_transform_get_world_transform_mat4f)(const FilaTransformManager*, FilaTransformManagerInstance, float[16]) = FilaTransformManager_getWorldTransformMat4f;
static void (*g_transform_set_transform_mat4)(FilaTransformManager*, FilaTransformManagerInstance, const double[16]) = FilaTransformManager_setTransformMat4;
static bool (*g_transform_get_transform_mat4)(const FilaTransformManager*, FilaTransformManagerInstance, double[16]) = FilaTransformManager_getTransformMat4;
static bool (*g_transform_get_world_transform_mat4)(const FilaTransformManager*, FilaTransformManagerInstance, double[16]) = FilaTransformManager_getWorldTransformMat4;
static void (*g_transform_open_tx)(FilaTransformManager*) = FilaTransformManager_openLocalTransformTransaction;
static void (*g_transform_commit_tx)(FilaTransformManager*) = FilaTransformManager_commitLocalTransformTransaction;

void fila_transform_manager_signature_compile_only(void) {
    (void)g_transform_has_component;
    (void)g_transform_get_instance;
    (void)g_transform_create;
    (void)g_transform_create_with_transform_mat4f;
    (void)g_transform_create_with_transform_mat4;
    (void)g_transform_destroy;
    (void)g_transform_get_component_count;
    (void)g_transform_empty;
    (void)g_transform_set_accurate_enabled;
    (void)g_transform_is_accurate_enabled;
    (void)g_transform_get_entities;
    (void)g_transform_get_entity;
    (void)g_transform_set_parent;
    (void)g_transform_get_parent;
    (void)g_transform_get_child_count;
    (void)g_transform_get_children;
    (void)g_transform_set_transform_mat4f;
    (void)g_transform_get_transform_mat4f;
    (void)g_transform_get_world_transform_mat4f;
    (void)g_transform_set_transform_mat4;
    (void)g_transform_get_transform_mat4;
    (void)g_transform_get_world_transform_mat4;
    (void)g_transform_open_tx;
    (void)g_transform_commit_tx;
}

