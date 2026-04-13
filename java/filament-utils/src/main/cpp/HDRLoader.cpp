#include <jni.h>
#include <filament/Engine.h>
#include <filament/Texture.h>
#include <image/LinearImage.h>
#include <imageio-lite/ImageDecoder.h>
#include <utils/EntityManager.h>
#include <sstream>
#include <string>

using namespace filament;
using namespace image;

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_utils_HDRLoader_nCreateHDRTexture(JNIEnv* env, jclass, jlong nativeEngine, jobject buffer, jint remaining, jint format) {
    Engine* engine = (Engine*) nativeEngine;
    void* data = env->GetDirectBufferAddress(buffer);
    std::string ins((char const*) data, (size_t) remaining);
    std::istringstream in(ins);

    LinearImage image = imageio_lite::ImageDecoder::decode(in, "memory.hdr");
    if (!image.isValid()) return 0;

    uint32_t width = image.getWidth();
    uint32_t height = image.getHeight();
    uint32_t channels = image.getChannels();
    size_t sizeInBytes = (size_t) width * height * channels * sizeof(float);

    Texture* texture = Texture::Builder()
            .width(width)
            .height(height)
            .levels(1)
            .sampler(Texture::Sampler::SAMPLER_2D)
            .format((Texture::InternalFormat) format)
            .build(*engine);

    auto image_ptr = new LinearImage(std::move(image));
    Texture::PixelBufferDescriptor desc(
            image_ptr->getPixelRef(), sizeInBytes,
            Texture::Format::RGB, Texture::Type::FLOAT,
            [](void*, size_t, void* user) {
                delete reinterpret_cast<LinearImage*>(user);
            }, image_ptr);

    texture->setImage(*engine, 0, std::move(desc));

    return (jlong) texture;
}
