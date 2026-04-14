#include "common/NioUtils.h"
#include <filament/Engine.h>
#include <filament/Texture.h>
#include <image/LinearImage.h>
#include <imageio-lite/ImageDecoder.h>
#include <jni.h>
#include <sstream>
#include <string>

using namespace filament;
using namespace image;

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_utils_TextureLoader_nLoadTexture(
    JNIEnv *env, jclass, jlong nativeEngine, jobject javaBuffer, jint remaining,
    jint type) {
  Engine *engine = (Engine *)nativeEngine;
  AutoBuffer buffer(env, javaBuffer, remaining);

  std::string data((char const *)buffer.getData(), buffer.getSize());
  std::istringstream in(data);

  // Use ImageDecoder to decode PNG/JPG/etc.
  // Note: imageio_lite returns a LinearImage.
  LinearImage image = imageio_lite::ImageDecoder::decode(in, "memory.img");

  if (!image.isValid())
    return 0;

  uint32_t width = image.getWidth();
  uint32_t height = image.getHeight();
  uint32_t channels = image.getChannels();

  // Determine formats
  Texture::InternalFormat internalFormat;
  Texture::Format format;
  if (channels == 3) {
    internalFormat = (type == 0) ? Texture::InternalFormat::SRGB8
                                 : Texture::InternalFormat::RGB8;
    format = Texture::Format::RGB;
  } else {
    internalFormat = (type == 0) ? Texture::InternalFormat::SRGB8_A8
                                 : Texture::InternalFormat::RGBA8;
    format = Texture::Format::RGBA;
  }

  Texture *texture =
      Texture::Builder()
          .width(width)
          .height(height)
          .levels(0xff) // generate mipmaps
          .sampler(Texture::Sampler::SAMPLER_2D)
          .format(internalFormat)
          .usage(Texture::Usage::DEFAULT | Texture::Usage::GEN_MIPMAPPABLE)
          .build(*engine);

  size_t sizeInBytes = (size_t)width * height * channels * sizeof(float);
  auto image_ptr = new LinearImage(std::move(image));

  Texture::PixelBufferDescriptor desc(
      image_ptr->getPixelRef(), sizeInBytes, format, Texture::Type::FLOAT,
      [](void *, size_t, void *user) {
        delete reinterpret_cast<LinearImage *>(user);
      },
      image_ptr);

  texture->setImage(*engine, 0, std::move(desc));
  texture->generateMipmaps(*engine);

  return (jlong)texture;
}
