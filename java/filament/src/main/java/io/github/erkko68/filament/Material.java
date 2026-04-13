package io.github.erkko68.filament;

import io.github.erkko68.filament.internal.NativeRegistry;
import java.lang.ref.Cleaner;
import java.nio.Buffer;

public class Material {
    private long mNativeObject;

    private Material(long nativeMaterial) {
        mNativeObject = nativeMaterial;
    }

    public static class Builder {
        private Buffer mBuffer;
        private int mSize;

        public Builder() {}

        public Builder payload(Buffer buffer, int size) {
            mBuffer = buffer;
            mSize = size;
            return this;
        }

        public Material build(Engine engine) {
            long nativeMaterial = nBuilderBuild(engine.getNativeObject(), mBuffer, mSize);
            if (nativeMaterial == 0) throw new IllegalStateException("Couldn't create Material");
            return new Material(nativeMaterial);
        }

        private static native long nBuilderBuild(long nativeEngine, Buffer buffer, int size);
    }

    public MaterialInstance createInstance() {
        return new MaterialInstance(this, nCreateInstance(getNativeObject()));
    }

    public MaterialInstance createInstance(String name) {
        return new MaterialInstance(this, nCreateInstanceWithName(getNativeObject(), name));
    }

    public MaterialInstance getDefaultInstance() {
        return new MaterialInstance(this, nGetDefaultInstance(getNativeObject()));
    }

    public String getName() {
        return nGetName(getNativeObject());
    }

    public long getNativeObject() {
        if (mNativeObject == 0) {
            throw new IllegalStateException("Calling method on destroyed Material");
        }
        return mNativeObject;
    }

    void clearNativeObject() {
        mNativeObject = 0;
    }

    private static native long nCreateInstance(long nativeMaterial);
    private static native long nCreateInstanceWithName(long nativeMaterial, String name);
    private static native long nGetDefaultInstance(long nativeMaterial);
    private static native String nGetName(long nativeMaterial);
}
