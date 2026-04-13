#include <jni.h>
#include <filament/Camera.h>
#include <math/mat4.h>

using namespace filament;
using namespace math;

extern "C" {

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Camera_nSetProjection(JNIEnv* env, jclass clazz, jlong nativeCamera, jdouble fov, jdouble aspect, jdouble near, jdouble far, jint projection) {
    ((Camera*) nativeCamera)->setProjection(fov, aspect, near, far, (Camera::Projection) projection);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Camera_nSetModelMatrix(JNIEnv* env, jclass clazz, jlong nativeCamera, jfloatArray matrix) {
    jfloat* m = env->GetFloatArrayElements(matrix, nullptr);
    ((Camera*) nativeCamera)->setModelMatrix(*reinterpret_cast<const mat4f*>(m));
    env->ReleaseFloatArrayElements(matrix, m, JNI_ABORT);
}

} // extern "C"
