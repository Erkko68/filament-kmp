#include <jni.h>
#include <filament/IndexBuffer.h>
#include <filament/Engine.h>
#include <backend/BufferDescriptor.h>

#include "common/NioUtils.h"
#include "common/CallbackUtils.h"

using namespace filament;

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_IndexBuffer_00024Builder_nCreateBuilder(JNIEnv* env, jclass) {
    return (jlong) new IndexBuffer::Builder();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_IndexBuffer_00024Builder_nDestroyBuilder(JNIEnv* env, jclass, jlong nativeBuilder) {
    delete (IndexBuffer::Builder*) nativeBuilder;
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_IndexBuffer_00024Builder_nBuilderIndexCount(JNIEnv* env, jclass, jlong nativeBuilder, jint indexCount) {
    ((IndexBuffer::Builder*) nativeBuilder)->indexCount((uint32_t) indexCount);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_IndexBuffer_00024Builder_nBuilderBufferType(JNIEnv* env, jclass, jlong nativeBuilder, jint indexType) {
    ((IndexBuffer::Builder*) nativeBuilder)->bufferType((IndexBuffer::IndexType) indexType);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_IndexBuffer_00024Builder_nBuilderBuild(JNIEnv* env, jclass, jlong nativeBuilder, jlong nativeEngine) {
    return (jlong) ((IndexBuffer::Builder*) nativeBuilder)->build(*(Engine*) nativeEngine);
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_IndexBuffer_nGetIndexCount(JNIEnv* env, jclass, jlong nativeIndexBuffer) {
    return (jint) ((IndexBuffer*) nativeIndexBuffer)->getIndexCount();
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_IndexBuffer_nSetBuffer(JNIEnv* env, jclass, jlong nativeIndexBuffer, jlong nativeEngine, jobject buffer, jint remaining, jint destOffsetInBytes, jint count, jobject handler, jobject callback) {
    IndexBuffer* ib = (IndexBuffer*) nativeIndexBuffer;
    Engine* engine = (Engine*) nativeEngine;
    
    void* data = NioUtils::getBufferAddress(env, buffer);
    if (!data) return -1;

    backend::BufferDescriptor desc(data, (size_t) count, CallbackUtils::createCallback(env, handler, callback));
    ib->setBuffer(*engine, std::move(desc), (uint32_t) destOffsetInBytes);
    return 0;
}
