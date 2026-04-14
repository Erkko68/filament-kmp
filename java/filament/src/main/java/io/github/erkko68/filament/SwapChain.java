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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A SwapChain represents an Operating System's native renderable surface.
 */
public class SwapChain {
    private final Object mSurface;
    private long mNativeObject;

    SwapChain(long nativeSwapChain, @Nullable Object surface) {
        mNativeObject = nativeSwapChain;
        mSurface = surface;
    }

    public static boolean isProtectedContentSupported(@NotNull Engine engine) {
        return nIsProtectedContentSupported(engine.getNativeObject());
    }

    public static boolean isSRGBSwapChainSupported(@NotNull Engine engine) {
        return nIsSRGBSwapChainSupported(engine.getNativeObject());
    }

    public static boolean isMSAASwapChainSupported(@NotNull Engine engine, int samples) {
        return nIsMSAASwapChainSupported(engine.getNativeObject(), samples);
    }

    @Nullable
    public Object getNativeWindow() {
        return mSurface;
    }

    public void setFrameCompletedCallback(@NotNull Object handler, @NotNull Runnable callback) {
        nSetFrameCompletedCallback(getNativeObject(), handler, callback);
    }

    public void setFrameScheduledCallback(@NotNull Object handler, @NotNull Runnable callback) {
        nSetFrameScheduledCallback(getNativeObject(), handler, callback);
    }

    public boolean isFrameScheduledCallbackSet() {
        return nIsFrameScheduledCallbackSet(getNativeObject());
    }

    public long getNativeObject() {
        if (mNativeObject == 0) {
            throw new IllegalStateException("Calling method on destroyed SwapChain");
        }
        return mNativeObject;
    }

    void clearNativeObject() {
        mNativeObject = 0;
    }

    private static native void nSetFrameCompletedCallback(long nativeSwapChain, Object handler, Runnable callback);
    private static native void nSetFrameScheduledCallback(long nativeSwapChain, Object handler, Runnable callback);
    private static native boolean nIsFrameScheduledCallbackSet(long nativeSwapChain);
    private static native boolean nIsSRGBSwapChainSupported(long nativeEngine);
    private static native boolean nIsMSAASwapChainSupported(long nativeEngine, int samples);
    private static native boolean nIsProtectedContentSupported(long nativeEngine);
}
