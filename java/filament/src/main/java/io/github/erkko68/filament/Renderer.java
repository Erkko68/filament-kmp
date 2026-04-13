package io.github.erkko68.filament;

public class Renderer {
    private long mNativeObject;

    Renderer(long nativeRenderer) {
        mNativeObject = nativeRenderer;
    }

    public boolean beginFrame(SwapChain swapChain, long frameTimeNanos) {
        return nBeginFrame(getNativeObject(), swapChain.getNativeObject(), frameTimeNanos);
    }

    public void render(View view) {
        nRender(getNativeObject(), view.getNativeObject());
    }

    public void endFrame() {
        nEndFrame(getNativeObject());
    }

    public long getNativeObject() {
        if (mNativeObject == 0) {
            throw new IllegalStateException("Calling method on destroyed Renderer");
        }
        return mNativeObject;
    }

    void clearNativeObject() {
        mNativeObject = 0;
    }

    private static native boolean nBeginFrame(long nativeRenderer, long nativeSwapChain, long frameTimeNanos);
    private static native void nRender(long nativeRenderer, long nativeView);
    private static native void nEndFrame(long nativeRenderer);
}
