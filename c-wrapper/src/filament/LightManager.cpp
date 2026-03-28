#include <filament/LightManager.h>

#include <filament/Engine.h>

#include <math/vec3.h>

#include <utils/Entity.h>

#include "../../../include/filament/LightManager.h"

namespace {
utils::Entity toEntity(FilaEntity entity) {
    return utils::Entity::import(entity);
}

filament::LightManager::Instance toInstance(FilaLightManagerInstance instance) {
    return filament::LightManager::Instance(instance);
}

FilaLightManagerInstance fromInstance(filament::LightManager::Instance instance) {
    return instance.asValue();
}

filament::LightManager::Type toLightType(FilaLightType type) {
    switch (type) {
        case FILA_LIGHT_TYPE_SUN:
            return filament::LightManager::Type::SUN;
        case FILA_LIGHT_TYPE_DIRECTIONAL:
            return filament::LightManager::Type::DIRECTIONAL;
        case FILA_LIGHT_TYPE_POINT:
            return filament::LightManager::Type::POINT;
        case FILA_LIGHT_TYPE_FOCUSED_SPOT:
            return filament::LightManager::Type::FOCUSED_SPOT;
        case FILA_LIGHT_TYPE_SPOT:
        default:
            return filament::LightManager::Type::SPOT;
    }
}

FilaLightType fromLightType(filament::LightManager::Type type) {
    switch (type) {
        case filament::LightManager::Type::SUN:
            return FILA_LIGHT_TYPE_SUN;
        case filament::LightManager::Type::DIRECTIONAL:
            return FILA_LIGHT_TYPE_DIRECTIONAL;
        case filament::LightManager::Type::POINT:
            return FILA_LIGHT_TYPE_POINT;
        case filament::LightManager::Type::FOCUSED_SPOT:
            return FILA_LIGHT_TYPE_FOCUSED_SPOT;
        case filament::LightManager::Type::SPOT:
        default:
            return FILA_LIGHT_TYPE_SPOT;
    }
}

using LightBuilder = filament::LightManager::Builder;
} // namespace

extern "C" {

bool FilaLightManager_hasComponent(const FilaLightManager* manager, FilaEntity entity) {
    if (!manager || entity == 0) {
        return false;
    }
    auto cppManager = reinterpret_cast<const filament::LightManager*>(manager);
    return cppManager->hasComponent(toEntity(entity));
}

FilaLightManagerInstance FilaLightManager_getInstance(const FilaLightManager* manager, FilaEntity entity) {
    if (!manager || entity == 0) {
        return 0;
    }
    auto cppManager = reinterpret_cast<const filament::LightManager*>(manager);
    return fromInstance(cppManager->getInstance(toEntity(entity)));
}

size_t FilaLightManager_getComponentCount(const FilaLightManager* manager) {
    if (!manager) {
        return 0;
    }
    auto cppManager = reinterpret_cast<const filament::LightManager*>(manager);
    return cppManager->getComponentCount();
}

bool FilaLightManager_empty(const FilaLightManager* manager) {
    if (!manager) {
        return true;
    }
    auto cppManager = reinterpret_cast<const filament::LightManager*>(manager);
    return cppManager->empty();
}

FilaEntity FilaLightManager_getEntity(const FilaLightManager* manager, FilaLightManagerInstance instance) {
    if (!manager || instance == 0) {
        return 0;
    }
    auto cppManager = reinterpret_cast<const filament::LightManager*>(manager);
    return utils::Entity::smuggle(cppManager->getEntity(toInstance(instance)));
}

size_t FilaLightManager_getEntities(const FilaLightManager* manager, FilaEntity* outEntities, size_t maxCount) {
    if (!manager || !outEntities || maxCount == 0) {
        return 0;
    }
    auto cppManager = reinterpret_cast<const filament::LightManager*>(manager);
    const size_t count = cppManager->getComponentCount();
    const size_t written = (count < maxCount) ? count : maxCount;
    const utils::Entity* entities = cppManager->getEntities();
    for (size_t i = 0; i < written; ++i) {
        outEntities[i] = utils::Entity::smuggle(entities[i]);
    }
    return written;
}

void FilaLightManager_destroy(FilaLightManager* manager, FilaEntity entity) {
    if (!manager || entity == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::LightManager*>(manager);
    cppManager->destroy(toEntity(entity));
}

FilaLightType FilaLightManager_getType(const FilaLightManager* manager, FilaLightManagerInstance instance) {
    if (!manager || instance == 0) {
        return FILA_LIGHT_TYPE_DIRECTIONAL;
    }
    auto cppManager = reinterpret_cast<const filament::LightManager*>(manager);
    return fromLightType(cppManager->getType(toInstance(instance)));
}

FilaLightManagerBuilder* FilaLightManagerBuilder_create(FilaLightType type) {
    auto builder = new LightBuilder(toLightType(type));
    return reinterpret_cast<FilaLightManagerBuilder*>(builder);
}

void FilaLightManagerBuilder_destroy(FilaLightManagerBuilder* builder) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<LightBuilder*>(builder);
    delete cppBuilder;
}

void FilaLightManagerBuilder_direction(FilaLightManagerBuilder* builder, float x, float y, float z) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<LightBuilder*>(builder);
    cppBuilder->direction(filament::math::float3{x, y, z});
}

void FilaLightManagerBuilder_position(FilaLightManagerBuilder* builder, float x, float y, float z) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<LightBuilder*>(builder);
    cppBuilder->position(filament::math::float3{x, y, z});
}

void FilaLightManagerBuilder_color(FilaLightManagerBuilder* builder, float r, float g, float b) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<LightBuilder*>(builder);
    cppBuilder->color(filament::LinearColor{r, g, b});
}

void FilaLightManagerBuilder_intensity(FilaLightManagerBuilder* builder, float intensity) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<LightBuilder*>(builder);
    cppBuilder->intensity(intensity);
}

void FilaLightManagerBuilder_falloff(FilaLightManagerBuilder* builder, float radius) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<LightBuilder*>(builder);
    cppBuilder->falloff(radius);
}

void FilaLightManagerBuilder_spotLightCone(FilaLightManagerBuilder* builder, float inner, float outer) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<LightBuilder*>(builder);
    cppBuilder->spotLightCone(inner, outer);
}

void FilaLightManagerBuilder_castShadows(FilaLightManagerBuilder* builder, bool enable) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<LightBuilder*>(builder);
    cppBuilder->castShadows(enable);
}

bool FilaLightManagerBuilder_build(FilaLightManagerBuilder* builder, FilaEngine* engine, FilaEntity entity) {
    if (!builder || !engine || entity == 0) {
        return false;
    }
    auto cppBuilder = reinterpret_cast<LightBuilder*>(builder);
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return cppBuilder->build(*cppEngine, toEntity(entity)) == LightBuilder::Result::Success;
}

} // extern "C"

