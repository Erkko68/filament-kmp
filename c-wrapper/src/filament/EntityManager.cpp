#include <utils/Entity.h>
#include <utils/EntityManager.h>
#include <utils/sstream.h>

#include <cstring>
#include <vector>

#include "../../include/filament/Types.h"
#include "../../include/utils/EntityManager.h"

namespace {

class BridgeEntityManagerListener final : public utils::EntityManager::Listener {
public:
    BridgeEntityManagerListener(FilaEntityManagerEntitiesDestroyedCallback callback, void* userData)
            : mCallback(callback), mUserData(userData) {}

    void onEntitiesDestroyed(size_t n, utils::Entity const* entities) noexcept override {
        if (!mCallback) {
            return;
        }
        std::vector<FilaEntity> converted(n);
        for (size_t i = 0; i < n; ++i) {
            converted[i] = utils::Entity::smuggle(entities[i]);
        }
        mCallback(n, converted.data(), mUserData);
    }

private:
    FilaEntityManagerEntitiesDestroyedCallback mCallback = nullptr;
    void* mUserData = nullptr;
};

} // namespace

struct FilaEntityManagerListener {
    BridgeEntityManagerListener bridge;
    bool registered = false;

    FilaEntityManagerListener(FilaEntityManagerEntitiesDestroyedCallback callback, void* userData)
            : bridge(callback, userData) {}
};


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

size_t FilaEntityManager_dumpActiveEntities(char* outText, size_t outTextSize) {
#if FILAMENT_UTILS_TRACK_ENTITIES
    utils::io::sstream out;
    utils::EntityManager::get().dumpActiveEntities(out);
    const char* text = out.c_str();
    const size_t len = out.length();
    if (!outText || outTextSize == 0u) {
        return len;
    }
    const size_t copyLen = len < (outTextSize - 1u) ? len : (outTextSize - 1u);
    if (copyLen > 0u) {
        std::memcpy(outText, text, copyLen);
    }
    outText[copyLen] = '\0';
    return len;
#else
    if (outText && outTextSize > 0u) {
        outText[0] = '\0';
    }
    return 0u;
#endif
}

FilaEntityManagerListener* FilaEntityManagerListener_create(
        FilaEntityManagerEntitiesDestroyedCallback callback,
        void* userData) {
    if (!callback) {
        return nullptr;
    }
    return new FilaEntityManagerListener(callback, userData);
}

void FilaEntityManagerListener_destroy(FilaEntityManagerListener* listener) {
    if (!listener) {
        return;
    }
    if (listener->registered) {
        utils::EntityManager::get().unregisterListener(&listener->bridge);
        listener->registered = false;
    }
    delete listener;
}

bool FilaEntityManager_registerListener(FilaEntityManagerListener* listener) {
    if (!listener) {
        return false;
    }
    utils::EntityManager::get().registerListener(&listener->bridge);
    listener->registered = true;
    return true;
}

bool FilaEntityManager_unregisterListener(FilaEntityManagerListener* listener) {
    if (!listener) {
        return false;
    }
    utils::EntityManager::get().unregisterListener(&listener->bridge);
    listener->registered = false;
    return true;
}

bool FilaEntityManagerListener_onEntitiesDestroyed(
        FilaEntityManagerListener* listener,
        size_t count,
        const FilaEntity* entities) {
    if (!listener || (count > 0u && !entities)) {
        return false;
    }
    std::vector<utils::Entity> converted(count);
    for (size_t i = 0u; i < count; ++i) {
        converted[i] = utils::Entity::import(entities[i]);
    }
    listener->bridge.onEntitiesDestroyed(count, converted.data());
    return true;
}

} // extern "C"

