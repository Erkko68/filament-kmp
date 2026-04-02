#include <stdio.h>

#include "filament/Engine.h"
#include "filament/Scene.h"
#include "filament/Skybox.h"
#include "filament/Texture.h"

int main(void) {
    printf("Running engine+texture+skybox functionality program...\n");

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
            FilaTexture_getDepth(texture, 0u) != 1u ||
            FilaTexture_getLevels(texture) != 1u ||
            FilaTexture_getTarget(texture) != FILA_TEXTURE_SAMPLER_CUBEMAP ||
            FilaTexture_getFormat(texture) != FILA_TEXTURE_FORMAT_RGBA8 ||
            !FilaTexture_isCreationComplete(texture)) {
        printf("Texture dimension query mismatch\n");
        FilaEngine_destroyTexture(engine, texture);
        FilaEngine_destroyScene(engine, scene);
        FilaEngine_destroy(&engine);
        return 1;
    }

    if (!FilaTexture_isTextureFormatSupported(engine, FILA_TEXTURE_FORMAT_RGBA8)) {
        printf("RGBA8 format should be supported on this backend\n");
        FilaEngine_destroyScene(engine, scene);
        FilaEngine_destroy(&engine);
        return 1;
    }

    (void)FilaTexture_isTextureFormatMipmappable(engine, FILA_TEXTURE_FORMAT_RGBA8);
    (void)FilaTexture_isTextureFormatCompressed(FILA_TEXTURE_FORMAT_RGBA8);
    (void)FilaTexture_isProtectedTexturesSupported(engine);
    (void)FilaTexture_isTextureSwizzleSupported(engine);
    (void)FilaTexture_computeTextureDataSize(
        FILA_PIXEL_DATA_FORMAT_RGBA,
        FILA_PIXEL_DATA_TYPE_UBYTE,
        4u,
        4u,
        1u);
    (void)FilaTexture_validatePixelFormatAndType(
        FILA_TEXTURE_FORMAT_RGBA8,
        FILA_PIXEL_DATA_FORMAT_RGBA,
        FILA_PIXEL_DATA_TYPE_UBYTE);
    (void)FilaTexture_getMaxTextureSize(engine, FILA_TEXTURE_SAMPLER_2D);
    (void)FilaTexture_getMaxArrayTextureLayers(engine);

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

    const FilaScene* constScene = scene;
    if (FilaScene_getSkyboxConst(constScene) != skybox) {
        printf("Scene const skybox binding mismatch\n");
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

    FilaSkybox_setColor(skybox, 0.15f, 0.2f, 0.25f, 1.0f);

    FilaScene_setSkybox(scene, (FilaSkybox*)0);
    if (FilaScene_getSkybox(scene) != (FilaSkybox*)0) {
        printf("Scene skybox clear failed\n");
        FilaEngine_destroySkybox(engine, skybox);
        FilaEngine_destroyTexture(engine, texture);
        FilaEngine_destroyScene(engine, scene);
        FilaEngine_destroy(&engine);
        return 1;
    }
    if (FilaScene_getSkyboxConst(constScene) != (const FilaSkybox*)0) {
        printf("Scene const skybox clear failed\n");
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

    printf("Engine+texture+skybox functionality program completed\n");
    return 0;
}
