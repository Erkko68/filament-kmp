#ifndef FILAMENT_C_TEXTURE_H
#define FILAMENT_C_TEXTURE_H

#include <stddef.h>
#include <stdint.h>

#include "Types.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef enum FilaTextureSampler {
    FILA_TEXTURE_SAMPLER_2D = 0,
    FILA_TEXTURE_SAMPLER_CUBEMAP = 1,
} FilaTextureSampler;

typedef enum FilaTextureFormat {
    FILA_TEXTURE_FORMAT_RGBA8 = 0,
} FilaTextureFormat;

FilaTextureBuilder* FilaTextureBuilder_create(void);
void FilaTextureBuilder_destroy(FilaTextureBuilder* builder);
void FilaTextureBuilder_width(FilaTextureBuilder* builder, uint32_t width);
void FilaTextureBuilder_height(FilaTextureBuilder* builder, uint32_t height);
void FilaTextureBuilder_levels(FilaTextureBuilder* builder, uint8_t levels);
void FilaTextureBuilder_sampler(FilaTextureBuilder* builder, FilaTextureSampler sampler);
void FilaTextureBuilder_format(FilaTextureBuilder* builder, FilaTextureFormat format);
FilaTexture* FilaTextureBuilder_build(FilaTextureBuilder* builder, FilaEngine* engine);

size_t FilaTexture_getWidth(const FilaTexture* texture, size_t level);
size_t FilaTexture_getHeight(const FilaTexture* texture, size_t level);
size_t FilaTexture_getLevels(const FilaTexture* texture);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_TEXTURE_H

