/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <jni.h>
#include <filament/Texture.h>
#include <filament/Engine.h>
#include <backend/PixelBufferDescriptor.h>
#include <algorithm>
#include "common/NioUtils.h"
#include "common/CallbackUtils.h"
#include <filament-generatePrefilterMipmap/generatePrefilterMipmap.h>
#if defined(__APPLE__)
#include <CoreFoundation/CoreFoundation.h>
#endif

using namespace filament;

static size_t getTextureDataSize(const Texture* texture, size_t level, Texture::Format format, Texture::Type type, size_t stride, size_t height, size_t alignment) {
    stride = stride == 0 ? texture->getWidth(level) : stride;
    height = height == 0 ? texture->getHeight(level) : height;
    return Texture::computeTextureDataSize(format, type, stride, height, alignment);
}

extern "C" {

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Texture_nCreateBuilder(JNIEnv*, jclass) {
    return (jlong) new Texture::Builder();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Texture_nDestroyBuilder(JNIEnv*, jclass, jlong nativeBuilder) {
    delete (Texture::Builder*) nativeBuilder;
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Texture_nBuilderWidth(JNIEnv*, jclass, jlong nativeBuilder, jint width) {
    ((Texture::Builder*) nativeBuilder)->width(width);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Texture_nBuilderHeight(JNIEnv*, jclass, jlong nativeBuilder, jint height) {
    ((Texture::Builder*) nativeBuilder)->height(height);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Texture_nBuilderDepth(JNIEnv*, jclass, jlong nativeBuilder, jint depth) {
    ((Texture::Builder*) nativeBuilder)->depth(depth);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Texture_nBuilderLevels(JNIEnv*, jclass, jlong nativeBuilder, jint levels) {
    ((Texture::Builder*) nativeBuilder)->levels(levels);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Texture_nBuilderSampler(JNIEnv*, jclass, jlong nativeBuilder, jint sampler) {
    ((Texture::Builder*) nativeBuilder)->sampler((Texture::Sampler) sampler);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Texture_nBuilderFormat(JNIEnv*, jclass, jlong nativeBuilder, jint format) {
    ((Texture::Builder*) nativeBuilder)->format((Texture::InternalFormat) format);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Texture_nBuilderUsage(JNIEnv*, jclass, jlong nativeBuilder, jint usage) {
    ((Texture::Builder*) nativeBuilder)->usage((Texture::Usage) usage);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Texture_nBuilderSwizzle(JNIEnv*, jclass, jlong nativeBuilder, jint r, jint g, jint b, jint a) {
    ((Texture::Builder*) nativeBuilder)->swizzle((Texture::Swizzle) r, (Texture::Swizzle) g, (Texture::Swizzle) b, (Texture::Swizzle) a);
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Texture_nBuilderBuild(JNIEnv*, jclass, jlong nativeBuilder, jlong nativeEngine) {
    return (jlong) ((Texture::Builder*) nativeBuilder)->build(*(Engine*) nativeEngine);
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_Texture_nGetWidth(JNIEnv*, jclass, jlong nativeTexture, jint level) {
    return (jint) ((Texture*) nativeTexture)->getWidth(level);
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_Texture_nGetHeight(JNIEnv*, jclass, jlong nativeTexture, jint level) {
    return (jint) ((Texture*) nativeTexture)->getHeight(level);
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_Texture_nGetDepth(JNIEnv*, jclass, jlong nativeTexture, jint level) {
    return (jint) ((Texture*) nativeTexture)->getDepth(level);
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_Texture_nGetLevels(JNIEnv*, jclass, jlong nativeTexture) {
    return (jint) ((Texture*) nativeTexture)->getLevels();
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_Texture_nGetTarget(JNIEnv*, jclass, jlong nativeTexture) {
    return (jint) ((Texture*) nativeTexture)->getTarget();
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_Texture_nGetFormat(JNIEnv*, jclass, jlong nativeTexture) {
    return (jint) ((Texture*) nativeTexture)->getFormat();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Texture_nSetImage(JNIEnv* env, jclass, jlong nativeTexture, jlong nativeEngine, jint level, jint x, jint y, jint z, jint w, jint h, jint d, jobject storage, jint remaining, jint left, jint top, jint type, jint alignment, jint stride, jint format, jobject handler, jobject runnable) {
    Texture* texture = (Texture*) nativeTexture;
    Engine* engine = (Engine*) nativeEngine;
    size_t sizeInBytes = getTextureDataSize(texture, level, (Texture::Format) format, (Texture::Type) type, stride, h, alignment) * d;
    AutoBuffer buffer(env, storage, remaining);
    auto* callback = JniBufferCallback::make(engine, env, handler, runnable, std::move(buffer));
    Texture::PixelBufferDescriptor desc(callback->getBuffer().getData(), sizeInBytes, (backend::PixelDataFormat) format, (backend::PixelDataType) type, (uint8_t) alignment, (uint32_t) left, (uint32_t) top, (uint32_t) stride, callback->getHandler(), &JniBufferCallback::postToJavaAndDestroy, callback);
    texture->setImage(*engine, level, x, y, z, w, h, d, std::move(desc));
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Texture_nGenerateMipmaps(JNIEnv*, jclass, jlong nativeTexture, jlong nativeEngine) {
    ((Texture*) nativeTexture)->generateMipmaps(*(Engine*) nativeEngine);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Texture_nGeneratePrefilterMipmap(JNIEnv* env, jclass, jlong nativeTexture, jlong nativeEngine, jobject storage, jint remaining, jint left, jint top, jint type, jint alignment, jint stride, jint format, jobject handler, jobject runnable, jintArray offsets_, jint sampleCount, jboolean mirror) {
    Texture* texture = (Texture*) nativeTexture;
    Engine* engine = (Engine*) nativeEngine;
    jint* offsets = env->GetIntArrayElements(offsets_, NULL);
    filament::FaceOffsets faceOffsets;
    std::copy_n(offsets, 6, faceOffsets.offsets);
    env->ReleaseIntArrayElements(offsets_, offsets, JNI_ABORT);
    size_t width = texture->getWidth(0);
    size_t height = texture->getHeight(0);
    size_t sizeInBytes = 6 * Texture::computeTextureDataSize((Texture::Format) format, (Texture::Type) type, (stride ? stride : width), height, (uint8_t) alignment);
    AutoBuffer buffer(env, storage, remaining);
    auto* callback = JniBufferCallback::make(engine, env, handler, runnable, std::move(buffer));
    backend::PixelBufferDescriptor desc(callback->getBuffer().getData(), sizeInBytes, (backend::PixelDataFormat) format, (backend::PixelDataType) type, (uint8_t) alignment, (uint32_t) left, (uint32_t) top, (uint32_t) stride, callback->getHandler(), &JniBufferCallback::postToJavaAndDestroy, callback);
    PrefilterOptions options;
    options.sampleCount = (uint16_t) sampleCount;
    options.mirror = (bool) mirror;
    filament::generatePrefilterMipmap(texture, *engine, std::move(desc), faceOffsets, &options);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Texture_nBuilderSamples(JNIEnv*, jclass, jlong nativeBuilder, jint samples) {
    ((Texture::Builder*) nativeBuilder)->samples((uint8_t) samples);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Texture_nBuilderImportTexture(JNIEnv*, jclass, jlong nativeBuilder, jlong id) {
#if defined(__APPLE__)
    // Filament calls CFRelease when it destroys an imported Metal texture.
    // CFBridgingRetain gives it ownership of one reference; our JVM handle retains the other.
    CFRetain((CFTypeRef)(intptr_t) id);
#endif
    ((Texture::Builder*) nativeBuilder)->import((intptr_t) id);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Texture_nBuilderExternal(JNIEnv*, jclass, jlong nativeBuilder) {
    ((Texture::Builder*) nativeBuilder)->external();
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_Texture_nValidatePixelFormatAndType(JNIEnv*, jclass, jint internalFormat, jint pixelDataFormat, jint pixelDataType) {
    return (jboolean) Texture::validatePixelFormatAndType((Texture::InternalFormat) internalFormat, (Texture::Format) pixelDataFormat, (Texture::Type) pixelDataType);
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_Texture_nGetMaxTextureSize(JNIEnv*, jclass, jlong nativeEngine, jint sampler) {
    return (jint) Texture::getMaxTextureSize(*(Engine*) nativeEngine, (Texture::Sampler) sampler);
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_Texture_nGetMaxArrayTextureLayers(JNIEnv*, jclass, jlong nativeEngine) {
    return (jint) Texture::getMaxArrayTextureLayers(*(Engine*) nativeEngine);
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_Texture_nIsTextureFormatSupported(JNIEnv*, jclass, jlong nativeEngine, jint format) {
    return (jboolean) Texture::isTextureFormatSupported(*(Engine*) nativeEngine, (Texture::InternalFormat) format);
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_Texture_nIsTextureFormatMipmappable(JNIEnv*, jclass, jlong nativeEngine, jint format) {
    return (jboolean) Texture::isTextureFormatMipmappable(*(Engine*) nativeEngine, (Texture::InternalFormat) format);
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_Texture_nIsTextureSwizzleSupported(JNIEnv*, jclass, jlong nativeEngine) {
    return (jboolean) Texture::isTextureSwizzleSupported(*(Engine*) nativeEngine);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Texture_nSetExternalStream(JNIEnv*, jclass, jlong nativeTexture, jlong nativeEngine, jlong nativeStream) {
    ((Texture*) nativeTexture)->setExternalStream(*(Engine*) nativeEngine, (Stream*) nativeStream);
}

} // extern "C"
