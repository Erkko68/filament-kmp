#include <jni.h>
#include <filament/LightManager.h>
#include <filament/Engine.h>

using namespace filament;

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_LightManager_nGetInstance(JNIEnv* env, jclass, jlong nativeLightManager, jint entity) {
    return (jint) ((LightManager*) nativeLightManager)->getInstance((utils::Entity&) entity);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_nSetIntensity(JNIEnv* env, jclass, jlong nativeLightManager, jint instance, jfloat intensity) {
    ((LightManager*) nativeLightManager)->setIntensity((LightManager::Instance) instance, (float) intensity);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_nSetColor(JNIEnv* env, jclass, jlong nativeLightManager, jint instance, jfloat r, jfloat g, jfloat b) {
    ((LightManager*) nativeLightManager)->setColor((LightManager::Instance) instance, {r, g, b});
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_nSetDirection(JNIEnv* env, jclass, jlong nativeLightManager, jint instance, jfloat x, jfloat y, jfloat z) {
    ((LightManager*) nativeLightManager)->setDirection((LightManager::Instance) instance, {x, y, z});
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_nSetPosition(JNIEnv* env, jclass, jlong nativeLightManager, jint instance, jfloat x, jfloat y, jfloat z) {
    ((LightManager*) nativeLightManager)->setPosition((LightManager::Instance) instance, {x, y, z});
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_nSetFalloff(JNIEnv* env, jclass, jlong nativeLightManager, jint instance, jfloat falloff) {
    ((LightManager*) nativeLightManager)->setFalloff((LightManager::Instance) instance, (float) falloff);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_nSetSunAngularRadius(JNIEnv* env, jclass, jlong nativeLightManager, jint instance, jfloat radius) {
    ((LightManager*) nativeLightManager)->setSunAngularRadius((LightManager::Instance) instance, (float) radius);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_nSetSunHaloSize(JNIEnv* env, jclass, jlong nativeLightManager, jint instance, jfloat size) {
    ((LightManager*) nativeLightManager)->setSunHaloSize((LightManager::Instance) instance, (float) size);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_nSetSunHaloFalloff(JNIEnv* env, jclass, jlong nativeLightManager, jint instance, jfloat falloff) {
    ((LightManager*) nativeLightManager)->setSunHaloFalloff((LightManager::Instance) instance, (float) falloff);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_LightManager_nDestroy(JNIEnv* env, jclass, jlong nativeLightManager, jint entity) {
    ((LightManager*) nativeLightManager)->destroy((utils::Entity&) entity);
}

extern "C" JNIEXPORT long JNICALL
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
