#include <stdio.h>
#include <math.h>

#include "filament/Engine.h"
#include "utils/EntityManager.h"
#include "filament/LightManager.h"

int main(void) {
    printf("Running engine+light_manager functionality program...\n");

    FilaEngine* engine = FilaEngine_create();
    if (!engine) {
        printf("Engine creation failed\n");
        return 1;
    }

    FilaLightManager* manager = FilaEngine_getLightManager(engine);
    if (!manager) {
        printf("LightManager retrieval failed\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaEntity entities[3] = {
            FilaEntityManager_create(),
            FilaEntityManager_create(),
            FilaEntityManager_create(),
    };
    if (entities[0] == 0 || entities[1] == 0 || entities[2] == 0) {
        printf("Entity creation failed\n");
        for (size_t i = 0; i < 3u; ++i) {
            if (entities[i] != 0) {
                FilaEntityManager_destroy(entities[i]);
            }
        }
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaLightType types[3] = {
            FILA_LIGHT_TYPE_DIRECTIONAL,
            FILA_LIGHT_TYPE_POINT,
            FILA_LIGHT_TYPE_SPOT,
    };

    FilaLightManagerShadowOptions shadowOptions = {
            .mapSize = 2048u,
            .shadowCascades = 3u,
            .cascadeSplitPositions = {0.2f, 0.5f, 0.8f},
            .constantBias = 0.002f,
            .normalBias = 1.25f,
            .shadowFar = 150.0f,
            .shadowNearHint = 0.5f,
            .shadowFarHint = 80.0f,
            .stable = true,
            .lispsm = false,
            .polygonOffsetConstant = 0.75f,
            .polygonOffsetSlope = 2.5f,
            .screenSpaceContactShadows = true,
            .stepCount = 12u,
            .maxShadowDistance = 0.6f,
            .vsm = { true, 1.5f },
            .shadowBulbRadius = 0.05f,
            .transform = {1.0f, 0.0f, 0.0f, 0.0f},
    };

    float uniformSplits[3] = {0.0f, 0.0f, 0.0f};
    float logSplits[3] = {0.0f, 0.0f, 0.0f};
    float practicalSplits[3] = {0.0f, 0.0f, 0.0f};
    FilaLightManagerShadowCascades_computeUniformSplits(uniformSplits, 4u);
    FilaLightManagerShadowCascades_computeLogSplits(logSplits, 4u, 0.1f, 100.0f);
    FilaLightManagerShadowCascades_computePracticalSplits(practicalSplits, 4u, 0.1f, 100.0f, 0.5f);
    for (size_t i = 0; i < 3u; ++i) {
        if (uniformSplits[i] <= 0.0f || uniformSplits[i] >= 1.0f ||
                logSplits[i] <= 0.0f || logSplits[i] >= 1.0f ||
                practicalSplits[i] <= 0.0f || practicalSplits[i] >= 1.0f) {
            printf("Shadow cascades split out-of-range\n");
            for (size_t j = 0; j < 3u; ++j) {
                FilaEntityManager_destroy(entities[j]);
            }
            FilaEngine_destroy(&engine);
            return 1;
        }
        if (i > 0u && (uniformSplits[i] <= uniformSplits[i - 1u] ||
                logSplits[i] <= logSplits[i - 1u] ||
                practicalSplits[i] <= practicalSplits[i - 1u])) {
            printf("Shadow cascades split not strictly increasing\n");
            for (size_t j = 0; j < 3u; ++j) {
                FilaEntityManager_destroy(entities[j]);
            }
            FilaEngine_destroy(&engine);
            return 1;
        }
    }

    for (size_t i = 0; i < 3u; ++i) {
        if (FilaLightManager_hasComponent(manager, entities[i])) {
            printf("Entity unexpectedly has light component\n");
            for (size_t j = 0; j < 3u; ++j) {
                FilaEntityManager_destroy(entities[j]);
            }
            FilaEngine_destroy(&engine);
            return 1;
        }

        FilaLightManagerBuilder* builder = FilaLightManagerBuilder_create(types[i]);
        if (!builder) {
            printf("Light builder creation failed\n");
            for (size_t j = 0; j < 3u; ++j) {
                FilaEntityManager_destroy(entities[j]);
            }
            FilaEngine_destroy(&engine);
            return 1;
        }
        FilaLightManagerBuilder_direction(builder, 0.0f, -1.0f, 0.0f);
        FilaLightManagerBuilder_position(builder, 0.0f, 1.0f + (float)i, 0.0f);
        FilaLightManagerBuilder_color(builder, 1.0f, 1.0f, 1.0f);
        FilaLightManagerBuilder_intensity(builder, 100000.0f);
        FilaLightManagerBuilder_intensityWattsEfficiency(builder, 60.0f, 0.087f);
        FilaLightManagerBuilder_intensityCandela(builder, 500.0f);
        FilaLightManagerBuilder_lightChannel(builder, 1u, true);
        FilaLightManagerBuilder_falloff(builder, 10.0f + (float)i);
        FilaLightManagerBuilder_spotLightCone(builder, 0.3f, 0.8f);
        FilaLightManagerBuilder_castShadows(builder, false);
        FilaLightManagerBuilder_castLight(builder, true);
        FilaLightManagerBuilder_sunAngularRadius(builder, 0.545f);
        FilaLightManagerBuilder_sunHaloSize(builder, 10.0f);
        FilaLightManagerBuilder_sunHaloFalloff(builder, 80.0f);
        FilaLightManagerBuilder_shadowOptions(builder, &shadowOptions);

        if (!FilaLightManagerBuilder_build(builder, engine, entities[i])) {
            printf("Light build failed\n");
            FilaLightManagerBuilder_destroy(builder);
            for (size_t j = 0; j < 3u; ++j) {
                FilaEntityManager_destroy(entities[j]);
            }
            FilaEngine_destroy(&engine);
            return 1;
        }
        FilaLightManagerBuilder_destroy(builder);

        if (!FilaLightManager_hasComponent(manager, entities[i])) {
            printf("Light component missing after build\n");
            for (size_t j = 0; j < 3u; ++j) {
                if (FilaLightManager_hasComponent(manager, entities[j])) {
                    FilaLightManager_destroy(manager, entities[j]);
                }
                FilaEntityManager_destroy(entities[j]);
            }
            FilaEngine_destroy(&engine);
            return 1;
        }

        FilaLightManagerInstance instance = FilaLightManager_getInstance(manager, entities[i]);
        if (instance == 0 || FilaLightManager_getType(manager, instance) != types[i]) {
            printf("Light type mismatch\n");
            for (size_t j = 0; j < 3u; ++j) {
                if (FilaLightManager_hasComponent(manager, entities[j])) {
                    FilaLightManager_destroy(manager, entities[j]);
                }
                FilaEntityManager_destroy(entities[j]);
            }
            FilaEngine_destroy(&engine);
            return 1;
        }

        if ((types[i] == FILA_LIGHT_TYPE_DIRECTIONAL) != FilaLightManager_isDirectional(manager, instance) ||
                (types[i] == FILA_LIGHT_TYPE_POINT) != FilaLightManager_isPointLight(manager, instance) ||
                (types[i] == FILA_LIGHT_TYPE_SPOT) != FilaLightManager_isSpotLight(manager, instance)) {
            printf("Light type helper mismatch\n");
            for (size_t j = 0; j < 3u; ++j) {
                if (FilaLightManager_hasComponent(manager, entities[j])) {
                    FilaLightManager_destroy(manager, entities[j]);
                }
                FilaEntityManager_destroy(entities[j]);
            }
            FilaEngine_destroy(&engine);
            return 1;
        }

        FilaLightManager_setPosition(manager, instance, 0.0f, 2.0f + (float)i, 0.0f);
        FilaLightManager_setDirection(manager, instance, 0.0f, -1.0f, 0.0f);
        FilaLightManager_setColor(manager, instance, 1.0f, 0.9f, 0.8f);
        FilaLightManager_setIntensity(manager, instance, 12345.0f + (float)i);
        FilaLightManager_setIntensityCandela(manager, instance, 321.0f + (float)i);
        FilaLightManager_setIntensityWattsEfficiency(manager, instance, 60.0f, 0.087f);
        FilaLightManager_setLightChannel(manager, instance, 1u, true);
        (void)FilaLightManager_getLightChannel(manager, instance, 1u);
        FilaLightManager_setFalloff(manager, instance, 8.0f + (float)i);
        FilaLightManager_setSpotLightCone(manager, instance, 0.2f, 0.7f);
        (void)FilaLightManager_getSpotLightOuterCone(manager, instance);
        (void)FilaLightManager_getSpotLightInnerCone(manager, instance);
        FilaLightManager_setSunAngularRadius(manager, instance, 0.545f);
        (void)FilaLightManager_getSunAngularRadius(manager, instance);
        FilaLightManager_setSunHaloSize(manager, instance, 10.0f);
        (void)FilaLightManager_getSunHaloSize(manager, instance);
        FilaLightManager_setSunHaloFalloff(manager, instance, 80.0f);
        (void)FilaLightManager_getSunHaloFalloff(manager, instance);
        FilaLightManager_setShadowCaster(manager, instance, (types[i] != FILA_LIGHT_TYPE_POINT));
        (void)FilaLightManager_isShadowCaster(manager, instance);

        if (types[i] == FILA_LIGHT_TYPE_DIRECTIONAL) {
            FilaLightManagerShadowOptions readback = shadowOptions;
            if (!FilaLightManager_getShadowOptions(manager, instance, &readback)) {
                printf("Light shadow-options readback failed\n");
                for (size_t j = 0; j < 3u; ++j) {
                    if (FilaLightManager_hasComponent(manager, entities[j])) {
                        FilaLightManager_destroy(manager, entities[j]);
                    }
                    FilaEntityManager_destroy(entities[j]);
                }
                FilaEngine_destroy(&engine);
                return 1;
            }
            if (readback.mapSize != shadowOptions.mapSize ||
                    readback.shadowCascades != shadowOptions.shadowCascades ||
                    !readback.stable ||
                    readback.lispsm ||
                    !readback.vsm.elvsm ||
                    fabsf(readback.vsm.blurWidth - shadowOptions.vsm.blurWidth) > 1e-5f) {
                printf("Light shadow-options values mismatch\n");
                for (size_t j = 0; j < 3u; ++j) {
                    if (FilaLightManager_hasComponent(manager, entities[j])) {
                        FilaLightManager_destroy(manager, entities[j]);
                    }
                    FilaEntityManager_destroy(entities[j]);
                }
                FilaEngine_destroy(&engine);
                return 1;
            }
            readback.mapSize = 1024u;
            readback.shadowCascades = 2u;
            readback.stable = false;
            readback.lispsm = true;
            FilaLightManager_setShadowOptions(manager, instance, &readback);
        }

        float position[3] = {0};
        float direction[3] = {0};
        float color[3] = {0};
        if (!FilaLightManager_getPosition(manager, instance, position) ||
                !FilaLightManager_getDirection(manager, instance, direction) ||
                !FilaLightManager_getColor(manager, instance, color)) {
            printf("Light getter call failed\n");
            for (size_t j = 0; j < 3u; ++j) {
                if (FilaLightManager_hasComponent(manager, entities[j])) {
                    FilaLightManager_destroy(manager, entities[j]);
                }
                FilaEntityManager_destroy(entities[j]);
            }
            FilaEngine_destroy(&engine);
            return 1;
        }
        if (position[1] < 2.0f || direction[1] > -0.5f || color[0] < 0.5f) {
            printf("Light getter values unexpected\n");
            for (size_t j = 0; j < 3u; ++j) {
                if (FilaLightManager_hasComponent(manager, entities[j])) {
                    FilaLightManager_destroy(manager, entities[j]);
                }
                FilaEntityManager_destroy(entities[j]);
            }
            FilaEngine_destroy(&engine);
            return 1;
        }
    }

    FilaEntity listed[8] = {0};
    if (FilaLightManager_getEntities(manager, listed, 8u) < 3u) {
        printf("Light manager entity listing failed\n");
        for (size_t i = 0; i < 3u; ++i) {
            FilaLightManager_destroy(manager, entities[i]);
            FilaEntityManager_destroy(entities[i]);
        }
        FilaEngine_destroy(&engine);
        return 1;
    }

    for (size_t i = 0; i < 3u; ++i) {
        FilaLightManager_destroy(manager, entities[i]);
        FilaEntityManager_destroy(entities[i]);
    }
    FilaEngine_destroy(&engine);

    printf("Engine+light_manager functionality program completed\n");
    return 0;
}

