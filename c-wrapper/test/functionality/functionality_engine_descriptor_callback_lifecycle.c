#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>

#include "backend/BufferDescriptor.h"
#include "backend/PixelBufferDescriptor.h"
#include "filament/BufferObject.h"
#include "filament/Engine.h"
#include "filament/Texture.h"

static int g_buffer_callback_count = 0;
static int g_pixel_callback_count = 0;

static void buffer_release_cb(void* buffer, size_t size, void* user) {
    (void)size;
    (void)user;
    free(buffer);
    g_buffer_callback_count++;
}

static void pixel_release_cb(void* buffer, size_t size, void* user) {
    (void)size;
    (void)user;
    free(buffer);
    g_pixel_callback_count++;
}

int main(void) {
    FilaEngine* engine = FilaEngine_create();
    if (!engine) {
        printf("Engine creation failed\n");
        return 1;
    }

    FilaBufferObjectBuilder* boBuilder = FilaBufferObjectBuilder_create();
    FilaBufferObjectBuilder_size(boBuilder, 16);
    FilaBufferObjectBuilder_bindingType(boBuilder, FILA_BUFFER_OBJECT_BINDING_VERTEX);
    FilaBufferObject* bo = FilaBufferObjectBuilder_build(boBuilder, engine);
    FilaBufferObjectBuilder_destroy(boBuilder);
    if (!bo) {
        printf("BufferObject creation failed\n");
        FilaEngine_destroy(&engine);
        return 1;
    }

    void* cpuBuffer = malloc(16);
    FilaBufferDescriptor* bd = FilaBufferDescriptor_create(cpuBuffer, 16, buffer_release_cb, NULL);
    FilaBufferObject_setBuffer(bo, engine, bd, 0);

    // Consumed descriptors should expose empty callback state and be safely destroyable.
    if (FilaBufferDescriptor_hasCallback(bd) ||
            FilaBufferDescriptor_getCallback(bd) != NULL ||
            FilaBufferDescriptor_getUser(bd) != NULL) {
        printf("BufferDescriptor consumed state invalid\n");
        FilaBufferDescriptor_destroy(bd);
        FilaEngine_destroyBufferObject(engine, bo);
        FilaEngine_destroy(&engine);
        return 1;
    }
    FilaBufferDescriptor_destroy(bd);

    FilaTextureBuilder* texBuilder = FilaTextureBuilder_create();
    FilaTextureBuilder_width(texBuilder, 1);
    FilaTextureBuilder_height(texBuilder, 1);
    FilaTextureBuilder_levels(texBuilder, 1);
    FilaTextureBuilder_sampler(texBuilder, FILA_TEXTURE_SAMPLER_2D);
    FilaTextureBuilder_format(texBuilder, FILA_TEXTURE_FORMAT_RGBA8);
    FilaTextureBuilder_usage(texBuilder, FILA_TEXTURE_USAGE_UPLOADABLE | FILA_TEXTURE_USAGE_SAMPLEABLE);
    FilaTexture* texture = FilaTextureBuilder_build(texBuilder, engine);
    FilaTextureBuilder_destroy(texBuilder);
    if (!texture) {
        printf("Texture creation failed\n");
        FilaEngine_destroyBufferObject(engine, bo);
        FilaEngine_destroy(&engine);
        return 1;
    }

    void* imageData = malloc(4);
    FilaPixelBufferDescriptor* pbd = FilaPixelBufferDescriptor_create(
        imageData,
        4,
        FILA_PIXEL_DATA_FORMAT_RGBA,
        FILA_PIXEL_DATA_TYPE_UBYTE,
        1,
        pixel_release_cb,
        NULL);
    FilaTexture_setImage2D(texture, engine, 0, pbd);

    if (FilaPixelBufferDescriptor_hasCallback(pbd) ||
            FilaPixelBufferDescriptor_getCallback(pbd) != NULL ||
            FilaPixelBufferDescriptor_getUser(pbd) != NULL) {
        printf("PixelBufferDescriptor consumed state invalid\n");
        FilaPixelBufferDescriptor_destroy(pbd);
        FilaEngine_destroyTexture(engine, texture);
        FilaEngine_destroyBufferObject(engine, bo);
        FilaEngine_destroy(&engine);
        return 1;
    }
    FilaPixelBufferDescriptor_destroy(pbd);

    FilaEngine_destroyTexture(engine, texture);
    FilaEngine_destroyBufferObject(engine, bo);
    FilaEngine_destroy(&engine);

    if (g_buffer_callback_count != 1 || g_pixel_callback_count != 1) {
        printf("Unexpected callback counts: buffer=%d pixel=%d\n", g_buffer_callback_count, g_pixel_callback_count);
        return 1;
    }

    return 0;
}

