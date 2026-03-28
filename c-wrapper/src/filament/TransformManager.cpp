#include <filament/TransformManager.h>

#include <utils/Entity.h>

#include "../../../include/filament/TransformManager.h"

namespace {
utils::Entity toEntity(FilaEntity entity) {
    return utils::Entity::import(entity);
}

filament::TransformManager::Instance toInstance(FilaTransformManagerInstance instance) {
    return filament::TransformManager::Instance(instance);
}

FilaTransformManagerInstance fromInstance(filament::TransformManager::Instance instance) {
    return instance.asValue();
}
} // namespace

extern "C" {

bool FilaTransformManager_hasComponent(const FilaTransformManager* manager, FilaEntity entity) {
    if (!manager || entity == 0) {
        return false;
    }
    auto cppManager = reinterpret_cast<const filament::TransformManager*>(manager);
    return cppManager->hasComponent(toEntity(entity));
}

FilaTransformManagerInstance FilaTransformManager_getInstance(const FilaTransformManager* manager, FilaEntity entity) {
    if (!manager || entity == 0) {
        return 0;
    }
    auto cppManager = reinterpret_cast<const filament::TransformManager*>(manager);
    return fromInstance(cppManager->getInstance(toEntity(entity)));
}

void FilaTransformManager_create(FilaTransformManager* manager, FilaEntity entity, FilaTransformManagerInstance parent) {
    if (!manager || entity == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::TransformManager*>(manager);
    cppManager->create(toEntity(entity), toInstance(parent));
}

void FilaTransformManager_destroy(FilaTransformManager* manager, FilaEntity entity) {
    if (!manager || entity == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::TransformManager*>(manager);
    cppManager->destroy(toEntity(entity));
}

size_t FilaTransformManager_getComponentCount(const FilaTransformManager* manager) {
    if (!manager) {
        return 0;
    }
    auto cppManager = reinterpret_cast<const filament::TransformManager*>(manager);
    return cppManager->getComponentCount();
}

bool FilaTransformManager_empty(const FilaTransformManager* manager) {
    if (!manager) {
        return true;
    }
    auto cppManager = reinterpret_cast<const filament::TransformManager*>(manager);
    return cppManager->empty();
}

FilaEntity FilaTransformManager_getEntity(const FilaTransformManager* manager, FilaTransformManagerInstance instance) {
    if (!manager || instance == 0) {
        return 0;
    }
    auto cppManager = reinterpret_cast<const filament::TransformManager*>(manager);
    return utils::Entity::smuggle(cppManager->getEntity(toInstance(instance)));
}

} // extern "C"

