#include <jni.h>
#include <filament/Engine.h>
#include <filament/Texture.h>
#include <image/HDRDecoder.h>
#include <utils/EntityManager.h>

using namespace filament;
using namespace image;

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_utils_HDRLoader_nCreateHDRTexture(JNIEnv* env, jclass, jlong nativeEngine, jobject buffer, jint remaining, jint format) {
    Engine* engine = (Engine*) nativeEngine;
    void* data = env->GetDirectBufferAddress(buffer);
    if (!data) return 0;

    HDRDecoder decoder(data, (size_t) remaining);
    if (!decoder.isValid()) return 0;

    LinearImage image = decoder.decode();
    if (!image.isValid()) return 0;

    Texture* texture = Texture::Builder()
            .width(image.getWidth())
            .height(image.getHeight())
            .levels(1)
            .sampler(Texture::Sampler::SAMPLER_2D)
            .format((Texture::InternalFormat) format)
            .build(*engine);

    auto image_ptr = new LinearImage(std::move(image));
    Texture::PixelBufferDescriptor desc(
            image_ptr->getPixelRef(), image_ptr->sizeInBytes(),
            Texture::Format::RGB, Texture::Type::FLOAT,
            [](void*, size_t, void* user) {
                delete reinterpret_cast<LinearImage*>(user);
            }, image_ptr);

    texture->setImage(*engine, 0, std::move(desc));

    return (jlong) texture;
}
