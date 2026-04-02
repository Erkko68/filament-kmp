#include <gltfio/NodeManager.h>

#include <cstring>

#include <utils/CString.h>
#include <utils/Entity.h>

#include "../../include/gltfio/NodeManager.h"

namespace {

utils::Entity toEntity(FilaEntity entity) {
    return utils::Entity::import(entity);
}

filament::gltfio::NodeManager::Instance toInstance(FilaGltfioNodeManagerInstance instance) {
    return filament::gltfio::NodeManager::Instance(instance);
}

FilaGltfioNodeManagerInstance fromInstance(filament::gltfio::NodeManager::Instance instance) {
    return instance.asValue();
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

} // namespace

extern "C" {

size_t FilaGltfioNodeManager_getMaxSceneCount(void) {
    return filament::gltfio::NodeManager::MAX_SCENE_COUNT;
}

bool FilaGltfioNodeManager_hasComponent(const FilaGltfioNodeManager* manager, FilaEntity entity) {
    if (!manager || entity == 0) {
        return false;
    }
    auto* cppManager = reinterpret_cast<const filament::gltfio::NodeManager*>(manager);
    return cppManager->hasComponent(toEntity(entity));
}

FilaGltfioNodeManagerInstance FilaGltfioNodeManager_getInstance(
        const FilaGltfioNodeManager* manager,
        FilaEntity entity) {
    if (!manager || entity == 0) {
        return 0u;
    }
    auto* cppManager = reinterpret_cast<const filament::gltfio::NodeManager*>(manager);
    return fromInstance(cppManager->getInstance(toEntity(entity)));
}

bool FilaGltfioNodeManager_isValidInstance(FilaGltfioNodeManagerInstance instance) {
    return toInstance(instance).isValid();
}

void FilaGltfioNodeManager_create(FilaGltfioNodeManager* manager, FilaEntity entity) {
    if (!manager || entity == 0) {
        return;
    }
    auto* cppManager = reinterpret_cast<filament::gltfio::NodeManager*>(manager);
    cppManager->create(toEntity(entity));
}

void FilaGltfioNodeManager_destroy(FilaGltfioNodeManager* manager, FilaEntity entity) {
    if (!manager || entity == 0) {
        return;
    }
    auto* cppManager = reinterpret_cast<filament::gltfio::NodeManager*>(manager);
    cppManager->destroy(toEntity(entity));
}

void FilaGltfioNodeManager_setExtras(
        FilaGltfioNodeManager* manager,
        FilaGltfioNodeManagerInstance instance,
        const char* extrasJson) {
    if (!manager || !extrasJson || !toInstance(instance).isValid()) {
        return;
    }
    auto* cppManager = reinterpret_cast<filament::gltfio::NodeManager*>(manager);
    cppManager->setExtras(toInstance(instance), utils::CString(extrasJson));
}

const char* FilaGltfioNodeManager_getExtras(
        const FilaGltfioNodeManager* manager,
        FilaGltfioNodeManagerInstance instance) {
    if (!manager || !toInstance(instance).isValid()) {
        return nullptr;
    }
    auto* cppManager = reinterpret_cast<const filament::gltfio::NodeManager*>(manager);
    return cppManager->getExtras(toInstance(instance)).c_str();
}

size_t FilaGltfioNodeManager_copyExtras(
        const FilaGltfioNodeManager* manager,
        FilaGltfioNodeManagerInstance instance,
        char* outExtras,
        size_t outExtrasSize) {
    return copyCString(FilaGltfioNodeManager_getExtras(manager, instance), outExtras, outExtrasSize);
}

void FilaGltfioNodeManager_setSceneMembership(
        FilaGltfioNodeManager* manager,
        FilaGltfioNodeManagerInstance instance,
        uint32_t sceneMask) {
    if (!manager || !toInstance(instance).isValid()) {
        return;
    }
    auto* cppManager = reinterpret_cast<filament::gltfio::NodeManager*>(manager);
    cppManager->setSceneMembership(toInstance(instance), filament::gltfio::NodeManager::SceneMask(sceneMask));
}

uint32_t FilaGltfioNodeManager_getSceneMembership(
        const FilaGltfioNodeManager* manager,
        FilaGltfioNodeManagerInstance instance) {
    if (!manager || !toInstance(instance).isValid()) {
        return 0u;
    }
    auto* cppManager = reinterpret_cast<const filament::gltfio::NodeManager*>(manager);
    return cppManager->getSceneMembership(toInstance(instance)).getValue();
}

} // extern "C"

