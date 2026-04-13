#include <jni.h>
#include <filament/Renderer.h>
#include <filament/SwapChain.h>
#include <filament/View.h>

using namespace filament;

extern "C" {

JNIEXPORT jboolean JNICALL
Java_io_github_erkko68_filament_Renderer_nBeginFrame(JNIEnv* env, jclass clazz, jlong nativeRenderer, jlong nativeSwapChain) {
    return ((Renderer*) nativeRenderer)->beginFrame((SwapChain*) nativeSwapChain);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Renderer_nRender(JNIEnv* env, jclass clazz, jlong nativeRenderer, jlong nativeView) {
    ((Renderer*) nativeRenderer)->render((View*) nativeView);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Renderer_nEndFrame(JNIEnv* env, jclass clazz, jlong nativeRenderer) {
    ((Renderer*) nativeRenderer)->endFrame();
}

} // extern "C"
