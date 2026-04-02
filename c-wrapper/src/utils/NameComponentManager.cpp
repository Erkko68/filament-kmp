#include <utils/Entity.h>
#include <utils/EntityManager.h>
#include <utils/NameComponentManager.h>

#include <cstring>
#include <new>

#include "../../include/utils/NameComponentManager.h"

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

} // namespace

extern "C" {

FilaNameComponentManager* FilaNameComponentManager_create(void) {
    auto* manager = new (std::nothrow) utils::NameComponentManager(utils::EntityManager::get());
    return reinterpret_cast<FilaNameComponentManager*>(manager);
}

void FilaNameComponentManager_destroy(FilaNameComponentManager* manager) {
    if (!manager) {
        return;
    }
    auto* cppManager = reinterpret_cast<utils::NameComponentManager*>(manager);
    delete cppManager;
}

bool FilaNameComponentManager_hasComponent(const FilaNameComponentManager* manager, FilaEntity entity) {
    if (!manager || entity == 0) {
        return false;
    }
    auto* cppManager = reinterpret_cast<const utils::NameComponentManager*>(manager);
    return cppManager->hasComponent(utils::Entity::import(entity));
}

FilaEntityInstance FilaNameComponentManager_getInstance(const FilaNameComponentManager* manager, FilaEntity entity) {
    if (!manager || entity == 0) {
        return 0u;
    }
    auto* cppManager = reinterpret_cast<const utils::NameComponentManager*>(manager);
    return cppManager->getInstance(utils::Entity::import(entity)).asValue();
}

void FilaNameComponentManager_addComponent(FilaNameComponentManager* manager, FilaEntity entity) {
    if (!manager || entity == 0) {
        return;
    }
    auto* cppManager = reinterpret_cast<utils::NameComponentManager*>(manager);
    cppManager->addComponent(utils::Entity::import(entity));
}

void FilaNameComponentManager_removeComponent(FilaNameComponentManager* manager, FilaEntity entity) {
    if (!manager || entity == 0) {
        return;
    }
    auto* cppManager = reinterpret_cast<utils::NameComponentManager*>(manager);
    cppManager->removeComponent(utils::Entity::import(entity));
}

void FilaNameComponentManager_setName(FilaNameComponentManager* manager, FilaEntityInstance instance, const char* name) {
    if (!manager || instance == 0u) {
        return;
    }
    auto* cppManager = reinterpret_cast<utils::NameComponentManager*>(manager);
    cppManager->setName(utils::NameComponentManager::Instance(instance), name ? name : "");
}

const char* FilaNameComponentManager_getName(const FilaNameComponentManager* manager, FilaEntityInstance instance) {
    if (!manager || instance == 0u) {
        return nullptr;
    }
    auto* cppManager = reinterpret_cast<const utils::NameComponentManager*>(manager);
    return cppManager->getName(utils::NameComponentManager::Instance(instance));
}

size_t FilaNameComponentManager_copyName(const FilaNameComponentManager* manager,
        FilaEntityInstance instance,
        char* outName,
        size_t outNameSize) {
    return copyCString(FilaNameComponentManager_getName(manager, instance), outName, outNameSize);
}

void FilaNameComponentManager_gc(FilaNameComponentManager* manager) {
    if (!manager) {
        return;
    }
    auto* cppManager = reinterpret_cast<utils::NameComponentManager*>(manager);
    cppManager->gc(utils::EntityManager::get());
}

} // extern "C"


