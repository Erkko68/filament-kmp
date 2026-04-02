#include <gltfio/Animator.h>
#include <gltfio/FilamentInstance.h>

#include <math/mat4.h>

#include <cstring>

#include <utils/Entity.h>

#include "../../include/gltfio/FilamentInstance.h"

namespace {

size_t copyCString(const char* text, char* outText, size_t outTextSize) {
    if (!text) {
        if (outText && outTextSize > 0u) {
            outText[0] = '\0';
        }
        return 0u;
    }

    const size_t length = std::strlen(text);
    if (!outText || outTextSize == 0u) {
        return length;
    }

    const size_t written = (length < (outTextSize - 1u)) ? length : (outTextSize - 1u);
    std::memcpy(outText, text, written);
    outText[written] = '\0';
    return length;
}

FilaEntity fromEntity(utils::Entity entity) {
    return utils::Entity::smuggle(entity);
}

void fromMat4f(const filament::math::mat4f& mat, float out16[16]) {
    for (size_t c = 0u; c < 4u; ++c) {
        for (size_t r = 0u; r < 4u; ++r) {
            out16[c * 4u + r] = mat[c][r];
        }
    }
}

void fromAabb(const filament::Aabb& aabb, FilaAabb* outAabb) {
    if (!outAabb) {
        return;
    }
    outAabb->min[0] = aabb.min.x;
    outAabb->min[1] = aabb.min.y;
    outAabb->min[2] = aabb.min.z;
    outAabb->max[0] = aabb.max.x;
    outAabb->max[1] = aabb.max.y;
    outAabb->max[2] = aabb.max.z;
}

} // namespace

