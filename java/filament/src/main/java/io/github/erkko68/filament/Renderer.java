package io.github.erkko68.filament;

public class Renderer {
    private long mNativeObject;
    private Engine mEngine;

    Renderer(Engine engine, long nativeRenderer) {
        mEngine = engine;
        mNativeObject = nativeRenderer;
    }

    public static class DisplayInfo {
        public float refreshRate = 60.0f;
    }

    public static class FrameRateOptions {
        public float interval = 1.0f;
        public float headRoomRatio = 0.0f;
        public float scaleRate = 1.0f / 15.0f;
        public int history = 15;
    }

    public static class ClearOptions {
        public float[] clearColor = { 0.0f, 0.0f, 0.0f, 0.0f };
        public boolean clear = false;
        public boolean discard = true;
    }

    public void setDisplayInfo(DisplayInfo info) {
        nSetDisplayInfo(getNativeObject(), info.refreshRate);
    }

    public void setFrameRateOptions(FrameRateOptions options) {
        nSetFrameRateOptions(getNativeObject(), options.interval, options.headRoomRatio, options.scaleRate, options.history);
    }

    public void setClearOptions(ClearOptions options) {
        nSetClearOptions(getNativeObject(), options.clearColor[0], options.clearColor[1], options.clearColor[2], options.clearColor[3], options.clear, options.discard);
    }

    public boolean beginFrame(SwapChain swapChain, long frameTimeNanos) {
        return nBeginFrame(getNativeObject(), swapChain.getNativeObject(), frameTimeNanos);
    }

    public void render(View view) {
        nRender(getNativeObject(), view.getNativeObject());
    }

    public void renderStandaloneView(View view) {
        nRenderStandaloneView(getNativeObject(), view.getNativeObject());
    }

    public void endFrame() {
        nEndFrame(getNativeObject());
    }

    public void skipFrame(long frameTimeNanos) {
        nSkipFrame(getNativeObject(), frameTimeNanos);
    }

    public boolean shouldRenderFrame() {
        return nShouldRenderFrame(getNativeObject());
    }

    public void copyFrame(SwapChain dstSwapChain, Viewport dstViewport, Viewport srcViewport, int flags) {
        nCopyFrame(getNativeObject(), dstSwapChain.getNativeObject(),
                dstViewport.left, dstViewport.bottom, dstViewport.width, dstViewport.height,
                srcViewport.left, srcViewport.bottom, srcViewport.width, srcViewport.height,
                flags);
    }

    public void readPixels(int x, int y, int width, int height, Texture.PixelBufferDescriptor buffer) {
        int result = nReadPixels(getNativeObject(), mEngine.getNativeObject(),
                x, y, width, height,
                buffer.storage, buffer.storage.remaining(),
                buffer.left, buffer.top, buffer.type.ordinal(), buffer.alignment,
                buffer.stride, buffer.format.ordinal(),
                buffer.handler, buffer.callback);
        if (result < 0) throw new java.nio.BufferOverflowException();
    }

    public double getUserTime() {
        return nGetUserTime(getNativeObject());
    }

    public void resetUserTime() {
        nResetUserTime(getNativeObject());
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
    private static native void nRenderStandaloneView(long nativeRenderer, long nativeView);
    private static native void nEndFrame(long nativeRenderer);
    private static native void nSetDisplayInfo(long nativeRenderer, float refreshRate);
    private static native void nSetFrameRateOptions(long nativeRenderer, float interval, float headRoom, float scaleRate, int history);
    private static native void nSetClearOptions(long nativeRenderer, float r, float g, float b, float a, boolean clear, boolean discard);
    private static native void nSkipFrame(long nativeRenderer, long frameTimeNanos);
    private static native boolean nShouldRenderFrame(long nativeRenderer);
    private static native void nCopyFrame(long nativeRenderer, long nativeSwapChain, int dstL, int dstB, int dstW, int dstH, int srcL, int srcB, int srcW, int srcH, int flags);
    private static native int nReadPixels(long nativeRenderer, long nativeEngine, int x, int y, int w, int h, java.nio.Buffer storage, int remaining, int left, int top, int type, int alignment, int stride, int format, Object handler, Runnable callback);
    private static native double nGetUserTime(long nativeRenderer);
    private static native void nResetUserTime(long nativeRenderer);
}
