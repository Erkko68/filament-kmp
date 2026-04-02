#include <gltfio/FilamentAsset.h>

#include <cstring>
#include <vector>

#include <utils/Entity.h>

#include "../../include/gltfio/FilamentAsset.h"

namespace {

FilaEntity fromEntity(utils::Entity entity) {
    return utils::Entity::smuggle(entity);
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

size_t copyEntityList(const utils::Entity* entities, size_t entityCount, FilaEntity* outEntities, size_t maxCount) {
    if (!entities || !outEntities || maxCount == 0u) {
        return 0u;
    }
    const size_t written = (entityCount < maxCount) ? entityCount : maxCount;
    for (size_t i = 0u; i < written; ++i) {
        outEntities[i] = fromEntity(entities[i]);
    }
    return written;
}

} // namespace

extern "C" {

size_t FilaGltfioFilamentAsset_getEntities(const FilaGltfioFilamentAsset* asset,
        FilaEntity* outEntities,
        size_t maxCount) {
    if (!asset) {
        return 0u;
    }
    auto* cppAsset = reinterpret_cast<const filament::gltfio::FilamentAsset*>(asset);
    return copyEntityList(cppAsset->getEntities(), cppAsset->getEntityCount(), outEntities, maxCount);
}

size_t FilaGltfioFilamentAsset_getEntityCount(const FilaGltfioFilamentAsset* asset) {
    if (!asset) {
        return 0u;
    }
    auto* cppAsset = reinterpret_cast<const filament::gltfio::FilamentAsset*>(asset);
    return cppAsset->getEntityCount();
}

size_t FilaGltfioFilamentAsset_getLightEntities(const FilaGltfioFilamentAsset* asset,
        FilaEntity* outEntities,
        size_t maxCount) {
    if (!asset) {
        return 0u;
    }
    auto* cppAsset = reinterpret_cast<const filament::gltfio::FilamentAsset*>(asset);
    return copyEntityList(cppAsset->getLightEntities(), cppAsset->getLightEntityCount(), outEntities, maxCount);
}

size_t FilaGltfioFilamentAsset_getLightEntityCount(const FilaGltfioFilamentAsset* asset) {
    if (!asset) {
        return 0u;
    }
    auto* cppAsset = reinterpret_cast<const filament::gltfio::FilamentAsset*>(asset);
    return cppAsset->getLightEntityCount();
}

size_t FilaGltfioFilamentAsset_getRenderableEntities(const FilaGltfioFilamentAsset* asset,
        FilaEntity* outEntities,
        size_t maxCount) {
    if (!asset) {
        return 0u;
    }
    auto* cppAsset = reinterpret_cast<const filament::gltfio::FilamentAsset*>(asset);
    return copyEntityList(cppAsset->getRenderableEntities(), cppAsset->getRenderableEntityCount(), outEntities, maxCount);
}

size_t FilaGltfioFilamentAsset_getRenderableEntityCount(const FilaGltfioFilamentAsset* asset) {
    if (!asset) {
        return 0u;
    }
    auto* cppAsset = reinterpret_cast<const filament::gltfio::FilamentAsset*>(asset);
    return cppAsset->getRenderableEntityCount();
}

size_t FilaGltfioFilamentAsset_getCameraEntities(const FilaGltfioFilamentAsset* asset,
        FilaEntity* outEntities,
        size_t maxCount) {
    if (!asset) {
        return 0u;
    }
    auto* cppAsset = reinterpret_cast<const filament::gltfio::FilamentAsset*>(asset);
    return copyEntityList(cppAsset->getCameraEntities(), cppAsset->getCameraEntityCount(), outEntities, maxCount);
}

size_t FilaGltfioFilamentAsset_getCameraEntityCount(const FilaGltfioFilamentAsset* asset) {
    if (!asset) {
        return 0u;
    }
    auto* cppAsset = reinterpret_cast<const filament::gltfio::FilamentAsset*>(asset);
    return cppAsset->getCameraEntityCount();
}

FilaEntity FilaGltfioFilamentAsset_getRoot(const FilaGltfioFilamentAsset* asset) {
    if (!asset) {
        return 0;
    }
    auto* cppAsset = reinterpret_cast<const filament::gltfio::FilamentAsset*>(asset);
    return fromEntity(cppAsset->getRoot());
}

FilaEntity FilaGltfioFilamentAsset_popRenderable(FilaGltfioFilamentAsset* asset) {
    if (!asset) {
        return 0;
    }
    auto* cppAsset = reinterpret_cast<filament::gltfio::FilamentAsset*>(asset);
    return fromEntity(cppAsset->popRenderable());
}

size_t FilaGltfioFilamentAsset_popRenderables(
        FilaGltfioFilamentAsset* asset,
        FilaEntity* outEntities,
        size_t maxCount) {
    if (!asset || maxCount == 0u) {
        return 0u;
    }
    auto* cppAsset = reinterpret_cast<filament::gltfio::FilamentAsset*>(asset);
    if (!outEntities) {
        return cppAsset->popRenderables(nullptr, maxCount);
    }

    std::vector<utils::Entity> entities(maxCount);
    const size_t written = cppAsset->popRenderables(entities.data(), maxCount);
    for (size_t i = 0u; i < written; ++i) {
        outEntities[i] = fromEntity(entities[i]);
    }
    return written;
}

size_t FilaGltfioFilamentAsset_getResourceUriCount(const FilaGltfioFilamentAsset* asset) {
    if (!asset) {
        return 0u;
    }
    auto* cppAsset = reinterpret_cast<const filament::gltfio::FilamentAsset*>(asset);
    return cppAsset->getResourceUriCount();
}

const char* FilaGltfioFilamentAsset_getResourceUriAt(const FilaGltfioFilamentAsset* asset, size_t uriIndex) {
    if (!asset) {
        return nullptr;
    }
    auto* cppAsset = reinterpret_cast<const filament::gltfio::FilamentAsset*>(asset);
    const char* const* uris = cppAsset->getResourceUris();
    const size_t count = cppAsset->getResourceUriCount();
    if (!uris || uriIndex >= count) {
        return nullptr;
    }
    return uris[uriIndex];
}

size_t FilaGltfioFilamentAsset_copyResourceUriAt(
        const FilaGltfioFilamentAsset* asset,
        size_t uriIndex,
        char* outUri,
        size_t outUriSize) {
    return copyCString(FilaGltfioFilamentAsset_getResourceUriAt(asset, uriIndex), outUri, outUriSize);
}

bool FilaGltfioFilamentAsset_getBoundingBox(const FilaGltfioFilamentAsset* asset, FilaAabb* outAabb) {
    if (!asset || !outAabb) {
        return false;
    }
    auto* cppAsset = reinterpret_cast<const filament::gltfio::FilamentAsset*>(asset);
    fromAabb(cppAsset->getBoundingBox(), outAabb);
    return true;
}

const char* FilaGltfioFilamentAsset_getName(const FilaGltfioFilamentAsset* asset, FilaEntity entity) {
    if (!asset || entity == 0) {
        return nullptr;
    }
    auto* cppAsset = reinterpret_cast<const filament::gltfio::FilamentAsset*>(asset);
    return cppAsset->getName(utils::Entity::import(entity));
}

size_t FilaGltfioFilamentAsset_copyName(const FilaGltfioFilamentAsset* asset,
        FilaEntity entity,
        char* outName,
        size_t outNameSize) {
    return copyCString(FilaGltfioFilamentAsset_getName(asset, entity), outName, outNameSize);
}

FilaEntity FilaGltfioFilamentAsset_getFirstEntityByName(FilaGltfioFilamentAsset* asset, const char* name) {
    if (!asset || !name || name[0] == '\0') {
        return 0;
    }
    auto* cppAsset = reinterpret_cast<filament::gltfio::FilamentAsset*>(asset);
    return fromEntity(cppAsset->getFirstEntityByName(name));
}

size_t FilaGltfioFilamentAsset_getEntitiesByName(const FilaGltfioFilamentAsset* asset,
        const char* name,
        FilaEntity* outEntities,
        size_t maxCount) {
    if (!asset || !name || name[0] == '\0') {
        return 0u;
    }
    auto* cppAsset = reinterpret_cast<const filament::gltfio::FilamentAsset*>(asset);
    if (!outEntities) {
        return cppAsset->getEntitiesByName(name, nullptr, 0u);
    }
    if (maxCount == 0u) {
        return 0u;
    }

    std::vector<utils::Entity> entities(maxCount);
    const size_t written = cppAsset->getEntitiesByName(name, entities.data(), maxCount);
    for (size_t i = 0u; i < written; ++i) {
        outEntities[i] = fromEntity(entities[i]);
    }
    return written;
}

size_t FilaGltfioFilamentAsset_getEntitiesByPrefix(const FilaGltfioFilamentAsset* asset,
        const char* prefix,
        FilaEntity* outEntities,
        size_t maxCount) {
    if (!asset || !prefix || prefix[0] == '\0') {
        return 0u;
    }
    auto* cppAsset = reinterpret_cast<const filament::gltfio::FilamentAsset*>(asset);
    if (!outEntities) {
        return cppAsset->getEntitiesByPrefix(prefix, nullptr, 0u);
    }
    if (maxCount == 0u) {
        return 0u;
    }

    std::vector<utils::Entity> entities(maxCount);
    const size_t written = cppAsset->getEntitiesByPrefix(prefix, entities.data(), maxCount);
    for (size_t i = 0u; i < written; ++i) {
        outEntities[i] = fromEntity(entities[i]);
    }
    return written;
}

const char* FilaGltfioFilamentAsset_getExtras(const FilaGltfioFilamentAsset* asset, FilaEntity entity) {
    if (!asset) {
        return nullptr;
    }
    auto* cppAsset = reinterpret_cast<const filament::gltfio::FilamentAsset*>(asset);
    return cppAsset->getExtras(utils::Entity::import(entity));
}

size_t FilaGltfioFilamentAsset_copyExtras(const FilaGltfioFilamentAsset* asset,
        FilaEntity entity,
        char* outExtras,
        size_t outExtrasSize) {
    return copyCString(FilaGltfioFilamentAsset_getExtras(asset, entity), outExtras, outExtrasSize);
}

const char* FilaGltfioFilamentAsset_getMorphTargetNameAt(const FilaGltfioFilamentAsset* asset,
        FilaEntity entity,
        size_t targetIndex) {
    if (!asset || entity == 0) {
        return nullptr;
    }
    auto* cppAsset = reinterpret_cast<const filament::gltfio::FilamentAsset*>(asset);
    return cppAsset->getMorphTargetNameAt(utils::Entity::import(entity), targetIndex);
}

size_t FilaGltfioFilamentAsset_copyMorphTargetNameAt(const FilaGltfioFilamentAsset* asset,
        FilaEntity entity,
        size_t targetIndex,
        char* outName,
        size_t outNameSize) {
    return copyCString(
            FilaGltfioFilamentAsset_getMorphTargetNameAt(asset, entity, targetIndex),
            outName,
            outNameSize);
}

size_t FilaGltfioFilamentAsset_getMorphTargetCountAt(const FilaGltfioFilamentAsset* asset, FilaEntity entity) {
    if (!asset || entity == 0) {
        return 0u;
    }
    auto* cppAsset = reinterpret_cast<const filament::gltfio::FilamentAsset*>(asset);
    return cppAsset->getMorphTargetCountAt(utils::Entity::import(entity));
}

FilaEntity FilaGltfioFilamentAsset_getWireframe(FilaGltfioFilamentAsset* asset) {
    if (!asset) {
        return 0;
    }
    auto* cppAsset = reinterpret_cast<filament::gltfio::FilamentAsset*>(asset);
    return fromEntity(cppAsset->getWireframe());
}

void FilaGltfioFilamentAsset_releaseSourceData(FilaGltfioFilamentAsset* asset) {
    if (!asset) {
        return;
    }
    auto* cppAsset = reinterpret_cast<filament::gltfio::FilamentAsset*>(asset);
    cppAsset->releaseSourceData();
}

const void* FilaGltfioFilamentAsset_getSourceAsset(FilaGltfioFilamentAsset* asset) {
    if (!asset) {
        return nullptr;
    }
    auto* cppAsset = reinterpret_cast<filament::gltfio::FilamentAsset*>(asset);
    return cppAsset->getSourceAsset();
}

size_t FilaGltfioFilamentAsset_getSceneCount(const FilaGltfioFilamentAsset* asset) {
    if (!asset) {
        return 0u;
    }
    auto* cppAsset = reinterpret_cast<const filament::gltfio::FilamentAsset*>(asset);
    return cppAsset->getSceneCount();
}

const char* FilaGltfioFilamentAsset_getSceneName(const FilaGltfioFilamentAsset* asset, size_t sceneIndex) {
    if (!asset) {
        return nullptr;
    }
    auto* cppAsset = reinterpret_cast<const filament::gltfio::FilamentAsset*>(asset);
    return cppAsset->getSceneName(sceneIndex);
}

size_t FilaGltfioFilamentAsset_copySceneName(const FilaGltfioFilamentAsset* asset,
        size_t sceneIndex,
        char* outSceneName,
        size_t outSceneNameSize) {
    return copyCString(FilaGltfioFilamentAsset_getSceneName(asset, sceneIndex), outSceneName, outSceneNameSize);
}

FilaGltfioFilamentInstance* FilaGltfioFilamentAsset_getInstance(FilaGltfioFilamentAsset* asset) {
    if (!asset) {
        return nullptr;
    }
    auto* cppAsset = reinterpret_cast<filament::gltfio::FilamentAsset*>(asset);
    return reinterpret_cast<FilaGltfioFilamentInstance*>(cppAsset->getInstance());
}

} // extern "C"

