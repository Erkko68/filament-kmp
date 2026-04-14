#include <jni.h>
#include <camutils/Manipulator.h>
#include <camutils/Bookmark.h>

using namespace filament::camutils;

using CameraManipulator = Manipulator<float>;

extern "C" {

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nCreateBuilder(JNIEnv*, jclass) {
    return (jlong) new CameraManipulator::Builder();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nDestroyBuilder(JNIEnv*, jclass, jlong nativeBuilder) {
    delete (CameraManipulator::Builder*) nativeBuilder;
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderViewport(JNIEnv*, jclass, jlong nativeBuilder, jint width, jint height) {
    ((CameraManipulator::Builder*) nativeBuilder)->viewport(width, height);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderTargetPosition(JNIEnv*, jclass, jlong nativeBuilder, jfloat x, jfloat y, jfloat z) {
    ((CameraManipulator::Builder*) nativeBuilder)->targetPosition(x, y, z);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderUpVector(JNIEnv*, jclass, jlong nativeBuilder, jfloat x, jfloat y, jfloat z) {
    ((CameraManipulator::Builder*) nativeBuilder)->upVector(x, y, z);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderZoomSpeed(JNIEnv*, jclass, jlong nativeBuilder, jfloat arg) {
    ((CameraManipulator::Builder*) nativeBuilder)->zoomSpeed(arg);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderOrbitHomePosition(JNIEnv*, jclass, jlong nativeBuilder, jfloat x, jfloat y, jfloat z) {
    ((CameraManipulator::Builder*) nativeBuilder)->orbitHomePosition(x, y, z);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderOrbitSpeed(JNIEnv*, jclass, jlong nativeBuilder, jfloat x, jfloat y) {
    ((CameraManipulator::Builder*) nativeBuilder)->orbitSpeed(x, y);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFovDirection(JNIEnv*, jclass, jlong nativeBuilder, jint arg) {
    ((CameraManipulator::Builder*) nativeBuilder)->fovDirection((Fov) arg);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFovDegrees(JNIEnv*, jclass, jlong nativeBuilder, jfloat arg) {
    ((CameraManipulator::Builder*) nativeBuilder)->fovDegrees(arg);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFarPlane(JNIEnv*, jclass, jlong nativeBuilder, jfloat distance) {
    ((CameraManipulator::Builder*) nativeBuilder)->farPlane(distance);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderMapExtent(JNIEnv*, jclass, jlong nativeBuilder, jfloat width, jfloat height) {
    ((CameraManipulator::Builder*) nativeBuilder)->mapExtent(width, height);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderMapMinDistance(JNIEnv*, jclass, jlong nativeBuilder, jfloat arg) {
    ((CameraManipulator::Builder*) nativeBuilder)->mapMinDistance(arg);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFlightStartPosition(JNIEnv*, jclass, jlong nativeBuilder, jfloat x, jfloat y, jfloat z) {
    ((CameraManipulator::Builder*) nativeBuilder)->flightStartPosition(x, y, z);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFlightStartOrientation(JNIEnv*, jclass, jlong nativeBuilder, jfloat pitch, jfloat yaw) {
    ((CameraManipulator::Builder*) nativeBuilder)->flightStartOrientation(pitch, yaw);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFlightMaxMoveSpeed(JNIEnv*, jclass, jlong nativeBuilder, jfloat maxSpeed) {
    ((CameraManipulator::Builder*) nativeBuilder)->flightMaxMoveSpeed(maxSpeed);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFlightSpeedSteps(JNIEnv*, jclass, jlong nativeBuilder, jint steps) {
    ((CameraManipulator::Builder*) nativeBuilder)->flightSpeedSteps(steps);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFlightPanSpeed(JNIEnv*, jclass, jlong nativeBuilder, jfloat x, jfloat y) {
    ((CameraManipulator::Builder*) nativeBuilder)->flightPanSpeed(x, y);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderFlightMoveDamping(JNIEnv*, jclass, jlong nativeBuilder, jfloat damping) {
    ((CameraManipulator::Builder*) nativeBuilder)->flightMoveDamping(damping);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderGroundPlane(JNIEnv*, jclass, jlong nativeBuilder, jfloat a, jfloat b, jfloat c, jfloat d) {
    ((CameraManipulator::Builder*) nativeBuilder)->groundPlane(a, b, c, d);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderPanning(JNIEnv*, jclass, jlong nativeBuilder, jboolean enabled) {
    ((CameraManipulator::Builder*) nativeBuilder)->panning(enabled);
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_00024Builder_nBuilderBuild(JNIEnv*, jclass, jlong nativeBuilder, jint mode) {
    return (jlong) ((CameraManipulator::Builder*) nativeBuilder)->build((Mode) mode);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_nDestroyManipulator(JNIEnv*, jclass, jlong nativeManip) {
    delete (CameraManipulator*) nativeManip;
}

JNIEXPORT jint JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_nGetMode(JNIEnv*, jclass, jlong nativeManip) {
    return (jint) ((CameraManipulator*) nativeManip)->getMode();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_nSetViewport(JNIEnv*, jclass, jlong nativeManip, jint width, jint height) {
    ((CameraManipulator*) nativeManip)->setViewport(width, height);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_nGetLookAt(JNIEnv* env, jclass, jlong nativeManip, jfloatArray eyePosition, jfloatArray targetPosition, jfloatArray upward) {
    filament::math::vec3<float> eye, target, up;
    ((CameraManipulator*) nativeManip)->getLookAt(&eye, &target, &up);
    env->SetFloatArrayRegion(eyePosition, 0, 3, &eye.x);
    env->SetFloatArrayRegion(targetPosition, 0, 3, &target.x);
    env->SetFloatArrayRegion(upward, 0, 3, &up.x);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_nRaycast(JNIEnv* env, jclass, jlong nativeManip, jint x, jint y, jfloatArray result) {
    filament::math::vec3<float> res;
    ((CameraManipulator*) nativeManip)->raycast(x, y, &res);
    env->SetFloatArrayRegion(result, 0, 3, &res.x);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_nGrabBegin(JNIEnv*, jclass, jlong nativeManip, jint x, jint y, jboolean strafe) {
    ((CameraManipulator*) nativeManip)->grabBegin(x, y, strafe);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_nGrabUpdate(JNIEnv*, jclass, jlong nativeManip, jint x, jint y) {
    ((CameraManipulator*) nativeManip)->grabUpdate(x, y);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_nGrabEnd(JNIEnv*, jclass, jlong nativeManip) {
    ((CameraManipulator*) nativeManip)->grabEnd();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_nKeyDown(JNIEnv*, jclass, jlong nativeManip, jint key) {
    ((CameraManipulator*) nativeManip)->keyDown((CameraManipulator::Key) key);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_nKeyUp(JNIEnv*, jclass, jlong nativeManip, jint key) {
    ((CameraManipulator*) nativeManip)->keyUp((CameraManipulator::Key) key);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_nScroll(JNIEnv*, jclass, jlong nativeManip, jint x, jint y, jfloat scrolldelta) {
    ((CameraManipulator*) nativeManip)->scroll(x, y, scrolldelta);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_nUpdate(JNIEnv*, jclass, jlong nativeManip, jfloat deltaTime) {
    ((CameraManipulator*) nativeManip)->update(deltaTime);
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_nGetCurrentBookmark(JNIEnv*, jclass, jlong nativeManip) {
    auto bookmark = ((CameraManipulator*) nativeManip)->getCurrentBookmark();
    return (jlong) new CameraManipulator::Bookmark(bookmark);
}

JNIEXPORT jlong JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_nGetHomeBookmark(JNIEnv*, jclass, jlong nativeManip) {
    auto bookmark = ((CameraManipulator*) nativeManip)->getHomeBookmark();
    return (jlong) new CameraManipulator::Bookmark(bookmark);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_utils_Manipulator_nJumpToBookmark(JNIEnv*, jclass, jlong nativeManip, jlong nativeBookmark) {
    ((CameraManipulator*) nativeManip)->jumpToBookmark(*(CameraManipulator::Bookmark*) nativeBookmark);
}

}
