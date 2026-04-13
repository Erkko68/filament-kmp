#include <jni.h>
#include <filament/Engine.h>
#include <filament/SwapChain.h>
#include <filament/View.h>
#include <filament/Renderer.h>
#include <filament/Camera.h>
#include <filament/Scene.h>

#include "common/VirtualMachineEnv.h"
#include "common/CallbackUtils.h"

using namespace filament;

extern "C" {

JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM* vm, void* reserved) {
    VirtualMachineEnv::get().setJvm(vm);
    return JNI_VERSION_1_6;
}

// Builder

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_Engine_nCreateBuilder(JNIEnv* env, jclass clazz) {
    return (jlong) new Engine::Builder();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Engine_nDestroyBuilder(JNIEnv* env, jclass clazz, jlong nativeBuilder) {
    delete (Engine::Builder*) nativeBuilder;
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Engine_nSetBuilderBackend(JNIEnv* env, jclass clazz, jlong nativeBuilder, jlong backend) {
    ((Engine::Builder*) nativeBuilder)->backend((Engine::Backend) backend);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Engine_nSetBuilderSharedContext(JNIEnv* env, jclass clazz, jlong nativeBuilder, jlong sharedContext) {
    ((Engine::Builder*) nativeBuilder)->sharedContext((void*) sharedContext);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Engine_nSetBuilderConfig(JNIEnv* env, jclass clazz, jlong nativeBuilder,
        jlong commandBufferSizeMB, jlong perRenderPassArenaSizeMB, jlong driverHandleArenaSizeMB,
        jlong minCommandBufferSizeMB, jlong perFrameCommandsSizeMB, jlong jobSystemThreadCount,
        jboolean disableParallelShaderCompile, jint stereoscopicType, jlong stereoscopicEyeCount,
        jlong resourceAllocatorCacheSizeMB, jlong resourceAllocatorCacheMaxAge,
        jboolean disableHandleUseAfterFreeCheck, jint preferredShaderLanguage,
        jboolean forceGLES2Context, jboolean assertNativeWindowIsValid, jint gpuContextPriority,
        jlong sharedUboInitialSizeInBytes) {
    
    Engine::Config config;
    config.commandBufferSizeMB = (uint32_t) commandBufferSizeMB;
    config.perRenderPassArenaSizeMB = (uint32_t) perRenderPassArenaSizeMB;
    config.driverHandleArenaSizeMB = (uint32_t) driverHandleArenaSizeMB;
    config.minCommandBufferSizeMB = (uint32_t) minCommandBufferSizeMB;
    config.perFrameCommandsSizeMB = (uint32_t) perFrameCommandsSizeMB;
    config.jobSystemThreadCount = (uint32_t) jobSystemThreadCount;
    // config.disableParallelShaderCompile = (bool) disableParallelShaderCompile;
    config.stereoscopicType = (Engine::StereoscopicType) stereoscopicType;
    config.stereoscopicEyeCount = (uint8_t) stereoscopicEyeCount;
    // config.resourceAllocatorCacheSizeMB = (uint32_t) resourceAllocatorCacheSizeMB;
    config.resourceAllocatorCacheMaxAge = (uint8_t) resourceAllocatorCacheMaxAge;
    // config.disableHandleUseAfterFreeCheck = (bool) disableHandleUseAfterFreeCheck;
    config.preferredShaderLanguage = (Engine::Config::ShaderLanguage) preferredShaderLanguage;
    config.forceGLES2Context = (bool) forceGLES2Context;
    // config.assertNativeWindowIsValid = (bool) assertNativeWindowIsValid;
    config.gpuContextPriority = (Engine::GpuContextPriority) gpuContextPriority;
    config.sharedUboInitialSizeInBytes = (uint32_t) sharedUboInitialSizeInBytes;

    ((Engine::Builder*) nativeBuilder)->config(&config);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Engine_nSetBuilderFeatureLevel(JNIEnv* env, jclass clazz, jlong nativeBuilder, jint featureLevel) {
    ((Engine::Builder*) nativeBuilder)->featureLevel((Engine::FeatureLevel) featureLevel);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Engine_nSetBuilderPaused(JNIEnv* env, jclass clazz, jlong nativeBuilder, jboolean paused) {
    ((Engine::Builder*) nativeBuilder)->paused(paused);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Engine_nSetBuilderFeature(JNIEnv* env, jclass clazz, jlong nativeBuilder, jstring name, jboolean value) {
    const char* nativeName = env->GetStringUTFChars(name, nullptr);
    ((Engine::Builder*) nativeBuilder)->feature(nativeName, value);
    env->ReleaseStringUTFChars(name, nativeName);
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_Engine_nBuilderBuild(JNIEnv* env, jclass clazz, jlong nativeBuilder) {
    return (jlong) ((Engine::Builder*) nativeBuilder)->build();
}

// Engine

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Engine_nDestroyEngine(JNIEnv* env, jclass clazz, jlong nativeEngine) {
    Engine* engine = (Engine*) nativeEngine;
    Engine::destroy(&engine);
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_Engine_nGetBackend(JNIEnv* env, jclass clazz, jlong nativeEngine) {
    return (jlong) ((Engine*) nativeEngine)->getBackend();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Engine_nFlush(JNIEnv* env, jclass clazz, jlong nativeEngine) {
    ((Engine*) nativeEngine)->flush();
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_Engine_nFlushAndWait(JNIEnv* env, jclass clazz, jlong nativeEngine, jlong timeout) {
    return ((Engine*) nativeEngine)->flushAndWait(timeout);
}

// SwapChain

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_Engine_nCreateSwapChain(JNIEnv* env, jclass clazz, jlong nativeEngine, jlong nativeWindow, jlong flags) {
    return (jlong) ((Engine*) nativeEngine)->createSwapChain((void*) nativeWindow, (uint64_t) flags);
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_Engine_nDestroySwapChain(JNIEnv* env, jclass clazz, jlong nativeEngine, jlong nativeSwapChain) {
    return ((Engine*) nativeEngine)->destroy((SwapChain*) nativeSwapChain);
}

// View

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_Engine_nCreateView(JNIEnv* env, jclass clazz, jlong nativeEngine) {
    return (jlong) ((Engine*) nativeEngine)->createView();
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_Engine_nDestroyView(JNIEnv* env, jclass clazz, jlong nativeEngine, jlong nativeView) {
    return ((Engine*) nativeEngine)->destroy((View*) nativeView);
}

// Renderer

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_Engine_nCreateRenderer(JNIEnv* env, jclass clazz, jlong nativeEngine) {
    return (jlong) ((Engine*) nativeEngine)->createRenderer();
}

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_Engine_nDestroyRenderer(JNIEnv* env, jclass clazz, jlong nativeEngine, jlong nativeRenderer) {
    return ((Engine*) nativeEngine)->destroy((Renderer*) nativeRenderer);
}

} // extern "C"
