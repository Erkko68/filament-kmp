package io.github.erkko68.filament;

import io.github.erkko68.filament.internal.NativeRegistry;
import java.lang.ref.Cleaner;

public class RenderTarget {
    private long mNativeObject;

    private RenderTarget(long nativeRenderTarget) {
        mNativeObject = nativeRenderTarget;
    }

    public enum AttachmentPoint {
        COLOR0,
        COLOR1,
        COLOR2,
        COLOR3,
        COLOR4,
        COLOR5,
        COLOR6,
        COLOR7,
        DEPTH,
        STENCIL
    }

    public static class Builder {
        private final long mNativeBuilder;
        private final Cleaner.Cleanable mCleanable;

        public Builder() {
            mNativeBuilder = nCreateBuilder();
            mCleanable = NativeRegistry.registerForCleanup(this, new BuilderCleanup(mNativeBuilder));
        }

        public Builder texture(AttachmentPoint attachment, Texture texture) {
            nBuilderTexture(mNativeBuilder, attachment.ordinal(), texture.getNativeObject());
            return this;
        }

        public Builder mipLevel(AttachmentPoint attachment, int level) {
            nBuilderMipLevel(mNativeBuilder, attachment.ordinal(), level);
            return this;
        }

        public Builder face(AttachmentPoint attachment, Texture.Sampler face) {
            nBuilderFace(mNativeBuilder, attachment.ordinal(), face.ordinal());
            return this;
        }

        public Builder layer(AttachmentPoint attachment, int layer) {
            nBuilderLayer(mNativeBuilder, attachment.ordinal(), layer);
            return this;
        }

        public RenderTarget build(Engine engine) {
            long nativeObject = nBuilderBuild(mNativeBuilder, engine.getNativeObject());
            if (nativeObject == 0) throw new IllegalStateException("Couldn't create RenderTarget");
            return new RenderTarget(nativeObject);
        }

        private static class BuilderCleanup implements Runnable {
            private final long mNativeObject;
            BuilderCleanup(long nativeObject) { mNativeObject = nativeObject; }
            @Override public void run() { nDestroyBuilder(mNativeObject); }
        }
    }

    public long getNativeObject() {
        if (mNativeObject == 0) {
            throw new IllegalStateException("Calling method on destroyed RenderTarget");
        }
        return mNativeObject;
    }

    void clearNativeObject() {
        mNativeObject = 0;
    }

    private static native long nCreateBuilder();
    private static native void nDestroyBuilder(long nativeBuilder);
    private static native void nBuilderTexture(long nativeBuilder, int attachment, long nativeTexture);
    private static native void nBuilderMipLevel(long nativeBuilder, int attachment, int level);
    private static native void nBuilderFace(long nativeBuilder, int attachment, int face);
    private static native void nBuilderLayer(long nativeBuilder, int attachment, int layer);
    private static native long nBuilderBuild(long nativeBuilder, long nativeEngine);
}
