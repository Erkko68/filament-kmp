#include <jni.h>
#include <filament/Texture.h>
#include <filament/Engine.h>
#include <backend/PixelBufferDescriptor.h>

#include "common/NioUtils.h"
#include "common/CallbackUtils.h"

using namespace filament;

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_Texture_00024Builder_nCreateBuilder(JNIEnv* env, jclass) {
    return (jlong) new Texture::Builder();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Texture_00024Builder_nDestroyBuilder(JNIEnv* env, jclass, jlong nativeBuilder) {
    delete (Texture::Builder*) nativeBuilder;
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Texture_00024Builder_nBuilderWidth(JNIEnv* env, jclass, jlong nativeBuilder, jint width) {
    ((Texture::Builder*) nativeBuilder)->width((uint32_t) width);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Texture_00024Builder_nBuilderHeight(JNIEnv* env, jclass, jlong nativeBuilder, jint height) {
    ((Texture::Builder*) nativeBuilder)->height((uint32_t) height);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Texture_00024Builder_nBuilderDepth(JNIEnv* env, jclass, jlong nativeBuilder, jint depth) {
    ((Texture::Builder*) nativeBuilder)->depth((uint32_t) depth);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Texture_00024Builder_nBuilderLevels(JNIEnv* env, jclass, jlong nativeBuilder, jint levels) {
    ((Texture::Builder*) nativeBuilder)->levels((uint8_t) levels);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Texture_00024Builder_nBuilderSampler(JNIEnv* env, jclass, jlong nativeBuilder, jint sampler) {
    ((Texture::Builder*) nativeBuilder)->sampler((Texture::Sampler) sampler);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Texture_00024Builder_nBuilderFormat(JNIEnv* env, jclass, jlong nativeBuilder, jint format) {
    ((Texture::Builder*) nativeBuilder)->format((Texture::InternalFormat) format);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Texture_00024Builder_nBuilderUsage(JNIEnv* env, jclass, jlong nativeBuilder, jint usage) {
    ((Texture::Builder*) nativeBuilder)->usage((uint32_t) usage);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_Texture_00024Builder_nBuilderBuild(JNIEnv* env, jclass, jlong nativeBuilder, jlong nativeEngine) {
    return (jlong) ((Texture::Builder*) nativeBuilder)->build(*(Engine*) nativeEngine);
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_Texture_nGetWidth(JNIEnv* env, jclass, jlong nativeTexture, jint level) {
    return (jint) ((Texture*) nativeTexture)->getWidth((uint8_t) level);
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_Texture_nGetHeight(JNIEnv* env, jclass, jlong nativeTexture, jint level) {
    return (jint) ((Texture*) nativeTexture)->getHeight((uint8_t) level);
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_Texture_nGetDepth(JNIEnv* env, jclass, jlong nativeTexture, jint level) {
    return (jint) ((Texture*) nativeTexture)->getDepth((uint8_t) level);
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_Texture_nGetLevels(JNIEnv* env, jclass, jlong nativeTexture) {
    return (jint) ((Texture*) nativeTexture)->getLevels();
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_Texture_setImage(JNIEnv* env, jclass, jlong nativeTexture, jlong nativeEngine, jint level,
                                                  jint xoffset, jint yoffset, jint zoffset, jint width, jint height, jint depth,
                                                  jobject storage, jint remaining,
                                                  jint left, jint top, jint type, jint alignment, jint stride, jint format,
                                                  jobject handler, jobject callback) {
    Texture* texture = (Texture*) nativeTexture;
    Engine* engine = (Engine*) nativeEngine;
    
    void* data = NioUtils::getBufferAddress(env, storage);
    if (!data) return -1;

    backend::PixelBufferDescriptor desc(data, (size_t) remaining, (backend::PixelDataFormat) format, (backend::PixelDataType) type, (uint8_t) alignment, (uint32_t) left, (uint32_t) top, (uint32_t) stride, CallbackUtils::createCallback(env, handler, callback));
    texture->setImage(*engine, (uint8_t) level, (uint32_t) xoffset, (uint32_t) yoffset, (uint32_t) zoffset, (uint32_t) width, (uint32_t) height, (uint32_t) depth, std::move(desc));
    return 0;
}
