#include <stddef.h>

#include "filament/Engine.h"
#include "filament/Texture.h"

// Verifies Texture builder and query APIs are consumable from C.
void fila_texture_module_compile_only(void) {
    FilaEngine* engine = (FilaEngine*)0;
    FilaTextureBuilder* builder = FilaTextureBuilder_create();
    FilaTextureBuilder_width(builder, 16u);
    FilaTextureBuilder_height(builder, 16u);
    FilaTextureBuilder_levels(builder, 1u);
    FilaTextureBuilder_sampler(builder, FILA_TEXTURE_SAMPLER_CUBEMAP);
    FilaTextureBuilder_format(builder, FILA_TEXTURE_FORMAT_RGBA8);

    FilaTexture* texture = FilaTextureBuilder_build(builder, engine);
    (void)FilaTexture_getWidth(texture, 0u);
    (void)FilaTexture_getHeight(texture, 0u);
    (void)FilaTexture_getLevels(texture);

    FilaTextureBuilder_destroy(builder);
    FilaEngine_destroyTexture(engine, texture);
}

