#include "TextureLoader.h"

#include <filament/Engine.h>
#include <filament/Texture.h>
#include <image/LinearImage.h>
#include <imageio-lite/ImageDecoder.h>
#include <sstream>
#include <string>

using namespace filament;
using namespace image;

extern "C" {

FilaTexture* FilaTextureLoader_loadTexture(FilaEngine* engine, const void* buffer, size_t size, bool srgb) {
    Engine* nativeEngine = reinterpret_cast<Engine*>(engine);
    
    std::string data(reinterpret_cast<const char*>(buffer), size);
    std::istringstream in(data);

    // imageio_lite returns a LinearImage.
    LinearImage image = imageio_lite::ImageDecoder::decode(in, "memory.img");
    
    if (!image.isValid()) return nullptr;

    uint32_t width = image.getWidth();
    uint32_t height = image.getHeight();
    uint32_t channels = image.getChannels();
    
    Texture::InternalFormat internalFormat;
    Texture::Format format;
    if (channels == 3) {
        internalFormat = srgb ? Texture::InternalFormat::SRGB8 : Texture::InternalFormat::RGB8;
        format = Texture::Format::RGB;
    } else {
        internalFormat = srgb ? Texture::InternalFormat::SRGB8_A8 : Texture::InternalFormat::RGBA8;
        format = Texture::Format::RGBA;
    }

    Texture* texture = Texture::Builder()
            .width(width)
            .height(height)
            .levels(0xff) // generate mipmaps
            .sampler(Texture::Sampler::SAMPLER_2D)
            .format(internalFormat)
            .usage(Texture::Usage::DEFAULT | Texture::Usage::GEN_MIPMAPPABLE)
            .build(*nativeEngine);

    size_t sizeInBytes = (size_t) width * height * channels * sizeof(float);
    auto image_ptr = new LinearImage(std::move(image));
    
    Texture::PixelBufferDescriptor desc(
            image_ptr->getPixelRef(), sizeInBytes,
            format, Texture::Type::FLOAT,
            [](void*, size_t, void* user) {
                delete reinterpret_cast<LinearImage*>(user);
            }, image_ptr);

    texture->setImage(*nativeEngine, 0, std::move(desc));
    texture->generateMipmaps(*nativeEngine);

    return reinterpret_cast<FilaTexture*>(texture);
}

}
