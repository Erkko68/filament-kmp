package io.github.erkko68.filament;

public class TransformManager {
    private long mNativeObject;

    TransformManager(long nativeTransformManager) {
        mNativeObject = nativeTransformManager;
    }

    public void setTransform(int instance, float[] matrix) {
        if (matrix.length < 16) throw new IllegalArgumentException("Matrix must be at least 16 floats");
        nSetTransform(mNativeObject, instance, matrix);
    }

    public int getInstance(int entity) {
        return nGetInstance(mNativeObject, entity);
    }

    private static native void nSetTransform(long nativeTransformManager, int instance, float[] matrix);
    private static native int nGetInstance(long nativeTransformManager, int entity);
}
