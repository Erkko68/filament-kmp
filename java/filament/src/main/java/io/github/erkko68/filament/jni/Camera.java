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

package io.github.erkko68.filament.jni;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Camera represents the eye through which the scene is viewed.
 */
public class Camera {
    private long mNativeObject;

    @Entity
    private final int mEntity;

    public enum Projection { PERSPECTIVE, ORTHO }
    public enum Fov { VERTICAL, HORIZONTAL }

    Camera(long nativeCamera, @Entity int entity) {
        mNativeObject = nativeCamera;
        mEntity = entity;
    }

    public void setProjection(@NotNull Projection projection, double left, double right, double bottom, double top, double near, double far) {
        nSetProjection(getNativeObject(), projection.ordinal(), left, right, bottom, top, near, far);
    }

    public void setProjection(double fovInDegrees, double aspect, double near, double far, @NotNull Fov direction) {
        nSetProjectionFov(getNativeObject(), fovInDegrees, aspect, near, far, direction.ordinal());
    }

    public void setLensProjection(double focalLength, double aspect, double near, double far) {
        nSetLensProjection(getNativeObject(), focalLength, aspect, near, far);
    }

    public void setCustomProjection(@NotNull double[] inProjection, double near, double far) {
        if (inProjection.length < 16) throw new IllegalArgumentException("Matrix must be 16 doubles");
        nSetCustomProjection(getNativeObject(), inProjection, inProjection, near, far);
    }

    public void setCustomProjection(@NotNull double[] inProjection, @NotNull double[] inProjectionForCulling, double near, double far) {
        if (inProjection.length < 16 || inProjectionForCulling.length < 16) throw new IllegalArgumentException("Matrices must be 16 doubles");
        nSetCustomProjection(getNativeObject(), inProjection, inProjectionForCulling, near, far);
    }

    public void setCustomEyeProjection(@NotNull double[] inProjection, int count, @NotNull double[] inProjectionForCulling, double near, double far) {
        if (inProjection.length < 16 * count || inProjectionForCulling.length < 16) {
            throw new IllegalArgumentException("Arrays too small");
        }
        nSetCustomEyeProjection(getNativeObject(), inProjection, count, inProjectionForCulling, near, far);
    }

    public void setEyeModelMatrix(int eye, @NotNull float[] model) {
        if (model.length < 16) throw new IllegalArgumentException("Matrix must be 16 floats");
        nSetEyeModelMatrix(getNativeObject(), eye, model);
    }

    public void setScaling(double xscaling, double yscaling) {
        nSetScaling(getNativeObject(), xscaling, yscaling);
    }

    public void setShift(double xshift, double yshift) {
        nSetShift(getNativeObject(), xshift, yshift);
    }

    public void getShift(@NotNull double[] out) {
        if (out.length < 2) throw new IllegalArgumentException("Array must be at least 2 doubles");
        nGetShift(getNativeObject(), out);
    }

    public double getFieldOfViewInDegrees(@NotNull Fov direction) {
        return nGetFieldOfViewInDegrees(getNativeObject(), direction.ordinal());
    }

    public void setModelMatrix(@NotNull float[] modelMatrix) {
        if (modelMatrix.length < 16) throw new IllegalArgumentException("Matrix must be 16 floats");
        nSetModelMatrix(getNativeObject(), modelMatrix);
    }

    public void setModelMatrix(@NotNull double[] modelMatrix) {
        if (modelMatrix.length < 16) throw new IllegalArgumentException("Matrix must be 16 doubles");
        nSetModelMatrixFp64(getNativeObject(), modelMatrix);
    }

