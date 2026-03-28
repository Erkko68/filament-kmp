#include <stdbool.h>
#include <stddef.h>

#include "filament/EntityManager.h"

// Function pointer assignments lock exported C signatures.
static FilaEntity (*g_entity_manager_create)(void) = FilaEntityManager_create;
static void (*g_entity_manager_create_many)(size_t, FilaEntity*) = FilaEntityManager_createMany;
static void (*g_entity_manager_destroy)(FilaEntity) = FilaEntityManager_destroy;
static void (*g_entity_manager_destroy_many)(size_t, FilaEntity*) = FilaEntityManager_destroyMany;
static bool (*g_entity_manager_is_alive)(FilaEntity) = FilaEntityManager_isAlive;
static size_t (*g_entity_manager_get_count)(void) = FilaEntityManager_getEntityCount;
static size_t (*g_entity_manager_get_max_count)(void) = FilaEntityManager_getMaxEntityCount;

void fila_entity_manager_signature_compile_only(void) {
    (void)g_entity_manager_create;
    (void)g_entity_manager_create_many;
    (void)g_entity_manager_destroy;
    (void)g_entity_manager_destroy_many;
    (void)g_entity_manager_is_alive;
    (void)g_entity_manager_get_count;
    (void)g_entity_manager_get_max_count;
}

