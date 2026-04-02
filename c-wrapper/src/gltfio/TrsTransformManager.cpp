#include <gltfio/TrsTransformManager.h>

#include <math/mat4.h>
#include <math/quat.h>
#include <math/vec3.h>
#include <utils/Entity.h>

#include "../../include/gltfio/TrsTransformManager.h"

namespace {

utils::Entity toEntity(FilaEntity entity) {
    return utils::Entity::import(entity);
}

filament::gltfio::TrsTransformManager::Instance toInstance(FilaGltfioTrsTransformManagerInstance instance) {
    return filament::gltfio::TrsTransformManager::Instance(instance);
}

FilaGltfioTrsTransformManagerInstance fromInstance(filament::gltfio::TrsTransformManager::Instance instance) {
    return instance.asValue();
}

filament::math::float3 toFloat3(const float v[3]) {
    return filament::math::float3(v[0], v[1], v[2]);
}

filament::math::quatf toQuatf(const float q[4]) {
    return filament::math::quatf(q[3], q[0], q[1], q[2]);
}

void fromFloat3(const filament::math::float3& in, float out[3]) {
    out[0] = in.x;
    out[1] = in.y;
    out[2] = in.z;
}

void fromQuatf(const filament::math::quatf& in, float out[4]) {
    out[0] = in.x;
    out[1] = in.y;
    out[2] = in.z;
    out[3] = in.w;
}

void fromMat4f(const filament::math::mat4f& in, float out[16]) {
    for (size_t c = 0u; c < 4u; ++c) {
        for (size_t r = 0u; r < 4u; ++r) {
            out[c * 4u + r] = in[c][r];
        }
    }
}

bool isValidInstance(FilaGltfioTrsTransformManagerInstance instance) {
    return toInstance(instance).isValid();
}

} // namespace

