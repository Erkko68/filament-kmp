package io.github.erkko68.filament;

public class Camera {
    private long mNativeObject;

    Camera(long nativeCamera) {
        mNativeObject = nativeCamera;
    }

    public void setProjection(double fov, double aspect, double near, double far, int projection) {
        nSetProjection(getNativeObject(), fov, aspect, near, far, projection);
    }

    public void setModelMatrix(float[] matrix) {
        nSetModelMatrix(getNativeObject(), matrix);
    }

    public long getNativeObject() {
        if (mNativeObject == 0) throw new IllegalStateException("Camera already destroyed");
        return mNativeObject;
    }

    void clearNativeObject() {
        mNativeObject = 0;
    }

    private static native void nSetProjection(long nativeCamera, double fov, double aspect, double near, double far, int projection);
    private static native void nSetModelMatrix(long nativeCamera, float[] matrix);
}
