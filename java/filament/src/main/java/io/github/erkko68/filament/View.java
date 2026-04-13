package io.github.erkko68.filament;

public class View {
    private long mNativeObject;

    View(long nativeView) {
        mNativeObject = nativeView;
    }

    public void setScene(Scene scene) {
        nSetScene(getNativeObject(), scene.getNativeObject());
    }

    public void setCamera(Camera camera) {
        nSetCamera(getNativeObject(), camera.getNativeObject());
    }

    public void setViewport(int left, int bottom, int width, int height) {
        nSetViewport(getNativeObject(), left, bottom, width, height);
    }

    public long getNativeObject() {
        if (mNativeObject == 0) throw new IllegalStateException("View already destroyed");
        return mNativeObject;
    }

    void clearNativeObject() {
        mNativeObject = 0;
    }

    private static native void nSetScene(long nativeView, long nativeScene);
    private static native void nSetCamera(long nativeView, long nativeCamera);
    private static native void nSetViewport(long nativeView, int left, int bottom, int width, int height);
}
