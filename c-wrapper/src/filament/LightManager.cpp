#include <filament/LightManager.h>

#include <utils/Entity.h>

#include "../../../include/filament/LightManager.h"

namespace {
utils::Entity toEntity(FilaEntity entity) {
    return utils::Entity::import(entity);
}

filament::LightManager::Instance toInstance(FilaLightManagerInstance instance) {
    return filament::LightManager::Instance(instance);
}

FilaLightManagerInstance fromInstance(filament::LightManager::Instance instance) {
    return instance.asValue();
}
} // namespace

extern "C" {

bool FilaLightManager_hasComponent(const FilaLightManager* manager, FilaEntity entity) {
    if (!manager || entity == 0) {
        return false;
    }
    auto cppManager = reinterpret_cast<const filament::LightManager*>(manager);
    return cppManager->hasComponent(toEntity(entity));
}

FilaLightManagerInstance FilaLightManager_getInstance(const FilaLightManager* manager, FilaEntity entity) {
    if (!manager || entity == 0) {
        return 0;
    }
    auto cppManager = reinterpret_cast<const filament::LightManager*>(manager);
    return fromInstance(cppManager->getInstance(toEntity(entity)));
}

size_t FilaLightManager_getComponentCount(const FilaLightManager* manager) {
    if (!manager) {
        return 0;
    }
    auto cppManager = reinterpret_cast<const filament::LightManager*>(manager);
    return cppManager->getComponentCount();
}

bool FilaLightManager_empty(const FilaLightManager* manager) {
    if (!manager) {
        return true;
    }
    auto cppManager = reinterpret_cast<const filament::LightManager*>(manager);
    return cppManager->empty();
}

FilaEntity FilaLightManager_getEntity(const FilaLightManager* manager, FilaLightManagerInstance instance) {
    if (!manager || instance == 0) {
        return 0;
    }
    auto cppManager = reinterpret_cast<const filament::LightManager*>(manager);
    return utils::Entity::smuggle(cppManager->getEntity(toInstance(instance)));
}

size_t FilaLightManager_getEntities(const FilaLightManager* manager, FilaEntity* outEntities, size_t maxCount) {
    if (!manager || !outEntities || maxCount == 0) {
        return 0;
    }
    auto cppManager = reinterpret_cast<const filament::LightManager*>(manager);
    const size_t count = cppManager->getComponentCount();
    const size_t written = (count < maxCount) ? count : maxCount;
    const utils::Entity* entities = cppManager->getEntities();
    for (size_t i = 0; i < written; ++i) {
        outEntities[i] = utils::Entity::smuggle(entities[i]);
    }
    return written;
}

void FilaLightManager_destroy(FilaLightManager* manager, FilaEntity entity) {
    if (!manager || entity == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::LightManager*>(manager);
    cppManager->destroy(toEntity(entity));
}

} // extern "C"

