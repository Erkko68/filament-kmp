package io.github.erkko68.filament;

public class EntityManager {
    private static final EntityManager sInstance = new EntityManager();
    private long mNativeObject;

    private EntityManager() {
        mNativeObject = nGetEntityManager();
    }

    public static EntityManager get() {
        return sInstance;
    }

    public int create() {
        return nCreate(mNativeObject);
    }

    public void destroy(int entity) {
        nDestroy(mNativeObject, entity);
    }

    private static native long nGetEntityManager();
    private static native int nCreate(long nativeEntityManager);
    private static native void nDestroy(long nativeEntityManager, int entity);
}
