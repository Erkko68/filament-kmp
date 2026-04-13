#include <jni.h>
#include <filament/Fence.h>

using namespace filament;

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_Fence_nWait(JNIEnv* env, jclass, jlong nativeFence, jint type, jlong timeoutNano) {
    return (jint) ((Fence*) nativeFence)->wait((Fence::Type) type, (uint64_t) timeoutNano);
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_Fence_nWaitAndDestroy(JNIEnv* env, jclass, jlong nativeFence, jint type) {
    return (jint) Fence::waitAndDestroy((Fence*) nativeFence, (Fence::Type) type);
}
