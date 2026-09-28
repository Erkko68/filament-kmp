// Android-only JNI entry points, linked into libfilament-c.so next to jni/'s portable ones.
// Backs io.github.erkko68.filament.jni.FilaAndroid.

#include <jni.h>
#include <android/native_window_jni.h>

#define FILA_ANDROID(ret, name) extern "C" JNIEXPORT ret JNICALL Java_io_github_erkko68_filament_jni_FilaAndroid_##name

FILA_ANDROID(jlong, windowFromSurface)(JNIEnv* env, jclass, jobject surface) {
    return reinterpret_cast<jlong>(ANativeWindow_fromSurface(env, surface));
}

FILA_ANDROID(void, releaseWindow)(JNIEnv*, jclass, jlong window) {
    ANativeWindow_release(reinterpret_cast<ANativeWindow*>(window));
}
