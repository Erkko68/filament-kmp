#include <jni.h>
#include <filament/TransformManager.h>
#include <utils/Entity.h>
#include <math/mat4.h>

using namespace filament;

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_TransformManager_nSetTransform(JNIEnv* env, jclass, jlong nativeTransformManager, jint instance, jfloatArray matrix) {
    jfloat* nativeMatrix = env->GetFloatArrayElements(matrix, nullptr);
    ((TransformManager*) nativeTransformManager)->setTransform((TransformManager::Instance) instance, *(math::mat4f*)nativeMatrix);
    env->ReleaseFloatArrayElements(matrix, nativeMatrix, JNI_ABORT);
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_TransformManager_nGetInstance(JNIEnv* env, jclass, jlong nativeTransformManager, jint entity) {
    return (jint) ((TransformManager*) nativeTransformManager)->getInstance((utils::Entity&) entity);
}
