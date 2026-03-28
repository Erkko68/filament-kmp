#include <stdio.h>

#include "filament/Engine.h"
#include "filament/EntityManager.h"
#include "filament/LightManager.h"

int main(void) {
    printf("Running engine+light_manager smoke program...\n");

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
        FilaLightManagerBuilder_falloff(builder, 10.0f + (float)i);
        FilaLightManagerBuilder_spotLightCone(builder, 0.3f, 0.8f);
        FilaLightManagerBuilder_castShadows(builder, false);

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

    printf("Engine+light_manager smoke program completed\n");
    return 0;
}

