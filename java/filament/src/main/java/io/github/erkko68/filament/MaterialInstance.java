package io.github.erkko68.filament;

public class MaterialInstance {
    private final Material mMaterial;
    private long mNativeObject;

    public MaterialInstance(Material material, long nativeMaterialInstance) {
        mMaterial = material;
        mNativeObject = nativeMaterialInstance;
    }

    public MaterialInstance(long nativeMaterialInstance) {
        mNativeObject = nativeMaterialInstance;
        mMaterial = new Material(nGetMaterial(nativeMaterialInstance));
    }

    public Material getMaterial() {
        return mMaterial;
    }

    public void setParameter(String name, float value) {
        nSetParameterFloat(getNativeObject(), name, value);
    }

    public void setParameter(String name, float v0, float v1) {
        nSetParameterFloat2(getNativeObject(), name, v0, v1);
    }

    public void setParameter(String name, float v0, float v1, float v2) {
        nSetParameterFloat3(getNativeObject(), name, v0, v1, v2);
    }

    public void setParameter(String name, float v0, float v1, float v2, float v3) {
        nSetParameterFloat4(getNativeObject(), name, v0, v1, v2, v3);
    }

    public void setParameter(String name, int value) {
        nSetParameterInt(getNativeObject(), name, value);
    }

    public void setParameter(String name, Texture texture, TextureSampler sampler) {
        nSetParameterTexture(getNativeObject(), name, texture.getNativeObject(), sampler.getSampler());
    }

    public void setParameter(String name, float[] value, int offset, int count) {
        nSetParameterFloatArray(getNativeObject(), name, value, offset, count);
    }

    public void setParameter(String name, int[] value, int offset, int count) {
        nSetParameterIntArray(getNativeObject(), name, value, offset, count);
    }

    /**
     * Sets a mat4f parameter.
     * @param matrix 16 floats in column-major order.
     */
    public void setParameter(String name, float[] matrix) {
        if (matrix.length < 16) throw new IllegalArgumentException("Matrix must be at least 16 floats");
        nSetParameterMat4f(getNativeObject(), name, matrix);
    }

    public long getNativeObject() {
        if (mNativeObject == 0) {
            throw new IllegalStateException("Calling method on destroyed MaterialInstance");
        }
        return mNativeObject;
    }

    void clearNativeObject() {
        mNativeObject = 0;
    }

    private static native void nSetParameterFloat(long nativeMaterialInstance, String name, float value);
    private static native void nSetParameterFloat2(long nativeMaterialInstance, String name, float v0, float v1);
    private static native void nSetParameterFloat3(long nativeMaterialInstance, String name, float v0, float v1, float v2);
    private static native void nSetParameterFloat4(long nativeMaterialInstance, String name, float v0, float v1, float v2, float v3);
    private static native void nSetParameterInt(long nativeMaterialInstance, String name, int value);
    private static native void nSetParameterTexture(long nativeMaterialInstance, String name, long nativeTexture, int sampler);
    private static native void nSetParameterFloatArray(long nativeMaterialInstance, String name, float[] value, int offset, int count);
    private static native void nSetParameterIntArray(long nativeMaterialInstance, String name, int[] value, int offset, int count);
    private static native void nSetParameterMat4f(long nativeMaterialInstance, String name, float[] matrix);
    private static native long nGetMaterial(long nativeMaterialInstance);
}
