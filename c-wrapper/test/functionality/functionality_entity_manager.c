#include <stdio.h>

#include "utils/EntityManager.h"

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

    printf("Entity manager functionality program completed\n");
    return 0;
}
