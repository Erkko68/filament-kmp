#include <stdio.h>

#include "utils/EntityManager.h"
#include "utils/NameComponentManager.h"

int main(void) {
    printf("Running functionality_utils_name_component_manager...\n");

    FilaNameComponentManager* manager = FilaNameComponentManager_create();
    if (!manager) {
        printf("NameComponentManager create failed\n");
        return 1;
    }

    FilaEntity entity = FilaEntityManager_create();
    if (entity == 0) {
        printf("Entity create failed\n");
        FilaNameComponentManager_destroy(manager);
        return 1;
    }

    if (FilaNameComponentManager_hasComponent(manager, entity)) {
        printf("Entity should not have name component initially\n");
        FilaEntityManager_destroy(entity);
        FilaNameComponentManager_destroy(manager);
        return 1;
    }

    FilaNameComponentManager_addComponent(manager, entity);
    if (!FilaNameComponentManager_hasComponent(manager, entity)) {
        printf("Name component add failed\n");
        FilaEntityManager_destroy(entity);
        FilaNameComponentManager_destroy(manager);
        return 1;
    }

    const FilaEntityInstance instance = FilaNameComponentManager_getInstance(manager, entity);
    if (instance == 0u) {
        printf("Name component instance missing\n");
        FilaEntityManager_destroy(entity);
        FilaNameComponentManager_destroy(manager);
        return 1;
    }

    FilaNameComponentManager_setName(manager, instance, "entity_demo");
    char copied[32] = {0};
    const size_t nameLen = FilaNameComponentManager_copyName(manager, instance, copied, sizeof(copied));
    if (nameLen == 0u || copied[0] == '\0') {
        printf("Name copy failed\n");
        FilaEntityManager_destroy(entity);
        FilaNameComponentManager_destroy(manager);
        return 1;
    }

    FilaNameComponentManager_gc(manager);
    FilaNameComponentManager_removeComponent(manager, entity);
    if (FilaNameComponentManager_hasComponent(manager, entity)) {
        printf("Name component remove failed\n");
        FilaEntityManager_destroy(entity);
        FilaNameComponentManager_destroy(manager);
        return 1;
    }

    if (FilaNameComponentManager_getInstance((const FilaNameComponentManager*)0, entity) != 0u ||
            FilaNameComponentManager_getName((const FilaNameComponentManager*)0, instance) != (const char*)0 ||
            FilaNameComponentManager_copyName((const FilaNameComponentManager*)0, instance, copied, sizeof(copied)) != 0u) {
        printf("Name component null-safety mismatch\n");
        FilaEntityManager_destroy(entity);
        FilaNameComponentManager_destroy(manager);
        return 1;
    }

    FilaEntityManager_destroy(entity);
    FilaNameComponentManager_destroy(manager);

    printf("functionality_utils_name_component_manager completed\n");
    return 0;
}

