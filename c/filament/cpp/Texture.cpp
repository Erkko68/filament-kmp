#include <filament/Texture.h>
#include <filament/Engine.h>
#include <filament/Stream.h>
#include <backend/PixelBufferDescriptor.h>
#include <backend/DriverEnums.h>

#include "FilaCommon.h"
#include "../c/Texture.h"

using namespace filament;
using namespace backend;

extern "C" {

FilaTextureBuilder* FilaTextureBuilder_create() {
    return reinterpret_cast<FilaTextureBuilder*>(new Texture::Builder());
}

void FilaTextureBuilder_destroy(FilaTextureBuilder* builder) {
    delete reinterpret_cast<Texture::Builder*>(builder);
}

FilaTexture* FilaTextureBuilder_build(FilaTextureBuilder* builder, FilaEngine* engine) {
    return reinterpret_cast<FilaTexture*>(FILA_CAST(Texture::Builder, builder)->build(*FILA_CAST(Engine, engine)));
}

void FilaTextureBuilder_width(FilaTextureBuilder* builder, uint32_t width) {
    FILA_CAST(Texture::Builder, builder)->width(width);
}

void FilaTextureBuilder_height(FilaTextureBuilder* builder, uint32_t height) {
    FILA_CAST(Texture::Builder, builder)->height(height);
}

void FilaTextureBuilder_depth(FilaTextureBuilder* builder, uint32_t depth) {
    FILA_CAST(Texture::Builder, builder)->depth(depth);
}

void FilaTextureBuilder_levels(FilaTextureBuilder* builder, uint8_t levels) {
    FILA_CAST(Texture::Builder, builder)->levels(levels);
}

void FilaTextureBuilder_samples(FilaTextureBuilder* builder, uint8_t samples) {
    FILA_CAST(Texture::Builder, builder)->samples(samples);
}

void FilaTextureBuilder_sampler(FilaTextureBuilder* builder, FilaTextureSampler sampler) {
    FILA_CAST(Texture::Builder, builder)->sampler(static_cast<Texture::Sampler>(sampler));
}

void FilaTextureBuilder_format(FilaTextureBuilder* builder, FilaTextureInternalFormat format) {
    FILA_CAST(Texture::Builder, builder)->format(static_cast<Texture::InternalFormat>(format));
}

void FilaTextureBuilder_usage(FilaTextureBuilder* builder, FilaTextureUsage usage) {
    FILA_CAST(Texture::Builder, builder)->usage(static_cast<Texture::Usage>(usage));
}

void FilaTextureBuilder_swizzle(FilaTextureBuilder* builder, FilaTextureSwizzle r, FilaTextureSwizzle g, FilaTextureSwizzle b, FilaTextureSwizzle a) {
    FILA_CAST(Texture::Builder, builder)->swizzle(
        static_cast<Texture::Swizzle>(r),
        static_cast<Texture::Swizzle>(g),
        static_cast<Texture::Swizzle>(b),
        static_cast<Texture::Swizzle>(a)
    );
}

void FilaTextureBuilder_importTexture(FilaTextureBuilder* builder, intptr_t id) {
    FILA_CAST(Texture::Builder, builder)->import(id);
}

void FilaTextureBuilder_external(FilaTextureBuilder* builder) {
    FILA_CAST(Texture::Builder, builder)->external();
}

// Texture Static Methods
bool FilaTexture_isTextureFormatSupported(FilaEngine* engine, FilaTextureInternalFormat format) {
    return Texture::isTextureFormatSupported(*FILA_CAST(Engine, engine), static_cast<Texture::InternalFormat>(format));
}

bool FilaTexture_isTextureFormatMipmappable(FilaEngine* engine, FilaTextureInternalFormat format) {
    return Texture::isTextureFormatMipmappable(*FILA_CAST(Engine, engine), static_cast<Texture::InternalFormat>(format));
}

bool FilaTexture_isTextureSwizzleSupported(FilaEngine* engine) {
    return Texture::isTextureSwizzleSupported(*FILA_CAST(Engine, engine));
}

size_t FilaTexture_getMaxTextureSize(FilaEngine* engine, FilaTextureSampler sampler) {
    return Texture::getMaxTextureSize(*FILA_CAST(Engine, engine), static_cast<Texture::Sampler>(sampler));
}

size_t FilaTexture_getMaxArrayTextureLayers(FilaEngine* engine) {
    return Texture::getMaxArrayTextureLayers(*FILA_CAST(Engine, engine));
}

bool FilaTexture_validatePixelFormatAndType(FilaTextureInternalFormat internalFormat, FilaPixelDataFormat format, FilaPixelDataType type) {
    return Texture::validatePixelFormatAndType(
        static_cast<Texture::InternalFormat>(internalFormat),
        static_cast<Texture::Format>(format),
        static_cast<Texture::Type>(type)
    );
}

// Texture instance methods
size_t FilaTexture_getWidth(const FilaTexture* texture, size_t level) {
    return FILA_CONST_CAST(Texture, texture)->getWidth(level);
}

size_t FilaTexture_getHeight(const FilaTexture* texture, size_t level) {
    return FILA_CONST_CAST(Texture, texture)->getHeight(level);
}

size_t FilaTexture_getDepth(const FilaTexture* texture, size_t level) {
    return FILA_CONST_CAST(Texture, texture)->getDepth(level);
}

size_t FilaTexture_getLevels(const FilaTexture* texture) {
    return FILA_CONST_CAST(Texture, texture)->getLevels();
}

FilaTextureSampler FilaTexture_getTarget(const FilaTexture* texture) {
    return static_cast<FilaTextureSampler>(FILA_CONST_CAST(Texture, texture)->getTarget());
}

FilaTextureInternalFormat FilaTexture_getFormat(const FilaTexture* texture) {
    return static_cast<FilaTextureInternalFormat>(FILA_CONST_CAST(Texture, texture)->getFormat());
}

void FilaTexture_setImage(FilaTexture* texture, FilaEngine* engine, size_t level, uint32_t xoffset, uint32_t yoffset, uint32_t zoffset, uint32_t width, uint32_t height, uint32_t depth, void* buffer, size_t sizeInBytes, FilaPixelDataFormat format, FilaPixelDataType type, uint8_t alignment, uint32_t left, uint32_t top, uint32_t stride, FilaCallbackHandler* handler, FilaBufferCallback callback, void* userData) {
    PixelBufferDescriptor desc(buffer, sizeInBytes, 
        static_cast<backend::PixelDataFormat>(format),
        static_cast<backend::PixelDataType>(type),
        alignment, left, top, stride,
        reinterpret_cast<backend::CallbackHandler*>(handler),
        [callback, userData](void* buf, size_t size, void*) {
            if (callback) {
                callback(buf, size, userData);
            }
        });
    FILA_CAST(Texture, texture)->setImage(*FILA_CAST(Engine, engine), level, xoffset, yoffset, zoffset, width, height, depth, std::move(desc));
}

void FilaTexture_setImageCubemap(FilaTexture* texture, FilaEngine* engine, size_t level, void* buffer, size_t sizeInBytes, FilaPixelDataFormat format, FilaPixelDataType type, uint8_t alignment, uint32_t left, uint32_t top, uint32_t stride, const FilaTextureFaceOffsets* faceOffsets, FilaCallbackHandler* handler, FilaBufferCallback callback, void* userData) {
    PixelBufferDescriptor desc(buffer, sizeInBytes, 
        static_cast<backend::PixelDataFormat>(format),
        static_cast<backend::PixelDataType>(type),
        alignment, left, top, stride,
        reinterpret_cast<backend::CallbackHandler*>(handler),
        [callback, userData](void* buf, size_t size, void*) {
            if (callback) {
                callback(buf, size, userData);
            }
        });
    
    Texture::FaceOffsets offsets;
    offsets.px = faceOffsets->px;
    offsets.nx = faceOffsets->nx;
    offsets.py = faceOffsets->py;
    offsets.ny = faceOffsets->ny;
    offsets.pz = faceOffsets->pz;
    offsets.nz = faceOffsets->nz;

#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
    FILA_CAST(Texture, texture)->setImage(*FILA_CAST(Engine, engine), level, std::move(desc), offsets);
#pragma clang diagnostic pop
}

void FilaTexture_setExternalImage(FilaTexture* texture, FilaEngine* engine, void* image) {
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
    FILA_CAST(Texture, texture)->setExternalImage(*FILA_CAST(Engine, engine), image);
#pragma clang diagnostic pop
}

void FilaTexture_setExternalStream(FilaTexture* texture, FilaEngine* engine, FilaStream* stream) {
    FILA_CAST(Texture, texture)->setExternalStream(*FILA_CAST(Engine, engine), reinterpret_cast<Stream*>(stream));
}

void FilaTexture_generateMipmaps(const FilaTexture* texture, FilaEngine* engine) {
    FILA_CONST_CAST(Texture, texture)->generateMipmaps(*FILA_CAST(Engine, engine));
}

} // extern "C"
