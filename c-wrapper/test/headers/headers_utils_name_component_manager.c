#include "utils/NameComponentManager.h"
#include "utils/EntityManager.h"

void test_headers_utils_name_component_manager(void) {
    FilaNameComponentManager* manager = FilaNameComponentManager_create();
    FilaEntity entity = FilaEntityManager_create();
    char name[32];

    (void)FilaNameComponentManager_hasComponent(manager, entity);
    FilaNameComponentManager_addComponent(manager, entity);
    FilaEntityInstance instance = FilaNameComponentManager_getInstance(manager, entity);
    FilaNameComponentManager_setName(manager, instance, "demo");
    (void)FilaNameComponentManager_getName(manager, instance);
    (void)FilaNameComponentManager_copyName(manager, instance, name, sizeof(name));
    FilaNameComponentManager_gc(manager);
    FilaNameComponentManager_removeComponent(manager, entity);

    FilaEntityManager_destroy(entity);
    FilaNameComponentManager_destroy(manager);
}


