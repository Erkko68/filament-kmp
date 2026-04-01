#ifndef FILAMENT_C_TEXTURE_H
#define FILAMENT_C_TEXTURE_H

#include <stddef.h>
#include <stdint.h>

#include "Types.h"
#include "BufferDescriptor.h"
#include "../backend/DriverEnums.h"

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

typedef FilaBackendTextureUsage FilaTextureUsage;

#define FILA_TEXTURE_USAGE_NONE FILA_BACKEND_TEXTURE_USAGE_NONE
#define FILA_TEXTURE_USAGE_COLOR_ATTACHMENT FILA_BACKEND_TEXTURE_USAGE_COLOR_ATTACHMENT
#define FILA_TEXTURE_USAGE_DEPTH_ATTACHMENT FILA_BACKEND_TEXTURE_USAGE_DEPTH_ATTACHMENT
#define FILA_TEXTURE_USAGE_STENCIL_ATTACHMENT FILA_BACKEND_TEXTURE_USAGE_STENCIL_ATTACHMENT
#define FILA_TEXTURE_USAGE_UPLOADABLE FILA_BACKEND_TEXTURE_USAGE_UPLOADABLE
#define FILA_TEXTURE_USAGE_SAMPLEABLE FILA_BACKEND_TEXTURE_USAGE_SAMPLEABLE
#define FILA_TEXTURE_USAGE_SUBPASS_INPUT FILA_BACKEND_TEXTURE_USAGE_SUBPASS_INPUT
#define FILA_TEXTURE_USAGE_BLIT_SRC FILA_BACKEND_TEXTURE_USAGE_BLIT_SRC
#define FILA_TEXTURE_USAGE_BLIT_DST FILA_BACKEND_TEXTURE_USAGE_BLIT_DST
#define FILA_TEXTURE_USAGE_PROTECTED FILA_BACKEND_TEXTURE_USAGE_PROTECTED
#define FILA_TEXTURE_USAGE_GEN_MIPMAPPABLE FILA_BACKEND_TEXTURE_USAGE_GEN_MIPMAPPABLE
#define FILA_TEXTURE_USAGE_DEFAULT FILA_BACKEND_TEXTURE_USAGE_DEFAULT

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

// Upload image data to a texture (2D, 3D, or cubemap)
void FilaTexture_setImage(FilaTexture* texture, FilaEngine* engine, size_t level, uint32_t xoffset, uint32_t yoffset, uint32_t zoffset, uint32_t width, uint32_t height, uint32_t depth, FilaPixelBufferDescriptor* buffer);
void FilaTexture_setImage2D(FilaTexture* texture, FilaEngine* engine, size_t level, FilaPixelBufferDescriptor* buffer);
void FilaTexture_generateMipmaps(FilaTexture* texture, FilaEngine* engine);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_TEXTURE_H

