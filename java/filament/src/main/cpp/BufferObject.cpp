#include <jni.h>
#include <filament/BufferObject.h>
#include <filament/Engine.h>
#include <backend/BufferDescriptor.h>
#include "common/NioUtils.h"

using namespace filament;

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_BufferObject_00024Builder_nCreateBuilder(JNIEnv* env, jclass) {
    return (jlong) new BufferObject::Builder();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_BufferObject_00024Builder_nDestroyBuilder(JNIEnv* env, jclass, jlong nativeBuilder) {
    delete (BufferObject::Builder*) nativeBuilder;
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_BufferObject_00024Builder_nBuilderSize(JNIEnv* env, jclass, jlong nativeBuilder, jint byteCount) {
    ((BufferObject::Builder*) nativeBuilder)->size((uint32_t) byteCount);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_BufferObject_00024Builder_nBuilderBindingType(JNIEnv* env, jclass, jlong nativeBuilder, jint type) {
    ((BufferObject::Builder*) nativeBuilder)->bindingType((BufferObject::BindingType) type);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_BufferObject_00024Builder_nBuilderBuild(JNIEnv* env, jclass, jlong nativeBuilder, jlong nativeEngine) {
    return (jlong) ((BufferObject::Builder*) nativeBuilder)->build(*(Engine*) nativeEngine);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_BufferObject_nSetBuffer(JNIEnv* env, jclass, jlong nativeBufferObject, jlong nativeEngine, jobject buffer, jint remaining, jint byteOffset, jint byteCount) {
    void* data = NioUtils::getBufferAddress(env, buffer);
    if (!data) return;
    backend::BufferDescriptor desc(data, (size_t) byteCount);
    ((BufferObject*) nativeBufferObject)->setBuffer(*(Engine*) nativeEngine, std::move(desc), (uint32_t) byteOffset);
}
