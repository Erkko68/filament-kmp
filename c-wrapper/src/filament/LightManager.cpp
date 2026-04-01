#include <filament/LightManager.h>

#include <filament/Engine.h>

#include <math/vec3.h>

#include <utils/Entity.h>

#include "../../include/filament/LightManager.h"

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

filament::LightManager::ShadowOptions toShadowOptions(const FilaLightManagerShadowOptions& options) {
    filament::LightManager::ShadowOptions converted;
    converted.mapSize = options.mapSize;
    converted.shadowCascades = options.shadowCascades;
    converted.cascadeSplitPositions[0] = options.cascadeSplitPositions[0];
    converted.cascadeSplitPositions[1] = options.cascadeSplitPositions[1];
    converted.cascadeSplitPositions[2] = options.cascadeSplitPositions[2];
    converted.constantBias = options.constantBias;
    converted.normalBias = options.normalBias;
    converted.shadowFar = options.shadowFar;
    converted.shadowNearHint = options.shadowNearHint;
    converted.shadowFarHint = options.shadowFarHint;
    converted.stable = options.stable;
    converted.lispsm = options.lispsm;
    converted.polygonOffsetConstant = options.polygonOffsetConstant;
    converted.polygonOffsetSlope = options.polygonOffsetSlope;
    converted.screenSpaceContactShadows = options.screenSpaceContactShadows;
    converted.stepCount = options.stepCount;
    converted.maxShadowDistance = options.maxShadowDistance;
    converted.vsm.elvsm = options.vsm.elvsm;
    converted.vsm.blurWidth = options.vsm.blurWidth;
    converted.shadowBulbRadius = options.shadowBulbRadius;
    converted.transform = filament::math::quatf{
            options.transform[0],
            options.transform[1],
            options.transform[2],
            options.transform[3]};
    return converted;
}

void fromShadowOptions(const filament::LightManager::ShadowOptions& options,
        FilaLightManagerShadowOptions* outOptions) {
    if (!outOptions) {
        return;
    }
    outOptions->mapSize = options.mapSize;
    outOptions->shadowCascades = options.shadowCascades;
    outOptions->cascadeSplitPositions[0] = options.cascadeSplitPositions[0];
    outOptions->cascadeSplitPositions[1] = options.cascadeSplitPositions[1];
    outOptions->cascadeSplitPositions[2] = options.cascadeSplitPositions[2];
    outOptions->constantBias = options.constantBias;
    outOptions->normalBias = options.normalBias;
    outOptions->shadowFar = options.shadowFar;
    outOptions->shadowNearHint = options.shadowNearHint;
    outOptions->shadowFarHint = options.shadowFarHint;
    outOptions->stable = options.stable;
    outOptions->lispsm = options.lispsm;
    outOptions->polygonOffsetConstant = options.polygonOffsetConstant;
    outOptions->polygonOffsetSlope = options.polygonOffsetSlope;
    outOptions->screenSpaceContactShadows = options.screenSpaceContactShadows;
    outOptions->stepCount = options.stepCount;
    outOptions->maxShadowDistance = options.maxShadowDistance;
    outOptions->vsm.elvsm = options.vsm.elvsm;
    outOptions->vsm.blurWidth = options.vsm.blurWidth;
    outOptions->shadowBulbRadius = options.shadowBulbRadius;
    outOptions->transform[0] = options.transform.w;
    outOptions->transform[1] = options.transform.x;
    outOptions->transform[2] = options.transform.y;
    outOptions->transform[3] = options.transform.z;
}
} // namespace

