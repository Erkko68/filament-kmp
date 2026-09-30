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

// A mipmapped 2D texture holding stb's pixels; frees them once uploaded.
Texture* upload(Engine& engine, void* pixels, int width, int height, Texture::InternalFormat internalFormat,
        size_t size, Texture::Format format, Texture::Type type) {
    Texture* texture = Texture::Builder()
            .width(width)
            .height(height)
            .levels(0xff)
            .sampler(Texture::Sampler::SAMPLER_2D)
            .format(internalFormat)
            .usage(Texture::Usage::DEFAULT | Texture::Usage::GEN_MIPMAPPABLE)
            .build(engine);
    if (!texture) {
        stbi_image_free(pixels);
        return nullptr;
    }
    texture->setImage(engine, 0, Texture::PixelBufferDescriptor(pixels, size, format, type,
            [](void* buf, size_t, void*) { stbi_image_free(buf); }, nullptr));
    texture->generateMipmaps(engine);
    return texture;
}

} // namespace

extern "C" {

FilaTexture* FilaHDRLoader_createTexture(FilaEngine* engine, const void* buffer, uint32_t size, FilaTextureFormat internalFormat) {
    int width = 0, height = 0, channels = 0;
    float* data = stbi_loadf_from_memory(static_cast<const unsigned char*>(buffer), int(size), &width, &height, &channels, 0);
    if (!data) return nullptr;
    return fila::c(upload(*fila::cpp(engine), data, width, height, Texture::InternalFormat(internalFormat),
            size_t(width) * height * channels * sizeof(float),
            channels == 3 ? Texture::Format::RGB : Texture::Format::RGBA, Texture::Type::FLOAT));
}

FilaTexture* FilaTextureLoader_loadTexture(FilaEngine* engine, const void* buffer, uint32_t size, bool srgb) {
    // Forcing 4 channels covers grayscale, palette, RGB and RGBA alike.
    int width = 0, height = 0, channels = 0;
    unsigned char* data = stbi_load_from_memory(static_cast<const unsigned char*>(buffer), int(size), &width, &height, &channels, 4);
    if (!data) return nullptr;
    return fila::c(upload(*fila::cpp(engine), data, width, height,
            srgb ? Texture::InternalFormat::SRGB8_A8 : Texture::InternalFormat::RGBA8,
            size_t(width) * height * 4, Texture::Format::RGBA, Texture::Type::UBYTE));
}

} // extern "C"
