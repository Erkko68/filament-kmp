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
    FILA_TEXTURE_SAMPLER_EXTERNAL = 3,
} FilaTextureSampler;

typedef enum FilaTextureFormat {
    FILA_TEXTURE_FORMAT_RGBA8 = 0,
} FilaTextureFormat;

typedef enum FilaTextureUsage {
    FILA_TEXTURE_USAGE_NONE = 0x0000,
    FILA_TEXTURE_USAGE_COLOR_ATTACHMENT = 0x0001,
    FILA_TEXTURE_USAGE_DEPTH_ATTACHMENT = 0x0002,
    FILA_TEXTURE_USAGE_STENCIL_ATTACHMENT = 0x0004,
    FILA_TEXTURE_USAGE_UPLOADABLE = 0x0008,
    FILA_TEXTURE_USAGE_SAMPLEABLE = 0x0010,
    FILA_TEXTURE_USAGE_SUBPASS_INPUT = 0x0020,
    FILA_TEXTURE_USAGE_BLIT_SRC = 0x0040,
    FILA_TEXTURE_USAGE_BLIT_DST = 0x0080,
    FILA_TEXTURE_USAGE_PROTECTED = 0x0100,
    FILA_TEXTURE_USAGE_GEN_MIPMAPPABLE = 0x0200,
    FILA_TEXTURE_USAGE_DEFAULT = 0x0018,
} FilaTextureUsage;

FilaTextureBuilder* FilaTextureBuilder_create(void);
void FilaTextureBuilder_destroy(FilaTextureBuilder* builder);
void FilaTextureBuilder_width(FilaTextureBuilder* builder, uint32_t width);
void FilaTextureBuilder_height(FilaTextureBuilder* builder, uint32_t height);
void FilaTextureBuilder_levels(FilaTextureBuilder* builder, uint8_t levels);
void FilaTextureBuilder_samples(FilaTextureBuilder* builder, uint8_t samples);
void FilaTextureBuilder_sampler(FilaTextureBuilder* builder, FilaTextureSampler sampler);
void FilaTextureBuilder_format(FilaTextureBuilder* builder, FilaTextureFormat format);
void FilaTextureBuilder_usage(FilaTextureBuilder* builder, uint16_t usage);
FilaTexture* FilaTextureBuilder_build(FilaTextureBuilder* builder, FilaEngine* engine);

size_t FilaTexture_getWidth(const FilaTexture* texture, size_t level);
size_t FilaTexture_getHeight(const FilaTexture* texture, size_t level);
size_t FilaTexture_getLevels(const FilaTexture* texture);

// Attach an external stream to this texture
void FilaTexture_setExternalStream(FilaTexture* texture, FilaEngine* engine, FilaStream* stream);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_TEXTURE_H

