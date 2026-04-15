#include <jni.h>
#include <filament/Engine.h>
#include <filament/Material.h>
#include <filament/MaterialInstance.h>
#include <gltfio/MaterialProvider.h>
#include <gltfio/materials/uberarchive.h>
#include "MaterialKey.h"

using namespace filament;
using namespace filament::gltfio;

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_gltfio_jni_UbershaderProvider_nCreateUbershaderProvider(JNIEnv*, jclass, jlong nativeEngine) {
    return (jlong) createUbershaderProvider((Engine*) nativeEngine, UBERARCHIVE_DEFAULT_DATA, UBERARCHIVE_DEFAULT_SIZE);
}


extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_jni_UbershaderProvider_nDestroyUbershaderProvider(JNIEnv*, jclass, jlong nativeProvider) {
    delete (MaterialProvider*) nativeProvider;
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_gltfio_jni_JitShaderProvider_nCreateJitShaderProvider(JNIEnv*, jclass, jlong nativeEngine) {
    return (jlong) createJitShaderProvider((Engine*) nativeEngine);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_jni_JitShaderProvider_nDestroyJitShaderProvider(JNIEnv*, jclass, jlong nativeProvider) {
    delete (MaterialProvider*) nativeProvider;
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_jni_UbershaderProvider_nDestroyMaterials(JNIEnv*, jclass, jlong nativeProvider) {
    ((MaterialProvider*) nativeProvider)->destroyMaterials();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_jni_JitShaderProvider_nDestroyMaterials(JNIEnv*, jclass, jlong nativeProvider) {
    ((MaterialProvider*) nativeProvider)->destroyMaterials();
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_gltfio_jni_UbershaderProvider_nCreateMaterialInstance(JNIEnv* env, jclass, jlong nativeProvider, jobject config, jintArray uvmap, jstring label, jstring extras) {
    MaterialKey nativeKey = {};
    MaterialKeyHelper::get().copy(env, nativeKey, config);
    const char* cl = label ? env->GetStringUTFChars(label, nullptr) : nullptr;
    const char* ce = extras ? env->GetStringUTFChars(extras, nullptr) : nullptr;
    jint* uvmapData = env->GetIntArrayElements(uvmap, nullptr);
    MaterialInstance* mi = ((MaterialProvider*) nativeProvider)->createMaterialInstance(&nativeKey, (UvMap*) uvmapData, cl, ce);
    env->ReleaseIntArrayElements(uvmap, uvmapData, 0);
    if (cl) env->ReleaseStringUTFChars(label, cl); if (ce) env->ReleaseStringUTFChars(extras, ce);
    MaterialKeyHelper::get().copy(env, config, nativeKey);
    return (jlong) mi;
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_gltfio_jni_JitShaderProvider_nCreateMaterialInstance(JNIEnv* env, jclass, jlong nativeProvider, jobject config, jintArray uvmap, jstring label, jstring extras) {
    return Java_io_github_erkko68_filament_gltfio_jni_UbershaderProvider_nCreateMaterialInstance(env, nullptr, nativeProvider, config, uvmap, label, extras);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_gltfio_jni_UbershaderProvider_nGetMaterial(JNIEnv* env, jclass, jlong nativeProvider, jobject config, jintArray uvmap, jstring label) {
    MaterialKey nativeKey = {};
    MaterialKeyHelper::get().copy(env, nativeKey, config);
    const char* cl = label ? env->GetStringUTFChars(label, nullptr) : nullptr;
    jint* uvmapData = env->GetIntArrayElements(uvmap, nullptr);
    Material* m = ((MaterialProvider*) nativeProvider)->getMaterial(&nativeKey, (UvMap*) uvmapData, cl);
    env->ReleaseIntArrayElements(uvmap, uvmapData, 0);
    if (cl) env->ReleaseStringUTFChars(label, cl);
    MaterialKeyHelper::get().copy(env, config, nativeKey);
    return (jlong) m;
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_gltfio_jni_JitShaderProvider_nGetMaterial(JNIEnv* env, jclass, jlong nativeProvider, jobject config, jintArray uvmap, jstring label) {
    return Java_io_github_erkko68_filament_gltfio_jni_UbershaderProvider_nGetMaterial(env, nullptr, nativeProvider, config, uvmap, label);
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_gltfio_jni_UbershaderProvider_nGetMaterialCount(JNIEnv*, jclass, jlong nativeProvider) {
    return (jint) ((MaterialProvider*) nativeProvider)->getMaterialsCount();
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_gltfio_jni_JitShaderProvider_nGetMaterialCount(JNIEnv*, jclass, jlong nativeProvider) {
    return (jint) ((MaterialProvider*) nativeProvider)->getMaterialsCount();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_jni_UbershaderProvider_nGetMaterials(JNIEnv* env, jclass, jlong nativeProvider, jlongArray result) {
    MaterialProvider* provider = (MaterialProvider*) nativeProvider;
    size_t count = provider->getMaterialsCount();
    const Material* const* materials = provider->getMaterials();
    jlong* data = env->GetLongArrayElements(result, nullptr);
    for (size_t i = 0; i < count; i++) data[i] = (jlong) materials[i];
    env->ReleaseLongArrayElements(result, data, 0);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_jni_JitShaderProvider_nGetMaterials(JNIEnv* env, jclass, jlong nativeProvider, jlongArray result) {
    Java_io_github_erkko68_filament_gltfio_jni_UbershaderProvider_nGetMaterials(env, nullptr, nativeProvider, result);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_gltfio_jni_UbershaderProvider_nNeedsDummyData(JNIEnv* env, jclass, jlong nativeProvider, jint attrib) {
    return (jboolean) ((MaterialProvider*) nativeProvider)->needsDummyData((VertexAttribute) attrib);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_gltfio_jni_JitShaderProvider_nNeedsDummyData(JNIEnv*, jclass, jlong nativeProvider, jint attrib) {
    return (jboolean) ((MaterialProvider*) nativeProvider)->needsDummyData((VertexAttribute) attrib);
}
