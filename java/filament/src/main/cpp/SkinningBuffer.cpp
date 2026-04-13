#include <jni.h>
#include <filament/SkinningBuffer.h>
#include <filament/Engine.h>
#include <backend/BufferDescriptor.h>
#include "common/NioUtils.h"

using namespace filament;

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_SkinningBuffer_00024Builder_nCreateBuilder(JNIEnv* env, jclass) {
    return (jlong) new SkinningBuffer::Builder();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_SkinningBuffer_00024Builder_nDestroyBuilder(JNIEnv* env, jclass, jlong nativeBuilder) {
    delete (SkinningBuffer::Builder*) nativeBuilder;
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_SkinningBuffer_00024Builder_nBuilderBoneCount(JNIEnv* env, jclass, jlong nativeBuilder, jint boneCount) {
    ((SkinningBuffer::Builder*) nativeBuilder)->boneCount((uint32_t) boneCount);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_SkinningBuffer_00024Builder_nBuilderInitialize(JNIEnv* env, jclass, jlong nativeBuilder, jboolean initialize) {
    ((SkinningBuffer::Builder*) nativeBuilder)->initialize((bool) initialize);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_SkinningBuffer_00024Builder_nBuilderBuild(JNIEnv* env, jclass, jlong nativeBuilder, jlong nativeEngine) {
    return (jlong) ((SkinningBuffer::Builder*) nativeBuilder)->build(*(Engine*) nativeEngine);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_SkinningBuffer_nSetBones(JNIEnv* env, jclass, jlong nativeBuffer, jlong nativeEngine, jobject transforms, jint remaining, jint count, jint offset) {
    void* data = NioUtils::getBufferAddress(env, transforms);
    if (!data) return;
    backend::BufferDescriptor desc(data, (size_t) remaining);
    ((SkinningBuffer*) nativeBuffer)->setBones(*(Engine*) nativeEngine, std::move(desc), (size_t) count, (size_t) offset);
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_SkinningBuffer_nGetBoneCount(JNIEnv* env, jclass, jlong nativeBuffer) {
    return (jint) ((SkinningBuffer*) nativeBuffer)->getBoneCount();
}
