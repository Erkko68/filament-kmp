/*
 * Copyright (C) 2019 The Android Open Source Project
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

public class RenderTarget {
    private static final int ATTACHMENT_COUNT = AttachmentPoint.values().length;
    private static final Texture.CubemapFace[] sCubemapFaceValues = Texture.CubemapFace.values();

    private long mNativeObject;
    private final Texture[] mTextures = new Texture[ATTACHMENT_COUNT];

    private RenderTarget(long nativeRenderTarget, Builder builder) {
        mNativeObject = nativeRenderTarget;
        System.arraycopy(builder.mTextures, 0, mTextures, 0, ATTACHMENT_COUNT);
    }

    public enum AttachmentPoint {
        COLOR,
        COLOR1,
        COLOR2,
        COLOR3,
        COLOR4,
        COLOR5,
        COLOR6,
        COLOR7,
        DEPTH
    }

    public static class Builder {
        private final long mNativeBuilder;
        private final Texture[] mTextures = new Texture[ATTACHMENT_COUNT];

        public Builder() {
            mNativeBuilder = nCreateBuilder();
            NativeRegistry.registerForCleanup(this, new BuilderCleanup(mNativeBuilder));
        }

        private static class BuilderCleanup implements Runnable {
            private final long mNativeBuilder;
            BuilderCleanup(long nativeBuilder) { mNativeBuilder = nativeBuilder; }
            @Override public void run() { nDestroyBuilder(mNativeBuilder); }
        }

        @NotNull public Builder texture(@NotNull AttachmentPoint attachment, @Nullable Texture texture) {
            mTextures[attachment.ordinal()] = texture;
            nBuilderTexture(mNativeBuilder, attachment.ordinal(), texture != null ? texture.getNativeObject() : 0);
            return this;
        }

        @NotNull public Builder mipLevel(@NotNull AttachmentPoint attachment, int level) { nBuilderMipLevel(mNativeBuilder, attachment.ordinal(), level); return this; }
        @NotNull public Builder face(@NotNull AttachmentPoint attachment, @NotNull Texture.CubemapFace face) { nBuilderFace(mNativeBuilder, attachment.ordinal(), face.ordinal()); return this; }
        @NotNull public Builder layer(@NotNull AttachmentPoint attachment, int layer) { nBuilderLayer(mNativeBuilder, attachment.ordinal(), layer); return this; }

        @NotNull public RenderTarget build(@NotNull Engine engine) {
            long nativeObject = nBuilderBuild(mNativeBuilder, engine.getNativeObject());
            if (nativeObject == 0) throw new IllegalStateException("Couldn't create RenderTarget");
            return new RenderTarget(nativeObject, this);
        }

    }

    @Nullable public Texture getTexture(@NotNull AttachmentPoint attachment) {
        return mTextures[attachment.ordinal()];
    }

    public int getMipLevel(@NotNull AttachmentPoint attachment) {
        return nGetMipLevel(getNativeObject(), attachment.ordinal());
    }

    @NotNull public Texture.CubemapFace getFace(@NotNull AttachmentPoint attachment) {
        return sCubemapFaceValues[nGetFace(getNativeObject(), attachment.ordinal())];
    }

    public int getLayer(@NotNull AttachmentPoint attachment) {
        return nGetLayer(getNativeObject(), attachment.ordinal());
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

    private static native int nGetMipLevel(long nativeRenderTarget, int attachment);
    private static native int nGetFace(long nativeRenderTarget, int attachment);
    private static native int nGetLayer(long nativeRenderTarget, int attachment);
}
