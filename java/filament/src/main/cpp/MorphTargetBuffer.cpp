#include <jni.h>
#include <filament/MorphTargetBuffer.h>
#include <filament/Engine.h>
#include <backend/BufferDescriptor.h>
#include "common/NioUtils.h"

using namespace filament;

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_MorphTargetBuffer_00024Builder_nCreateBuilder(JNIEnv* env, jclass) {
    return (jlong) new MorphTargetBuffer::Builder();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_MorphTargetBuffer_00024Builder_nDestroyBuilder(JNIEnv* env, jclass, jlong nativeBuilder) {
    delete (MorphTargetBuffer::Builder*) nativeBuilder;
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_MorphTargetBuffer_00024Builder_nBuilderVertexCount(JNIEnv* env, jclass, jlong nativeBuilder, jint vertexCount) {
    ((MorphTargetBuffer::Builder*) nativeBuilder)->vertexCount((uint32_t) vertexCount);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_MorphTargetBuffer_00024Builder_nBuilderCount(JNIEnv* env, jclass, jlong nativeBuilder, jint count) {
    ((MorphTargetBuffer::Builder*) nativeBuilder)->count((uint8_t) count);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_MorphTargetBuffer_00024Builder_nBuilderBuild(JNIEnv* env, jclass, jlong nativeBuilder, jlong nativeEngine) {
    return (jlong) ((MorphTargetBuffer::Builder*) nativeBuilder)->build(*(Engine*) nativeEngine);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_MorphTargetBuffer_nSetPositionsAt(JNIEnv* env, jclass, jlong nativeBuffer, jlong nativeEngine, jint index, jobject buffer, jint remaining, jint offset) {
    void* data = NioUtils::getBufferAddress(env, buffer);
    if (!data) return;
    backend::BufferDescriptor desc(data, (size_t) remaining);
    ((MorphTargetBuffer*) nativeBuffer)->setPositionsAt(*(Engine*) nativeEngine, (size_t) index, std::move(desc), (uint32_t) offset);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_MorphTargetBuffer_nSetTangentsAt(JNIEnv* env, jclass, jlong nativeBuffer, jlong nativeEngine, jint index, jobject buffer, jint remaining, jint offset) {
    void* data = NioUtils::getBufferAddress(env, buffer);
    if (!data) return;
    backend::BufferDescriptor desc(data, (size_t) remaining);
    ((MorphTargetBuffer*) nativeBuffer)->setTangentsAt(*(Engine*) nativeEngine, (size_t) index, std::move(desc), (uint32_t) offset);
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_MorphTargetBuffer_nGetVertexCount(JNIEnv* env, jclass, jlong nativeBuffer) {
    return (jint) ((MorphTargetBuffer*) nativeBuffer)->getVertexCount();
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_MorphTargetBuffer_nGetCount(JNIEnv* env, jclass, jlong nativeBuffer) {
    return (jint) ((MorphTargetBuffer*) nativeBuffer)->getCount();
}
