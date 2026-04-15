#include <jni.h>
#include <gltfio/Animator.h>

using namespace filament;
using namespace filament::gltfio;

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_jni_Animator_nApplyAnimation(JNIEnv*, jclass, jlong nativeAnimator,
        jint index, jfloat time) {
    Animator* animator = (Animator*) nativeAnimator;
    animator->applyAnimation(static_cast<size_t>(index), time);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_jni_Animator_nUpdateBoneMatrices(JNIEnv*, jclass, jlong nativeAnimator) {
    Animator* animator = (Animator*) nativeAnimator;
    animator->updateBoneMatrices();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_jni_Animator_nApplyCrossFade(JNIEnv*, jclass, jlong nativeAnimator,
        jint previousAnimIndex, jfloat previousAnimTime, jfloat alpha) {
    Animator* animator = (Animator*) nativeAnimator;
    animator->applyCrossFade(previousAnimIndex, previousAnimTime, alpha);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_gltfio_jni_Animator_nResetBoneMatrices(JNIEnv*, jclass, jlong nativeAnimator) {
    Animator* animator = (Animator*) nativeAnimator;
    animator->resetBoneMatrices();
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_gltfio_jni_Animator_nGetAnimationCount(JNIEnv*, jclass, jlong nativeAnimator) {
    Animator* animator = (Animator*) nativeAnimator;
    return (jint) animator->getAnimationCount();
}

extern "C" JNIEXPORT float JNICALL
Java_io_github_erkko68_filament_gltfio_jni_Animator_nGetAnimationDuration(JNIEnv*, jclass,
        jlong nativeAnimator, jint index) {
    Animator* animator = (Animator*) nativeAnimator;
    return animator->getAnimationDuration(static_cast<size_t>(index));
}

extern "C" JNIEXPORT jstring JNICALL
Java_io_github_erkko68_filament_gltfio_jni_Animator_nGetAnimationName(JNIEnv* env, jclass,
        jlong nativeAnimator, jint index) {
    Animator* animator = (Animator*) nativeAnimator;
    const char* val = animator->getAnimationName(static_cast<size_t>(index));
    return val ? env->NewStringUTF(val) : nullptr;
}
