#include <jni.h>
#include <filament/SkinningBuffer.h>
#include <filament/Engine.h>
#include <math/mat4.h>

#include "common/NioUtils.h"

using namespace filament;

extern "C" {

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_SkinningBuffer_nCreateBuilder(JNIEnv*, jclass) {
    return (jlong) new SkinningBuffer::Builder();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_SkinningBuffer_nDestroyBuilder(JNIEnv*, jclass, jlong nativeBuilder) {
    delete (SkinningBuffer::Builder*) nativeBuilder;
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_SkinningBuffer_nBuilderBoneCount(JNIEnv*, jclass, jlong nativeBuilder, jint boneCount) {
    ((SkinningBuffer::Builder*) nativeBuilder)->boneCount((uint32_t) boneCount);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_jni_SkinningBuffer_nBuilderInitialize(JNIEnv*, jclass, jlong nativeBuilder, jboolean initialize) {
    ((SkinningBuffer::Builder*) nativeBuilder)->initialize((bool) initialize);
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_jni_SkinningBuffer_nBuilderBuild(JNIEnv*, jclass, jlong nativeBuilder, jlong nativeEngine) {
    return (jlong) ((SkinningBuffer::Builder*) nativeBuilder)->build(*(Engine*) nativeEngine);
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_SkinningBuffer_nSetBonesAsMatrices(JNIEnv* env, jclass, jlong nativeBuffer, jlong nativeEngine, jobject matrices, jint remaining, jint boneCount, jint offset) {
    SkinningBuffer* buffer = (SkinningBuffer*) nativeBuffer;
    Engine* engine = (Engine*) nativeEngine;
    AutoBuffer nioBuffer(env, matrices, boneCount * 16);
    void* data = nioBuffer.getData();
    if (nioBuffer.getSize() > (size_t)(remaining << nioBuffer.getShift())) return -1;
    buffer->setBones(*engine, (filament::math::mat4f const*) data, (size_t) boneCount, (size_t) offset);
    return 0;
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_SkinningBuffer_nSetBonesAsQuaternions(JNIEnv* env, jclass, jlong nativeBuffer, jlong nativeEngine, jobject quaternions, jint remaining, jint boneCount, jint offset) {
    SkinningBuffer* buffer = (SkinningBuffer*) nativeBuffer;
    Engine* engine = (Engine*) nativeEngine;
    AutoBuffer nioBuffer(env, quaternions, boneCount * 8);
    void* data = nioBuffer.getData();
    if (nioBuffer.getSize() > (size_t)(remaining << nioBuffer.getShift())) return -1;
    buffer->setBones(*engine, (RenderableManager::Bone const*) data, (size_t) boneCount, (size_t) offset);
    return 0;
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_SkinningBuffer_nGetBoneCount(JNIEnv*, jclass, jlong nativeBuffer) {
    return (jint) ((SkinningBuffer*) nativeBuffer)->getBoneCount();
}

} // extern "C"
