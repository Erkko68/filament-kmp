#include <jni.h>
#include <filament/SkinningBuffer.h>
#include <filament/Engine.h>
#include <math/mat4.h>

#include "common/NioUtils.h"

using namespace filament;

extern "C" {

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_SkinningBuffer_00024Builder_nCreateBuilder(JNIEnv*, jclass) {
    return (jlong) new SkinningBuffer::Builder();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_SkinningBuffer_00024Builder_nDestroyBuilder(JNIEnv*, jclass, jlong nativeBuilder) {
    delete (SkinningBuffer::Builder*) nativeBuilder;
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_SkinningBuffer_00024Builder_nBuilderBoneCount(JNIEnv*, jclass, jlong nativeBuilder, jint boneCount) {
    ((SkinningBuffer::Builder*) nativeBuilder)->boneCount((uint32_t) boneCount);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_SkinningBuffer_00024Builder_nBuilderInitialize(JNIEnv*, jclass, jlong nativeBuilder, jboolean initialize) {
    ((SkinningBuffer::Builder*) nativeBuilder)->initialize((bool) initialize);
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_SkinningBuffer_00024Builder_nBuilderBuild(JNIEnv*, jclass, jlong nativeBuilder, jlong nativeEngine) {
    return (jlong) ((SkinningBuffer::Builder*) nativeBuilder)->build(*(Engine*) nativeEngine);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_SkinningBuffer_nSetBones(JNIEnv* env, jclass, jlong nativeBuffer, jlong nativeEngine, jobject storage, jint remaining, jint count, jint offset) {
    SkinningBuffer* buffer = (SkinningBuffer*) nativeBuffer;
    Engine* engine = (Engine*) nativeEngine;
    AutoBuffer nioBuffer(env, storage, 0);
    void* data = nioBuffer.getData();
    buffer->setBones(*engine, (RenderableManager::Bone const*) data, (size_t) count, (size_t) offset);
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_SkinningBuffer_nGetBoneCount(JNIEnv*, jclass, jlong nativeBuffer) {
    return (jint) ((SkinningBuffer*) nativeBuffer)->getBoneCount();
}

} // extern "C"
