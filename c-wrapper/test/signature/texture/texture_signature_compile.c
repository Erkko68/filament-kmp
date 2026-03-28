#include <stddef.h>

#include "filament/Texture.h"

// Function pointer assignments lock exported C signatures.
static FilaTextureBuilder* (*g_texture_builder_create)(void) = FilaTextureBuilder_create;
static void (*g_texture_builder_destroy)(FilaTextureBuilder*) = FilaTextureBuilder_destroy;
static void (*g_texture_builder_width)(FilaTextureBuilder*, uint32_t) = FilaTextureBuilder_width;
static void (*g_texture_builder_height)(FilaTextureBuilder*, uint32_t) = FilaTextureBuilder_height;
static void (*g_texture_builder_levels)(FilaTextureBuilder*, uint8_t) = FilaTextureBuilder_levels;
static void (*g_texture_builder_sampler)(FilaTextureBuilder*, FilaTextureSampler) = FilaTextureBuilder_sampler;
static void (*g_texture_builder_format)(FilaTextureBuilder*, FilaTextureFormat) = FilaTextureBuilder_format;
static FilaTexture* (*g_texture_builder_build)(FilaTextureBuilder*, FilaEngine*) = FilaTextureBuilder_build;
static size_t (*g_texture_get_width)(const FilaTexture*, size_t) = FilaTexture_getWidth;
static size_t (*g_texture_get_height)(const FilaTexture*, size_t) = FilaTexture_getHeight;
static size_t (*g_texture_get_levels)(const FilaTexture*) = FilaTexture_getLevels;

void fila_texture_signature_compile_only(void) {
    (void)g_texture_builder_create;
    (void)g_texture_builder_destroy;
    (void)g_texture_builder_width;
    (void)g_texture_builder_height;
    (void)g_texture_builder_levels;
    (void)g_texture_builder_sampler;
    (void)g_texture_builder_format;
    (void)g_texture_builder_build;
    (void)g_texture_get_width;
    (void)g_texture_get_height;
    (void)g_texture_get_levels;
}

