#include <jni.h>
#include <filament/VertexBuffer.h>
#include <filament/Engine.h>
#include <backend/BufferDescriptor.h>

#include "common/NioUtils.h"
#include "common/CallbackUtils.h"

using namespace filament;

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_VertexBuffer_00024Builder_nCreateBuilder(JNIEnv* env, jclass) {
    return (jlong) new VertexBuffer::Builder();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_VertexBuffer_00024Builder_nDestroyBuilder(JNIEnv* env, jclass, jlong nativeBuilder) {
    delete (VertexBuffer::Builder*) nativeBuilder;
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_VertexBuffer_00024Builder_nBuilderVertexCount(JNIEnv* env, jclass, jlong nativeBuilder, jint vertexCount) {
    ((VertexBuffer::Builder*) nativeBuilder)->vertexCount((uint32_t) vertexCount);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_VertexBuffer_00024Builder_nBuilderEnableBufferObjects(JNIEnv* env, jclass, jlong nativeBuilder, jboolean enabled) {
    ((VertexBuffer::Builder*) nativeBuilder)->enableBufferObjects((bool) enabled);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_VertexBuffer_00024Builder_nBuilderBufferCount(JNIEnv* env, jclass, jlong nativeBuilder, jint bufferCount) {
    ((VertexBuffer::Builder*) nativeBuilder)->bufferCount((uint8_t) bufferCount);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_VertexBuffer_00024Builder_nBuilderAttribute(JNIEnv* env, jclass, jlong nativeBuilder, jint attribute, jint bufferIndex, jint attributeType, jint byteOffset, jint byteStride) {
    ((VertexBuffer::Builder*) nativeBuilder)->attribute((VertexAttribute) attribute, (uint8_t) bufferIndex, (VertexBuffer::AttributeType) attributeType, (uint32_t) byteOffset, (uint8_t) byteStride);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_VertexBuffer_00024Builder_nBuilderNormalized(JNIEnv* env, jclass, jlong nativeBuilder, jint attribute, jboolean normalized) {
    ((VertexBuffer::Builder*) nativeBuilder)->normalized((VertexAttribute) attribute, (bool) normalized);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_VertexBuffer_00024Builder_nBuilderBuild(JNIEnv* env, jclass, jlong nativeBuilder, jlong nativeEngine) {
    return (jlong) ((VertexBuffer::Builder*) nativeBuilder)->build(*(Engine*) nativeEngine);
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_VertexBuffer_nGetVertexCount(JNIEnv* env, jclass, jlong nativeVertexBuffer) {
    return (jint) ((VertexBuffer*) nativeVertexBuffer)->getVertexCount();
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_VertexBuffer_nSetBufferAt(JNIEnv* env, jclass, jlong nativeVertexBuffer, jlong nativeEngine, jint bufferIndex, jobject buffer, jint remaining, jint destOffsetInBytes, jint count, jobject handler, jobject callback) {
    VertexBuffer* vb = (VertexBuffer*) nativeVertexBuffer;
    Engine* engine = (Engine*) nativeEngine;
    
    void* data = NioUtils::getBufferAddress(env, buffer);
    if (!data) return -1;

    backend::BufferDescriptor desc(data, (size_t) count, CallbackUtils::createCallback(env, handler, callback));
    vb->setBufferAt(*engine, (uint8_t) bufferIndex, std::move(desc), (uint32_t) destOffsetInBytes);
    return 0;
}
