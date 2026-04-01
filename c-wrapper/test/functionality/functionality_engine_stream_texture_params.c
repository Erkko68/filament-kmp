#include <math.h>
#include <stdio.h>

#include "filament/Engine.h"
#include "filament/Stream.h"
#include "filament/Texture.h"
#include "filament/TextureSampler.h"
#include "backend/CallbackHandler.h"

static void noop_stream_callback(void* image, void* userData) {
    (void)image;
    (void)userData;
}

static void noop_handler_dispatch(void* callbackUser, FilaCallbackHandlerCallback callback, void* handlerUser) {
    (void)handlerUser;
    if (callback) {
        callback(callbackUser);
    }
}

int main(void) {
    printf("Running engine+stream+texture_params functionality program...\n");

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
    FilaTextureParams_setCompareMode(params, FILA_SAMPLER_COMPARE_TO_TEXTURE, FILA_SAMPLER_COMPARE_LE);

    if (FilaTextureParams_getMinFilter(params) != FILA_SAMPLER_MIN_LINEAR_MIPMAP_LINEAR ||
            FilaTextureParams_getMagFilter(params) != FILA_SAMPLER_MAG_LINEAR ||
            FilaTextureParams_getWrapModeS(params) != FILA_SAMPLER_WRAP_REPEAT ||
            FilaTextureParams_getWrapModeT(params) != FILA_SAMPLER_WRAP_MIRRORED_REPEAT ||
            FilaTextureParams_getWrapModeR(params) != FILA_SAMPLER_WRAP_CLAMP_TO_EDGE ||
            fabsf(FilaTextureParams_getAnisotropy(params) - 4.0f) > 0.001f ||
            FilaTextureParams_getCompareMode(params) != FILA_SAMPLER_COMPARE_TO_TEXTURE ||
            FilaTextureParams_getCompareFunc(params) != FILA_SAMPLER_COMPARE_LE) {
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

    FilaStream_setAcquiredImage((FilaStream*)0, (void*)0, noop_stream_callback, (void*)0, (const float*)0);
    FilaStream_setAcquiredImageWithHandler((FilaStream*)0, (void*)0, (FilaCallbackHandler*)0, noop_stream_callback, (void*)0, (const float*)0);

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
    FilaTextureBuilder_import(textureBuilder, (intptr_t)0);

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
    FilaTexture_setExternalImagePlane(texture, engine, (void*)0, 0u);
    FilaTexture_setImage2DRegion(texture, engine, 0u, 0u, 0u, 1u, 1u, (FilaPixelBufferDescriptor*)0);
    {
        static unsigned char acquiredImageStub = 0u;
        const float identity3x3[9] = {
            1.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 1.0f,
        };
        FilaStream_setAcquiredImage(stream, &acquiredImageStub, noop_stream_callback, (void*)0, identity3x3);
        {
            FilaCallbackHandler* handler = FilaCallbackHandler_create(noop_handler_dispatch, (void*)0);
            FilaStream_setAcquiredImageWithHandler(stream, &acquiredImageStub, handler, noop_stream_callback, (void*)0, identity3x3);
            FilaCallbackHandler_destroy(handler);
        }
    }
    FilaStream_setDimensions(stream, 8u, 8u);
    {
        const FilaStreamType streamType = FilaStream_getStreamType(stream);
        if (streamType != FILA_STREAM_NATIVE && streamType != FILA_STREAM_ACQUIRED) {
            printf("Stream type contract mismatch\n");
            FilaEngine_destroyTexture(engine, texture);
            FilaEngine_destroyStream(engine, stream);
            FilaTextureParams_destroy(params);
            FilaEngine_destroy(&engine);
            return 1;
        }
    }
    (void)FilaStream_getTimestamp(stream);

    FilaEngine_destroyTexture(engine, texture);
    FilaEngine_destroyStream(engine, stream);
    FilaTextureParams_destroy(params);
    FilaEngine_destroy(&engine);

    printf("Engine+stream+texture_params functionality program completed\n");
    return 0;
}

