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
 * Holds a set of buffers that define the geometry of a Renderable.
 */
public class VertexBuffer {
    private long mNativeObject;

    public VertexBuffer(long nativeVertexBuffer) {
        mNativeObject = nativeVertexBuffer;
    }

    public enum VertexAttribute {
        POSITION, TANGENTS, COLOR, UV0, UV1, BONE_INDICES, BONE_WEIGHTS, UNUSED,
        CUSTOM0, CUSTOM1, CUSTOM2, CUSTOM3, CUSTOM4, CUSTOM5, CUSTOM6, CUSTOM7
    }

    public enum AttributeType {
        BYTE, BYTE2, BYTE3, BYTE4,
        UBYTE, UBYTE2, UBYTE3, UBYTE4,
        SHORT, SHORT2, SHORT3, SHORT4,
        USHORT, USHORT2, USHORT3, USHORT4,
        INT, UINT,
        FLOAT, FLOAT2, FLOAT3, FLOAT4,
        HALF, HALF2, HALF3, HALF4,
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

        @NotNull public Builder vertexCount(int vertexCount) { nBuilderVertexCount(mNativeBuilder, vertexCount); return this; }
        @NotNull public Builder enableBufferObjects(boolean enabled) { nBuilderEnableBufferObjects(mNativeBuilder, enabled); return this; }
        @NotNull public Builder bufferCount(int bufferCount) { nBuilderBufferCount(mNativeBuilder, bufferCount); return this; }

        @NotNull public Builder attribute(@NotNull VertexAttribute attribute, int bufferIndex, @NotNull AttributeType attributeType, int byteOffset, int byteStride) {
            nBuilderAttribute(mNativeBuilder, attribute.ordinal(), bufferIndex, attributeType.ordinal(), byteOffset, byteStride);
            return this;
        }

        @NotNull public Builder attribute(@NotNull VertexAttribute attribute, int bufferIndex, @NotNull AttributeType attributeType) {
            return attribute(attribute, bufferIndex, attributeType, 0, 0);
        }

        @NotNull public Builder normalized(@NotNull VertexAttribute attribute) { nBuilderNormalized(mNativeBuilder, attribute.ordinal(), true); return this; }
        @NotNull public Builder normalized(@NotNull VertexAttribute attribute, boolean enabled) { nBuilderNormalized(mNativeBuilder, attribute.ordinal(), enabled); return this; }

        @NotNull public VertexBuffer build(@NotNull Engine engine) {
            long nativeVertexBuffer = nBuilderBuild(mNativeBuilder, engine.getNativeObject());
            if (nativeVertexBuffer == 0) throw new IllegalStateException("Couldn't create VertexBuffer");
            return new VertexBuffer(nativeVertexBuffer);
        }

    }

    public int getVertexCount() { return nGetVertexCount(getNativeObject()); }

    public void setBufferAt(@NotNull Engine engine, int bufferIndex, @NotNull Buffer buffer) {
        setBufferAt(engine, bufferIndex, buffer, 0, 0, null, null);
    }

    public void setBufferAt(@NotNull Engine engine, int bufferIndex, @NotNull Buffer buffer, int destOffsetInBytes, int count) {
        setBufferAt(engine, bufferIndex, buffer, destOffsetInBytes, count, null, null);
    }

    public void setBufferAt(@NotNull Engine engine, int bufferIndex, @NotNull Buffer buffer, int destOffsetInBytes, int count, @Nullable Object handler, @Nullable Runnable callback) {
        int result = nSetBufferAt(getNativeObject(), engine.getNativeObject(), bufferIndex, buffer, buffer.remaining(), destOffsetInBytes, count == 0 ? buffer.remaining() : count, handler, callback);
        if (result < 0) throw new BufferOverflowException();
    }

    public void setBufferObjectAt(@NotNull Engine engine, int bufferIndex, @NotNull BufferObject bufferObject) {
        nSetBufferObjectAt(getNativeObject(), engine.getNativeObject(), bufferIndex, bufferObject.getNativeObject());
    }

    public long getNativeObject() {
        if (mNativeObject == 0) throw new IllegalStateException("Calling method on destroyed VertexBuffer");
        return mNativeObject;
    }

    void clearNativeObject() { mNativeObject = 0; }

    private static native long nCreateBuilder();
    private static native void nDestroyBuilder(long nativeBuilder);
    private static native void nBuilderVertexCount(long nativeBuilder, int vertexCount);
    private static native void nBuilderEnableBufferObjects(long nativeBuilder, boolean enabled);
    private static native void nBuilderBufferCount(long nativeBuilder, int bufferCount);
    private static native void nBuilderAttribute(long nativeBuilder, int attribute, int bufferIndex, int attributeType, int byteOffset, int byteStride);
    private static native void nBuilderNormalized(long nativeBuilder, int attribute, boolean normalized);
    private static native long nBuilderBuild(long nativeBuilder, long nativeEngine);

    private static native int nGetVertexCount(long nativeVertexBuffer);
    private static native int nSetBufferAt(long nativeVertexBuffer, long nativeEngine, int bufferIndex, Buffer buffer, int remaining, int destOffsetInBytes, int count, Object handler, Runnable callback);
    private static native void nSetBufferObjectAt(long nativeVertexBuffer, long nativeEngine, int bufferIndex, long nativeBufferObject);
}
