package io.github.erkko68.filament;

public class Scene {
    private long mNativeObject;

    Scene(long nativeScene) {
        mNativeObject = nativeScene;
    }

    public void addEntity(int entity) {
        nAddEntity(getNativeObject(), entity);
    }

    public void removeEntity(int entity) {
        nRemoveEntity(getNativeObject(), entity);
    }

    public void setIndirectLight(IndirectLight indirectLight) {
        nSetIndirectLight(getNativeObject(), indirectLight != null ? indirectLight.getNativeObject() : 0);
    }

    public void setSkybox(Skybox skybox) {
        nSetSkybox(getNativeObject(), skybox != null ? skybox.getNativeObject() : 0);
    }

    public long getNativeObject() {
        if (mNativeObject == 0) {
            throw new IllegalStateException("Calling method on destroyed Scene");
        }
        return mNativeObject;
    }

    void clearNativeObject() {
        mNativeObject = 0;
    }

    private static native void nAddEntity(long nativeScene, int entity);
    private static native void nRemoveEntity(long nativeScene, int entity);
    private static native void nSetIndirectLight(long nativeScene, long nativeIndirectLight);
    private static native void nSetSkybox(long nativeScene, long nativeSkybox);
}