extern "C" {

void FilaLightManagerShadowCascades_computeUniformSplits(float* splitPositions, uint8_t cascades) {
    if (!splitPositions || cascades < 1u || cascades > 4u) {
        return;
    }
    filament::LightManager::ShadowCascades::computeUniformSplits(splitPositions, cascades);
}

void FilaLightManagerShadowCascades_computeLogSplits(float* splitPositions,
        uint8_t cascades,
        float near,
        float far) {
    if (!splitPositions || cascades < 1u || cascades > 4u) {
        return;
    }
    filament::LightManager::ShadowCascades::computeLogSplits(splitPositions, cascades, near, far);
}

void FilaLightManagerShadowCascades_computePracticalSplits(float* splitPositions,
        uint8_t cascades,
        float near,
        float far,
        float lambda) {
    if (!splitPositions || cascades < 1u || cascades > 4u) {
        return;
    }
    filament::LightManager::ShadowCascades::computePracticalSplits(splitPositions, cascades, near, far, lambda);
}

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

bool FilaLightManager_isDirectional(const FilaLightManager* manager, FilaLightManagerInstance instance) {
    if (!manager || instance == 0) {
        return false;
    }
    auto cppManager = reinterpret_cast<const filament::LightManager*>(manager);
    return cppManager->isDirectional(toInstance(instance));
}

bool FilaLightManager_isPointLight(const FilaLightManager* manager, FilaLightManagerInstance instance) {
    if (!manager || instance == 0) {
        return false;
    }
    auto cppManager = reinterpret_cast<const filament::LightManager*>(manager);
    return cppManager->isPointLight(toInstance(instance));
}

bool FilaLightManager_isSpotLight(const FilaLightManager* manager, FilaLightManagerInstance instance) {
    if (!manager || instance == 0) {
        return false;
    }
    auto cppManager = reinterpret_cast<const filament::LightManager*>(manager);
    return cppManager->isSpotLight(toInstance(instance));
}

void FilaLightManager_setPosition(FilaLightManager* manager, FilaLightManagerInstance instance, float x, float y, float z) {
    if (!manager || instance == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::LightManager*>(manager);
    cppManager->setPosition(toInstance(instance), filament::math::float3{x, y, z});
}

bool FilaLightManager_getPosition(const FilaLightManager* manager, FilaLightManagerInstance instance, float outPosition[3]) {
    if (!manager || instance == 0 || !outPosition) {
        return false;
    }
    auto cppManager = reinterpret_cast<const filament::LightManager*>(manager);
    const auto& p = cppManager->getPosition(toInstance(instance));
    outPosition[0] = p.x;
    outPosition[1] = p.y;
    outPosition[2] = p.z;
    return true;
}

void FilaLightManager_setDirection(FilaLightManager* manager, FilaLightManagerInstance instance, float x, float y, float z) {
    if (!manager || instance == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::LightManager*>(manager);
    cppManager->setDirection(toInstance(instance), filament::math::float3{x, y, z});
}

bool FilaLightManager_getDirection(const FilaLightManager* manager, FilaLightManagerInstance instance, float outDirection[3]) {
    if (!manager || instance == 0 || !outDirection) {
        return false;
    }
    auto cppManager = reinterpret_cast<const filament::LightManager*>(manager);
    const auto& d = cppManager->getDirection(toInstance(instance));
    outDirection[0] = d.x;
    outDirection[1] = d.y;
    outDirection[2] = d.z;
    return true;
}

void FilaLightManager_setColor(FilaLightManager* manager, FilaLightManagerInstance instance, float r, float g, float b) {
    if (!manager || instance == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::LightManager*>(manager);
    cppManager->setColor(toInstance(instance), filament::LinearColor{r, g, b});
}

bool FilaLightManager_getColor(const FilaLightManager* manager, FilaLightManagerInstance instance, float outColor[3]) {
    if (!manager || instance == 0 || !outColor) {
        return false;
    }
    auto cppManager = reinterpret_cast<const filament::LightManager*>(manager);
    const auto& c = cppManager->getColor(toInstance(instance));
    outColor[0] = c.x;
    outColor[1] = c.y;
    outColor[2] = c.z;
    return true;
}

void FilaLightManager_setIntensity(FilaLightManager* manager, FilaLightManagerInstance instance, float intensity) {
    if (!manager || instance == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::LightManager*>(manager);
    cppManager->setIntensity(toInstance(instance), intensity);
}

float FilaLightManager_getIntensity(const FilaLightManager* manager, FilaLightManagerInstance instance) {
    if (!manager || instance == 0) {
        return 0.0f;
    }
    auto cppManager = reinterpret_cast<const filament::LightManager*>(manager);
    return cppManager->getIntensity(toInstance(instance));
}

void FilaLightManager_setIntensityCandela(FilaLightManager* manager, FilaLightManagerInstance instance, float intensity) {
    if (!manager || instance == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::LightManager*>(manager);
    cppManager->setIntensityCandela(toInstance(instance), intensity);
}

void FilaLightManager_setIntensityWattsEfficiency(FilaLightManager* manager,
        FilaLightManagerInstance instance,
        float watts,
        float efficiency) {
    if (!manager || instance == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::LightManager*>(manager);
    cppManager->setIntensity(toInstance(instance), watts, efficiency);
}

void FilaLightManager_setLightChannel(FilaLightManager* manager, FilaLightManagerInstance instance, unsigned int channel, bool enable) {
    if (!manager || instance == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::LightManager*>(manager);
    cppManager->setLightChannel(toInstance(instance), channel, enable);
}

bool FilaLightManager_getLightChannel(const FilaLightManager* manager, FilaLightManagerInstance instance, unsigned int channel) {
    if (!manager || instance == 0) {
        return false;
    }
    auto cppManager = reinterpret_cast<const filament::LightManager*>(manager);
    return cppManager->getLightChannel(toInstance(instance), channel);
}

void FilaLightManager_setFalloff(FilaLightManager* manager, FilaLightManagerInstance instance, float radius) {
    if (!manager || instance == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::LightManager*>(manager);
    cppManager->setFalloff(toInstance(instance), radius);
}

float FilaLightManager_getFalloff(const FilaLightManager* manager, FilaLightManagerInstance instance) {
    if (!manager || instance == 0) {
        return 0.0f;
    }
    auto cppManager = reinterpret_cast<const filament::LightManager*>(manager);
    return cppManager->getFalloff(toInstance(instance));
}

void FilaLightManager_setSpotLightCone(FilaLightManager* manager, FilaLightManagerInstance instance, float inner, float outer) {
    if (!manager || instance == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::LightManager*>(manager);
    cppManager->setSpotLightCone(toInstance(instance), inner, outer);
}

float FilaLightManager_getSpotLightOuterCone(const FilaLightManager* manager, FilaLightManagerInstance instance) {
    if (!manager || instance == 0) {
        return 0.0f;
    }
    auto cppManager = reinterpret_cast<const filament::LightManager*>(manager);
    return cppManager->getSpotLightOuterCone(toInstance(instance));
}

float FilaLightManager_getSpotLightInnerCone(const FilaLightManager* manager, FilaLightManagerInstance instance) {
    if (!manager || instance == 0) {
        return 0.0f;
    }
    auto cppManager = reinterpret_cast<const filament::LightManager*>(manager);
    return cppManager->getSpotLightInnerCone(toInstance(instance));
}

void FilaLightManager_setSunAngularRadius(FilaLightManager* manager, FilaLightManagerInstance instance, float angularRadius) {
    if (!manager || instance == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::LightManager*>(manager);
    cppManager->setSunAngularRadius(toInstance(instance), angularRadius);
}

float FilaLightManager_getSunAngularRadius(const FilaLightManager* manager, FilaLightManagerInstance instance) {
    if (!manager || instance == 0) {
        return 0.0f;
    }
    auto cppManager = reinterpret_cast<const filament::LightManager*>(manager);
    return cppManager->getSunAngularRadius(toInstance(instance));
}

void FilaLightManager_setSunHaloSize(FilaLightManager* manager, FilaLightManagerInstance instance, float haloSize) {
    if (!manager || instance == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::LightManager*>(manager);
    cppManager->setSunHaloSize(toInstance(instance), haloSize);
}

float FilaLightManager_getSunHaloSize(const FilaLightManager* manager, FilaLightManagerInstance instance) {
    if (!manager || instance == 0) {
        return 0.0f;
    }
    auto cppManager = reinterpret_cast<const filament::LightManager*>(manager);
    return cppManager->getSunHaloSize(toInstance(instance));
}

void FilaLightManager_setSunHaloFalloff(FilaLightManager* manager, FilaLightManagerInstance instance, float haloFalloff) {
    if (!manager || instance == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::LightManager*>(manager);
    cppManager->setSunHaloFalloff(toInstance(instance), haloFalloff);
}

float FilaLightManager_getSunHaloFalloff(const FilaLightManager* manager, FilaLightManagerInstance instance) {
    if (!manager || instance == 0) {
        return 0.0f;
    }
    auto cppManager = reinterpret_cast<const filament::LightManager*>(manager);
    return cppManager->getSunHaloFalloff(toInstance(instance));
}

void FilaLightManager_setShadowCaster(FilaLightManager* manager, FilaLightManagerInstance instance, bool shadowCaster) {
    if (!manager || instance == 0) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::LightManager*>(manager);
    cppManager->setShadowCaster(toInstance(instance), shadowCaster);
}

bool FilaLightManager_isShadowCaster(const FilaLightManager* manager, FilaLightManagerInstance instance) {
    if (!manager || instance == 0) {
        return false;
    }
    auto cppManager = reinterpret_cast<const filament::LightManager*>(manager);
    return cppManager->isShadowCaster(toInstance(instance));
}

void FilaLightManager_setShadowOptions(FilaLightManager* manager,
        FilaLightManagerInstance instance,
        const FilaLightManagerShadowOptions* options) {
    if (!manager || instance == 0 || !options) {
        return;
    }
    auto cppManager = reinterpret_cast<filament::LightManager*>(manager);
    cppManager->setShadowOptions(toInstance(instance), toShadowOptions(*options));
}

bool FilaLightManager_getShadowOptions(const FilaLightManager* manager,
        FilaLightManagerInstance instance,
        FilaLightManagerShadowOptions* outOptions) {
    if (!manager || instance == 0 || !outOptions) {
        return false;
    }
    auto cppManager = reinterpret_cast<const filament::LightManager*>(manager);
    fromShadowOptions(cppManager->getShadowOptions(toInstance(instance)), outOptions);
    return true;
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

void FilaLightManagerBuilder_intensityWattsEfficiency(FilaLightManagerBuilder* builder,
        float watts,
        float efficiency) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<LightBuilder*>(builder);
    cppBuilder->intensity(watts, efficiency);
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

void FilaLightManagerBuilder_castLight(FilaLightManagerBuilder* builder, bool enable) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<LightBuilder*>(builder);
    cppBuilder->castLight(enable);
}

void FilaLightManagerBuilder_lightChannel(FilaLightManagerBuilder* builder, unsigned int channel, bool enable) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<LightBuilder*>(builder);
    cppBuilder->lightChannel(channel, enable);
}

void FilaLightManagerBuilder_intensityCandela(FilaLightManagerBuilder* builder, float intensity) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<LightBuilder*>(builder);
    cppBuilder->intensityCandela(intensity);
}

void FilaLightManagerBuilder_sunAngularRadius(FilaLightManagerBuilder* builder, float angularRadius) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<LightBuilder*>(builder);
    cppBuilder->sunAngularRadius(angularRadius);
}

void FilaLightManagerBuilder_sunHaloSize(FilaLightManagerBuilder* builder, float haloSize) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<LightBuilder*>(builder);
    cppBuilder->sunHaloSize(haloSize);
}

void FilaLightManagerBuilder_sunHaloFalloff(FilaLightManagerBuilder* builder, float haloFalloff) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<LightBuilder*>(builder);
    cppBuilder->sunHaloFalloff(haloFalloff);
}

void FilaLightManagerBuilder_shadowOptions(FilaLightManagerBuilder* builder,
        const FilaLightManagerShadowOptions* options) {
    if (!builder || !options) {
        return;
    }
    auto cppBuilder = reinterpret_cast<LightBuilder*>(builder);
    cppBuilder->shadowOptions(toShadowOptions(*options));
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

