#include <jni.h>
#include <filament/Skybox.h>
#include <filament/Engine.h>
#include <filament/Texture.h>
#include <math/vec4.h>

using namespace filament;

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_Skybox_00024Builder_nCreateBuilder(JNIEnv* env, jclass) {
    return (jlong) new Skybox::Builder();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Skybox_00024Builder_nDestroyBuilder(JNIEnv* env, jclass, jlong nativeBuilder) {
    delete (Skybox::Builder*) nativeBuilder;
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Skybox_00024Builder_nBuilderEnvironment(JNIEnv* env, jclass, jlong nativeBuilder, jlong nativeTexture) {
    ((Skybox::Builder*) nativeBuilder)->environment((Texture*) nativeTexture);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Skybox_00024Builder_nBuilderShowSun(JNIEnv* env, jclass, jlong nativeBuilder, jboolean show) {
    ((Skybox::Builder*) nativeBuilder)->showSun((bool) show);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Skybox_00024Builder_nBuilderIntensity(JNIEnv* env, jclass, jlong nativeBuilder, jfloat intensity) {
    ((Skybox::Builder*) nativeBuilder)->intensity((float) intensity);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Skybox_00024Builder_nBuilderColor(JNIEnv* env, jclass, jlong nativeBuilder, jfloat r, jfloat g, jfloat b, jfloat a) {
    ((Skybox::Builder*) nativeBuilder)->color({r, g, b, a});
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_Skybox_00024Builder_nBuilderBuild(JNIEnv* env, jclass, jlong nativeBuilder, jlong nativeEngine) {
    return (jlong) ((Skybox::Builder*) nativeBuilder)->build(*(Engine*) nativeEngine);
}

extern "C" JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_Skybox_nGetIntensity(JNIEnv* env, jclass, jlong nativeSkybox) {
    return (jfloat) ((Skybox*) nativeSkybox)->getIntensity();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Skybox_nSetColor(JNIEnv* env, jclass, jlong nativeSkybox, jfloat r, jfloat g, jfloat b, jfloat a) {
    ((Skybox*) nativeSkybox)->setColor({r, g, b, a});
}
