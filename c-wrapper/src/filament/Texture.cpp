#include <filament/Engine.h>
#include <filament/Texture.h>
#include <filament/Stream.h>

#include "../../include/filament/Texture.h"
#include "../../include/filament/BufferDescriptor.h"

namespace {
using TextureBuilder = filament::Texture::Builder;

filament::Texture::Sampler toSampler(FilaTextureSampler sampler) {
    switch (sampler) {
        case FILA_TEXTURE_SAMPLER_CUBEMAP_ARRAY:
            return filament::Texture::Sampler::SAMPLER_CUBEMAP_ARRAY;
        case FILA_TEXTURE_SAMPLER_3D:
            return filament::Texture::Sampler::SAMPLER_3D;
        case FILA_TEXTURE_SAMPLER_2D_ARRAY:
            return filament::Texture::Sampler::SAMPLER_2D_ARRAY;
        case FILA_TEXTURE_SAMPLER_EXTERNAL:
            return filament::Texture::Sampler::SAMPLER_EXTERNAL;
        case FILA_TEXTURE_SAMPLER_CUBEMAP:
            return filament::Texture::Sampler::SAMPLER_CUBEMAP;
        case FILA_TEXTURE_SAMPLER_2D:
        default:
            return filament::Texture::Sampler::SAMPLER_2D;
    }
}

FilaTextureSampler toTextureSampler(filament::Texture::Sampler sampler) {
    return static_cast<FilaTextureSampler>(sampler);
}

filament::Texture::InternalFormat toInternalFormat(FilaTextureFormat format) {
    return static_cast<filament::Texture::InternalFormat>(format);
}

FilaTextureFormat toTextureFormat(filament::Texture::InternalFormat format) {
    return static_cast<FilaTextureFormat>(format);
}

filament::Texture::Usage toUsage(uint16_t usage) {
    return static_cast<filament::Texture::Usage>(usage);
}

filament::Texture::Swizzle toSwizzle(FilaTextureSwizzle swizzle) {
    return static_cast<filament::Texture::Swizzle>(swizzle);
}
} // namespace

extern "C" {

FilaTextureBuilder* FilaTextureBuilder_create(void) {
    auto builder = new TextureBuilder();
    return reinterpret_cast<FilaTextureBuilder*>(builder);
}

void FilaTextureBuilder_destroy(FilaTextureBuilder* builder) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<TextureBuilder*>(builder);
    delete cppBuilder;
}

void FilaTextureBuilder_width(FilaTextureBuilder* builder, uint32_t width) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<TextureBuilder*>(builder);
    cppBuilder->width(width);
}

void FilaTextureBuilder_height(FilaTextureBuilder* builder, uint32_t height) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<TextureBuilder*>(builder);
    cppBuilder->height(height);
}

void FilaTextureBuilder_depth(FilaTextureBuilder* builder, uint32_t depth) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<TextureBuilder*>(builder);
    cppBuilder->depth(depth);
}

void FilaTextureBuilder_levels(FilaTextureBuilder* builder, uint8_t levels) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<TextureBuilder*>(builder);
    cppBuilder->levels(levels);
}

void FilaTextureBuilder_samples(FilaTextureBuilder* builder, uint8_t samples) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<TextureBuilder*>(builder);
    cppBuilder->samples(samples);
}

void FilaTextureBuilder_sampler(FilaTextureBuilder* builder, FilaTextureSampler sampler) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<TextureBuilder*>(builder);
    cppBuilder->sampler(toSampler(sampler));
}

void FilaTextureBuilder_format(FilaTextureBuilder* builder, FilaTextureFormat format) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<TextureBuilder*>(builder);
    cppBuilder->format(toInternalFormat(format));
}

void FilaTextureBuilder_usage(FilaTextureBuilder* builder, uint16_t usage) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<TextureBuilder*>(builder);
    cppBuilder->usage(toUsage(usage));
}

void FilaTextureBuilder_swizzle(
        FilaTextureBuilder* builder, FilaTextureSwizzle r, FilaTextureSwizzle g,
        FilaTextureSwizzle b, FilaTextureSwizzle a) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<TextureBuilder*>(builder);
    cppBuilder->swizzle(toSwizzle(r), toSwizzle(g), toSwizzle(b), toSwizzle(a));
}

void FilaTextureBuilder_external(FilaTextureBuilder* builder) {
    if (!builder) {
        return;
    }
    auto cppBuilder = reinterpret_cast<TextureBuilder*>(builder);
    cppBuilder->external();
}

FilaTexture* FilaTextureBuilder_build(FilaTextureBuilder* builder, FilaEngine* engine) {
    if (!builder || !engine) {
        return nullptr;
    }
    auto cppBuilder = reinterpret_cast<TextureBuilder*>(builder);
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return reinterpret_cast<FilaTexture*>(cppBuilder->build(*cppEngine));
}

size_t FilaTexture_getWidth(const FilaTexture* texture, size_t level) {
    if (!texture) {
        return 0;
    }
    auto cppTexture = reinterpret_cast<const filament::Texture*>(texture);
    return cppTexture->getWidth(level);
}

size_t FilaTexture_getHeight(const FilaTexture* texture, size_t level) {
    if (!texture) {
        return 0;
    }
    auto cppTexture = reinterpret_cast<const filament::Texture*>(texture);
    return cppTexture->getHeight(level);
}

size_t FilaTexture_getDepth(const FilaTexture* texture, size_t level) {
    if (!texture) {
        return 0;
    }
    auto cppTexture = reinterpret_cast<const filament::Texture*>(texture);
    return cppTexture->getDepth(level);
}

