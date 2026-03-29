#include <math.h>
#include <stdio.h>

#include "filament/Engine.h"
#include "filament/Stream.h"
#include "filament/Texture.h"
#include "filament/TextureSampler.h"

int main(void) {
    printf("Running engine+stream+texture_params smoke program...\n");

    FilaEngine* engine = FilaEngine_create();
    if (!engine) {
        printf("Engine creation failed\n");
        return 1;
    }

    FilaTextureParams* params = FilaTextureParams_create();
    if (!params) {
        printf("Texture params creation failed\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaTextureParams_setMinFilter(params, FILA_SAMPLER_MIN_LINEAR_MIPMAP_LINEAR);
    FilaTextureParams_setMagFilter(params, FILA_SAMPLER_MAG_LINEAR);
    FilaTextureParams_setWrapModeS(params, FILA_SAMPLER_WRAP_REPEAT);
    FilaTextureParams_setWrapModeT(params, FILA_SAMPLER_WRAP_MIRRORED_REPEAT);
    FilaTextureParams_setWrapModeR(params, FILA_SAMPLER_WRAP_CLAMP_TO_EDGE);
    FilaTextureParams_setAnisotropy(params, 4.0f);

    if (FilaTextureParams_getMinFilter(params) != FILA_SAMPLER_MIN_LINEAR_MIPMAP_LINEAR ||
            FilaTextureParams_getMagFilter(params) != FILA_SAMPLER_MAG_LINEAR ||
            FilaTextureParams_getWrapModeS(params) != FILA_SAMPLER_WRAP_REPEAT ||
            FilaTextureParams_getWrapModeT(params) != FILA_SAMPLER_WRAP_MIRRORED_REPEAT ||
            FilaTextureParams_getWrapModeR(params) != FILA_SAMPLER_WRAP_CLAMP_TO_EDGE ||
            fabsf(FilaTextureParams_getAnisotropy(params) - 4.0f) > 0.001f) {
        printf("Texture params query mismatch\n");
        FilaTextureParams_destroy(params);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaStreamBuilder* streamBuilder = FilaStreamBuilder_create();
    if (!streamBuilder) {
        printf("Stream builder creation failed\n");
        FilaTextureParams_destroy(params);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaStreamBuilder_width(streamBuilder, 16u);
    FilaStreamBuilder_height(streamBuilder, 16u);

    FilaStream* stream = FilaStreamBuilder_build(streamBuilder, engine);
    FilaStreamBuilder_destroy(streamBuilder);

    if (!stream) {
        printf("Stream build failed\n");
        FilaTextureParams_destroy(params);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaTextureBuilder* textureBuilder = FilaTextureBuilder_create();
    if (!textureBuilder) {
        printf("Texture builder creation failed\n");
        FilaEngine_destroyStream(engine, stream);
        FilaTextureParams_destroy(params);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaTextureBuilder_width(textureBuilder, 16u);
    FilaTextureBuilder_height(textureBuilder, 16u);
    FilaTextureBuilder_levels(textureBuilder, 1u);
    FilaTextureBuilder_sampler(textureBuilder, FILA_TEXTURE_SAMPLER_EXTERNAL);
    FilaTextureBuilder_format(textureBuilder, FILA_TEXTURE_FORMAT_RGBA8);

    FilaTexture* texture = FilaTextureBuilder_build(textureBuilder, engine);
    FilaTextureBuilder_destroy(textureBuilder);

    if (!texture) {
        printf("Texture build failed\n");
        FilaEngine_destroyStream(engine, stream);
        FilaTextureParams_destroy(params);
        FilaEngine_destroy(&engine);
        return 1;
    }

    FilaTexture_setExternalStream(texture, engine, stream);
    FilaStream_setDimensions(stream, 8u, 8u);
    (void)FilaStream_getStreamType(stream);
    (void)FilaStream_getTimestamp(stream);

    FilaEngine_destroyTexture(engine, texture);
    FilaEngine_destroyStream(engine, stream);
    FilaTextureParams_destroy(params);
    FilaEngine_destroy(&engine);

    printf("Engine+stream+texture_params smoke program completed\n");
    return 0;
}

