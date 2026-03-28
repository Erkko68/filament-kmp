#include <stdio.h>

#include "filament/Engine.h"
#include "filament/Scene.h"
#include "filament/Skybox.h"
#include "filament/Texture.h"

int main(void) {
    printf("Running engine+texture+skybox smoke program...\n");

    FilaEngine* engine = FilaEngine_create();
    if (!engine) {
        printf("Engine creation failed\n");
        return 1;
    }

    FilaScene* scene = FilaEngine_createScene(engine);
    if (!scene) {
        printf("Scene creation failed\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaTextureBuilder* textureBuilder = FilaTextureBuilder_create();
    if (!textureBuilder) {
        printf("Texture builder creation failed\n");
        FilaEngine_destroyScene(engine, scene);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaTextureBuilder_width(textureBuilder, 4u);
    FilaTextureBuilder_height(textureBuilder, 4u);
    FilaTextureBuilder_levels(textureBuilder, 1u);
    FilaTextureBuilder_sampler(textureBuilder, FILA_TEXTURE_SAMPLER_CUBEMAP);
    FilaTextureBuilder_format(textureBuilder, FILA_TEXTURE_FORMAT_RGBA8);

    FilaTexture* texture = FilaTextureBuilder_build(textureBuilder, engine);
    FilaTextureBuilder_destroy(textureBuilder);

    if (!texture) {
        printf("Texture build failed\n");
        FilaEngine_destroyScene(engine, scene);
        FilaEngine_destroy(&engine);
        return 1;
    }

    if (FilaTexture_getWidth(texture, 0u) != 4u ||
            FilaTexture_getHeight(texture, 0u) != 4u ||
            FilaTexture_getLevels(texture) != 1u) {
        printf("Texture dimension query mismatch\n");
        FilaEngine_destroyTexture(engine, texture);
        FilaEngine_destroyScene(engine, scene);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaSkyboxBuilder* skyboxBuilder = FilaSkyboxBuilder_create();
    if (!skyboxBuilder) {
        printf("Skybox builder creation failed\n");
        FilaEngine_destroyTexture(engine, texture);
        FilaEngine_destroyScene(engine, scene);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaSkyboxBuilder_environment(skyboxBuilder, texture);
    FilaSkyboxBuilder_showSun(skyboxBuilder, false);
    FilaSkyboxBuilder_intensity(skyboxBuilder, 30000.0f);
    FilaSkyboxBuilder_priority(skyboxBuilder, 7u);

    FilaSkybox* skybox = FilaSkyboxBuilder_build(skyboxBuilder, engine);
    FilaSkyboxBuilder_destroy(skyboxBuilder);

    if (!skybox) {
        printf("Skybox build failed\n");
        FilaEngine_destroyTexture(engine, texture);
        FilaEngine_destroyScene(engine, scene);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaScene_setSkybox(scene, skybox);
    if (FilaScene_getSkybox(scene) != skybox) {
        printf("Scene skybox binding mismatch\n");
        FilaEngine_destroySkybox(engine, skybox);
        FilaEngine_destroyTexture(engine, texture);
        FilaEngine_destroyScene(engine, scene);
        FilaEngine_destroy(&engine);
        return 1;
    }

    if (FilaSkybox_getTexture(skybox) != texture) {
        printf("Skybox texture query mismatch\n");
        FilaScene_setSkybox(scene, (FilaSkybox*)0);
        FilaEngine_destroySkybox(engine, skybox);
        FilaEngine_destroyTexture(engine, texture);
        FilaEngine_destroyScene(engine, scene);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaScene_setSkybox(scene, (FilaSkybox*)0);
    if (FilaScene_getSkybox(scene) != (FilaSkybox*)0) {
        printf("Scene skybox clear failed\n");
        FilaEngine_destroySkybox(engine, skybox);
        FilaEngine_destroyTexture(engine, texture);
        FilaEngine_destroyScene(engine, scene);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaEngine_destroySkybox(engine, skybox);
    FilaEngine_destroyTexture(engine, texture);
    FilaEngine_destroyScene(engine, scene);
    FilaEngine_destroy(&engine);

    printf("Engine+texture+skybox smoke program completed\n");
    return 0;
}