size_t FilaTexture_getLevels(const FilaTexture* texture) {
    if (!texture) {
        return 0;
    }
    auto cppTexture = reinterpret_cast<const filament::Texture*>(texture);
    return cppTexture->getLevels();
}

void FilaTexture_setExternalStream(FilaTexture* texture, FilaEngine* engine, FilaStream* stream) {
    if (!texture || !engine) {
        return;
    }
    auto cppTexture = reinterpret_cast<filament::Texture*>(texture);
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppStream = reinterpret_cast<filament::Stream*>(stream);
    cppTexture->setExternalStream(*cppEngine, cppStream);
}

void FilaTexture_setImage(FilaTexture* texture, FilaEngine* engine, size_t level, uint32_t xoffset, uint32_t yoffset, uint32_t zoffset, uint32_t width, uint32_t height, uint32_t depth, FilaPixelBufferDescriptor* buffer) {
    if (!texture || !engine || !buffer) {
        return;
    }
    if (!buffer->impl) {
        return;
    }
    auto cppTexture = reinterpret_cast<filament::Texture*>(texture);
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppBuffer = reinterpret_cast<filament::backend::PixelBufferDescriptor*>(buffer->impl);
    cppTexture->setImage(*cppEngine, level, xoffset, yoffset, zoffset, width, height, depth, std::move(*cppBuffer));
    delete cppBuffer;
    buffer->impl = nullptr;
    buffer->callback = nullptr;
    buffer->user = nullptr;
    buffer->handler = nullptr;
    buffer->consumed = true;
}

void FilaTexture_setImage2D(FilaTexture* texture, FilaEngine* engine, size_t level, FilaPixelBufferDescriptor* buffer) {
    if (!texture || !engine || !buffer) {
        return;
    }
    if (!buffer->impl) {
        return;
    }
    auto cppTexture = reinterpret_cast<filament::Texture*>(texture);
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppBuffer = reinterpret_cast<filament::backend::PixelBufferDescriptor*>(buffer->impl);
    cppTexture->setImage(*cppEngine, level, std::move(*cppBuffer));
    delete cppBuffer;
    buffer->impl = nullptr;
    buffer->callback = nullptr;
    buffer->user = nullptr;
    buffer->handler = nullptr;
    buffer->consumed = true;
}

void FilaTexture_generateMipmaps(FilaTexture* texture, FilaEngine* engine) {
    if (!texture || !engine) {
        return;
    }
    auto cppTexture = reinterpret_cast<filament::Texture*>(texture);
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    cppTexture->generateMipmaps(*cppEngine);
}

FilaTextureSampler FilaTexture_getTarget(const FilaTexture* texture) {
    if (!texture) {
        return FILA_TEXTURE_SAMPLER_2D;
    }
    auto cppTexture = reinterpret_cast<const filament::Texture*>(texture);
    return toTextureSampler(cppTexture->getTarget());
}

FilaTextureFormat FilaTexture_getFormat(const FilaTexture* texture) {
    if (!texture) {
        return FILA_TEXTURE_FORMAT_RGBA8;
    }
    auto cppTexture = reinterpret_cast<const filament::Texture*>(texture);
    return toTextureFormat(cppTexture->getFormat());
}

bool FilaTexture_isCreationComplete(const FilaTexture* texture) {
    if (!texture) {
        return false;
    }
    auto cppTexture = reinterpret_cast<const filament::Texture*>(texture);
    return cppTexture->isCreationComplete();
}

bool FilaTexture_isTextureFormatSupported(FilaEngine* engine, FilaTextureFormat format) {
    if (!engine) {
        return false;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return filament::Texture::isTextureFormatSupported(*cppEngine, toInternalFormat(format));
}

bool FilaTexture_isTextureFormatMipmappable(FilaEngine* engine, FilaTextureFormat format) {
    if (!engine) {
        return false;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return filament::Texture::isTextureFormatMipmappable(*cppEngine, toInternalFormat(format));
}

bool FilaTexture_isTextureFormatCompressed(FilaTextureFormat format) {
    return filament::Texture::isTextureFormatCompressed(toInternalFormat(format));
}

bool FilaTexture_isProtectedTexturesSupported(FilaEngine* engine) {
    if (!engine) {
        return false;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return filament::Texture::isProtectedTexturesSupported(*cppEngine);
}

bool FilaTexture_isTextureSwizzleSupported(FilaEngine* engine) {
    if (!engine) {
        return false;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return filament::Texture::isTextureSwizzleSupported(*cppEngine);
}

size_t FilaTexture_computeTextureDataSize(
        FilaTexturePixelDataFormat format, FilaTexturePixelDataType type, size_t stride,
        size_t height, size_t alignment) {
    return filament::Texture::computeTextureDataSize(
            static_cast<filament::Texture::Format>(format),
            static_cast<filament::Texture::Type>(type),
            stride, height, alignment);
}

bool FilaTexture_validatePixelFormatAndType(
        FilaTextureFormat internalFormat, FilaTexturePixelDataFormat format,
        FilaTexturePixelDataType type) {
    return filament::Texture::validatePixelFormatAndType(
            toInternalFormat(internalFormat),
            static_cast<filament::Texture::Format>(format),
            static_cast<filament::Texture::Type>(type));
}

size_t FilaTexture_getMaxTextureSize(FilaEngine* engine, FilaTextureSampler sampler) {
    if (!engine) {
        return 0;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return filament::Texture::getMaxTextureSize(*cppEngine, toSampler(sampler));
}

size_t FilaTexture_getMaxArrayTextureLayers(FilaEngine* engine) {
    if (!engine) {
        return 0;
    }
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    return filament::Texture::getMaxArrayTextureLayers(*cppEngine);
}

} // extern "C"

