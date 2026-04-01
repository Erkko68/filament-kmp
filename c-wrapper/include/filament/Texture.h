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
    FILA_TEXTURE_SAMPLER_2D = FILA_BACKEND_SAMPLER_TYPE_2D,
    FILA_TEXTURE_SAMPLER_2D_ARRAY = FILA_BACKEND_SAMPLER_TYPE_2D_ARRAY,
    FILA_TEXTURE_SAMPLER_CUBEMAP = FILA_BACKEND_SAMPLER_TYPE_CUBEMAP,
    FILA_TEXTURE_SAMPLER_EXTERNAL = FILA_BACKEND_SAMPLER_TYPE_EXTERNAL,
    FILA_TEXTURE_SAMPLER_3D = FILA_BACKEND_SAMPLER_TYPE_3D,
    FILA_TEXTURE_SAMPLER_CUBEMAP_ARRAY = FILA_BACKEND_SAMPLER_TYPE_CUBEMAP_ARRAY,
} FilaTextureSampler;

typedef FilaBackendTextureFormat FilaTextureFormat;
typedef FilaBackendTextureSwizzle FilaTextureSwizzle;

typedef FilaPixelDataFormat FilaTexturePixelDataFormat;
typedef FilaPixelDataType FilaTexturePixelDataType;

#define FILA_TEXTURE_FORMAT_RGBA8 FILA_BACKEND_TEXTURE_FORMAT_RGBA8

#define FILA_TEXTURE_SWIZZLE_SUBSTITUTE_ZERO FILA_BACKEND_TEXTURE_SWIZZLE_SUBSTITUTE_ZERO
#define FILA_TEXTURE_SWIZZLE_SUBSTITUTE_ONE FILA_BACKEND_TEXTURE_SWIZZLE_SUBSTITUTE_ONE
#define FILA_TEXTURE_SWIZZLE_CHANNEL_0 FILA_BACKEND_TEXTURE_SWIZZLE_CHANNEL_0
#define FILA_TEXTURE_SWIZZLE_CHANNEL_1 FILA_BACKEND_TEXTURE_SWIZZLE_CHANNEL_1
#define FILA_TEXTURE_SWIZZLE_CHANNEL_2 FILA_BACKEND_TEXTURE_SWIZZLE_CHANNEL_2
#define FILA_TEXTURE_SWIZZLE_CHANNEL_3 FILA_BACKEND_TEXTURE_SWIZZLE_CHANNEL_3

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
void FilaTextureBuilder_depth(FilaTextureBuilder* builder, uint32_t depth);
void FilaTextureBuilder_swizzle(
    FilaTextureBuilder* builder,
    FilaTextureSwizzle r,
    FilaTextureSwizzle g,
    FilaTextureSwizzle b,
    FilaTextureSwizzle a);
void FilaTextureBuilder_external(FilaTextureBuilder* builder);
void FilaTextureBuilder_import(FilaTextureBuilder* builder, intptr_t id);
FilaTexture* FilaTextureBuilder_build(FilaTextureBuilder* builder, FilaEngine* engine);

size_t FilaTexture_getWidth(const FilaTexture* texture, size_t level);
size_t FilaTexture_getHeight(const FilaTexture* texture, size_t level);
size_t FilaTexture_getLevels(const FilaTexture* texture);
size_t FilaTexture_getDepth(const FilaTexture* texture, size_t level);
FilaTextureSampler FilaTexture_getTarget(const FilaTexture* texture);
FilaTextureFormat FilaTexture_getFormat(const FilaTexture* texture);
bool FilaTexture_isCreationComplete(const FilaTexture* texture);

bool FilaTexture_isTextureFormatSupported(FilaEngine* engine, FilaTextureFormat format);
bool FilaTexture_isTextureFormatMipmappable(FilaEngine* engine, FilaTextureFormat format);
bool FilaTexture_isTextureFormatCompressed(FilaTextureFormat format);
bool FilaTexture_isProtectedTexturesSupported(FilaEngine* engine);
bool FilaTexture_isTextureSwizzleSupported(FilaEngine* engine);
size_t FilaTexture_computeTextureDataSize(
    FilaTexturePixelDataFormat format,
    FilaTexturePixelDataType type,
    size_t stride,
    size_t height,
    size_t alignment);
bool FilaTexture_validatePixelFormatAndType(
    FilaTextureFormat internalFormat,
    FilaTexturePixelDataFormat format,
    FilaTexturePixelDataType type);
size_t FilaTexture_getMaxTextureSize(FilaEngine* engine, FilaTextureSampler sampler);
size_t FilaTexture_getMaxArrayTextureLayers(FilaEngine* engine);

// Attach an external stream to this texture
void FilaTexture_setExternalImagePlane(FilaTexture* texture, FilaEngine* engine, void* externalImage, size_t plane);
void FilaTexture_setExternalStream(FilaTexture* texture, FilaEngine* engine, FilaStream* stream);

// Upload image data to a texture (2D, 3D, or cubemap)
void FilaTexture_setImage(FilaTexture* texture, FilaEngine* engine, size_t level, uint32_t xoffset, uint32_t yoffset, uint32_t zoffset, uint32_t width, uint32_t height, uint32_t depth, FilaPixelBufferDescriptor* buffer);
void FilaTexture_setImage2D(FilaTexture* texture, FilaEngine* engine, size_t level, FilaPixelBufferDescriptor* buffer);
void FilaTexture_setImage2DRegion(FilaTexture* texture, FilaEngine* engine, size_t level, uint32_t xoffset, uint32_t yoffset, uint32_t width, uint32_t height, FilaPixelBufferDescriptor* buffer);
void FilaTexture_generateMipmaps(FilaTexture* texture, FilaEngine* engine);

#ifdef __cplusplus
}
#endif

#endif // FILAMENT_C_TEXTURE_H

