#include <stdio.h>
#include <stdbool.h>

#include "utils/EntityManager.h"

typedef struct ListenerState {
    size_t callCount;
    size_t totalDestroyed;
} ListenerState;

static void on_entities_destroyed(size_t count, const FilaEntity* entities, void* userData) {
    ListenerState* state = (ListenerState*)userData;
    if (!state) {
        return;
    }
    state->callCount += 1u;
    state->totalDestroyed += count;
    (void)entities;
}

int main(void) {
    printf("Running entity manager functionality program...\n");

    FilaEntity entity = FilaEntityManager_create();
    if (entity == 0) {
        printf("Entity creation failed\n");
        return 1;
    }

    if (!FilaEntityManager_isAlive(entity)) {
        printf("Entity is not alive after creation\n");
        FilaEntityManager_destroy(entity);
        return 1;
    }

    const size_t beforeDestroyCount = FilaEntityManager_getEntityCount();
    FilaEntityManager_destroy(entity);

    if (FilaEntityManager_isAlive(entity)) {
        printf("Entity is still alive after destroy\n");
        return 1;
    }

    const size_t afterDestroyCount = FilaEntityManager_getEntityCount();
    if (afterDestroyCount > beforeDestroyCount) {
        printf("Entity count increased unexpectedly\n");
        return 1;
    }

    {
        const size_t maxCount = FilaEntityManager_getMaxEntityCount();
        if (FilaEntityManager_getGenerationForIndex(maxCount + 1u) != 0u) {
            printf("Generation lookup should return 0 for out-of-range index\n");
            return 1;
        }
        (void)FilaEntityManager_getGenerationForIndex(0u);
    }

    FilaEntity batch[3] = {0};
    FilaEntityManager_createMany(3u, batch);
    for (size_t i = 0; i < 3u; ++i) {
        if (batch[i] == 0 || !FilaEntityManager_isAlive(batch[i])) {
            printf("Bulk entity creation failed at index %zu\n", i);
            FilaEntityManager_destroyMany(3u, batch);
            return 1;
        }
    }

    FilaEntityManager_destroyMany(3u, batch);
    for (size_t i = 0; i < 3u; ++i) {
        if (batch[i] != 0) {
            printf("Bulk entity destroy did not clear slot %zu\n", i);
            return 1;
        }
    }

    {
        FilaEntity active[8] = {0};
        const bool tracking = FilaEntityManager_isTrackingEnabled();
        const size_t written = FilaEntityManager_getActiveEntities(active, 8u);
        if (!tracking && written != 0u) {
            printf("Active entities should be unavailable when tracking is disabled\n");
            return 1;
        }

        char dumpText[256] = {0};
        const size_t dumpLen = FilaEntityManager_dumpActiveEntities(dumpText, sizeof(dumpText));
        const size_t probeLen = FilaEntityManager_dumpActiveEntities((char*)0, 0u);
        if (dumpLen != probeLen) {
            printf("Active entities dump length mismatch\n");
            return 1;
        }
        if (!tracking && dumpLen != 0u) {
            printf("Active entities dump should be unavailable when tracking is disabled\n");
            return 1;
        }
    }

    {
        ListenerState state = {0u, 0u};
        FilaEntityManagerListener* listener = FilaEntityManagerListener_create(on_entities_destroyed, &state);
        if (!listener) {
            printf("EntityManager listener creation failed\n");
            return 1;
        }
        if (!FilaEntityManager_registerListener(listener)) {
            printf("EntityManager registerListener failed\n");
            FilaEntityManagerListener_destroy(listener);
            return 1;
        }

        FilaEntity entities[2] = {0};
        FilaEntityManager_createMany(2u, entities);
        FilaEntityManager_destroyMany(2u, entities);
        if (state.callCount == 0u || state.totalDestroyed < 2u) {
            printf("EntityManager listener did not observe destruction\n");
            FilaEntityManager_unregisterListener(listener);
            FilaEntityManagerListener_destroy(listener);
            return 1;
        }

        const size_t callCountBeforeUnregister = state.callCount;
        if (!FilaEntityManager_unregisterListener(listener)) {
            printf("EntityManager unregisterListener failed\n");
            FilaEntityManagerListener_destroy(listener);
            return 1;
        }

        FilaEntity postEntities[1] = {0};
        FilaEntityManager_createMany(1u, postEntities);
        FilaEntityManager_destroyMany(1u, postEntities);
        if (state.callCount != callCountBeforeUnregister) {
            printf("EntityManager listener should not be called after unregister\n");
            FilaEntityManagerListener_destroy(listener);
            return 1;
        }

        if (!FilaEntityManagerListener_onEntitiesDestroyed(listener, 0u, (const FilaEntity*)0)) {
            printf("EntityManager listener manual callback bridge failed\n");
            FilaEntityManagerListener_destroy(listener);
            return 1;
        }

        FilaEntityManagerListener_destroy(listener);
    }

    if (FilaEntityManager_registerListener((FilaEntityManagerListener*)0) ||
            FilaEntityManager_unregisterListener((FilaEntityManagerListener*)0) ||
            FilaEntityManagerListener_onEntitiesDestroyed((FilaEntityManagerListener*)0, 0u, (const FilaEntity*)0) ||
            FilaEntityManagerListener_create((FilaEntityManagerEntitiesDestroyedCallback)0, (void*)0) != (FilaEntityManagerListener*)0) {
        printf("EntityManager listener null-safety mismatch\n");
        return 1;
    }

    printf("Entity manager functionality program completed\n");
    return 0;
}
