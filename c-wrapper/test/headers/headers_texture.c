#include <stddef.h>

#include "filament/Engine.h"
#include "filament/Texture.h"

// Verifies Texture builder and query APIs are consumable from C.
void test_headers_texture(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaTextureBuilder* builder = FilaTextureBuilder_create();
    FilaTextureBuilder_width(builder, 16u);
    FilaTextureBuilder_height(builder, 16u);
    FilaTextureBuilder_levels(builder, 1u);
    FilaTextureBuilder_samples(builder, 1u);
    FilaTextureBuilder_usage(builder, (uint16_t)(FILA_TEXTURE_USAGE_DEFAULT | FILA_TEXTURE_USAGE_COLOR_ATTACHMENT));
    FilaTextureBuilder_sampler(builder, FILA_TEXTURE_SAMPLER_CUBEMAP_ARRAY);
    FilaTextureBuilder_swizzle(
        builder,
        FILA_TEXTURE_SWIZZLE_CHANNEL_0,
        FILA_TEXTURE_SWIZZLE_CHANNEL_1,
        FILA_TEXTURE_SWIZZLE_CHANNEL_2,
        FILA_TEXTURE_SWIZZLE_CHANNEL_3);
    FilaTextureBuilder_external(builder);
    FilaTextureBuilder_import(builder, (intptr_t)0);

    FilaTexture* texture = FilaTextureBuilder_build(builder, engine);
    (void)FilaTexture_getWidth(texture, 0u);
    (void)FilaTexture_getHeight(texture, 0u);
    (void)FilaTexture_getLevels(texture);
    (void)FilaTexture_getDepth(texture, 0u);
    (void)FilaTexture_getTarget(texture);
    (void)FilaTexture_getFormat(texture);
    (void)FilaTexture_isCreationComplete(texture);

    (void)FilaTexture_isTextureFormatSupported(engine, FILA_TEXTURE_FORMAT_RGBA8);
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
    FilaTexture_setExternalImagePlane(texture, engine, (void*)0, 0u);

    FilaTextureBuilder_destroy(builder);
    FilaEngine_destroyTexture(engine, texture);
}
