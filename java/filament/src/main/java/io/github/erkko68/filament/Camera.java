package io.github.erkko68.filament;

public class Camera {
    private long mNativeObject;

    Camera(long nativeCamera) {
        mNativeObject = nativeCamera;
    }

    public enum Projection {
        PERSPECTIVE,
        ORTHO
    }

    public void setProjection(Projection projection, double left, double right, double bottom, double top, double near, double far) {
        nSetProjection(getNativeObject(), projection.ordinal(), left, right, bottom, top, near, far);
    }

    public void setProjection(double fovInDegrees, double aspect, double near, double far, Camera.Fov direction) {
        nSetProjectionFov(getNativeObject(), fovInDegrees, aspect, near, far, direction.ordinal());
    }

    public void lookAt(double eyeX, double eyeY, double eyeZ, double centerX, double centerY, double centerZ, double upX, double upY, double upZ) {
        nLookAt(getNativeObject(), eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
    }

    public void setModelMatrix(float[] matrix) {
        if (matrix.length < 16) throw new IllegalArgumentException("Matrix must be 16 floats");
        nSetModelMatrix(getNativeObject(), matrix);
    }

    public enum Fov {
        VERTICAL,
        HORIZONTAL
    }

    public long getNativeObject() {
        if (mNativeObject == 0) {
            throw new IllegalStateException("Calling method on destroyed Camera");
        }
        return mNativeObject;
    }

    void clearNativeObject() {
        mNativeObject = 0;
    }

    private static native void nSetProjection(long nativeCamera, int projection, double left, double right, double bottom, double top, double near, double far);
    private static native void nSetProjectionFov(long nativeCamera, double fovInDegrees, double aspect, double near, double far, int direction);
    private static native void nLookAt(long nativeCamera, double eyeX, double eyeY, double eyeZ, double centerX, double centerY, double centerZ, double upX, double upY, double upZ);
    private static native void nSetModelMatrix(long nativeCamera, float[] matrix);
}
