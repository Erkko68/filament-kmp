#include <filament/Engine.h>
#include <filament/Texture.h>
#include <filament/Stream.h>

#include "filament/Texture.h"
#include "filament/BufferDescriptor.h"

namespace {
using TextureBuilder = filament::Texture::Builder;

filament::Texture::Sampler toSampler(FilaTextureSampler sampler) {
    switch (sampler) {
        case FILA_TEXTURE_SAMPLER_EXTERNAL:
            return filament::Texture::Sampler::SAMPLER_EXTERNAL;
        case FILA_TEXTURE_SAMPLER_CUBEMAP:
            return filament::Texture::Sampler::SAMPLER_CUBEMAP;
        case FILA_TEXTURE_SAMPLER_2D:
        default:
            return filament::Texture::Sampler::SAMPLER_2D;
    }
}

filament::Texture::InternalFormat toInternalFormat(FilaTextureFormat format) {
    switch (format) {
        case FILA_TEXTURE_FORMAT_RGBA8:
        default:
            return filament::Texture::InternalFormat::RGBA8;
    }
}

filament::Texture::Usage toUsage(uint16_t usage) {
    return static_cast<filament::Texture::Usage>(usage);
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
    auto cppTexture = reinterpret_cast<filament::Texture*>(texture);
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppBuffer = reinterpret_cast<filament::backend::PixelBufferDescriptor*>(buffer->impl);
    cppTexture->setImage(*cppEngine, level, xoffset, yoffset, zoffset, width, height, depth, std::move(*cppBuffer));
}

void FilaTexture_setImage2D(FilaTexture* texture, FilaEngine* engine, size_t level, FilaPixelBufferDescriptor* buffer) {
    if (!texture || !engine || !buffer) {
        return;
    }
    auto cppTexture = reinterpret_cast<filament::Texture*>(texture);
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    auto cppBuffer = reinterpret_cast<filament::backend::PixelBufferDescriptor*>(buffer->impl);
    cppTexture->setImage(*cppEngine, level, std::move(*cppBuffer));
}

void FilaTexture_generateMipmaps(FilaTexture* texture, FilaEngine* engine) {
    if (!texture || !engine) {
        return;
    }
    auto cppTexture = reinterpret_cast<filament::Texture*>(texture);
    auto cppEngine = reinterpret_cast<filament::Engine*>(engine);
    cppTexture->generateMipmaps(*cppEngine);
}

} // extern "C"

