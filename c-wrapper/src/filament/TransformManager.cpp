#include <filament/TransformManager.h>

#include <math/mat4.h>

#include <utils/Entity.h>

#include <vector>

#include "../../include/filament/TransformManager.h"

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

filament::math::mat4f toMat4f(const float m[16]) {
    return filament::math::mat4f(
            m[0], m[1], m[2], m[3],
            m[4], m[5], m[6], m[7],
            m[8], m[9], m[10], m[11],
            m[12], m[13], m[14], m[15]);
}

filament::math::mat4 toMat4(const double m[16]) {
    return filament::math::mat4(
            m[0], m[1], m[2], m[3],
            m[4], m[5], m[6], m[7],
            m[8], m[9], m[10], m[11],
            m[12], m[13], m[14], m[15]);
}

void fromMat4f(const filament::math::mat4f& mat, float out[16]) {
    for (size_t c = 0; c < 4; ++c) {
        for (size_t r = 0; r < 4; ++r) {
            out[c * 4 + r] = mat[c][r];
        }
    }
}

void fromMat4(const filament::math::mat4& mat, double out[16]) {
    for (size_t c = 0; c < 4; ++c) {
        for (size_t r = 0; r < 4; ++r) {
            out[c * 4 + r] = mat[c][r];
        }
    }
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

void FilaTransformManager_createWithTransformMat4f(FilaTransformManager* manager,
        FilaEntity entity,
        FilaTransformManagerInstance parent,
        const float localTransform[16]) {
    if (!manager || entity == 0 || !localTransform) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::TransformManager*>(manager);
    cppManager->create(toEntity(entity), toInstance(parent), toMat4f(localTransform));
}

void FilaTransformManager_createWithTransformMat4(FilaTransformManager* manager,
        FilaEntity entity,
        FilaTransformManagerInstance parent,
        const double localTransform[16]) {
    if (!manager || entity == 0 || !localTransform) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::TransformManager*>(manager);
    cppManager->create(toEntity(entity), toInstance(parent), toMat4(localTransform));
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

void FilaTransformManager_setAccurateTranslationsEnabled(FilaTransformManager* manager, bool enable) {
    if (!manager) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::TransformManager*>(manager);
    cppManager->setAccurateTranslationsEnabled(enable);
}

bool FilaTransformManager_isAccurateTranslationsEnabled(const FilaTransformManager* manager) {
    if (!manager) {
        return false;
    }
    auto cppManager = reinterpret_cast<const filament::TransformManager*>(manager);
    return cppManager->isAccurateTranslationsEnabled();
}

size_t FilaTransformManager_getEntities(const FilaTransformManager* manager,
        FilaEntity* outEntities,
        size_t maxCount) {
    if (!manager || !outEntities || maxCount == 0) {
        return 0;
    }
    auto cppManager = reinterpret_cast<const filament::TransformManager*>(manager);
    const size_t count = cppManager->getComponentCount();
    const size_t written = (count < maxCount) ? count : maxCount;
    const utils::Entity* entities = cppManager->getEntities();
    for (size_t i = 0; i < written; ++i) {
        outEntities[i] = utils::Entity::smuggle(entities[i]);
    }
    return written;
}

FilaEntity FilaTransformManager_getEntity(const FilaTransformManager* manager, FilaTransformManagerInstance instance) {
    if (!manager || instance == 0) {
        return 0;
    }
    auto cppManager = reinterpret_cast<const filament::TransformManager*>(manager);
    return utils::Entity::smuggle(cppManager->getEntity(toInstance(instance)));
}

void FilaTransformManager_setParent(FilaTransformManager* manager,
        FilaTransformManagerInstance instance,
        FilaTransformManagerInstance newParent) {
    if (!manager || instance == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::TransformManager*>(manager);
    cppManager->setParent(toInstance(instance), toInstance(newParent));
}

FilaEntity FilaTransformManager_getParent(const FilaTransformManager* manager,
        FilaTransformManagerInstance instance) {
    if (!manager || instance == 0) {
        return 0;
    }
    auto cppManager = reinterpret_cast<const filament::TransformManager*>(manager);
    return utils::Entity::smuggle(cppManager->getParent(toInstance(instance)));
}

size_t FilaTransformManager_getChildCount(const FilaTransformManager* manager,
        FilaTransformManagerInstance instance) {
    if (!manager || instance == 0) {
        return 0;
    }
    auto cppManager = reinterpret_cast<const filament::TransformManager*>(manager);
    return cppManager->getChildCount(toInstance(instance));
}

size_t FilaTransformManager_getChildren(const FilaTransformManager* manager,
        FilaTransformManagerInstance instance,
        FilaEntity* outChildren,
        size_t maxCount) {
    if (!manager || instance == 0 || !outChildren || maxCount == 0) {
        return 0;
    }
    auto cppManager = reinterpret_cast<const filament::TransformManager*>(manager);
    std::vector<utils::Entity> children(maxCount);
    const size_t written = cppManager->getChildren(toInstance(instance), children.data(), maxCount);
    for (size_t i = 0; i < written; ++i) {
        outChildren[i] = utils::Entity::smuggle(children[i]);
    }
    return written;
}

size_t FilaTransformManager_getChildInstances(const FilaTransformManager* manager,
        FilaTransformManagerInstance instance,
        FilaTransformManagerInstance* outChildren,
        size_t maxCount) {
    if (!manager || instance == 0 || !outChildren || maxCount == 0) {
        return 0;
    }
    auto cppManager = reinterpret_cast<const filament::TransformManager*>(manager);
    const auto begin = cppManager->getChildrenBegin(toInstance(instance));
    const auto end = cppManager->getChildrenEnd(toInstance(instance));
    size_t written = 0;
    for (auto it = begin; it != end && written < maxCount; ++it) {
        outChildren[written++] = fromInstance(*it);
    }
    return written;
}

bool FilaTransformManager_getChildrenBegin(const FilaTransformManager* manager,
        FilaTransformManagerInstance instance,
        FilaTransformManagerInstance* outBegin) {
    if (!manager || instance == 0 || !outBegin) {
        return false;
    }
    auto cppManager = reinterpret_cast<const filament::TransformManager*>(manager);
    const auto begin = cppManager->getChildrenBegin(toInstance(instance));
    const auto end = cppManager->getChildrenEnd(toInstance(instance));
    *outBegin = (begin == end) ? 0u : fromInstance(*begin);
    return true;
}

bool FilaTransformManager_getChildrenEnd(const FilaTransformManager* manager,
        FilaTransformManagerInstance instance,
        FilaTransformManagerInstance* outEnd) {
    if (!manager || instance == 0 || !outEnd) {
        return false;
    }
    // C API uses 0 as the end sentinel for child-iterator parity helpers.
    *outEnd = 0u;
    return true;
}

void FilaTransformManager_forEachChildInstance(const FilaTransformManager* manager,
        FilaTransformManagerInstance instance,
        FilaTransformManagerInstanceCallback callback,
        void* userData) {
    if (!manager || instance == 0 || !callback) {
        return;
    }
    auto cppManager = reinterpret_cast<const filament::TransformManager*>(manager);
    const auto begin = cppManager->getChildrenBegin(toInstance(instance));
    const auto end = cppManager->getChildrenEnd(toInstance(instance));
    for (auto it = begin; it != end; ++it) {
        callback(fromInstance(*it), userData);
    }
}

void FilaTransformManager_setTransformMat4f(FilaTransformManager* manager,
        FilaTransformManagerInstance instance,
        const float localTransform[16]) {
    if (!manager || instance == 0 || !localTransform) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::TransformManager*>(manager);
    cppManager->setTransform(toInstance(instance), toMat4f(localTransform));
}

bool FilaTransformManager_getTransformMat4f(const FilaTransformManager* manager,
        FilaTransformManagerInstance instance,
        float outLocalTransform[16]) {
    if (!manager || instance == 0 || !outLocalTransform) {
        return false;
    }
    auto cppManager = reinterpret_cast<const filament::TransformManager*>(manager);
    fromMat4f(cppManager->getTransform(toInstance(instance)), outLocalTransform);
    return true;
}

bool FilaTransformManager_getWorldTransformMat4f(const FilaTransformManager* manager,
        FilaTransformManagerInstance instance,
        float outWorldTransform[16]) {
    if (!manager || instance == 0 || !outWorldTransform) {
        return false;
    }
    auto cppManager = reinterpret_cast<const filament::TransformManager*>(manager);
    fromMat4f(cppManager->getWorldTransform(toInstance(instance)), outWorldTransform);
    return true;
}

void FilaTransformManager_setTransformMat4(FilaTransformManager* manager,
        FilaTransformManagerInstance instance,
        const double localTransform[16]) {
    if (!manager || instance == 0 || !localTransform) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::TransformManager*>(manager);
    cppManager->setTransform(toInstance(instance), toMat4(localTransform));
}

bool FilaTransformManager_getTransformMat4(const FilaTransformManager* manager,
        FilaTransformManagerInstance instance,
        double outLocalTransform[16]) {
    if (!manager || instance == 0 || !outLocalTransform) {
        return false;
    }
    auto cppManager = reinterpret_cast<const filament::TransformManager*>(manager);
    fromMat4(cppManager->getTransformAccurate(toInstance(instance)), outLocalTransform);
    return true;
}

bool FilaTransformManager_getTransformAccurate(const FilaTransformManager* manager,
        FilaTransformManagerInstance instance,
        double outLocalTransform[16]) {
    return FilaTransformManager_getTransformMat4(manager, instance, outLocalTransform);
}

bool FilaTransformManager_getWorldTransformMat4(const FilaTransformManager* manager,
        FilaTransformManagerInstance instance,
        double outWorldTransform[16]) {
    if (!manager || instance == 0 || !outWorldTransform) {
        return false;
    }
    auto cppManager = reinterpret_cast<const filament::TransformManager*>(manager);
    fromMat4(cppManager->getWorldTransformAccurate(toInstance(instance)), outWorldTransform);
    return true;
}

bool FilaTransformManager_getWorldTransformAccurate(const FilaTransformManager* manager,
        FilaTransformManagerInstance instance,
        double outWorldTransform[16]) {
    return FilaTransformManager_getWorldTransformMat4(manager, instance, outWorldTransform);
}

void FilaTransformManager_openLocalTransformTransaction(FilaTransformManager* manager) {
    if (!manager) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::TransformManager*>(manager);
    cppManager->openLocalTransformTransaction();
}

void FilaTransformManager_commitLocalTransformTransaction(FilaTransformManager* manager) {
    if (!manager) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::TransformManager*>(manager);
    cppManager->commitLocalTransformTransaction();
}

} // extern "C"

