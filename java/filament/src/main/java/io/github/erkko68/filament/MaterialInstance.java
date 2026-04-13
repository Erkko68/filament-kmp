package io.github.erkko68.filament;

public class MaterialInstance {
    private final Material mMaterial;
    private long mNativeObject;

    MaterialInstance(Material material, long nativeMaterialInstance) {
        mMaterial = material;
        mNativeObject = nativeMaterialInstance;
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

    public void setParameter(String name, Texture texture, Texture.Sampler sampler) {
        nSetParameterTexture(getNativeObject(), name, texture.getNativeObject(), sampler.ordinal());
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
}