    public void lookAt(double eyeX, double eyeY, double eyeZ, double centerX, double centerY, double centerZ, double upX, double upY, double upZ) {
        nLookAt(getNativeObject(), eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
    }

    public float getNear() { return (float) nGetNear(getNativeObject()); }
    public float getCullingFar() { return (float) nGetCullingFar(getNativeObject()); }

    public void getProjectionMatrix(@NotNull double[] out) {
        if (out.length < 16) throw new IllegalArgumentException("Matrix must be 16 doubles");
        nGetProjectionMatrix(getNativeObject(), out);
    }

    public void getCullingProjectionMatrix(@NotNull double[] out) {
        if (out.length < 16) throw new IllegalArgumentException("Matrix must be 16 doubles");
        nGetCullingProjectionMatrix(getNativeObject(), out);
    }

    public void getScaling(@NotNull double[] out) {
        if (out.length < 4) throw new IllegalArgumentException("Array must be at least 4 doubles");
        nGetScaling(getNativeObject(), out);
    }

    public void getModelMatrix(@NotNull float[] out) {
        if (out.length < 16) throw new IllegalArgumentException("Matrix must be 16 floats");
        nGetModelMatrix(getNativeObject(), out);
    }

    public void getModelMatrix(@NotNull double[] out) {
        if (out.length < 16) throw new IllegalArgumentException("Matrix must be 16 doubles");
        nGetModelMatrixFp64(getNativeObject(), out);
    }

    public void getViewMatrix(@NotNull float[] out) {
        if (out.length < 16) throw new IllegalArgumentException("Matrix must be 16 floats");
        nGetViewMatrix(getNativeObject(), out);
    }

    public void getViewMatrix(@NotNull double[] out) {
        if (out.length < 16) throw new IllegalArgumentException("Matrix must be 16 doubles");
        nGetViewMatrixFp64(getNativeObject(), out);
    }

    public void getPosition(@NotNull float[] out) {
        if (out.length < 3) throw new IllegalArgumentException("Array must be at least 3 floats");
        nGetPosition(getNativeObject(), out);
    }

    public void getLeftVector(@NotNull float[] out) {
        if (out.length < 3) throw new IllegalArgumentException("Array must be at least 3 floats");
        nGetLeftVector(getNativeObject(), out);
    }

    public void getUpVector(@NotNull float[] out) {
        if (out.length < 3) throw new IllegalArgumentException("Array must be at least 3 floats");
        nGetUpVector(getNativeObject(), out);
    }

    public void getForwardVector(@NotNull float[] out) {
        if (out.length < 3) throw new IllegalArgumentException("Array must be at least 3 floats");
        nGetForwardVector(getNativeObject(), out);
    }

    public void setExposure(float aperture, float shutterSpeed, float sensitivity) {
        nSetExposure(getNativeObject(), aperture, shutterSpeed, sensitivity);
    }

    public void setExposure(float exposure) {
        setExposure(1.0f, 1.2f, 100.0f * (1.0f / exposure));
    }

    public float getAperture() { return nGetAperture(getNativeObject()); }
    public float getShutterSpeed() { return nGetShutterSpeed(getNativeObject()); }
    public float getSensitivity() { return nGetSensitivity(getNativeObject()); }
    public double getFocalLength() { return nGetFocalLength(getNativeObject()); }

    public void setFocusDistance(float distance) { nSetFocusDistance(getNativeObject(), distance); }
    public float getFocusDistance() { return nGetFocusDistance(getNativeObject()); }

    @Entity
    public int getEntity() { return mEntity; }

    public long getNativeObject() {
        if (mNativeObject == 0) throw new IllegalStateException("Calling method on destroyed Camera");
        return mNativeObject;
    }

    void clearNativeObject() { mNativeObject = 0; }

    private static native void nSetProjection(long nativeCamera, int projection, double left, double right, double bottom, double top, double near, double far);
    private static native void nSetProjectionFov(long nativeCamera, double fovInDegrees, double aspect, double near, double far, int direction);
    private static native void nSetLensProjection(long nativeCamera, double focalLength, double aspect, double near, double far);
    private static native void nSetCustomProjection(long nativeCamera, double[] inProjection, double[] inProjectionForCulling, double near, double far);
    private static native void nSetCustomEyeProjection(long nativeCamera, double[] inProjection, int count, double[] inProjectionForCulling, double near, double far);
    private static native void nSetEyeModelMatrix(long nativeCamera, int eye, float[] model);
    private static native void nSetScaling(long nativeCamera, double xscaling, double yscaling);
    private static native void nSetShift(long nativeCamera, double xshift, double yshift);
    private static native void nGetShift(long nativeCamera, double[] out);
    private static native double nGetFieldOfViewInDegrees(long nativeCamera, int direction);
    private static native void nSetModelMatrix(long nativeCamera, float[] matrix);
    private static native void nSetModelMatrixFp64(long nativeCamera, double[] matrix);
    private static native void nLookAt(long nativeCamera, double eyeX, double eyeY, double eyeZ, double centerX, double centerY, double centerZ, double upX, double upY, double upZ);
    private static native double nGetNear(long nativeCamera);
    private static native double nGetCullingFar(long nativeCamera);
    private static native void nGetProjectionMatrix(long nativeCamera, double[] out);
    private static native void nGetCullingProjectionMatrix(long nativeCamera, double[] out);
    private static native void nGetScaling(long nativeCamera, double[] out);
    private static native void nGetModelMatrix(long nativeCamera, float[] out);
    private static native void nGetModelMatrixFp64(long nativeCamera, double[] out);
    private static native void nGetViewMatrix(long nativeCamera, float[] out);
    private static native void nGetViewMatrixFp64(long nativeCamera, double[] out);
    private static native void nGetPosition(long nativeCamera, float[] out);
    private static native void nGetLeftVector(long nativeCamera, float[] out);
    private static native void nGetUpVector(long nativeCamera, float[] out);
    private static native void nGetForwardVector(long nativeCamera, float[] out);
    private static native void nSetExposure(long nativeCamera, float aperture, float shutterSpeed, float sensitivity);
    private static native float nGetAperture(long nativeCamera);
    private static native float nGetShutterSpeed(long nativeCamera);
    private static native float nGetSensitivity(long nativeCamera);
    private static native double nGetFocalLength(long nativeCamera);
    private static native void nSetFocusDistance(long nativeCamera, float distance);
    private static native float nGetFocusDistance(long nativeCamera);
}
