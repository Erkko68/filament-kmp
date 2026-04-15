#include <jni.h>
#include <filament/Engine.h>
#include <gltfio/ResourceLoader.h>
#include <gltfio/TextureProvider.h>
#include "common/NioUtils.h"

using namespace filament;
using namespace filament::gltfio;

static void destroy(void*, size_t, void *userData) {
    AutoBuffer* buffer = (AutoBuffer*) userData;
    delete buffer;
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_gltfio_jni_ResourceLoader_nCreateResourceLoader(JNIEnv*, jclass,
        jlong nativeEngine, jboolean normalizeSkinningWeights) {
    Engine* engine = (Engine*) nativeEngine;
    return (jlong) new ResourceLoader({ engine, {}, (bool) normalizeSkinningWeights});
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_jni_ResourceLoader_nDestroyResourceLoader(JNIEnv*, jclass,
        jlong nativeLoader) {
    ResourceLoader* loader = (ResourceLoader*) nativeLoader;
    delete loader;
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_jni_ResourceLoader_nAddResourceData(JNIEnv* env, jclass,
        jlong nativeLoader, jstring url, jobject javaBuffer, jint remaining) {
    ResourceLoader* loader = (ResourceLoader*) nativeLoader;
    AutoBuffer* buffer = new AutoBuffer(env, javaBuffer, remaining);
    const char* cstring = env->GetStringUTFChars(url, nullptr);
    loader->addResourceData(cstring,
            ResourceLoader::BufferDescriptor(buffer->getData(), buffer->getSize(), &destroy,
                    buffer));
    env->ReleaseStringUTFChars(url, cstring);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_gltfio_jni_ResourceLoader_nHasResourceData(JNIEnv* env, jclass,
        jlong nativeLoader, jstring url) {
    ResourceLoader* loader = (ResourceLoader*) nativeLoader;
    const char* cstring = env->GetStringUTFChars(url, nullptr);
    bool status = loader->hasResourceData(cstring);
    env->ReleaseStringUTFChars(url, cstring);
    return (jboolean) status;
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_jni_ResourceLoader_nEvictResourceData(JNIEnv*, jclass,
        jlong nativeLoader) {
    ResourceLoader* loader = (ResourceLoader*) nativeLoader;
    loader->evictResourceData();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_jni_ResourceLoader_nLoadResources(JNIEnv*, jclass,
        jlong nativeLoader, jlong nativeAsset) {
    ResourceLoader* loader = (ResourceLoader*) nativeLoader;
    FilamentAsset* asset = (FilamentAsset*) nativeAsset;
    loader->loadResources(asset);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_gltfio_jni_ResourceLoader_nAsyncBeginLoad(JNIEnv*, jclass,
        jlong nativeLoader, jlong nativeAsset) {
    ResourceLoader* loader = (ResourceLoader*) nativeLoader;
    FilamentAsset* asset = (FilamentAsset*) nativeAsset;
    return (jboolean) loader->asyncBeginLoad(asset);
}

extern "C" JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_gltfio_jni_ResourceLoader_nAsyncGetLoadProgress(JNIEnv*, jclass,
        jlong nativeLoader) {
    ResourceLoader* loader = (ResourceLoader*) nativeLoader;
    return loader->asyncGetLoadProgress();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_jni_ResourceLoader_nAsyncUpdateLoad(JNIEnv*, jclass,
        jlong nativeLoader) {
    ResourceLoader* loader = (ResourceLoader*) nativeLoader;
    loader->asyncUpdateLoad();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_jni_ResourceLoader_nAsyncCancelLoad(JNIEnv*, jclass,
        jlong nativeLoader) {
    ResourceLoader* loader = (ResourceLoader*) nativeLoader;
    loader->asyncCancelLoad();
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_gltfio_jni_ResourceLoader_nCreateStbProvider(JNIEnv*, jclass,
        jlong nativeEngine) {
    Engine* engine = (Engine*) nativeEngine;
    return (jlong) createStbProvider(engine);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_gltfio_jni_ResourceLoader_nCreateKtx2Provider(JNIEnv*, jclass,
        jlong nativeEngine) {
    Engine* engine = (Engine*) nativeEngine;
    return (jlong) createKtx2Provider(engine);
}

extern "C" JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_gltfio_jni_ResourceLoader_nIsWebpSupported(JNIEnv*, jclass) {
    return (jboolean) isWebpSupported();
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_gltfio_jni_ResourceLoader_nCreateWebpProvider(JNIEnv*, jclass,
        jlong nativeEngine) {
    Engine* engine = (Engine*) nativeEngine;
    return (jlong) createWebpProvider(engine);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_jni_ResourceLoader_nDestroyTextureProvider(JNIEnv*, jclass,
        jlong nativeProvider) {
    TextureProvider* provider = (TextureProvider*) nativeProvider;
    delete provider;
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_jni_ResourceLoader_nAddTextureProvider(JNIEnv* env, jclass,
        jlong nativeLoader, jstring url, jlong nativeProvider) {
    const char* curl = env->GetStringUTFChars(url, nullptr);
    ((ResourceLoader*)nativeLoader)->addTextureProvider(curl, (TextureProvider*)nativeProvider);
    env->ReleaseStringUTFChars(url, curl);
}
