package io.github.erkko68.filament;

public class View {
    private long mNativeObject;

    View(long nativeView) {
        mNativeObject = nativeView;
    }

    public void setScene(Scene scene) {
        nSetScene(getNativeObject(), scene != null ? scene.getNativeObject() : 0);
    }

    public void setCamera(Camera camera) {
        nSetCamera(getNativeObject(), camera != null ? camera.getNativeObject() : 0);
    }

    public void setViewport(Viewport viewport) {
        nSetViewport(getNativeObject(), viewport.left, viewport.bottom, viewport.width, viewport.height);
    }

    public void setClearColor(float r, float g, float b, float a) {
        nSetClearColor(getNativeObject(), r, g, b, a);
    }

    public void setColorGrading(ColorGrading colorGrading) {
        nSetColorGrading(getNativeObject(), colorGrading != null ? colorGrading.getNativeObject() : 0);
    }

    public long getNativeObject() {
        if (mNativeObject == 0) {
            throw new IllegalStateException("Calling method on destroyed View");
        }
        return mNativeObject;
    }

    void clearNativeObject() {
        mNativeObject = 0;
    }

    private static native void nSetScene(long nativeView, long nativeScene);
    private static native void nSetCamera(long nativeView, long nativeCamera);
    private static native void nSetViewport(long nativeView, int left, int bottom, int width, int height);
    private static native void nSetClearColor(long nativeView, float r, float g, float b, float a);
    private static native void nSetColorGrading(long nativeView, long nativeColorGrading);
}
