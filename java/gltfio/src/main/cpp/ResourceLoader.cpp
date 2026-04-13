#include <jni.h>
#include <gltfio/ResourceLoader.h>
#include <gltfio/FilamentAsset.h>
#include <gltfio/TextureProvider.h>
#include <filament/Engine.h>
#include <backend/BufferDescriptor.h>

using namespace filament;
using namespace gltfio;

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_gltfio_ResourceLoader_nCreateResourceLoader(JNIEnv* env, jclass, jlong nativeEngine, jboolean normalizeSkinningWeights) {
    ResourceConfiguration config = {};
    config.engine = (Engine*) nativeEngine;
    config.normalizeSkinningWeights = (bool) normalizeSkinningWeights;
    return (jlong) new ResourceLoader(config);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_ResourceLoader_nDestroyResourceLoader(JNIEnv* env, jclass, jlong nativeLoader) {
    delete (ResourceLoader*) nativeLoader;
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_ResourceLoader_nAddResourceData(JNIEnv* env, jclass, jlong nativeLoader, jstring url, jobject buffer, jint remaining) {
    const char* nativeUrl = env->GetStringUTFChars(url, nullptr);
    void* data = env->GetDirectBufferAddress(buffer);
    ResourceLoader* loader = (ResourceLoader*) nativeLoader;
    
    // Create a BufferDescriptor. We don't provide a callback here because for addResourceData,
    // Filament usually copies the data or expects the client to manage life cycle.
    // However, native ResourceLoader::addResourceData takes BufferDescriptor&&
    loader->addResourceData(nativeUrl, backend::BufferDescriptor(data, (size_t) remaining));
    
    env->ReleaseStringUTFChars(url, nativeUrl);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_ResourceLoader_nEvictResourceData(JNIEnv* env, jclass, jlong nativeLoader) {
    ((ResourceLoader*) nativeLoader)->evictResourceData();
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_gltfio_ResourceLoader_nHasResourceData(JNIEnv* env, jclass, jlong nativeLoader, jstring url) {
    const char* nativeUrl = env->GetStringUTFChars(url, nullptr);
    bool result = ((ResourceLoader*) nativeLoader)->hasResourceData(nativeUrl);
    env->ReleaseStringUTFChars(url, nativeUrl);
    return (jboolean) result;
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_ResourceLoader_nLoadResources(JNIEnv* env, jclass, jlong nativeLoader, jlong nativeAsset) {
    ((ResourceLoader*) nativeLoader)->loadResources((FilamentAsset*) nativeAsset);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_gltfio_ResourceLoader_nAsyncBeginLoad(JNIEnv* env, jclass, jlong nativeLoader, jlong nativeAsset) {
    return (jboolean) ((ResourceLoader*) nativeLoader)->asyncBeginLoad((FilamentAsset*) nativeAsset);
}

extern "C" JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_gltfio_ResourceLoader_nAsyncGetLoadProgress(JNIEnv* env, jclass, jlong nativeLoader) {
    return ((ResourceLoader*) nativeLoader)->asyncGetLoadProgress();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_ResourceLoader_nAsyncUpdateLoad(JNIEnv* env, jclass, jlong nativeLoader) {
    ((ResourceLoader*) nativeLoader)->asyncUpdateLoad();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_ResourceLoader_nAsyncCancelLoad(JNIEnv* env, jclass, jlong nativeLoader) {
    ((ResourceLoader*) nativeLoader)->asyncCancelLoad();
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_gltfio_ResourceLoader_nCreateStbProvider(JNIEnv* env, jclass, jlong nativeEngine) {
    return (jlong) createStbProvider((Engine*) nativeEngine);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_gltfio_ResourceLoader_nCreateKtx2Provider(JNIEnv* env, jclass, jlong nativeEngine) {
    return (jlong) createKtx2Provider((Engine*) nativeEngine);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_ResourceLoader_nAddTextureProvider(JNIEnv* env, jclass, jlong nativeLoader, jstring url, jlong nativeProvider) {
    const char* nativeUrl = env->GetStringUTFChars(url, nullptr);
    ((ResourceLoader*) nativeLoader)->addTextureProvider(nativeUrl, (TextureProvider*) nativeProvider);
    env->ReleaseStringUTFChars(url, nativeUrl);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_ResourceLoader_nDestroyTextureProvider(JNIEnv* env, jclass, jlong nativeProvider) {
    // Providers are typically destroyed by the client or via specialized methods in gltfio.
    // Native gltfio providers often don't have a virtual destructor but are managed via factory functions.
}
