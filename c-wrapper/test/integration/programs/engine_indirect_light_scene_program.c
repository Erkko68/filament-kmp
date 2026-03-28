#include <stdio.h>

#include "filament/Engine.h"
#include "filament/IndirectLight.h"
#include "filament/Scene.h"
#include "filament/Texture.h"

int main(void) {
    printf("Running engine+indirect_light+scene smoke program...\n");

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

    FilaTexture* reflections = FilaTextureBuilder_build(textureBuilder, engine);
    FilaTextureBuilder_destroy(textureBuilder);

    if (!reflections) {
        printf("Reflections texture build failed\n");
        FilaEngine_destroyScene(engine, scene);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaIndirectLightBuilder* ilBuilder = FilaIndirectLightBuilder_create();
    if (!ilBuilder) {
        printf("IndirectLight builder creation failed\n");
        FilaEngine_destroyTexture(engine, reflections);
        FilaEngine_destroyScene(engine, scene);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaIndirectLightBuilder_reflections(ilBuilder, reflections);
    FilaIndirectLightBuilder_intensity(ilBuilder, 25000.0f);

    FilaIndirectLight* indirectLight = FilaIndirectLightBuilder_build(ilBuilder, engine);
    FilaIndirectLightBuilder_destroy(ilBuilder);

    if (!indirectLight) {
        printf("IndirectLight build failed\n");
        FilaEngine_destroyTexture(engine, reflections);
        FilaEngine_destroyScene(engine, scene);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaScene_setIndirectLight(scene, indirectLight);
    if (FilaScene_getIndirectLight(scene) != indirectLight) {
        printf("Scene indirect-light binding mismatch\n");
        FilaEngine_destroyIndirectLight(engine, indirectLight);
        FilaEngine_destroyTexture(engine, reflections);
        FilaEngine_destroyScene(engine, scene);
        FilaEngine_destroy(&engine);
        return 1;
    }

    if (FilaIndirectLight_getReflectionsTexture(indirectLight) != reflections) {
        printf("IndirectLight reflections texture mismatch\n");
        FilaScene_setIndirectLight(scene, (FilaIndirectLight*)0);
        FilaEngine_destroyIndirectLight(engine, indirectLight);
        FilaEngine_destroyTexture(engine, reflections);
        FilaEngine_destroyScene(engine, scene);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaIndirectLight_setIntensity(indirectLight, 12000.0f);
    if (FilaIndirectLight_getIntensity(indirectLight) <= 0.0f) {
        printf("IndirectLight intensity query failed\n");
        FilaScene_setIndirectLight(scene, (FilaIndirectLight*)0);
        FilaEngine_destroyIndirectLight(engine, indirectLight);
        FilaEngine_destroyTexture(engine, reflections);
        FilaEngine_destroyScene(engine, scene);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaScene_setIndirectLight(scene, (FilaIndirectLight*)0);
    if (FilaScene_getIndirectLight(scene) != (FilaIndirectLight*)0) {
        printf("Scene indirect-light clear failed\n");
        FilaEngine_destroyIndirectLight(engine, indirectLight);
        FilaEngine_destroyTexture(engine, reflections);
        FilaEngine_destroyScene(engine, scene);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaEngine_destroyIndirectLight(engine, indirectLight);
    FilaEngine_destroyTexture(engine, reflections);
    FilaEngine_destroyScene(engine, scene);
    FilaEngine_destroy(&engine);

    printf("Engine+indirect_light+scene smoke program completed\n");
    return 0;
}

