#include <jni.h>
#include <filament/Camera.h>
#include <math/mat4.h>

using namespace filament;
using namespace math;

extern "C" {

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Camera_nSetProjection(JNIEnv* env, jclass, jlong nativeCamera, jint projection, jdouble left, jdouble right, jdouble bottom, jdouble top, jdouble near, jdouble far) {
    ((Camera*) nativeCamera)->setProjection((Camera::Projection) projection, left, right, bottom, top, near, far);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Camera_nSetProjectionFov(JNIEnv* env, jclass, jlong nativeCamera, jdouble fovInDegrees, jdouble aspect, jdouble near, jdouble far, jint direction) {
    ((Camera*) nativeCamera)->setProjection(fovInDegrees, aspect, near, far, (Camera::Fov) direction);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Camera_nLookAt(JNIEnv* env, jclass, jlong nativeCamera, jdouble eyeX, jdouble eyeY, jdouble eyeZ, jdouble centerX, jdouble centerY, jdouble centerZ, jdouble upX, jdouble upY, jdouble upZ) {
    ((Camera*) nativeCamera)->lookAt({eyeX, eyeY, eyeZ}, {centerX, centerY, centerZ}, {upX, upY, upZ});
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Camera_nSetModelMatrix(JNIEnv* env, jclass, jlong nativeCamera, jfloatArray matrix) {
    jfloat* m = env->GetFloatArrayElements(matrix, nullptr);
    ((Camera*) nativeCamera)->setModelMatrix(*reinterpret_cast<const mat4f*>(m));
    env->ReleaseFloatArrayElements(matrix, m, JNI_ABORT);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Camera_nSetExposure(JNIEnv* env, jclass, jlong nativeCamera, jfloat aperture, jfloat shutterSpeed, jfloat sensitivity) {
    ((Camera*) nativeCamera)->setExposure(aperture, shutterSpeed, sensitivity);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Camera_nSetFocusDistance(JNIEnv* env, jclass, jlong nativeCamera, jfloat distance) {
    ((Camera*) nativeCamera)->setFocusDistance(distance);
}

JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_Camera_nGetNear(JNIEnv* env, jclass, jlong nativeCamera) {
    return ((Camera*) nativeCamera)->getNear();
}

JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_Camera_nGetCullingFar(JNIEnv* env, jclass, jlong nativeCamera) {
    return ((Camera*) nativeCamera)->getCullingFar();
}

JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_Camera_nGetAperture(JNIEnv* env, jclass, jlong nativeCamera) {
    return ((Camera*) nativeCamera)->getAperture();
}

JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_Camera_nGetShutterSpeed(JNIEnv* env, jclass, jlong nativeCamera) {
    return ((Camera*) nativeCamera)->getShutterSpeed();
}

JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_Camera_nGetSensitivity(JNIEnv* env, jclass, jlong nativeCamera) {
    return ((Camera*) nativeCamera)->getSensitivity();
}

JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_Camera_nGetFocusDistance(JNIEnv* env, jclass, jlong nativeCamera) {
    return ((Camera*) nativeCamera)->getFocusDistance();
}

} // extern "C"
