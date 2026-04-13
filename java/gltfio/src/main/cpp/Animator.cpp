#include <jni.h>
#include <gltfio/Animator.h>
#include <utils/Entity.h>

using namespace filament;
using namespace gltfio;

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_Animator_nApplyAnimation(JNIEnv* env, jclass, jlong nativeAnimator, jint index, jfloat time) {
    ((Animator*) nativeAnimator)->applyAnimation((size_t) index, (float) time);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_Animator_nUpdateBoneMatrices(JNIEnv* env, jclass, jlong nativeAnimator) {
    ((Animator*) nativeAnimator)->updateBoneMatrices();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_Animator_nApplyCrossFade(JNIEnv* env, jclass, jlong nativeAnimator, jint animIndex, jfloat animTime, jfloat alpha) {
    ((Animator*) nativeAnimator)->applyCrossFade((size_t) animIndex, (float) animTime, (float) alpha);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_Animator_nResetBoneMatrices(JNIEnv* env, jclass, jlong nativeAnimator) {
    ((Animator*) nativeAnimator)->resetBoneMatrices();
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_gltfio_Animator_nGetAnimationCount(JNIEnv* env, jclass, jlong nativeAnimator) {
    return (jint) ((Animator*) nativeAnimator)->getAnimationCount();
}

extern "C" JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_gltfio_Animator_nGetAnimationDuration(JNIEnv* env, jclass, jlong nativeAnimator, jint index) {
    return (jfloat) ((Animator*) nativeAnimator)->getAnimationDuration((size_t) index);
}

extern "C" JNIEXPORT jstring JNICALL
Java_io_github_erkko68_filament_gltfio_Animator_nGetAnimationName(JNIEnv* env, jclass, jlong nativeAnimator, jint index) {
    return env->NewStringUTF(((Animator*) nativeAnimator)->getAnimationName((size_t) index));
}
