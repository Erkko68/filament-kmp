package io.github.erkko68.filament;

public class SwapChain {
    private long mNativeObject;

    SwapChain(long nativeSwapChain) {
        mNativeObject = nativeSwapChain;
    }

    public long getNativeObject() {
        if (mNativeObject == 0) throw new IllegalStateException("SwapChain already destroyed");
        return mNativeObject;
    }

    void clearNativeObject() {
        mNativeObject = 0;
    }
}
