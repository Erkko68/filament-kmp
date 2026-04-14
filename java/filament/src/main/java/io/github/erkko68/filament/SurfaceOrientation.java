/*
 * Copyright (C) 2020 The Android Open Source Project
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

import org.jetbrains.annotations.NotNull;
import java.nio.Buffer;

/**
 * Helper used to populate TANGENTS buffers.
 */
public class SurfaceOrientation {
    private long mNativeObject;

    private SurfaceOrientation(long nativeSurfaceOrientation) {
        mNativeObject = nativeSurfaceOrientation;
    }

    public static class Builder {
        private int mVertexCount;
        private int mTriangleCount;

        private Buffer mNormals;
        private int mNormalsStride;

        private Buffer mTangents;
        private int mTangentsStride;

        private Buffer mTexCoords;
        private int mTexCoordsStride;

        private Buffer mPositions;
        private int mPositionsStride;

        private Buffer mTrianglesUint16;
        private Buffer mTrianglesUint32;

        @NotNull
        public Builder vertexCount(int vertexCount) {
            mVertexCount = vertexCount;
            return this;
        }

        @NotNull
        public Builder normals(@NotNull Buffer buffer) {
            mNormals = buffer;
            mNormalsStride = 0;
            return this;
        }

        @NotNull
        public Builder tangents(@NotNull Buffer buffer) {
            mTangents = buffer;
            mTangentsStride = 0;
            return this;
        }

        @NotNull
        public Builder uvs(@NotNull Buffer buffer) {
            mTexCoords = buffer;
            mTexCoordsStride = 0;
            return this;
        }

        @NotNull
        public Builder positions(@NotNull Buffer buffer) {
            mPositions = buffer;
            mPositionsStride = 0;
            return this;
        }

        @NotNull
        public Builder triangleCount(int triangleCount) {
            mTriangleCount = triangleCount;
            return this;
        }

        @NotNull
        public Builder triangles_uint16(@NotNull Buffer buffer) {
            mTrianglesUint16 = buffer;
            return this;
        }

        @NotNull
        public Builder triangles_uint32(@NotNull Buffer buffer) {
            mTrianglesUint32 = buffer;
            return this;
        }

        @NotNull
        public SurfaceOrientation build() {
            long builder = nCreateBuilder();
            nBuilderVertexCount(builder, mVertexCount);
            nBuilderTriangleCount(builder, mTriangleCount);

            if (mNormals != null) {
                nBuilderNormals(builder, mNormals, mNormals.remaining(), mNormalsStride);
            }

            if (mTangents != null) {
                nBuilderTangents(builder, mTangents, mTangents.remaining(), mTangentsStride);
            }

            if (mTexCoords != null) {
                nBuilderUVs(builder, mTexCoords, mTexCoords.remaining(), mTexCoordsStride);
            }

            if (mPositions != null) {
                nBuilderPositions(builder, mPositions, mPositions.remaining(), mPositionsStride);
            }

            if (mTrianglesUint16 != null) {
                nBuilderTriangles16(builder, mTrianglesUint16, mTrianglesUint16.remaining());
            }

            if (mTrianglesUint32 != null) {
                nBuilderTriangles32(builder, mTrianglesUint32, mTrianglesUint32.remaining());
            }

            long nativeSurfaceOrientation = nBuilderBuild(builder);
            nDestroyBuilder(builder);
            if (nativeSurfaceOrientation == 0) {
                throw new IllegalStateException("Could not create SurfaceOrientation");
            }
            return new SurfaceOrientation(nativeSurfaceOrientation);
        }
    }

    public long getNativeObject() {
        if (mNativeObject == 0) {
            throw new IllegalStateException("Calling method on destroyed SurfaceOrientation");
        }
        return mNativeObject;
    }

    public int getVertexCount() {
        return nGetVertexCount(mNativeObject);
    }

    public void getQuatsAsFloat(@NotNull Buffer buffer) {
        nGetQuatsAsFloat(mNativeObject, buffer, buffer.remaining());
    }

    public void getQuatsAsHalf(@NotNull Buffer buffer) {
        nGetQuatsAsHalf(mNativeObject, buffer, buffer.remaining());
    }

    public void getQuatsAsShort(@NotNull Buffer buffer) {
        nGetQuatsAsShort(mNativeObject, buffer, buffer.remaining());
    }

    public void destroy() {
        nDestroy(mNativeObject);
        mNativeObject = 0;
    }

    private static native long nCreateBuilder();
    private static native void nDestroyBuilder(long nativeBuilder);

    private static native void nBuilderVertexCount(long nativeBuilder, int vertexCount);
    private static native void nBuilderNormals(long nativeBuilder, Buffer buffer, int remaining, int stride);
    private static native void nBuilderTangents(long nativeBuilder, Buffer buffer, int remaining, int stride);
    private static native void nBuilderUVs(long nativeBuilder, Buffer buffer, int remaining, int stride);
    private static native void nBuilderPositions(long nativeBuilder, Buffer buffer, int remaining, int stride);
    private static native void nBuilderTriangleCount(long nativeBuilder, int triangleCount);
    private static native void nBuilderTriangles16(long nativeBuilder, Buffer buffer, int remaining);
    private static native void nBuilderTriangles32(long nativeBuilder, Buffer buffer, int remaining);
    private static native long nBuilderBuild(long nativeBuilder);

    private static native int nGetVertexCount(long nativeSurfaceOrientation);
    private static native void nGetQuatsAsFloat(long nativeSurfaceOrientation, Buffer buffer, int remaining);
    private static native void nGetQuatsAsHalf(long nativeSurfaceOrientation, Buffer buffer, int remaining);
    private static native void nGetQuatsAsShort(long nativeSurfaceOrientation, Buffer buffer, int remaining);
    private static native void nDestroy(long nativeSurfaceOrientation);
}
