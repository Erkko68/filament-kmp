#include <jni.h>
#include <filament/View.h>
#include <filament/Scene.h>
#include <filament/Camera.h>
#include <filament/Viewport.h>

using namespace filament;

extern "C" {

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_View_nSetScene(JNIEnv* env, jclass clazz, jlong nativeView, jlong nativeScene) {
    ((View*) nativeView)->setScene((Scene*) nativeScene);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_View_nSetCamera(JNIEnv* env, jclass clazz, jlong nativeView, jlong nativeCamera) {
    ((View*) nativeView)->setCamera((Camera*) nativeCamera);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_View_nSetViewport(JNIEnv* env, jclass clazz, jlong nativeView, jint left, jint bottom, jint width, jint height) {
    ((View*) nativeView)->setViewport({left, bottom, (uint32_t) width, (uint32_t) height});
}

} // extern "C"
