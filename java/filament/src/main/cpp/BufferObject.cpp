/*
 * Copyright (C) 2021 The Android Open Source Project
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

#include <functional>
#include <stdlib.h>
#include <string.h>

#include <filament/BufferObject.h>
#include <filament/Engine.h>

#include <backend/BufferDescriptor.h>

#include "common/CallbackUtils.h"
#include "common/NioUtils.h"

using namespace filament;
using namespace backend;

extern "C" {

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_BufferObject_nCreateBuilder(JNIEnv*, jclass) {
    return (jlong) new BufferObject::Builder();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_BufferObject_nDestroyBuilder(JNIEnv*, jclass, jlong nativeBuilder) {
    BufferObject::Builder* builder = (BufferObject::Builder*) nativeBuilder;
    delete builder;
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_BufferObject_nBuilderSize(JNIEnv*, jclass, jlong nativeBuilder, jint byteCount) {
    BufferObject::Builder* builder = (BufferObject::Builder*) nativeBuilder;
    builder->size((uint32_t) byteCount);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_BufferObject_nBuilderBindingType(JNIEnv*, jclass, jlong nativeBuilder, jint bindingType) {
    using BindingType = BufferObject::BindingType;
    BufferObject::Builder* builder = (BufferObject::Builder*) nativeBuilder;
    BindingType types[] = {BindingType::VERTEX};
    builder->bindingType(types[bindingType]);
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_BufferObject_nBuilderBuild(JNIEnv*, jclass, jlong nativeBuilder, jlong nativeEngine) {
    BufferObject::Builder* builder = (BufferObject::Builder*) nativeBuilder;
    Engine* engine = (Engine*) nativeEngine;
    return (jlong) builder->build(*engine);
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_BufferObject_nGetByteCount(JNIEnv*, jclass, jlong nativeBufferObject) {
    BufferObject* bufferObject = (BufferObject*) nativeBufferObject;
    return (jint) bufferObject->getByteCount();
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_BufferObject_nSetBuffer(JNIEnv* env, jclass, jlong nativeBufferObject, jlong nativeEngine, jobject buffer, jint remaining, jint destOffsetInBytes, jint count, jobject handler, jobject runnable) {
    BufferObject* bufferObject = (BufferObject*) nativeBufferObject;
    Engine* engine = (Engine*) nativeEngine;

    AutoBuffer nioBuffer(env, buffer, count);
    void* data = nioBuffer.getData();
    size_t sizeInBytes = nioBuffer.getSize();
    if (sizeInBytes > (remaining << nioBuffer.getShift())) {
        // BufferOverflowException
        return -1;
    }

    auto* callback = JniBufferCallback::make(engine, env, handler, runnable, std::move(nioBuffer));

    BufferDescriptor desc(data, sizeInBytes,
            callback->getHandler(), &JniBufferCallback::postToJavaAndDestroy, callback);

    bufferObject->setBuffer(*engine, std::move(desc), (uint32_t) destOffsetInBytes);

    return 0;
}

} // extern "C"
