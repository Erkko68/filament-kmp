/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#include <jni.h>
#include <filament/Camera.h>
#include <utils/Entity.h>
#include <math/mat4.h>
#include <algorithm>

using namespace filament;

extern "C" {

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Camera_nSetProjection(JNIEnv*, jclass, jlong nativeCamera, jint projection, jdouble left, jdouble right, jdouble bottom, jdouble top, jdouble near, jdouble far) {
    Camera* camera = (Camera*) nativeCamera;
    camera->setProjection((Camera::Projection) projection, left, right, bottom, top, near, far);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Camera_nSetProjectionFov(JNIEnv*, jclass, jlong nativeCamera, jdouble fovInDegrees, jdouble aspect, jdouble near, jdouble far, jint direction) {
    Camera* camera = (Camera*) nativeCamera;
    camera->setProjection(fovInDegrees, aspect, near, far, (Camera::Fov) direction);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Camera_nSetLensProjection(JNIEnv*, jclass, jlong nativeCamera, jdouble focalLength, jdouble aspect, jdouble near, jdouble far) {
    Camera* camera = (Camera*) nativeCamera;
    camera->setLensProjection(focalLength, aspect, near, far);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Camera_nSetCustomProjection(JNIEnv* env, jclass, jlong nativeCamera, jdoubleArray inProjection_, jdoubleArray inProjectionForCulling_, jdouble near, jdouble far) {
    Camera* camera = (Camera*) nativeCamera;
    jdouble* inProjection = env->GetDoubleArrayElements(inProjection_, NULL);
    jdouble* inProjectionForCulling = env->GetDoubleArrayElements(inProjectionForCulling_, NULL);
    camera->setCustomProjection(*(const filament::math::mat4*)inProjection, *(const filament::math::mat4*)inProjectionForCulling, near, far);
    env->ReleaseDoubleArrayElements(inProjection_, inProjection, JNI_ABORT);
    env->ReleaseDoubleArrayElements(inProjectionForCulling_, inProjectionForCulling, JNI_ABORT);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Camera_nSetCustomEyeProjection(JNIEnv* env, jclass, jlong nativeCamera, jdoubleArray inProjection_, jint count, jdoubleArray inProjectionForCulling_, jdouble near, jdouble far) {
    Camera* camera = (Camera*) nativeCamera;
    jdouble* inProjection = env->GetDoubleArrayElements(inProjection_, NULL);
    jdouble* inProjectionForCulling = env->GetDoubleArrayElements(inProjectionForCulling_, NULL);
    camera->setCustomEyeProjection((const filament::math::mat4*)inProjection, (size_t) count, *(const filament::math::mat4*)inProjectionForCulling, near, far);
    env->ReleaseDoubleArrayElements(inProjection_, inProjection, JNI_ABORT);
    env->ReleaseDoubleArrayElements(inProjectionForCulling_, inProjectionForCulling, JNI_ABORT);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Camera_nSetEyeModelMatrix(JNIEnv* env, jclass, jlong nativeCamera, jint eye, jfloatArray in_) {
    Camera* camera = (Camera*) nativeCamera;
    jfloat* in = env->GetFloatArrayElements(in_, NULL);
    camera->setEyeModelMatrix((size_t) eye, (filament::math::mat4)*(const filament::math::mat4f*)in);
    env->ReleaseFloatArrayElements(in_, in, JNI_ABORT);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Camera_nSetScaling(JNIEnv*, jclass, jlong nativeCamera, jdouble x, jdouble y) {
    Camera* camera = (Camera*) nativeCamera;
    camera->setScaling({x, y});
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Camera_nSetShift(JNIEnv*, jclass, jlong nativeCamera, jdouble x, jdouble y) {
    Camera* camera = (Camera*) nativeCamera;
    camera->setShift({x, y});
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Camera_nGetShift(JNIEnv* env, jclass, jlong nativeCamera, jdoubleArray out_) {
    Camera* camera = (Camera*) nativeCamera;
    jdouble* out = env->GetDoubleArrayElements(out_, NULL);
    filament::math::double2 s = camera->getShift();
    out[0] = s.x;
    out[1] = s.y;
    env->ReleaseDoubleArrayElements(out_, out, 0);
}

JNIEXPORT jdouble JNICALL
Java_io_github_erkko68_filament_Camera_nGetFieldOfViewInDegrees(JNIEnv*, jclass, jlong nativeCamera, jint direction) {
    Camera* camera = (Camera*) nativeCamera;
    return camera->getFieldOfViewInDegrees((Camera::Fov) direction);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Camera_nSetModelMatrix(JNIEnv* env, jclass, jlong nativeCamera, jfloatArray in_) {
    Camera* camera = (Camera*) nativeCamera;
    jfloat* in = env->GetFloatArrayElements(in_, NULL);
    camera->setModelMatrix((filament::math::mat4)*(const filament::math::mat4f*)in);
    env->ReleaseFloatArrayElements(in_, in, JNI_ABORT);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Camera_nSetModelMatrixFp64(JNIEnv* env, jclass, jlong nativeCamera, jdoubleArray in_) {
    Camera* camera = (Camera*) nativeCamera;
    jdouble* in = env->GetDoubleArrayElements(in_, NULL);
    camera->setModelMatrix(*(const filament::math::mat4*)in);
    env->ReleaseDoubleArrayElements(in_, in, JNI_ABORT);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Camera_nLookAt(JNIEnv*, jclass, jlong nativeCamera, jdouble eyeX, jdouble eyeY, jdouble eyeZ, jdouble centerX, jdouble centerY, double centerZ, jdouble upX, jdouble upY, jdouble upZ) {
    Camera* camera = (Camera*) nativeCamera;
    camera->lookAt({eyeX, eyeY, eyeZ}, {centerX, centerY, centerZ}, {upX, upY, upZ});
}

JNIEXPORT jdouble JNICALL
Java_io_github_erkko68_filament_Camera_nGetNear(JNIEnv*, jclass, jlong nativeCamera) {
    return ((Camera*) nativeCamera)->getNear();
}

JNIEXPORT jdouble JNICALL
Java_io_github_erkko68_filament_Camera_nGetCullingFar(JNIEnv*, jclass, jlong nativeCamera) {
    return ((Camera*) nativeCamera)->getCullingFar();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Camera_nGetProjectionMatrix(JNIEnv* env, jclass, jlong nativeCamera, jdoubleArray out_) {
    Camera* camera = (Camera*) nativeCamera;
    jdouble* out = env->GetDoubleArrayElements(out_, NULL);
    const filament::math::mat4& m = camera->getProjectionMatrix();
    std::copy_n(&m[0][0], 16, out);
    env->ReleaseDoubleArrayElements(out_, out, 0);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Camera_nGetCullingProjectionMatrix(JNIEnv* env, jclass, jlong nativeCamera, jdoubleArray out_) {
    Camera* camera = (Camera*) nativeCamera;
    jdouble* out = env->GetDoubleArrayElements(out_, NULL);
    const filament::math::mat4& m = camera->getCullingProjectionMatrix();
    std::copy_n(&m[0][0], 16, out);
    env->ReleaseDoubleArrayElements(out_, out, 0);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Camera_nGetScaling(JNIEnv* env, jclass, jlong nativeCamera, jdoubleArray out_) {
    Camera* camera = (Camera*) nativeCamera;
    jdouble* out = env->GetDoubleArrayElements(out_, NULL);
    const filament::math::double4& s = camera->getScaling();
    std::copy_n(&s[0], 4, out);
    env->ReleaseDoubleArrayElements(out_, out, 0);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Camera_nGetModelMatrix(JNIEnv* env, jclass, jlong nativeCamera, jfloatArray out_) {
    Camera* camera = (Camera*) nativeCamera;
    jfloat* out = env->GetFloatArrayElements(out_, NULL);
    const filament::math::mat4f& m = (filament::math::mat4f)camera->getModelMatrix();
    std::copy_n(&m[0][0], 16, out);
    env->ReleaseFloatArrayElements(out_, out, 0);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Camera_nGetModelMatrixFp64(JNIEnv* env, jclass, jlong nativeCamera, jdoubleArray out_) {
    Camera* camera = (Camera*) nativeCamera;
    jdouble* out = env->GetDoubleArrayElements(out_, NULL);
    const filament::math::mat4& m = camera->getModelMatrix();
    std::copy_n(&m[0][0], 16, out);
    env->ReleaseDoubleArrayElements(out_, out, 0);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Camera_nGetViewMatrix(JNIEnv* env, jclass, jlong nativeCamera, jfloatArray out_) {
    Camera* camera = (Camera*) nativeCamera;
    jfloat* out = env->GetFloatArrayElements(out_, NULL);
    const filament::math::mat4f& m = (filament::math::mat4f)camera->getViewMatrix();
    std::copy_n(&m[0][0], 16, out);
    env->ReleaseFloatArrayElements(out_, out, 0);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Camera_nGetViewMatrixFp64(JNIEnv* env, jclass, jlong nativeCamera, jdoubleArray out_) {
    Camera* camera = (Camera*) nativeCamera;
    jdouble* out = env->GetDoubleArrayElements(out_, NULL);
    const filament::math::mat4& m = camera->getViewMatrix();
    std::copy_n(&m[0][0], 16, out);
    env->ReleaseDoubleArrayElements(out_, out, 0);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Camera_nGetPosition(JNIEnv* env, jclass, jlong nativeCamera, jfloatArray out_) {
    Camera* camera = (Camera*) nativeCamera;
    jfloat* out = env->GetFloatArrayElements(out_, NULL);
    *(filament::math::float3*)out = camera->getPosition();
    env->ReleaseFloatArrayElements(out_, out, 0);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Camera_nGetLeftVector(JNIEnv* env, jclass, jlong nativeCamera, jfloatArray out_) {
    Camera* camera = (Camera*) nativeCamera;
    jfloat* out = env->GetFloatArrayElements(out_, NULL);
    *(filament::math::float3*)out = camera->getLeftVector();
    env->ReleaseFloatArrayElements(out_, out, 0);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Camera_nGetUpVector(JNIEnv* env, jclass, jlong nativeCamera, jfloatArray out_) {
    Camera* camera = (Camera*) nativeCamera;
    jfloat* out = env->GetFloatArrayElements(out_, NULL);
    *(filament::math::float3*)out = camera->getUpVector();
    env->ReleaseFloatArrayElements(out_, out, 0);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Camera_nGetForwardVector(JNIEnv* env, jclass, jlong nativeCamera, jfloatArray out_) {
    Camera* camera = (Camera*) nativeCamera;
    jfloat* out = env->GetFloatArrayElements(out_, NULL);
    *(filament::math::float3*)out = camera->getForwardVector();
    env->ReleaseFloatArrayElements(out_, out, 0);
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Camera_nSetExposure(JNIEnv*, jclass, jlong nativeCamera, float aperture, float shutterSpeed, float sensitivity) {
    ((Camera*) nativeCamera)->setExposure(aperture, shutterSpeed, sensitivity);
}

JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_Camera_nGetAperture(JNIEnv*, jclass, jlong nativeCamera) {
    return ((Camera*) nativeCamera)->getAperture();
}

JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_Camera_nGetShutterSpeed(JNIEnv*, jclass, jlong nativeCamera) {
    return ((Camera*) nativeCamera)->getShutterSpeed();
}

JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_Camera_nGetSensitivity(JNIEnv*, jclass, jlong nativeCamera) {
    return ((Camera*) nativeCamera)->getSensitivity();
}

JNIEXPORT jdouble JNICALL
Java_io_github_erkko68_filament_Camera_nGetFocalLength(JNIEnv*, jclass, jlong nativeCamera) {
    return ((Camera*) nativeCamera)->getFocalLength();
}

JNIEXPORT void JNICALL
Java_io_github_erkko68_filament_Camera_nSetFocusDistance(JNIEnv*, jclass, jlong nativeCamera, jfloat distance) {
    ((Camera*) nativeCamera)->setFocusDistance(distance);
}

JNIEXPORT jfloat JNICALL
Java_io_github_erkko68_filament_Camera_nGetFocusDistance(JNIEnv*, jclass, jlong nativeCamera) {
    return ((Camera*) nativeCamera)->getFocusDistance();
}

} // extern "C"
