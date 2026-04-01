#include "utils/EntityManager.h"

// Verifies EntityManager API is consumable from C.
void test_headers_entity_manager(void) {
    FilaEntity entity = FilaEntityManager_create();
    FilaEntity entities[4] = {0};

    FilaEntityManager_createMany(4u, entities);
    (void)FilaEntityManager_isAlive(entity);
    (void)FilaEntityManager_getEntityCount();
    (void)FilaEntityManager_getMaxEntityCount();
    FilaEntityManager_destroyMany(4u, entities);
    FilaEntityManager_destroy(entity);
}
