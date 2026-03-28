#include <utils/Entity.h>
#include <utils/EntityManager.h>

#include "filament/Types.h"

extern "C" {

FilaEntity FilaEntityManager_create(void) {
    utils::Entity entity = utils::EntityManager::get().create();
    return utils::Entity::smuggle(entity);
}

void FilaEntityManager_destroy(FilaEntity entity) {
    if (entity == 0) {
        return;
    }
    utils::EntityManager::get().destroy(utils::Entity::import(entity));
}

bool FilaEntityManager_isAlive(FilaEntity entity) {
    if (entity == 0) {
        return false;
    }
    return utils::EntityManager::get().isAlive(utils::Entity::import(entity));
}

size_t FilaEntityManager_getEntityCount(void) {
    return utils::EntityManager::get().getEntityCount();
}

size_t FilaEntityManager_getMaxEntityCount(void) {
    return utils::EntityManager::getMaxEntityCount();
}

} // extern "C"

