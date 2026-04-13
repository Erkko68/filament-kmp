package io.github.erkko68.filament;

public class Renderer {
    private long mNativeObject;

    Renderer(long nativeRenderer) {
        mNativeObject = nativeRenderer;
    }

    public boolean beginFrame(SwapChain swapChain) {
        return nBeginFrame(getNativeObject(), swapChain.getNativeObject());
    }

    public void render(View view) {
        nRender(getNativeObject(), view.getNativeObject());
    }

    public void endFrame() {
        nEndFrame(getNativeObject());
    }

    public long getNativeObject() {
        if (mNativeObject == 0) throw new IllegalStateException("Renderer already destroyed");
        return mNativeObject;
    }

    void clearNativeObject() {
        mNativeObject = 0;
    }

    private static native boolean nBeginFrame(long nativeRenderer, long nativeSwapChain);
    private static native void nRender(long nativeRenderer, long nativeView);
    private static native void nEndFrame(long nativeRenderer);
}
