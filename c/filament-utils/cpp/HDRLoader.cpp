#include <filament/Engine.h>
#include <filament/Texture.h>
#include <image/LinearImage.h>
#include <imageio/ImageDecoder.h>
#include <utils/Log.h>

#include <sstream>
#include <string>

#include "../../filament/cpp/FilaCommon.h"
#include "../c/HDRLoader.h"

using namespace filament;
using namespace image;
using namespace utils;

extern "C" {

FilaTexture *FilaHDRLoader_createTexture(FilaEngine *engine, const void *buffer,
                                         size_t size, int32_t internalFormat) {
  Engine *e = FILA_CAST(Engine, engine);

  // Direct from JNI pattern
  std::string ins((char const *)buffer, size);
  std::istringstream in(ins);

  // ImageDecoder::decode returns a LinearImage by value
  LinearImage *image = new LinearImage(ImageDecoder::decode(in, "memory.hdr"));

  if (image->getChannels() != 3) {
    delete image;
    return nullptr;
  }

  Texture *texture =
      Texture::Builder()
          .width(image->getWidth())
          .height(image->getHeight())
          .levels(0xff)
          .sampler(Texture::Sampler::SAMPLER_2D)
          .usage(Texture::Usage::DEFAULT | Texture::Usage::GEN_MIPMAPPABLE)
          .format(static_cast<Texture::InternalFormat>(internalFormat))
          .build(*e);

  if (texture == nullptr) {
    delete image;
    return nullptr;
  }

  // Free callback for LinearImage
  Texture::PixelBufferDescriptor::Callback freeCallback = [](void *buf, size_t,
                                                             void *userdata) {
    delete reinterpret_cast<LinearImage *>(userdata);
  };

  Texture::PixelBufferDescriptor pbd(
      image->getPixelRef(),
      image->getWidth() * image->getHeight() * 3 * sizeof(float),
      Texture::PixelBufferDescriptor::PixelDataFormat::RGB,
      Texture::PixelBufferDescriptor::PixelDataType::FLOAT, freeCallback,
      image);

  texture->setImage(*e, 0, std::move(pbd));
  texture->generateMipmaps(*e);

  return reinterpret_cast<FilaTexture *>(texture);
}

} // extern "C"
