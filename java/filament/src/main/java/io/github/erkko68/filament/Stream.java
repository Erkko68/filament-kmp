package io.github.erkko68.filament;

import io.github.erkko68.filament.internal.NativeRegistry;
import java.lang.ref.Cleaner;

public class Stream {
    private long mNativeObject;

    private Stream(long nativeStream) {
        mNativeObject = nativeStream;
    }

    public static class Builder {
        private final long mNativeBuilder;
        private final Cleaner.Cleanable mCleanable;

        public Builder() {
            mNativeBuilder = nCreateBuilder();
            mCleanable = NativeRegistry.registerForCleanup(this, new BuilderCleanup(mNativeBuilder));
        }

        public Builder stream(long nativeTexture) {
            nBuilderStream(mNativeBuilder, nativeTexture);
            return this;
        }

        public Builder width(int width) {
            nBuilderWidth(mNativeBuilder, width);
            return this;
        }

        public Builder height(int height) {
            nBuilderHeight(mNativeBuilder, height);
            return this;
        }

        public Stream build(Engine engine) {
            long nativeObject = nBuilderBuild(mNativeBuilder, engine.getNativeObject());
            if (nativeObject == 0) throw new IllegalStateException("Couldn't create Stream");
            return new Stream(nativeObject);
        }

        private static class BuilderCleanup implements Runnable {
            private final long mNativeObject;
            BuilderCleanup(long nativeObject) { mNativeObject = nativeObject; }
            @Override public void run() { nDestroyBuilder(mNativeObject); }
        }
    }

    public StreamType getStreamType() {
        return StreamType.values()[nGetStreamType(getNativeObject())];
    }

    public enum StreamType {
        NATIVE,
        ACQUIRED
    }

    public long getNativeObject() {
        if (mNativeObject == 0) {
            throw new IllegalStateException("Calling method on destroyed Stream");
        }
        return mNativeObject;
    }

    void clearNativeObject() {
        mNativeObject = 0;
    }

    private static native long nCreateBuilder();
    private static native void nDestroyBuilder(long nativeBuilder);
    private static native void nBuilderStream(long nativeBuilder, long nativeTexture);
    private static native void nBuilderWidth(long nativeBuilder, int width);
    private static native void nBuilderHeight(long nativeBuilder, int height);
    private static native long nBuilderBuild(long nativeBuilder, long nativeEngine);
    private static native int nGetStreamType(long nativeStream);
}
