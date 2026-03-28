#include "filament/EntityManager.h"

// Verifies EntityManager API is consumable from C.
void fila_entity_manager_module_compile_only(void) {
    FilaEntity entity = FilaEntityManager_create();
    (void)FilaEntityManager_isAlive(entity);
    (void)FilaEntityManager_getEntityCount();
    (void)FilaEntityManager_getMaxEntityCount();
    FilaEntityManager_destroy(entity);
}

