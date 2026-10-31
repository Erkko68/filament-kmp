#include "../generated/Includes.hpp"
#include "FilaUtilsManual.h"

#include <cstddef>

using namespace filament;

extern "C" {
float* stbi_loadf_from_memory(const unsigned char* buffer, int len, int* x, int* y, int* channels_in_file, int desired_channels);
unsigned char* stbi_load_from_memory(const unsigned char* buffer, int len, int* x, int* y, int* channels_in_file, int desired_channels);
void stbi_image_free(void* retval_from_stbi_load);
}

namespace {

struct DecodedImage {
    void* pixels;
    int width, height;
    size_t size;
    Texture::Format format;
    Texture::Type type;
};

} // namespace

extern "C" {

void* FilaHDRLoader_decode(const void* buffer, uint32_t size) {
    int width = 0, height = 0, channels = 0;
    float* data = stbi_loadf_from_memory(static_cast<const unsigned char*>(buffer), int(size), &width, &height, &channels, 0);
    if (!data) return nullptr;
    return new DecodedImage{data, width, height, size_t(width) * height * channels * sizeof(float),
            channels == 3 ? Texture::Format::RGB : Texture::Format::RGBA, Texture::Type::FLOAT};
}

void* FilaTextureLoader_decode(const void* buffer, uint32_t size) {
    // Forcing 4 channels covers grayscale, palette, RGB and RGBA alike.
    int width = 0, height = 0, channels = 0;
    unsigned char* data = stbi_load_from_memory(static_cast<const unsigned char*>(buffer), int(size), &width, &height, &channels, 4);
    if (!data) return nullptr;
    return new DecodedImage{data, width, height, size_t(width) * height * 4, Texture::Format::RGBA, Texture::Type::UBYTE};
}

void FilaDecodedImage_destroy(void* image) {
    auto* decoded = static_cast<DecodedImage*>(image);
    stbi_image_free(decoded->pixels);
    delete decoded;
}

FilaTexture* FilaDecodedImage_createTexture(void* image, FilaEngine* engine, FilaTextureFormat internalFormat) {
    auto* decoded = static_cast<DecodedImage*>(image);
    Texture* texture = Texture::Builder()
            .width(decoded->width)
            .height(decoded->height)
            .levels(0xff)
            .sampler(Texture::Sampler::SAMPLER_2D)
            .format(Texture::InternalFormat(internalFormat))
            .usage(Texture::Usage::DEFAULT | Texture::Usage::GEN_MIPMAPPABLE)
            .build(*fila::cpp(engine));
    if (!texture) {
        FilaDecodedImage_destroy(image);
        return nullptr;
    }
    // setImage takes the pixels and frees them once uploaded.
    texture->setImage(*fila::cpp(engine), 0, Texture::PixelBufferDescriptor(decoded->pixels, decoded->size, decoded->format, decoded->type,
            [](void* buf, size_t, void*) { stbi_image_free(buf); }, nullptr));
    texture->generateMipmaps(*fila::cpp(engine));
    delete decoded;
    return fila::c(texture);
}

} // extern "C"