extern "C" {

size_t FilaGltfioFilamentInstance_getEntities(const FilaGltfioFilamentInstance* instance,
        FilaEntity* outEntities,
        size_t maxCount) {
    if (!instance || !outEntities || maxCount == 0u) {
        return 0u;
    }
    auto* cppInstance = reinterpret_cast<const filament::gltfio::FilamentInstance*>(instance);
    const size_t entityCount = cppInstance->getEntityCount();
    const size_t written = (entityCount < maxCount) ? entityCount : maxCount;
    const utils::Entity* entities = cppInstance->getEntities();
    for (size_t i = 0u; i < written; ++i) {
        outEntities[i] = fromEntity(entities[i]);
    }
    return written;
}

size_t FilaGltfioFilamentInstance_getEntityCount(const FilaGltfioFilamentInstance* instance) {
    if (!instance) {
        return 0u;
    }
    auto* cppInstance = reinterpret_cast<const filament::gltfio::FilamentInstance*>(instance);
    return cppInstance->getEntityCount();
}

FilaEntity FilaGltfioFilamentInstance_getRoot(const FilaGltfioFilamentInstance* instance) {
    if (!instance) {
        return 0;
    }
    auto* cppInstance = reinterpret_cast<const filament::gltfio::FilamentInstance*>(instance);
    return fromEntity(cppInstance->getRoot());
}

void FilaGltfioFilamentInstance_applyMaterialVariant(FilaGltfioFilamentInstance* instance, size_t variantIndex) {
    if (!instance) {
        return;
    }
    auto* cppInstance = reinterpret_cast<filament::gltfio::FilamentInstance*>(instance);
    cppInstance->applyMaterialVariant(variantIndex);
}

size_t FilaGltfioFilamentInstance_getMaterialVariantCount(const FilaGltfioFilamentInstance* instance) {
    if (!instance) {
        return 0u;
    }
    auto* cppInstance = reinterpret_cast<const filament::gltfio::FilamentInstance*>(instance);
    return cppInstance->getMaterialVariantCount();
}

const char* FilaGltfioFilamentInstance_getMaterialVariantName(const FilaGltfioFilamentInstance* instance, size_t variantIndex) {
    if (!instance) {
        return nullptr;
    }
    auto* cppInstance = reinterpret_cast<const filament::gltfio::FilamentInstance*>(instance);
    return cppInstance->getMaterialVariantName(variantIndex);
}

size_t FilaGltfioFilamentInstance_copyMaterialVariantName(const FilaGltfioFilamentInstance* instance,
        size_t variantIndex,
        char* outName,
        size_t outNameSize) {
    const char* name = FilaGltfioFilamentInstance_getMaterialVariantName(instance, variantIndex);
    return copyCString(name, outName, outNameSize);
}

FilaGltfioAnimator* FilaGltfioFilamentInstance_getAnimator(FilaGltfioFilamentInstance* instance) {
    if (!instance) {
        return nullptr;
    }
    auto* cppInstance = reinterpret_cast<filament::gltfio::FilamentInstance*>(instance);
    return reinterpret_cast<FilaGltfioAnimator*>(cppInstance->getAnimator());
}

const FilaGltfioFilamentAsset* FilaGltfioFilamentInstance_getAsset(const FilaGltfioFilamentInstance* instance) {
    if (!instance) {
        return nullptr;
    }
    auto* cppInstance = reinterpret_cast<const filament::gltfio::FilamentInstance*>(instance);
    return reinterpret_cast<const FilaGltfioFilamentAsset*>(cppInstance->getAsset());
}

size_t FilaGltfioFilamentInstance_getSkinCount(const FilaGltfioFilamentInstance* instance) {
    if (!instance) {
        return 0u;
    }
    auto* cppInstance = reinterpret_cast<const filament::gltfio::FilamentInstance*>(instance);
    return cppInstance->getSkinCount();
}

const char* FilaGltfioFilamentInstance_getSkinNameAt(const FilaGltfioFilamentInstance* instance, size_t skinIndex) {
    if (!instance) {
        return nullptr;
    }
    auto* cppInstance = reinterpret_cast<const filament::gltfio::FilamentInstance*>(instance);
    return cppInstance->getSkinNameAt(skinIndex);
}

size_t FilaGltfioFilamentInstance_copySkinNameAt(const FilaGltfioFilamentInstance* instance,
        size_t skinIndex,
        char* outName,
        size_t outNameSize) {
    const char* name = FilaGltfioFilamentInstance_getSkinNameAt(instance, skinIndex);
    return copyCString(name, outName, outNameSize);
}

size_t FilaGltfioFilamentInstance_getJointCountAt(const FilaGltfioFilamentInstance* instance, size_t skinIndex) {
    if (!instance) {
        return 0u;
    }
    auto* cppInstance = reinterpret_cast<const filament::gltfio::FilamentInstance*>(instance);
    return cppInstance->getJointCountAt(skinIndex);
}

size_t FilaGltfioFilamentInstance_getJointsAt(const FilaGltfioFilamentInstance* instance,
        size_t skinIndex,
        FilaEntity* outJoints,
        size_t maxCount) {
    if (!instance || !outJoints || maxCount == 0u) {
        return 0u;
    }
    auto* cppInstance = reinterpret_cast<const filament::gltfio::FilamentInstance*>(instance);
    const size_t jointCount = cppInstance->getJointCountAt(skinIndex);
    const size_t written = (jointCount < maxCount) ? jointCount : maxCount;
    const utils::Entity* joints = cppInstance->getJointsAt(skinIndex);
    if (!joints) {
        return 0u;
    }
    for (size_t i = 0u; i < written; ++i) {
        outJoints[i] = fromEntity(joints[i]);
    }
    return written;
}

void FilaGltfioFilamentInstance_attachSkin(
        FilaGltfioFilamentInstance* instance,
        size_t skinIndex,
        FilaEntity target) {
    if (!instance || target == 0) {
        return;
    }
    auto* cppInstance = reinterpret_cast<filament::gltfio::FilamentInstance*>(instance);
    cppInstance->attachSkin(skinIndex, utils::Entity::import(target));
}

void FilaGltfioFilamentInstance_detachSkin(
        FilaGltfioFilamentInstance* instance,
        size_t skinIndex,
        FilaEntity target) {
    if (!instance || target == 0) {
        return;
    }
    auto* cppInstance = reinterpret_cast<filament::gltfio::FilamentInstance*>(instance);
    cppInstance->detachSkin(skinIndex, utils::Entity::import(target));
}

size_t FilaGltfioFilamentInstance_getInverseBindMatricesAt(
        const FilaGltfioFilamentInstance* instance,
        size_t skinIndex,
        float* outMatrices4x4,
        size_t maxMatrices) {
    if (!instance || !outMatrices4x4 || maxMatrices == 0u) {
        return 0u;
    }
    auto* cppInstance = reinterpret_cast<const filament::gltfio::FilamentInstance*>(instance);
    const size_t count = cppInstance->getJointCountAt(skinIndex);
    const size_t written = (count < maxMatrices) ? count : maxMatrices;
    const filament::math::mat4f* matrices = cppInstance->getInverseBindMatricesAt(skinIndex);
    if (!matrices) {
        return 0u;
    }
    for (size_t i = 0u; i < written; ++i) {
        fromMat4f(matrices[i], outMatrices4x4 + i * 16u);
    }
    return written;
}

size_t FilaGltfioFilamentInstance_getMaterialInstanceCount(const FilaGltfioFilamentInstance* instance) {
    if (!instance) {
        return 0u;
    }
    auto* cppInstance = reinterpret_cast<const filament::gltfio::FilamentInstance*>(instance);
    return cppInstance->getMaterialInstanceCount();
}

size_t FilaGltfioFilamentInstance_getMaterialInstances(const FilaGltfioFilamentInstance* instance,
        FilaMaterialInstance** outMaterialInstances,
        size_t maxCount) {
    if (!instance || !outMaterialInstances || maxCount == 0u) {
        return 0u;
    }
    auto* cppInstance = reinterpret_cast<const filament::gltfio::FilamentInstance*>(instance);
    const size_t count = cppInstance->getMaterialInstanceCount();
    const size_t written = (count < maxCount) ? count : maxCount;
    const filament::MaterialInstance* const* mats = cppInstance->getMaterialInstances();
    if (!mats) {
        return 0u;
    }
    for (size_t i = 0u; i < written; ++i) {
        outMaterialInstances[i] = reinterpret_cast<FilaMaterialInstance*>(const_cast<filament::MaterialInstance*>(mats[i]));
    }
    return written;
}

void FilaGltfioFilamentInstance_detachMaterialInstances(FilaGltfioFilamentInstance* instance) {
    if (!instance) {
        return;
    }
    auto* cppInstance = reinterpret_cast<filament::gltfio::FilamentInstance*>(instance);
    cppInstance->detachMaterialInstances();
}

void FilaGltfioFilamentInstance_recomputeBoundingBoxes(FilaGltfioFilamentInstance* instance) {
    if (!instance) {
        return;
    }
    auto* cppInstance = reinterpret_cast<filament::gltfio::FilamentInstance*>(instance);
    cppInstance->recomputeBoundingBoxes();
}

bool FilaGltfioFilamentInstance_getBoundingBox(const FilaGltfioFilamentInstance* instance, FilaAabb* outAabb) {
    if (!instance || !outAabb) {
        return false;
    }
    auto* cppInstance = reinterpret_cast<const filament::gltfio::FilamentInstance*>(instance);
    fromAabb(cppInstance->getBoundingBox(), outAabb);
    return true;
}

} // extern "C"

