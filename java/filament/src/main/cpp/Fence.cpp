#include <jni.h>
#include <filament/Fence.h>

using namespace filament;

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_Fence_nWait(JNIEnv* env, jclass, jlong nativeFence, jint mode, jlong timeoutNano) {
    return (jint) ((Fence*) nativeFence)->wait((Fence::Mode) mode, (uint64_t) timeoutNano);
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_jni_Fence_nWaitAndDestroy(JNIEnv* env, jclass, jlong nativeFence, jint mode) {
    return (jint) Fence::waitAndDestroy((Fence*) nativeFence, (Fence::Mode) mode);
}
