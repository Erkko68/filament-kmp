package io.github.erkko68.filament.jni;

public class EntityManager {
    private static final EntityManager INSTANCE = new EntityManager();
    private long mNativeObject;

    private EntityManager() {
        mNativeObject = nGetEntityManager();
    }

    /** Package-private: used by Engine to wrap the engine-owned EntityManager. */
    EntityManager(long nativeEntityManager) {
        mNativeObject = nativeEntityManager;
    }

    public static EntityManager get() {
        return INSTANCE;
    }

    public int create() {
        return nCreate(getNativeObject());
    }

    public void destroy(int entity) {
        nDestroy(getNativeObject(), entity);
    }

    public boolean isAlive(int entity) {
        return nIsAlive(getNativeObject(), entity);
    }

    public long getNativeObject() {
        if (mNativeObject == 0) {
            throw new IllegalStateException("Calling method on destroyed EntityManager");
        }
        return mNativeObject;
    }

    private static native long nGetEntityManager();
    private static native int nCreate(long nativeEntityManager);
    private static native void nDestroy(long nativeEntityManager, int entity);
    private static native boolean nIsAlive(long nativeEntityManager, int entity);
}
