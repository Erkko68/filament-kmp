#include <jni.h>
#include <filament/Material.h>
#include <filament/MaterialInstance.h>
#include <filament/Texture.h>
#include <filament/Engine.h>
#include <backend/BufferDescriptor.h>

#include "common/NioUtils.h"

using namespace filament;

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_Material_00024Builder_nBuilderBuild(JNIEnv* env, jclass, jlong nativeEngine, jobject buffer, jint size) {
    Engine* engine = (Engine*) nativeEngine;
    void* data = NioUtils::getBufferAddress(env, buffer);
    if (!data) return 0;

    // Materials are usually small and loaded once, no callback needed for memory if managed by JVM
    Material::Builder builder;
    builder.package(data, (size_t) size);
    return (jlong) builder.build(*engine);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_Material_nCreateInstance(JNIEnv* env, jclass, jlong nativeMaterial) {
    return (jlong) ((Material*) nativeMaterial)->createInstance();
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_Material_nCreateInstanceWithName(JNIEnv* env, jclass, jlong nativeMaterial, jstring name) {
    const char* nativeName = env->GetStringUTFChars(name, nullptr);
    jlong instance = (jlong) ((Material*) nativeMaterial)->createInstance(nativeName);
    env->ReleaseStringUTFChars(name, nativeName);
    return instance;
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_Material_nGetDefaultInstance(JNIEnv* env, jclass, jlong nativeMaterial) {
    return (jlong) ((Material*) nativeMaterial)->getDefaultInstance();
}

extern "C" JNIEXPORT jstring JNICALL
Java_io_github_erkko68_filament_Material_nGetName(JNIEnv* env, jclass, jlong nativeMaterial) {
    return env->NewStringUTF( ((Material*) nativeMaterial)->getName() );
}

// MaterialInstance methods

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_MaterialInstance_nSetParameterFloat(JNIEnv* env, jclass, jlong nativeMaterialInstance, jstring name, jfloat value) {
    const char* nativeName = env->GetStringUTFChars(name, nullptr);
    ((MaterialInstance*) nativeMaterialInstance)->setParameter(nativeName, (float) value);
    env->ReleaseStringUTFChars(name, nativeName);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_MaterialInstance_nSetParameterFloat2(JNIEnv* env, jclass, jlong nativeMaterialInstance, jstring name, jfloat v0, jfloat v1) {
    const char* nativeName = env->GetStringUTFChars(name, nullptr);
    ((MaterialInstance*) nativeMaterialInstance)->setParameter(nativeName, math::float2(v0, v1));
    env->ReleaseStringUTFChars(name, nativeName);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_MaterialInstance_nSetParameterFloat3(JNIEnv* env, jclass, jlong nativeMaterialInstance, jstring name, jfloat v0, jfloat v1, jfloat v2) {
    const char* nativeName = env->GetStringUTFChars(name, nullptr);
    ((MaterialInstance*) nativeMaterialInstance)->setParameter(nativeName, math::float3(v0, v1, v2));
    env->ReleaseStringUTFChars(name, nativeName);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_MaterialInstance_nSetParameterFloat4(JNIEnv* env, jclass, jlong nativeMaterialInstance, jstring name, jfloat v0, jfloat v1, jfloat v2, jfloat v3) {
    const char* nativeName = env->GetStringUTFChars(name, nullptr);
    ((MaterialInstance*) nativeMaterialInstance)->setParameter(nativeName, math::float4(v0, v1, v2, v3));
    env->ReleaseStringUTFChars(name, nativeName);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_MaterialInstance_nSetParameterInt(JNIEnv* env, jclass, jlong nativeMaterialInstance, jstring name, jint value) {
    const char* nativeName = env->GetStringUTFChars(name, nullptr);
    ((MaterialInstance*) nativeMaterialInstance)->setParameter(nativeName, (int32_t) value);
    env->ReleaseStringUTFChars(name, nativeName);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_MaterialInstance_nSetParameterTexture(JNIEnv* env, jclass, jlong nativeMaterialInstance, jstring name, jlong nativeTexture, jint sampler) {
    const char* nativeName = env->GetStringUTFChars(name, nullptr);
    TextureSampler s((TextureSampler::MinFilter)sampler, (TextureSampler::MagFilter)sampler); // Simple mapping for now
    ((MaterialInstance*) nativeMaterialInstance)->setParameter(nativeName, (Texture*) nativeTexture, s);
    env->ReleaseStringUTFChars(name, nativeName);
}
