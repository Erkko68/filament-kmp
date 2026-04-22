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

package io.github.erkko68.filament.jni;

import io.github.erkko68.filament.jni.internal.NativeRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Stream {
    private long mNativeObject;
    private long mNativeEngine;

    private Stream(long nativeStream, @NotNull Engine engine) {
        mNativeObject = nativeStream;
        mNativeEngine = engine.getNativeObject();
    }

    public static class Builder {
        private final long mNativeBuilder;

        public Builder() {
            mNativeBuilder = nCreateBuilder();
            NativeRegistry.registerForCleanup(this, new BuilderCleanup(mNativeBuilder));
        }

        private static class BuilderCleanup implements Runnable {
            private final long mNativeBuilder;
            BuilderCleanup(long nativeBuilder) { mNativeBuilder = nativeBuilder; }
            @Override public void run() { nDestroyBuilder(mNativeBuilder); }
        }

        @NotNull public Builder width(int width) { nBuilderWidth(mNativeBuilder, width); return this; }
        @NotNull public Builder height(int height) { nBuilderHeight(mNativeBuilder, height); return this; }

@NotNull public Stream build(@NotNull Engine engine) {
            long nativeObject = nBuilderBuild(mNativeBuilder, engine.getNativeObject());
            if (nativeObject == 0) throw new IllegalStateException("Couldn't create Stream");
            return new Stream(nativeObject, engine);
        }

    }

    public enum StreamType {
        NATIVE,
        ACQUIRED
    }

    public StreamType getStreamType() {
        return StreamType.values()[nGetStreamType(getNativeObject())];
    }

    public void setDimensions(int width, int height) {
        nSetDimensions(getNativeObject(), width, height);
    }

    public long getTimestamp() {
        return nGetTimestamp(getNativeObject());
    }

    public void setAcquiredImage(@Nullable Object hwbuffer, @Nullable Object handler, @Nullable Runnable callback) {
        nSetAcquiredImage(getNativeObject(), mNativeEngine, hwbuffer, handler, callback);
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
    private static native void nBuilderWidth(long nativeBuilder, int width);
    private static native void nBuilderHeight(long nativeBuilder, int height);
    private static native long nBuilderBuild(long nativeBuilder, long nativeEngine);

    private static native int nGetStreamType(long nativeStream);
    private static native void nSetDimensions(long nativeStream, int width, int height);
    private static native long nGetTimestamp(long nativeStream);
    private static native void nSetAcquiredImage(long nativeStream, long nativeEngine, Object hwbuffer, Object handler, Runnable callback);
}
