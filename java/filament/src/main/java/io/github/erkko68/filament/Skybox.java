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

public class Skybox {
    private long mNativeObject;

    public Skybox(long nativeSkybox) {
        mNativeObject = nativeSkybox;
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

        @NotNull public Builder environment(@NotNull Texture cubemap) { nBuilderEnvironment(mNativeBuilder, cubemap.getNativeObject()); return this; }
        @NotNull public Builder showSun(boolean show) { nBuilderShowSun(mNativeBuilder, show); return this; }
        @NotNull public Builder intensity(float envIntensity) { nBuilderIntensity(mNativeBuilder, envIntensity); return this; }
        @NotNull public Builder color(float r, float g, float b, float a) { nBuilderColor(mNativeBuilder, r, g, b, a); return this; }
        @NotNull public Builder color(@NotNull float[] color) { nBuilderColor(mNativeBuilder, color[0], color[1], color[2], color[3]); return this; }
        @NotNull public Builder priority(int priority) { nBuilderPriority(mNativeBuilder, priority); return this; }

        @NotNull public Skybox build(@NotNull Engine engine) {
            long nativeObject = nBuilderBuild(mNativeBuilder, engine.getNativeObject());
            if (nativeObject == 0) throw new IllegalStateException("Couldn't create Skybox");
            return new Skybox(nativeObject);
        }

    }

    public void setColor(float r, float g, float b, float a) { nSetColor(getNativeObject(), r, g, b, a); }
    public void setColor(@NotNull float[] color) { nSetColor(getNativeObject(), color[0], color[1], color[2], color[3]); }
    public void setLayerMask(int select, int values) { nSetLayerMask(getNativeObject(), select & 0xff, values & 0xff); }
    public int getLayerMask() { return nGetLayerMask(getNativeObject()); }
    public float getIntensity() { return nGetIntensity(getNativeObject()); }
    @Nullable public Texture getTexture() {
        long nativeTexture = nGetTexture(getNativeObject());
        return nativeTexture == 0 ? null : new Texture(nativeTexture);
    }

    public long getNativeObject() {
        if (mNativeObject == 0) {
            throw new IllegalStateException("Calling method on destroyed Skybox");
        }
        return mNativeObject;
    }

    void clearNativeObject() {
        mNativeObject = 0;
    }

    private static native long nCreateBuilder();
    private static native void nDestroyBuilder(long nativeSkyboxBuilder);
    private static native void nBuilderEnvironment(long nativeSkyboxBuilder, long nativeTexture);
    private static native void nBuilderShowSun(long nativeSkyboxBuilder, boolean show);
    private static native void nBuilderIntensity(long nativeSkyboxBuilder, float intensity);
    private static native void nBuilderColor(long nativeSkyboxBuilder, float r, float g, float b, float a);
    private static native void nBuilderPriority(long nativeSkyboxBuilder, int priority);
    private static native long nBuilderBuild(long nativeSkyboxBuilder, long nativeEngine);
    private static native void nSetLayerMask(long nativeSkybox, int select, int value);
    private static native int  nGetLayerMask(long nativeSkybox);
    private static native float nGetIntensity(long nativeSkybox);
    private static native void nSetColor(long nativeSkybox, float r, float g, float b, float a);
    private static native long nGetTexture(long nativeSkybox);
}
