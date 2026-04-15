package io.github.erkko68.filament.jni;

public class Fence {
    private long mNativeObject;

    public enum Mode {
        FLUSH,
        DONT_FLUSH
    }

    public enum FenceStatus {
        ERROR,
        ALREADY_SIGNALED,
        TIMEOUT_EXPIRED,
        CONDITION_SATISFIED
    }

    public static final long WAIT_FOR_EVER = -1L;

    Fence(long nativeFence) {
        mNativeObject = nativeFence;
    }

    public FenceStatus wait(Mode mode, long timeoutNano) {
        return FenceStatus.values()[nWait(getNativeObject(), mode.ordinal(), timeoutNano)];
    }

    public static FenceStatus waitAndDestroy(Fence fence, Mode mode) {
        return FenceStatus.values()[nWaitAndDestroy(fence.getNativeObject(), mode.ordinal())];
    }

    public long getNativeObject() {
        if (mNativeObject == 0) {
            throw new IllegalStateException("Calling method on destroyed Fence");
        }
        return mNativeObject;
    }

    void clearNativeObject() {
        mNativeObject = 0;
    }

    private static native int nWait(long nativeFence, int type, long timeoutNano);
    private static native int nWaitAndDestroy(long nativeFence, int type);
}
