#include <filament/Scene.h>
#include <filament/Skybox.h>
#include <filament/IndirectLight.h>

#include <utils/Entity.h>

#include "../../include/filament/Scene.h"
#include "../../include/filament/Types.h"

#include <vector>

namespace {
utils::Entity toEntity(FilaEntity entity) {
    return utils::Entity::import(entity);
}
} // namespace

extern "C" {

void FilaScene_addEntity(FilaScene* scene, FilaEntity entity) {
    if (!scene) {
        return;
    }
    auto cppScene = reinterpret_cast<filament::Scene*>(scene);
    cppScene->addEntity(toEntity(entity));
}

void FilaScene_removeEntity(FilaScene* scene, FilaEntity entity) {
    if (!scene) {
        return;
    }
    auto cppScene = reinterpret_cast<filament::Scene*>(scene);
    cppScene->remove(toEntity(entity));
}

void FilaScene_removeAllEntities(FilaScene* scene) {
    if (!scene) {
        return;
    }
    auto cppScene = reinterpret_cast<filament::Scene*>(scene);
    cppScene->removeAllEntities();
}

size_t FilaScene_getEntityCount(const FilaScene* scene) {
    if (!scene) {
        return 0;
    }
    auto cppScene = reinterpret_cast<const filament::Scene*>(scene);
    return cppScene->getEntityCount();
}

bool FilaScene_hasEntity(const FilaScene* scene, FilaEntity entity) {
    if (!scene) {
        return false;
    }
    auto cppScene = reinterpret_cast<const filament::Scene*>(scene);
    return cppScene->hasEntity(toEntity(entity));
}

void FilaScene_setSkybox(FilaScene* scene, FilaSkybox* skybox) {
    if (!scene) {
        return;
    }
    auto cppScene = reinterpret_cast<filament::Scene*>(scene);
    auto cppSkybox = reinterpret_cast<filament::Skybox*>(skybox);
    cppScene->setSkybox(cppSkybox);
}

FilaSkybox* FilaScene_getSkybox(FilaScene* scene) {
    if (!scene) {
        return nullptr;
    }
    auto cppScene = reinterpret_cast<filament::Scene*>(scene);
    return reinterpret_cast<FilaSkybox*>(cppScene->getSkybox());
}

void FilaScene_setIndirectLight(FilaScene* scene, FilaIndirectLight* indirectLight) {
    if (!scene) {
        return;
    }
    auto cppScene = reinterpret_cast<filament::Scene*>(scene);
    auto cppIndirectLight = reinterpret_cast<filament::IndirectLight*>(indirectLight);
    cppScene->setIndirectLight(cppIndirectLight);
}

FilaIndirectLight* FilaScene_getIndirectLight(FilaScene* scene) {
    if (!scene) {
        return nullptr;
    }
    auto cppScene = reinterpret_cast<filament::Scene*>(scene);
    return reinterpret_cast<FilaIndirectLight*>(cppScene->getIndirectLight());
}

void FilaScene_addEntities(FilaScene* scene, const FilaEntity* entities, size_t count) {
    if (!scene || !entities || count == 0) {
        return;
    }
    std::vector<utils::Entity> converted(count);
    for (size_t i = 0; i < count; ++i) {
        converted[i] = toEntity(entities[i]);
    }
    auto cppScene = reinterpret_cast<filament::Scene*>(scene);
    cppScene->addEntities(converted.data(), count);
}

void FilaScene_removeEntities(FilaScene* scene, const FilaEntity* entities, size_t count) {
    if (!scene || !entities || count == 0) {
        return;
    }
    std::vector<utils::Entity> converted(count);
    for (size_t i = 0; i < count; ++i) {
        converted[i] = toEntity(entities[i]);
    }
    auto cppScene = reinterpret_cast<filament::Scene*>(scene);
    cppScene->removeEntities(converted.data(), count);
}

size_t FilaScene_getRenderableCount(const FilaScene* scene) {
    if (!scene) {
        return 0;
    }
    auto cppScene = reinterpret_cast<const filament::Scene*>(scene);
    return cppScene->getRenderableCount();
}

size_t FilaScene_getLightCount(const FilaScene* scene) {
    if (!scene) {
        return 0;
    }
    auto cppScene = reinterpret_cast<const filament::Scene*>(scene);
    return cppScene->getLightCount();
}

void FilaScene_forEach(const FilaScene* scene, FilaSceneEntityCallback callback, void* userData) {
    if (!scene || !callback) {
        return;
    }
    auto cppScene = reinterpret_cast<const filament::Scene*>(scene);
    cppScene->forEach([callback, userData](utils::Entity entity) {
        callback(utils::Entity::smuggle(entity), userData);
    });
}

} // extern "C"

