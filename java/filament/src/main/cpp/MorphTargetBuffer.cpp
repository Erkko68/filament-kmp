#include <jni.h>
#include <filament/MorphTargetBuffer.h>
#include <filament/Engine.h>
#include "common/NioUtils.h"

using namespace filament;

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_MorphTargetBuffer_nCreateBuilder(JNIEnv* env, jclass) {
    return (jlong) new MorphTargetBuffer::Builder();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MorphTargetBuffer_nDestroyBuilder(JNIEnv* env, jclass, jlong nativeBuilder) {
    delete (MorphTargetBuffer::Builder*) nativeBuilder;
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MorphTargetBuffer_nBuilderVertexCount(JNIEnv* env, jclass, jlong nativeBuilder, jint vertexCount) {
    ((MorphTargetBuffer::Builder*) nativeBuilder)->vertexCount((uint32_t) vertexCount);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MorphTargetBuffer_nBuilderCount(JNIEnv* env, jclass, jlong nativeBuilder, jint count) {
    ((MorphTargetBuffer::Builder*) nativeBuilder)->count((size_t) count);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MorphTargetBuffer_nBuilderWithPositions(JNIEnv*, jclass, jlong nativeBuilder, jboolean enabled) {
    ((MorphTargetBuffer::Builder*) nativeBuilder)->withPositions((bool) enabled);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MorphTargetBuffer_nBuilderWithTangents(JNIEnv*, jclass, jlong nativeBuilder, jboolean enabled) {
    ((MorphTargetBuffer::Builder*) nativeBuilder)->withTangents((bool) enabled);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MorphTargetBuffer_nBuilderEnableCustomMorphing(JNIEnv*, jclass, jlong nativeBuilder, jboolean enabled) {
    ((MorphTargetBuffer::Builder*) nativeBuilder)->enableCustomMorphing((bool) enabled);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_MorphTargetBuffer_nBuilderBuild(JNIEnv* env, jclass, jlong nativeBuilder, jlong nativeEngine) {
    return (jlong) ((MorphTargetBuffer::Builder*) nativeBuilder)->build(*(Engine*) nativeEngine);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MorphTargetBuffer_nSetPositionsAt(JNIEnv* env, jclass, jlong nativeBuffer, jlong nativeEngine, jint index, jobject buffer, jint remaining, jint offset) {
    AutoBuffer autoBuffer(env, buffer, remaining);
    void* data = autoBuffer.getData();
    if (!data) return;
    ((MorphTargetBuffer*) nativeBuffer)->setPositionsAt(*(Engine*) nativeEngine, (size_t) index, static_cast<math::float3 const*>(data), (size_t) remaining, (size_t) offset);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_MorphTargetBuffer_nSetTangentsAt(JNIEnv* env, jclass, jlong nativeBuffer, jlong nativeEngine, jint index, jobject buffer, jint remaining, jint offset) {
    AutoBuffer autoBuffer(env, buffer, remaining);
    void* data = autoBuffer.getData();
    if (!data) return;
    ((MorphTargetBuffer*) nativeBuffer)->setTangentsAt(*(Engine*) nativeEngine, (size_t) index, static_cast<math::short4 const*>(data), (size_t) remaining, (size_t) offset);
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_MorphTargetBuffer_nGetVertexCount(JNIEnv* env, jclass, jlong nativeBuffer) {
    return (jint) ((MorphTargetBuffer*) nativeBuffer)->getVertexCount();
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_MorphTargetBuffer_nGetCount(JNIEnv* env, jclass, jlong nativeBuffer) {
    return (jint) ((MorphTargetBuffer*) nativeBuffer)->getCount();
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_MorphTargetBuffer_nHasPositions(JNIEnv*, jclass, jlong nativeBuffer) {
    return (jboolean) ((MorphTargetBuffer*) nativeBuffer)->hasPositions();
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_MorphTargetBuffer_nHasTangents(JNIEnv*, jclass, jlong nativeBuffer) {
    return (jboolean) ((MorphTargetBuffer*) nativeBuffer)->hasTangents();
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_jni_MorphTargetBuffer_nIsCustomMorphingEnabled(JNIEnv*, jclass, jlong nativeBuffer) {
    return (jboolean) ((MorphTargetBuffer*) nativeBuffer)->isCustomMorphingEnabled();
}
