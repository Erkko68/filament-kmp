package io.github.erkko68.filament;

/**
 * Wraps an OS-native window surface handle (e.g. an NSView/NSWindow pointer on macOS,
 * an HWND on Windows, or an X11 Window on Linux) for use with
 * {@link Engine#createSwapChainFromNativeSurface}.
 */
public class NativeSurface {
    private final int mWidth;
    private final int mHeight;
    private final long mNativeObject;

    public NativeSurface(int width, int height) {
        mWidth = width;
        mHeight = height;
        mNativeObject = nCreateSurface(width, height);
    }

    /**
     * @param nativeObject A native window handle (platform-specific pointer cast to long).
     */
    public NativeSurface(long nativeObject) {
        mNativeObject = nativeObject;
        mWidth = 0;
        mHeight = 0;
    }

    public void dispose() {
        if (mNativeObject != 0 && mWidth != 0) {
            nDestroySurface(mNativeObject);
        }
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public long getNativeObject() {
        return mNativeObject;
    }

    private static native long nCreateSurface(int width, int height);
    private static native void nDestroySurface(long nativeObject);
}
