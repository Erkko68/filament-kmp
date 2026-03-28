#include <stdbool.h>
#include <stddef.h>

#include "filament/Scene.h"

// Function pointer assignments lock exported C signatures.
static void (*g_scene_add_entity)(FilaScene*, FilaEntity) = FilaScene_addEntity;
static void (*g_scene_remove_entity)(FilaScene*, FilaEntity) = FilaScene_removeEntity;
static void (*g_scene_remove_all_entities)(FilaScene*) = FilaScene_removeAllEntities;
static size_t (*g_scene_get_entity_count)(const FilaScene*) = FilaScene_getEntityCount;
static bool (*g_scene_has_entity)(const FilaScene*, FilaEntity) = FilaScene_hasEntity;
static void (*g_scene_set_skybox)(FilaScene*, FilaSkybox*) = FilaScene_setSkybox;
static FilaSkybox* (*g_scene_get_skybox)(FilaScene*) = FilaScene_getSkybox;
static void (*g_scene_set_indirect_light)(FilaScene*, FilaIndirectLight*) = FilaScene_setIndirectLight;
static FilaIndirectLight* (*g_scene_get_indirect_light)(FilaScene*) = FilaScene_getIndirectLight;

void fila_scene_signature_compile_only(void) {
    (void)g_scene_add_entity;
    (void)g_scene_remove_entity;
    (void)g_scene_remove_all_entities;
    (void)g_scene_get_entity_count;
    (void)g_scene_has_entity;
    (void)g_scene_set_skybox;
    (void)g_scene_get_skybox;
    (void)g_scene_set_indirect_light;
    (void)g_scene_get_indirect_light;
}

