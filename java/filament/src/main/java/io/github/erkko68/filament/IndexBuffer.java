/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.erkko68.filament;

import io.github.erkko68.filament.internal.NativeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.Buffer;
import java.nio.BufferOverflowException;

/**
 * A buffer containing vertex indices into a VertexBuffer. Indices can be 16 or 32 bit.
 */
public class IndexBuffer {
    private long mNativeObject;

    public IndexBuffer(long nativeIndexBuffer) {
        mNativeObject = nativeIndexBuffer;
    }

    public static class Builder {
        private final long mNativeBuilder;

        public enum IndexType {
            USHORT, UINT
        }

        public Builder() {
            mNativeBuilder = nCreateBuilder();
            NativeRegistry.registerForCleanup(this, new BuilderCleanup(mNativeBuilder));
        }

        private static class BuilderCleanup implements Runnable {
            private final long mNativeBuilder;
            BuilderCleanup(long nativeBuilder) { mNativeBuilder = nativeBuilder; }
            @Override public void run() { nDestroyBuilder(mNativeBuilder); }
        }

        @NotNull public Builder indexCount(int indexCount) { nBuilderIndexCount(mNativeBuilder, indexCount); return this; }
        @NotNull public Builder bufferType(@NotNull IndexType indexType) { nBuilderBufferType(mNativeBuilder, indexType.ordinal()); return this; }

        @NotNull public IndexBuffer build(@NotNull Engine engine) {
            long nativeIndexBuffer = nBuilderBuild(mNativeBuilder, engine.getNativeObject());
            if (nativeIndexBuffer == 0) throw new IllegalStateException("Couldn't create IndexBuffer");
            return new IndexBuffer(nativeIndexBuffer);
        }
    }

    public int getIndexCount() { return nGetIndexCount(getNativeObject()); }

    public void setBuffer(@NotNull Engine engine, @NotNull Buffer buffer) {
        setBuffer(engine, buffer, 0, 0, null, null);
    }

    public void setBuffer(@NotNull Engine engine, @NotNull Buffer buffer, int destOffsetInBytes, int count) {
        setBuffer(engine, buffer, destOffsetInBytes, count, null, null);
    }

    public void setBuffer(@NotNull Engine engine, @NotNull Buffer buffer, int destOffsetInBytes, int count, @Nullable Object handler, @Nullable Runnable callback) {
        int result = nSetBuffer(getNativeObject(), engine.getNativeObject(), buffer, buffer.remaining(), destOffsetInBytes, count == 0 ? buffer.remaining() : count, handler, callback);
        if (result < 0) throw new BufferOverflowException();
    }

    public long getNativeObject() {
        if (mNativeObject == 0) throw new IllegalStateException("Calling method on destroyed IndexBuffer");
        return mNativeObject;
    }

    void clearNativeObject() { mNativeObject = 0; }

    private static native long nCreateBuilder();
    private static native void nDestroyBuilder(long nativeBuilder);
    private static native void nBuilderIndexCount(long nativeBuilder, int indexCount);
    private static native void nBuilderBufferType(long nativeBuilder, int indexType);
    private static native long nBuilderBuild(long nativeBuilder, long nativeEngine);

    private static native int nGetIndexCount(long nativeIndexBuffer);
    private static native int nSetBuffer(long nativeIndexBuffer, long nativeEngine, Buffer buffer, int remaining, int destOffsetInBytes, int count, Object handler, Runnable callback);
}
