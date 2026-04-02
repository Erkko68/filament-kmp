#include "utils/EntityManager.h"

// Verifies EntityManager API is consumable from C.
void test_headers_entity_manager(void) {
    FilaEntity entity = FilaEntityManager_create();
    FilaEntity entities[4] = {0};
    char dumpText[128];
    FilaEntityManagerListener* listener = (FilaEntityManagerListener*)0;

    FilaEntityManager_createMany(4u, entities);
    (void)FilaEntityManager_isAlive(entity);
    (void)FilaEntityManager_getEntityCount();
    (void)FilaEntityManager_getMaxEntityCount();
    (void)FilaEntityManager_getGenerationForIndex(0u);
    (void)FilaEntityManager_isTrackingEnabled();
    (void)FilaEntityManager_getActiveEntities(entities, 4u);
    (void)FilaEntityManager_dumpActiveEntities(dumpText, sizeof(dumpText));
    listener = FilaEntityManagerListener_create((FilaEntityManagerEntitiesDestroyedCallback)0, (void*)0);
    (void)FilaEntityManager_registerListener(listener);
    (void)FilaEntityManagerListener_onEntitiesDestroyed(listener, 0u, entities);
    (void)FilaEntityManager_unregisterListener(listener);
    FilaEntityManagerListener_destroy(listener);
    FilaEntityManager_destroyMany(4u, entities);
    FilaEntityManager_destroy(entity);
}
