#include "filament/Engine.h"
#include "utils/EntityManager.h"
#include "filament/LightManager.h"

// Verifies LightManager API is consumable from C and composes with Engine + EntityManager.
void test_headers_light_manager(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaLightManager* manager = FilaEngine_getLightManager(engine);
    FilaEntity entity = FilaEntityManager_create();
    FilaEntity entities[4] = {0};
    float outPosition[3] = {0};
    float outDirection[3] = {0};
    float outColor[3] = {0};
    FilaLightManagerBuilder* builder = FilaLightManagerBuilder_create(FILA_LIGHT_TYPE_DIRECTIONAL);
    FilaLightManagerShadowOptions shadowOptions = {
            .mapSize = 1024u,
            .shadowCascades = 2u,
            .cascadeSplitPositions = {0.2f, 0.5f, 0.8f},
            .constantBias = 0.001f,
            .normalBias = 1.0f,
            .shadowFar = 0.0f,
            .shadowNearHint = 1.0f,
            .shadowFarHint = 100.0f,
            .stable = false,
            .lispsm = true,
            .polygonOffsetConstant = 0.5f,
            .polygonOffsetSlope = 2.0f,
            .screenSpaceContactShadows = false,
            .stepCount = 8u,
            .maxShadowDistance = 0.3f,
            .vsm = { false, 0.0f },
            .shadowBulbRadius = 0.02f,
            .transform = {1.0f, 0.0f, 0.0f, 0.0f},
    };
    FilaLightManagerShadowOptions outShadowOptions = shadowOptions;
    float shadowSplits[3] = {0.0f, 0.0f, 0.0f};

    (void)FilaLightManager_hasComponent(manager, entity);
    FilaLightManagerInstance instance = FilaLightManager_getInstance(manager, entity);
    (void)FilaLightManager_getEntity(manager, instance);
    (void)FilaLightManager_getComponentCount(manager);
    (void)FilaLightManager_empty(manager);
    (void)FilaLightManager_getEntities(manager, entities, 4u);
    (void)FilaLightManager_getType(manager, instance);
    (void)FilaLightManager_isDirectional(manager, instance);
    (void)FilaLightManager_isPointLight(manager, instance);
    (void)FilaLightManager_isSpotLight(manager, instance);
    FilaLightManager_setPosition(manager, instance, 0.0f, 1.0f, 2.0f);
    (void)FilaLightManager_getPosition(manager, instance, outPosition);
    FilaLightManager_setDirection(manager, instance, 0.0f, -1.0f, 0.0f);
    (void)FilaLightManager_getDirection(manager, instance, outDirection);
    FilaLightManager_setColor(manager, instance, 1.0f, 0.9f, 0.8f);
    (void)FilaLightManager_getColor(manager, instance, outColor);
    FilaLightManager_setIntensity(manager, instance, 5000.0f);
    (void)FilaLightManager_getIntensity(manager, instance);
    FilaLightManager_setIntensityCandela(manager, instance, 500.0f);
    FilaLightManager_setIntensityWattsEfficiency(manager, instance, 60.0f, 0.087f);
    FilaLightManager_setLightChannel(manager, instance, 1u, true);
    (void)FilaLightManager_getLightChannel(manager, instance, 1u);
    FilaLightManager_setFalloff(manager, instance, 15.0f);
    (void)FilaLightManager_getFalloff(manager, instance);
    FilaLightManager_setSpotLightCone(manager, instance, 0.2f, 0.7f);
    (void)FilaLightManager_getSpotLightOuterCone(manager, instance);
    (void)FilaLightManager_getSpotLightInnerCone(manager, instance);
    FilaLightManager_setSunAngularRadius(manager, instance, 0.545f);
    (void)FilaLightManager_getSunAngularRadius(manager, instance);
    FilaLightManager_setSunHaloSize(manager, instance, 10.0f);
    (void)FilaLightManager_getSunHaloSize(manager, instance);
    FilaLightManager_setSunHaloFalloff(manager, instance, 80.0f);
    (void)FilaLightManager_getSunHaloFalloff(manager, instance);
    FilaLightManager_setShadowCaster(manager, instance, true);
    (void)FilaLightManager_isShadowCaster(manager, instance);
    FilaLightManagerShadowCascades_computeUniformSplits(shadowSplits, 4u);
    FilaLightManagerShadowCascades_computeLogSplits(shadowSplits, 4u, 0.1f, 100.0f);
    FilaLightManagerShadowCascades_computePracticalSplits(shadowSplits, 4u, 0.1f, 100.0f, 0.5f);
    FilaLightManager_setShadowOptions(manager, instance, &shadowOptions);
    (void)FilaLightManager_getShadowOptions(manager, instance, &outShadowOptions);
    FilaLightManagerBuilder_direction(builder, 0.0f, -1.0f, 0.0f);
    FilaLightManagerBuilder_position(builder, 0.0f, 2.0f, 0.0f);
    FilaLightManagerBuilder_color(builder, 1.0f, 1.0f, 1.0f);
    FilaLightManagerBuilder_intensity(builder, 100000.0f);
    FilaLightManagerBuilder_intensityWattsEfficiency(builder, 60.0f, 0.087f);
    FilaLightManagerBuilder_falloff(builder, 10.0f);
    FilaLightManagerBuilder_spotLightCone(builder, 0.3f, 0.8f);
    FilaLightManagerBuilder_castShadows(builder, true);
    FilaLightManagerBuilder_castLight(builder, true);
    FilaLightManagerBuilder_lightChannel(builder, 1u, true);
    FilaLightManagerBuilder_intensityCandela(builder, 500.0f);
    FilaLightManagerBuilder_sunAngularRadius(builder, 0.545f);
    FilaLightManagerBuilder_sunHaloSize(builder, 10.0f);
    FilaLightManagerBuilder_sunHaloFalloff(builder, 80.0f);
    FilaLightManagerBuilder_shadowOptions(builder, &shadowOptions);
    (void)FilaLightManagerBuilder_build(builder, engine, entity);
    FilaLightManagerBuilder_destroy(builder);
    FilaLightManager_destroy(manager, entity);
    FilaEntityManager_destroy(entity);
}
