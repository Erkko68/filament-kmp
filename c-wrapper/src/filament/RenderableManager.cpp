#include <filament/RenderableManager.h>

#include <filament/Engine.h>

#include <utils/Entity.h>

#include "../../../include/filament/RenderableManager.h"

namespace {
utils::Entity toEntity(FilaEntity entity) {
    return utils::Entity::import(entity);
}

filament::RenderableManager::Instance toInstance(FilaRenderableManagerInstance instance) {
    return filament::RenderableManager::Instance(instance);
}

FilaRenderableManagerInstance fromInstance(filament::RenderableManager::Instance instance) {
    return instance.asValue();
}

using RenderableBuilder = filament::RenderableManager::Builder;
} // namespace

extern "C" {

bool FilaRenderableManager_hasComponent(const FilaRenderableManager* manager, FilaEntity entity) {
    if (!manager || entity == 0) {
        return false;
    }
    auto cppManager = reinterpret_cast<const filament::RenderableManager*>(manager);
    return cppManager->hasComponent(toEntity(entity));
}

FilaRenderableManagerInstance FilaRenderableManager_getInstance(const FilaRenderableManager* manager, FilaEntity entity) {
    if (!manager || entity == 0) {
        return 0;
    }
    auto cppManager = reinterpret_cast<const filament::RenderableManager*>(manager);
    return fromInstance(cppManager->getInstance(toEntity(entity)));
}

size_t FilaRenderableManager_getComponentCount(const FilaRenderableManager* manager) {
    if (!manager) {
        return 0;
    }
    auto cppManager = reinterpret_cast<const filament::RenderableManager*>(manager);
    return cppManager->getComponentCount();
}

bool FilaRenderableManager_empty(const FilaRenderableManager* manager) {
    if (!manager) {
        return true;
    }
    auto cppManager = reinterpret_cast<const filament::RenderableManager*>(manager);
    return cppManager->empty();
}

FilaEntity FilaRenderableManager_getEntity(const FilaRenderableManager* manager, FilaRenderableManagerInstance instance) {
    if (!manager || instance == 0) {
        return 0;
    }
    auto cppManager = reinterpret_cast<const filament::RenderableManager*>(manager);
    return utils::Entity::smuggle(cppManager->getEntity(toInstance(instance)));
}

size_t FilaRenderableManager_getEntities(const FilaRenderableManager* manager, FilaEntity* outEntities, size_t maxCount) {
    if (!manager || !outEntities || maxCount == 0) {
        return 0;
    }
    auto cppManager = reinterpret_cast<const filament::RenderableManager*>(manager);
    const size_t count = cppManager->getComponentCount();
    const size_t written = (count < maxCount) ? count : maxCount;
    const utils::Entity* entities = cppManager->getEntities();
    for (size_t i = 0; i < written; ++i) {
        outEntities[i] = utils::Entity::smuggle(entities[i]);
    }
    return written;
}

void FilaRenderableManager_destroy(FilaRenderableManager* manager, FilaEntity entity) {
    if (!manager || entity == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::RenderableManager*>(manager);
    cppManager->destroy(toEntity(entity));
}

size_t FilaRenderableManager_getPrimitiveCount(const FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance) {
    if (!manager || instance == 0) {
        return 0;
    }
    auto cppManager = reinterpret_cast<const filament::RenderableManager*>(manager);
    return cppManager->getPrimitiveCount(toInstance(instance));
}

void FilaRenderableManager_setLayerMask(FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance,
        uint8_t select,
        uint8_t values) {
    if (!manager || instance == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::RenderableManager*>(manager);
    cppManager->setLayerMask(toInstance(instance), select, values);
}

uint8_t FilaRenderableManager_getLayerMask(const FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance) {
    if (!manager || instance == 0) {
        return 0;
    }
    auto cppManager = reinterpret_cast<const filament::RenderableManager*>(manager);
    return cppManager->getLayerMask(toInstance(instance));
}

void FilaRenderableManager_setPriority(FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance,
        uint8_t priority) {
    if (!manager || instance == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::RenderableManager*>(manager);
    cppManager->setPriority(toInstance(instance), priority);
}

uint8_t FilaRenderableManager_getPriority(const FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance) {
    if (!manager || instance == 0) {
        return 0;
    }
    auto cppManager = reinterpret_cast<const filament::RenderableManager*>(manager);
    return cppManager->getPriority(toInstance(instance));
}

void FilaRenderableManager_setCulling(FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance,
        bool enable) {
    if (!manager || instance == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::RenderableManager*>(manager);
    cppManager->setCulling(toInstance(instance), enable);
}

bool FilaRenderableManager_isCullingEnabled(const FilaRenderableManager* manager,
        FilaRenderableManagerInstance instance) {
    if (!manager || instance == 0) {
        return false;
    }
    auto cppManager = reinterpret_cast<const filament::RenderableManager*>(manager);
    return cppManager->isCullingEnabled(toInstance(instance));
}

FilaRenderableManagerBuilder* FilaRenderableManagerBuilder_create(size_t primitiveCount) {
    auto builder = new RenderableBuilder(primitiveCount);
    return reinterpret_cast<FilaRenderableManagerBuilder*>(builder);
}

void FilaRenderableManagerBuilder_destroy(FilaRenderableManagerBuilder* builder) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    delete cppBuilder;
}

void FilaRenderableManagerBuilder_layerMask(FilaRenderableManagerBuilder* builder, uint8_t select, uint8_t values) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    cppBuilder->layerMask(select, values);
}

void FilaRenderableManagerBuilder_priority(FilaRenderableManagerBuilder* builder, uint8_t priority) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    cppBuilder->priority(priority);
}

void FilaRenderableManagerBuilder_culling(FilaRenderableManagerBuilder* builder, bool enable) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    cppBuilder->culling(enable);
}

void FilaRenderableManagerBuilder_castShadows(FilaRenderableManagerBuilder* builder, bool enable) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    cppBuilder->castShadows(enable);
}

void FilaRenderableManagerBuilder_receiveShadows(FilaRenderableManagerBuilder* builder, bool enable) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    cppBuilder->receiveShadows(enable);
}

bool FilaRenderableManagerBuilder_build(FilaRenderableManagerBuilder* builder, FilaEngine* engine, FilaEntity entity) {
    if (!builder || !engine || entity == 0) {
        return false;
    }
    auto cppBuilder = reinterpret_cast<RenderableBuilder*>(builder);
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return cppBuilder->build(*cppEngine, toEntity(entity)) == RenderableBuilder::Result::Success;
}

} // extern "C"

