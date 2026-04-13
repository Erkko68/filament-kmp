package io.github.erkko68.filament;

public class Fence {
    private long mNativeObject;

    public enum Type {
        SOFT,
        HARD
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

    public FenceStatus wait(Type type, long timeoutNano) {
        return FenceStatus.values()[nWait(getNativeObject(), type.ordinal(), timeoutNano)];
    }

    public static FenceStatus waitAndDestroy(Fence fence, Type type) {
        return FenceStatus.values()[nWaitAndDestroy(fence.getNativeObject(), type.ordinal())];
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
