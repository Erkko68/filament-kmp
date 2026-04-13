#include <jni.h>
#include <camutils/CameraManipulator.h>

using namespace filament::camutils;

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nCreateBuilder(JNIEnv* env, jclass) {
    return (jlong) new CameraManipulator::Builder();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nDestroyBuilder(JNIEnv* env, jclass, jlong nativeBuilder) {
    delete (CameraManipulator::Builder*) nativeBuilder;
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderViewport(JNIEnv* env, jclass, jlong nativeBuilder, jint width, jint height) {
    ((CameraManipulator::Builder*) nativeBuilder)->viewport(width, height);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderTargetPosition(JNIEnv* env, jclass, jlong nativeBuilder, jfloat x, jfloat y, jfloat z) {
    ((CameraManipulator::Builder*) nativeBuilder)->targetPosition(x, y, z);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderUpVector(JNIEnv* env, jclass, jlong nativeBuilder, jfloat x, jfloat y, jfloat z) {
    ((CameraManipulator::Builder*) nativeBuilder)->upVector(x, y, z);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderZoomSpeed(JNIEnv* env, jclass, jlong nativeBuilder, jfloat arg) {
    ((CameraManipulator::Builder*) nativeBuilder)->zoomSpeed(arg);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderOrbitHomePosition(JNIEnv* env, jclass, jlong nativeBuilder, jfloat x, jfloat y, jfloat z) {
    ((CameraManipulator::Builder*) nativeBuilder)->orbitHomePosition(x, y, z);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderOrbitSpeed(JNIEnv* env, jclass, jlong nativeBuilder, jfloat x, jfloat y) {
    ((CameraManipulator::Builder*) nativeBuilder)->orbitSpeed(x, y);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFovDirection(JNIEnv* env, jclass, jlong nativeBuilder, jint arg) {
    ((CameraManipulator::Builder*) nativeBuilder)->fovDirection((Fov) arg);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFovDegrees(JNIEnv* env, jclass, jlong nativeBuilder, jfloat arg) {
    ((CameraManipulator::Builder*) nativeBuilder)->fovDegrees(arg);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFarPlane(JNIEnv* env, jclass, jlong nativeBuilder, jfloat distance) {
    ((CameraManipulator::Builder*) nativeBuilder)->farPlane(distance);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderMapExtent(JNIEnv* env, jclass, jlong nativeBuilder, jfloat width, jfloat height) {
    ((CameraManipulator::Builder*) nativeBuilder)->mapExtent(width, height);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderMapMinDistance(JNIEnv* env, jclass, jlong nativeBuilder, jfloat arg) {
    ((CameraManipulator::Builder*) nativeBuilder)->mapMinDistance(arg);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFlightStartPosition(JNIEnv* env, jclass, jlong nativeBuilder, jfloat x, jfloat y, jfloat z) {
    ((CameraManipulator::Builder*) nativeBuilder)->flightStartPosition(x, y, z);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFlightStartOrientation(JNIEnv* env, jclass, jlong nativeBuilder, jfloat pitch, jfloat yaw) {
    ((CameraManipulator::Builder*) nativeBuilder)->flightStartOrientation(pitch, yaw);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFlightMaxMoveSpeed(JNIEnv* env, jclass, jlong nativeBuilder, jfloat maxSpeed) {
    ((CameraManipulator::Builder*) nativeBuilder)->flightMaxMoveSpeed(maxSpeed);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFlightSpeedSteps(JNIEnv* env, jclass, jlong nativeBuilder, jint steps) {
    ((CameraManipulator::Builder*) nativeBuilder)->flightSpeedSteps(steps);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFlightPanSpeed(JNIEnv* env, jclass, jlong nativeBuilder, jfloat x, jfloat y) {
    ((CameraManipulator::Builder*) nativeBuilder)->flightPanSpeed(x, y);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFlightMoveDamping(JNIEnv* env, jclass, jlong nativeBuilder, jfloat damping) {
    ((CameraManipulator::Builder*) nativeBuilder)->flightMoveDamping(damping);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderGroundPlane(JNIEnv* env, jclass, jlong nativeBuilder, jfloat a, jfloat b, jfloat c, jfloat d) {
    ((CameraManipulator::Builder*) nativeBuilder)->groundPlane(a, b, c, d);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderPanning(JNIEnv* env, jclass, jlong nativeBuilder, jboolean enabled) {
    ((CameraManipulator::Builder*) nativeBuilder)->panning(enabled);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderBuild(JNIEnv* env, jclass, jlong nativeBuilder, jint mode) {
    return (jlong) ((CameraManipulator::Builder*) nativeBuilder)->build((Mode) mode);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_nDestroyManipulator(JNIEnv* env, jclass, jlong nativeManip) {
    delete (CameraManipulator*) nativeManip;
}

extern "C" JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_nGetMode(JNIEnv* env, jclass, jlong nativeManip) {
    return (jint) ((CameraManipulator*) nativeManip)->getMode();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_nSetViewport(JNIEnv* env, jclass, jlong nativeManip, jint width, jint height) {
    ((CameraManipulator*) nativeManip)->setViewport(width, height);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_nGetLookAtFloat(JNIEnv* env, jclass, jlong nativeManip, jfloatArray eyePosition, jfloatArray targetPosition, jfloatArray upward) {
    float eye[3], target[3], up[3];
    ((CameraManipulator*) nativeManip)->getLookAt(eye, target, up);
    env->SetFloatArrayRegion(eyePosition, 0, 3, eye);
    env->SetFloatArrayRegion(targetPosition, 0, 3, target);
    env->SetFloatArrayRegion(upward, 0, 3, up);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_nGetLookAtDouble(JNIEnv* env, jclass, jlong nativeManip, jdoubleArray eyePosition, jdoubleArray targetPosition, jdoubleArray upward) {
    double eye[3], target[3], up[3];
    ((CameraManipulator*) nativeManip)->getLookAt(eye, target, up);
    env->SetDoubleArrayRegion(eyePosition, 0, 3, eye);
    env->SetDoubleArrayRegion(targetPosition, 0, 3, target);
    env->SetDoubleArrayRegion(upward, 0, 3, up);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_nRaycast(JNIEnv* env, jclass, jlong nativeManip, jint x, jint y, jfloatArray result) {
    float res[3];
    ((CameraManipulator*) nativeManip)->raycast(x, y, res);
    env->SetFloatArrayRegion(result, 0, 3, res);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_nGrabBegin(JNIEnv* env, jclass, jlong nativeManip, jint x, jint y, jboolean strafe) {
    ((CameraManipulator*) nativeManip)->grabBegin(x, y, strafe);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_nGrabUpdate(JNIEnv* env, jclass, jlong nativeManip, jint x, jint y) {
    ((CameraManipulator*) nativeManip)->grabUpdate(x, y);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_nGrabEnd(JNIEnv* env, jclass, jlong nativeManip) {
    ((CameraManipulator*) nativeManip)->grabEnd();
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_nKeyDown(JNIEnv* env, jclass, jlong nativeManip, jint key) {
    ((CameraManipulator*) nativeManip)->keyDown((CameraManipulator::Key) key);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_nKeyUp(JNIEnv* env, jclass, jlong nativeManip, jint key) {
    ((CameraManipulator*) nativeManip)->keyUp((CameraManipulator::Key) key);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_nScroll(JNIEnv* env, jclass, jlong nativeManip, jint x, jint y, jfloat scrolldelta) {
    ((CameraManipulator*) nativeManip)->scroll(x, y, scrolldelta);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_nUpdate(JNIEnv* env, jclass, jlong nativeManip, jfloat deltaTime) {
    ((CameraManipulator*) nativeManip)->update(deltaTime);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_nGetCurrentBookmark(JNIEnv* env, jclass, jlong nativeManip) {
    // In camutils, getCurrentBookmark returns a struct, usually JNI implementation allocates it on heap
    auto bookmark = ((CameraManipulator*) nativeManip)->getCurrentBookmark();
    return (jlong) new CameraManipulator::Bookmark(bookmark);
}

extern "C" JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_nGetHomeBookmark(JNIEnv* env, jclass, jlong nativeManip) {
    auto bookmark = ((CameraManipulator*) nativeManip)->getHomeBookmark();
    return (jlong) new CameraManipulator::Bookmark(bookmark);
}

extern "C" JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_nJumpToBookmark(JNIEnv* env, jclass, jlong nativeManip, jlong nativeBookmark) {
    ((CameraManipulator*) nativeManip)->jumpToBookmark(*(CameraManipulator::Bookmark*) nativeBookmark);
}
