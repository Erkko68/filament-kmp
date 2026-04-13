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

    public long getNativeObject() {
        if (mNativeObject == 0) throw new IllegalStateException("Scene already destroyed");
        return mNativeObject;
    }

    void clearNativeObject() {
        mNativeObject = 0;
    }

    private static native void nAddEntity(long nativeScene, int entity);
    private static native void nRemoveEntity(long nativeScene, int entity);
}
