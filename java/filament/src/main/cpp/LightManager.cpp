#include <jni.h>
#include <filament/LightManager.h>
#include <filament/Engine.h>

using namespace filament;

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_LightManager_00024Builder_nCreateBuilder(JNIEnv* env, jclass, jint type) {
    return (jlong) new LightManager::Builder((LightManager::Type) type);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_00024Builder_nDestroyBuilder(JNIEnv* env, jclass, jlong nativeBuilder) {
    delete (LightManager::Builder*) nativeBuilder;
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_00024Builder_nBuilderCastShadows(JNIEnv* env, jclass, jlong nativeBuilder, jboolean enabled) {
    ((LightManager::Builder*) nativeBuilder)->castShadows((bool) enabled);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_00024Builder_nBuilderColor(JNIEnv* env, jclass, jlong nativeBuilder, jfloat r, jfloat g, jfloat b) {
    ((LightManager::Builder*) nativeBuilder)->color({r, g, b});
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_00024Builder_nBuilderIntensity(JNIEnv* env, jclass, jlong nativeBuilder, jfloat intensity) {
    ((LightManager::Builder*) nativeBuilder)->intensity((float) intensity);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_00024Builder_nBuilderDirection(JNIEnv* env, jclass, jlong nativeBuilder, jfloat x, jfloat y, jfloat z) {
    ((LightManager::Builder*) nativeBuilder)->direction({x, y, z});
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_00024Builder_nBuilderBuild(JNIEnv* env, jclass, jlong nativeBuilder, jlong nativeEngine, jint entity) {
    ((LightManager::Builder*) nativeBuilder)->build(*(Engine*) nativeEngine, (utils::Entity&) entity);
}
