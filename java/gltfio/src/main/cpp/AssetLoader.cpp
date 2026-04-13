#include <jni.h>
#include <gltfio/AssetLoader.h>
#include <gltfio/MaterialProvider.h>
#include <filament/Engine.h>
#include <utils/EntityManager.h>

using namespace filament;
using namespace gltfio;

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_gltfio_AssetLoader_nCreateAssetLoader(JNIEnv* env, jclass, jlong nativeEngine, jobject provider, jlong nativeEntities) {
    jclass providerClass = env->GetObjectClass(provider);
    jmethodID getNativeMethod = env->GetMethodID(providerClass, "getNativeObject", "()J");
    jlong nativeProvider = env->CallLongMethod(provider, getNativeMethod);
    
    AssetConfiguration config = {};
    config.engine = (Engine*) nativeEngine;
    config.materials = (MaterialProvider*) nativeProvider;
    config.entities = (utils::EntityManager*) nativeEntities;
    
    return (jlong) AssetLoader::create(config);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_AssetLoader_nDestroyAssetLoader(JNIEnv* env, jclass, jlong nativeLoader) {
    AssetLoader* loader = (AssetLoader*) nativeLoader;
    AssetLoader::destroy(&loader);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_gltfio_AssetLoader_nCreateAsset(JNIEnv* env, jclass, jlong nativeLoader, jobject buffer, jint remaining) {
    AssetLoader* loader = (AssetLoader*) nativeLoader;
    const uint8_t* data = (const uint8_t*) env->GetDirectBufferAddress(buffer);
    return (jlong) loader->createAsset(data, (uint32_t) remaining);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_gltfio_AssetLoader_nCreateInstancedAsset(JNIEnv* env, jclass, jlong nativeLoader, jobject buffer, jint remaining, jlongArray nativeInstances) {
    AssetLoader* loader = (AssetLoader*) nativeLoader;
    const uint8_t* data = (const uint8_t*) env->GetDirectBufferAddress(buffer);
    jsize count = env->GetArrayLength(nativeInstances);
    jlong* instances = env->GetLongArrayElements(nativeInstances, nullptr);
    
    FilamentAsset* asset = loader->createInstancedAsset(data, (uint32_t) remaining, (FilamentInstance**) instances, (size_t) count);
    
    env->ReleaseLongArrayElements(nativeInstances, instances, 0);
    return (jlong) asset;
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_gltfio_AssetLoader_nCreateInstance(JNIEnv* env, jclass, jlong nativeLoader, jlong nativeAsset) {
    AssetLoader* loader = (AssetLoader*) nativeLoader;
    return (jlong) loader->createInstance((FilamentAsset*) nativeAsset);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_AssetLoader_nEnableDiagnostics(JNIEnv* env, jclass, jlong nativeLoader, jboolean enable) {
    AssetLoader* loader = (AssetLoader*) nativeLoader;
    loader->enableDiagnostics((bool) enable);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_AssetLoader_nDestroyAsset(JNIEnv* env, jclass, jlong nativeLoader, jlong nativeAsset) {
    AssetLoader* loader = (AssetLoader*) nativeLoader;
    loader->destroyAsset((const FilamentAsset*) nativeAsset);
}
