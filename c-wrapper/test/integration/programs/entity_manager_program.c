#include <stdio.h>

#include "filament/EntityManager.h"

int main(void) {
    printf("Running entity manager smoke program...\n");

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

    printf("Entity manager smoke program completed\n");
    return 0;
}

