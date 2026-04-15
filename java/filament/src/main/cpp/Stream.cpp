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

#include <assert.h>
#include <jni.h>

#include <filament/Stream.h>
#include <backend/PixelBufferDescriptor.h>

#include "common/NioUtils.h"
#include "common/CallbackUtils.h"

using namespace filament;
using namespace backend;

class StreamBuilder {
public:
    StreamBuilder() noexcept {
        mBuilder = new Stream::Builder{};
    }

    ~StreamBuilder() {
        delete mBuilder;
    }

    Stream::Builder* builder() const noexcept { return mBuilder; }

private:
    Stream::Builder* mBuilder = nullptr;
};

extern "C" {

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Stream_nCreateBuilder(JNIEnv*, jclass) {
    return (jlong) new StreamBuilder{};
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Stream_nDestroyBuilder(JNIEnv* env, jclass, jlong nativeStreamBuilder) {
    StreamBuilder* builder = (StreamBuilder*) nativeStreamBuilder;
    delete builder;
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Stream_nBuilderWidth(JNIEnv*, jclass, jlong nativeStreamBuilder, jint width) {
    StreamBuilder* builder = (StreamBuilder*) nativeStreamBuilder;
    builder->builder()->width((uint32_t) width);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Stream_nBuilderHeight(JNIEnv*, jclass, jlong nativeStreamBuilder, jint height) {
    StreamBuilder* builder = (StreamBuilder*) nativeStreamBuilder;
    builder->builder()->height((uint32_t) height);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Stream_nBuilderStreamSource(JNIEnv*, jclass, jlong nativeStreamBuilder, jobject streamSource) {
    StreamBuilder* builder = (StreamBuilder*) nativeStreamBuilder;
    // On non-Android JVM, we don't have SurfaceTexture. We just pass the handle if possible, 
    // or ignore it if it's not a supported type. 
    // For now, we provide the symbol for parity.
    builder->builder()->stream(streamSource);
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Stream_nBuilderBuild(JNIEnv*, jclass, jlong nativeStreamBuilder, jlong nativeEngine) {
    StreamBuilder* builder = (StreamBuilder*) nativeStreamBuilder;
    Engine* engine = (Engine*) nativeEngine;
    return (jlong) builder->builder()->build(*engine);
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_Stream_nGetStreamType(JNIEnv*, jclass, jlong nativeStream) {
    Stream* stream = (Stream*) nativeStream;
    return (jint) stream->getStreamType();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Stream_nSetDimensions(JNIEnv*, jclass, jlong nativeStream, jint width, jint height) {
    Stream* stream = (Stream*) nativeStream;
    stream->setDimensions((uint32_t) width, (uint32_t) height);
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_Stream_nGetTimestamp(JNIEnv*, jclass, jlong nativeStream) {
    Stream* stream = (Stream*) nativeStream;
    return stream->getTimestamp();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_Stream_nSetAcquiredImage(JNIEnv* env, jclass, jlong nativeStream, jlong nativeEngine, jobject hwbuffer, jobject handler, jobject runnable) {
    Engine* engine = (Engine*) nativeEngine;
    Stream* stream = (Stream*) nativeStream;

    // For non-Android desktop JVM platforms, we just reserve this or map generically if needed.
    auto* callback = JniImageCallback::make(engine, env, handler, runnable, 0);
    void* nativeBuffer = nullptr;

    stream->setAcquiredImage((void*) nativeBuffer, callback->getHandler(), &JniImageCallback::postToJavaAndDestroy, callback);
}

} // extern "C"
