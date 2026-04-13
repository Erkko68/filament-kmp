package io.github.erkko68.filament.utils;

public class Bookmark {
    private long mNativeObject;

    Bookmark(long nativeObject) {
        mNativeObject = nativeObject;
    }

    long getNativeObject() {
        return mNativeObject;
    }

    @Override
    protected void finalize() throws Throwable {
        if (mNativeObject != 0) {
            nDestroyBookmark(mNativeObject);
            mNativeObject = 0;
        }
        super.finalize();
    }

    private static native void nDestroyBookmark(long nativeObject);
}