extern "C" {

bool FilaGltfioTrsTransformManager_hasComponent(
        const FilaGltfioTrsTransformManager* manager,
        FilaEntity entity) {
    if (!manager || entity == 0) {
        return false;
    }
    auto* cppManager = reinterpret_cast<const filament::gltfio::TrsTransformManager*>(manager);
    return cppManager->hasComponent(toEntity(entity));
}

FilaGltfioTrsTransformManagerInstance FilaGltfioTrsTransformManager_getInstance(
        const FilaGltfioTrsTransformManager* manager,
        FilaEntity entity) {
    if (!manager || entity == 0) {
        return 0u;
    }
    auto* cppManager = reinterpret_cast<const filament::gltfio::TrsTransformManager*>(manager);
    return fromInstance(cppManager->getInstance(toEntity(entity)));
}

bool FilaGltfioTrsTransformManager_isValidInstance(FilaGltfioTrsTransformManagerInstance instance) {
    return isValidInstance(instance);
}

void FilaGltfioTrsTransformManager_create(
        FilaGltfioTrsTransformManager* manager,
        FilaEntity entity) {
    if (!manager || entity == 0) {
        return;
    }
    auto* cppManager = reinterpret_cast<filament::gltfio::TrsTransformManager*>(manager);
    cppManager->create(toEntity(entity));
}

void FilaGltfioTrsTransformManager_createWithTrs(
        FilaGltfioTrsTransformManager* manager,
        FilaEntity entity,
        const float translation3[3],
        const float rotation4[4],
        const float scale3[3]) {
    if (!manager || entity == 0 || !translation3 || !rotation4 || !scale3) {
        return;
    }
    auto* cppManager = reinterpret_cast<filament::gltfio::TrsTransformManager*>(manager);
    cppManager->create(toEntity(entity), toFloat3(translation3), toQuatf(rotation4), toFloat3(scale3));
}

void FilaGltfioTrsTransformManager_destroy(
        FilaGltfioTrsTransformManager* manager,
        FilaEntity entity) {
    if (!manager || entity == 0) {
        return;
    }
    auto* cppManager = reinterpret_cast<filament::gltfio::TrsTransformManager*>(manager);
    cppManager->destroy(toEntity(entity));
}

void FilaGltfioTrsTransformManager_setTranslation(
        FilaGltfioTrsTransformManager* manager,
        FilaGltfioTrsTransformManagerInstance instance,
        const float translation3[3]) {
    if (!manager || !translation3 || !isValidInstance(instance)) {
        return;
    }
    auto* cppManager = reinterpret_cast<filament::gltfio::TrsTransformManager*>(manager);
    cppManager->setTranslation(toInstance(instance), toFloat3(translation3));
}

bool FilaGltfioTrsTransformManager_getTranslation(
        const FilaGltfioTrsTransformManager* manager,
        FilaGltfioTrsTransformManagerInstance instance,
        float outTranslation3[3]) {
    if (!manager || !outTranslation3 || !isValidInstance(instance)) {
        return false;
    }
    auto* cppManager = reinterpret_cast<const filament::gltfio::TrsTransformManager*>(manager);
    fromFloat3(cppManager->getTranslation(toInstance(instance)), outTranslation3);
    return true;
}

void FilaGltfioTrsTransformManager_setRotation(
        FilaGltfioTrsTransformManager* manager,
        FilaGltfioTrsTransformManagerInstance instance,
        const float rotation4[4]) {
    if (!manager || !rotation4 || !isValidInstance(instance)) {
        return;
    }
    auto* cppManager = reinterpret_cast<filament::gltfio::TrsTransformManager*>(manager);
    cppManager->setRotation(toInstance(instance), toQuatf(rotation4));
}

bool FilaGltfioTrsTransformManager_getRotation(
        const FilaGltfioTrsTransformManager* manager,
        FilaGltfioTrsTransformManagerInstance instance,
        float outRotation4[4]) {
    if (!manager || !outRotation4 || !isValidInstance(instance)) {
        return false;
    }
    auto* cppManager = reinterpret_cast<const filament::gltfio::TrsTransformManager*>(manager);
    fromQuatf(cppManager->getRotation(toInstance(instance)), outRotation4);
    return true;
}

void FilaGltfioTrsTransformManager_setScale(
        FilaGltfioTrsTransformManager* manager,
        FilaGltfioTrsTransformManagerInstance instance,
        const float scale3[3]) {
    if (!manager || !scale3 || !isValidInstance(instance)) {
        return;
    }
    auto* cppManager = reinterpret_cast<filament::gltfio::TrsTransformManager*>(manager);
    cppManager->setScale(toInstance(instance), toFloat3(scale3));
}

bool FilaGltfioTrsTransformManager_getScale(
        const FilaGltfioTrsTransformManager* manager,
        FilaGltfioTrsTransformManagerInstance instance,
        float outScale3[3]) {
    if (!manager || !outScale3 || !isValidInstance(instance)) {
        return false;
    }
    auto* cppManager = reinterpret_cast<const filament::gltfio::TrsTransformManager*>(manager);
    fromFloat3(cppManager->getScale(toInstance(instance)), outScale3);
    return true;
}

void FilaGltfioTrsTransformManager_setTrs(
        FilaGltfioTrsTransformManager* manager,
        FilaGltfioTrsTransformManagerInstance instance,
        const float translation3[3],
        const float rotation4[4],
        const float scale3[3]) {
    if (!manager || !translation3 || !rotation4 || !scale3 || !isValidInstance(instance)) {
        return;
    }
    auto* cppManager = reinterpret_cast<filament::gltfio::TrsTransformManager*>(manager);
    cppManager->setTrs(toInstance(instance), toFloat3(translation3), toQuatf(rotation4), toFloat3(scale3));
}

bool FilaGltfioTrsTransformManager_getTransform(
        const FilaGltfioTrsTransformManager* manager,
        FilaGltfioTrsTransformManagerInstance instance,
        float outMat4[16]) {
    if (!manager || !outMat4 || !isValidInstance(instance)) {
        return false;
    }
    auto* cppManager = reinterpret_cast<const filament::gltfio::TrsTransformManager*>(manager);
    fromMat4f(cppManager->getTransform(toInstance(instance)), outMat4);
    return true;
}

} // extern "C"

