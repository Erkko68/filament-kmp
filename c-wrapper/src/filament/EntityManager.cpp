#include <utils/Entity.h>
#include <utils/EntityManager.h>

#include <vector>

#include "../../include/filament/Types.h"

extern "C" {

FilaEntity FilaEntityManager_create(void) {
    utils::Entity entity = utils::EntityManager::get().create();
    return utils::Entity::smuggle(entity);
}

void FilaEntityManager_createMany(size_t count, FilaEntity* outEntities) {
    if (count == 0 || !outEntities) {
        return;
    }

    std::vector<utils::Entity> entities(count);
    utils::EntityManager::get().create(count, entities.data());
    for (size_t i = 0; i < count; ++i) {
        outEntities[i] = utils::Entity::smuggle(entities[i]);
    }
}

void FilaEntityManager_destroy(FilaEntity entity) {
    if (entity == 0) {
        return;
    }
    utils::EntityManager::get().destroy(utils::Entity::import(entity));
}

void FilaEntityManager_destroyMany(size_t count, FilaEntity* entities) {
    if (count == 0 || !entities) {
        return;
    }

    std::vector<utils::Entity> nativeEntities(count);
    for (size_t i = 0; i < count; ++i) {
        nativeEntities[i] = utils::Entity::import(entities[i]);
    }
    utils::EntityManager::get().destroy(count, nativeEntities.data());
    for (size_t i = 0; i < count; ++i) {
        entities[i] = 0;
    }
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

