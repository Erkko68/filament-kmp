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

uint8_t FilaEntityManager_getGenerationForIndex(size_t index) {
    if (index > utils::EntityManager::getMaxEntityCount()) {
        return 0u;
    }
    return utils::EntityManager::get().getGenerationForIndex(index);
}

bool FilaEntityManager_isTrackingEnabled(void) {
#if FILAMENT_UTILS_TRACK_ENTITIES
    return true;
#else
    return false;
#endif
}

size_t FilaEntityManager_getActiveEntities(FilaEntity* outEntities, size_t maxCount) {
    if (!outEntities || maxCount == 0u) {
        return 0u;
    }
#if FILAMENT_UTILS_TRACK_ENTITIES
    const auto active = utils::EntityManager::get().getActiveEntities();
    const size_t count = active.size() < maxCount ? active.size() : maxCount;
    for (size_t i = 0u; i < count; ++i) {
        outEntities[i] = utils::Entity::smuggle(active[i]);
    }
    return count;
#else
    return 0u;
#endif
}

} // extern "C"

